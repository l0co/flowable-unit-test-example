package com.lifeinide.flowable.test

import com.fasterxml.jackson.databind.ObjectMapper
import org.flowable.common.engine.api.delegate.event.FlowableEvent
import org.flowable.common.engine.impl.interceptor.CommandContext
import org.flowable.common.engine.impl.util.DefaultClockImpl
import org.flowable.engine.delegate.BpmnError
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl
import org.flowable.engine.impl.event.logger.AbstractEventFlusher
import org.flowable.engine.impl.event.logger.EventFlusher
import org.flowable.engine.impl.event.logger.EventLogger
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl
import org.flowable.engine.runtime.ProcessInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Logs basic flowable events for test purposes.
 *
 * @author Lukasz Frankowski
 */
class ProcessTestEnvironment: EventLogger(DefaultClockImpl(), ObjectMapper()) {

    var processInstance: ProcessInstance? = null
    var exception: Throwable? = null
        set(value) {
            if (value is BpmnError)
                logger.debug("Exception thrown: {}", value.errorCode)
            else
                logger.debug("Exception thrown", value)
            field = value
        }

    val events: List<Map<String, Any>> = mutableListOf()

    protected val eventFlusher: AbstractEventFlusher = object: AbstractEventFlusher() {
        override fun closeFailure(commandContext: CommandContext?) {
        }

        override fun afterSessionsFlush(commandContext: CommandContext?) {
        }

        @Suppress("UNCHECKED_CAST")
        override fun closing(commandContext: CommandContext?) {
            // extract data from event handlers
            for (eventHandler in eventHandlers) {
                val eventLogEntryEntity = eventHandler.generateEventLogEntry(commandContext)
                val map = objectMapper.readValue(eventLogEntryEntity.data, HashMap::class.java) as HashMap<String, Any>
                map[FIELD_TYPE] = eventLogEntryEntity.type
                (events as MutableList).add(map)
            }
        }
    }

    override fun createEventFlusher(): EventFlusher = eventFlusher

    override fun onEvent(event: FlowableEvent?) {
        // getting the process instance on process start
        if (event is FlowableProcessStartedEventImpl && event.entity is ExecutionEntityImpl) {
            processInstance = (event.entity as ExecutionEntityImpl).processInstance
        }

        super.onEvent(event)
    }

    companion object {

        @JvmStatic val logger: Logger = LoggerFactory.getLogger(ProcessTestEnvironment::class.java)

        const val FIELD_TYPE = "TYPE"

    }
}
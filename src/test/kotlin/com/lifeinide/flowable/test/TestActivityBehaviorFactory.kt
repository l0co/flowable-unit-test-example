package com.lifeinide.flowable.test

import org.flowable.bpmn.model.CallActivity
import org.flowable.bpmn.model.EndEvent
import org.flowable.bpmn.model.ErrorEventDefinition
import org.flowable.bpmn.model.MapExceptionEntry
import org.flowable.common.engine.api.FlowableException
import org.flowable.engine.delegate.BpmnError
import org.flowable.engine.delegate.DelegateExecution
import org.flowable.engine.impl.bpmn.behavior.CallActivityBehavior
import org.flowable.engine.impl.bpmn.behavior.ErrorEndEventActivityBehavior
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Lukasz Frankowski
 */
class TestActivityBehaviorFactory: DefaultActivityBehaviorFactory() {

    protected val mockedCallActivityResults: MutableMap<String, MockedCallActivityResult> = ConcurrentHashMap()

    inner class MockableCallActivityBehavior(
        internal val wrappedBehavior: CallActivityBehavior,
        protected val id: String,
        processDefinitionKey: String?,
        calledElementType: String?,
        mapExceptions: MutableList<MapExceptionEntry>?
    ): CallActivityBehavior(processDefinitionKey, calledElementType, true, mapExceptions) {

        override fun execute(execution: DelegateExecution) {
            mockedCallActivityResults[id]?.execute(execution) ?: run { wrappedBehavior.execute(execution) }
        }

        override fun completed(execution: DelegateExecution?) {
            if (!isMocked())
                wrappedBehavior.completed(execution)
        }

        override fun completing(execution: DelegateExecution?, subProcessInstance: DelegateExecution?) {
            if (!isMocked())
                wrappedBehavior.completing(execution, subProcessInstance)
        }

        fun isMocked(): Boolean {
            return mockedCallActivityResults[id] != null
        }

    }

    override fun createErrorEndEventActivityBehavior(endEvent: EndEvent?, errorEventDefinition: ErrorEventDefinition?): ErrorEndEventActivityBehavior {
        return object: ErrorEndEventActivityBehavior(errorEventDefinition?.errorCode) {
            override fun execute(execution: DelegateExecution?) {
                try {
                    super.execute(execution)
                } catch (e: FlowableException) {
                    errorEventDefinition?.errorCode?.let {errorCode ->
                        logger.debug("Re-throwing BPMN error with code: {} on uncaught end event exception", errorCode)
                        throw BpmnError(errorCode)
                    }
                    throw e
                }
            }
        }
    }

    override fun createCallActivityBehavior(callActivity: CallActivity): CallActivityBehavior {
        return MockableCallActivityBehavior(super.createCallActivityBehavior(callActivity), callActivity.id,
            callActivity.calledElement, callActivity.calledElementType, callActivity.mapExceptions)
    }

    /**
     * Registers mocked call activity result.
     */
    fun registerMockedCallActivityResults(vararg mockedCallActivities: Pair<String, MockedCallActivityResult>) {
        for ((callActivityId, mockedCallActivityResult) in mockedCallActivities) {
            if (mockedCallActivityResults[callActivityId] != null && mockedCallActivityResults[callActivityId] != mockedCallActivityResult)
                throw IllegalStateException("Mock call activity: $callActivityId result is already registered")
            mockedCallActivityResults[callActivityId] = mockedCallActivityResult
        }
    }

    /**
     * Unregisters all mocked call activity results.
     */
    fun unregisterMockedCallActivityResults() {
        mockedCallActivityResults.clear()
    }

    companion object {

        @JvmStatic val logger: Logger = LoggerFactory.getLogger(TestActivityBehaviorFactory::class.java)

    }

}
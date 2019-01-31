package com.lifeinide.flowable.test

import org.assertj.core.api.Assertions.assertThat
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType
import org.flowable.engine.impl.event.logger.handler.Fields
import org.flowable.variable.service.impl.persistence.entity.VariableScopeImpl
import kotlin.reflect.KClass

/**
 * Wraps [ProcessTestEnvironment] with handy assertions
 *
 * @author Lukasz Frankowski
 */
class ProcessAssertions(protected val processTestEnvironment: ProcessTestEnvironment) {

    /**
     * Checks whether the process variable is set
     **/
    fun assertVariable(name: String, value: Any) {
        assertThat((processTestEnvironment.processInstance as VariableScopeImpl).getVariable(name)).isEqualTo(value)
    }

    /**
     * Checks whether the activity has been started. This means the process flow reached the activity, but it could be either completed
     * or fail with an exception.
     **/
    fun assertActivityStarted(name: String, started: Boolean = true) {
        val events = processTestEnvironment.events.filter {
            it[ProcessTestEnvironment.FIELD_TYPE] == FlowableEngineEventType.ACTIVITY_STARTED.name && it[Fields.ACTIVITY_ID] == name
        }

        assertThat(events)
            .withFailMessage("Activity $name has ${if (started) "not" else ""} been started")
            .hasSize(if (started) 1 else 0)
    }

    fun assertActivityNotStarted(name: String) = assertActivityStarted(name, false)

    /**
     * Checks whether the activity has been completed, ie. ended without exception.
     **/
    fun assertActivityCompleted(name: String, completed: Boolean = true) {
        val events = processTestEnvironment.events.filter {
            it[ProcessTestEnvironment.FIELD_TYPE] == FlowableEngineEventType.ACTIVITY_COMPLETED.name && it[Fields.ACTIVITY_ID] == name
        }

        assertThat(events)
            .withFailMessage("Activity $name has ${if (completed) "not" else ""} been completed")
            .hasSize(if (completed) 1 else 0)
    }

    fun assertActivityNotCompleted(name: String) = assertActivityCompleted(name, false)

    /**
     * Checks whether process ended with the exception of given class.
     */
    fun assertException(clazz: KClass<out Throwable>) {
        assertThat(processTestEnvironment.exception)
            .withFailMessage("Process hasn't ended with exception")
            .isNotNull()

        assertThat(processTestEnvironment.exception)
            .withFailMessage("Process ended with exception: ${processTestEnvironment.exception!!.javaClass.simpleName} " +
                "which is different than asserted: ${clazz.java.simpleName}")
            .isOfAnyClassIn(clazz.java)
    }

}
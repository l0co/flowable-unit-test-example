package com.lifeinide.flowable.test

import org.flowable.bpmn.model.CallActivity
import org.flowable.engine.delegate.BpmnError
import org.flowable.engine.delegate.DelegateExecution
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation
import org.flowable.engine.impl.persistence.entity.ExecutionEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Lukasz Frankowski
 */
abstract class MockedCallActivityResult: AbstractBpmnActivityBehavior() {

    class NoOpCallActivityResult: MockedCallActivityResult() {

        override fun execute(execution: DelegateExecution?) {
            logger.debug("Mocking call activity: ${execution!!.currentFlowElement.id} by doing nothing")
            super.execute(execution)
        }

    }

    class VariableSetCallActivityResult(vararg val variables: Pair<String, Any>): MockedCallActivityResult() {

        override fun execute(execution: DelegateExecution) {
            for ((key, value) in variables) {
                (execution.currentFlowElement as CallActivity).let {
                    var mapped = false
                    for (ioParameter in it.outParameters)
                        if (ioParameter.source == key) {
                            logger.debug("Mocking call activity: ${execution.currentFlowElement.id} by mapping result variable: " +
                                "${ioParameter.source} -> ${ioParameter.target} with value: $value")
                            execution.setVariable(ioParameter.target, value)
                            mapped = true
                            break
                        }

                    if (!mapped) {
                        logger.debug("Skipping mocking call activity: ${execution.currentFlowElement.id} by mapping result variable: " +
                            "$key, because of not found variable mapping in output")
                    }
                }
            }


            super.execute(execution)
        }
    }

    class ExceptionCallActivityResult(val ex: Throwable): MockedCallActivityResult() {

        constructor(errorCode: String): this(BpmnError(errorCode))

        override fun execute(execution: DelegateExecution?) {
            try {
                logger.debug("Mocking call activity: ${execution!!.currentFlowElement.id} by throwning exception: {}",
                    if (ex is BpmnError) "BpmnError(${ex.errorCode})" else ex::class.simpleName)
                throw ex
            } catch (exc: Throwable) {
                // taken from ServiceTaskExpressionActivityBehavior
                var cause: Throwable? = exc
                var error: BpmnError? = null
                while (cause != null) {
                    if (cause is BpmnError) {
                        error = cause
                        break
                    } else if (cause is RuntimeException) {
                        if (ErrorPropagation.mapException(cause as RuntimeException?, execution as ExecutionEntity, ArrayList())) {
                            return
                        }
                    }
                    cause = cause.cause
                }

                if (error != null) {
                    ErrorPropagation.propagateError(error, execution)
                } else {
                    throw exc
                }
            }
        }

    }

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(MockedCallActivityResult::class.java)
    }

}
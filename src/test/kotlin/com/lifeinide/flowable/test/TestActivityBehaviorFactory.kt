package com.lifeinide.flowable.test

import org.flowable.bpmn.model.EndEvent
import org.flowable.bpmn.model.ErrorEventDefinition
import org.flowable.common.engine.api.FlowableException
import org.flowable.engine.delegate.BpmnError
import org.flowable.engine.delegate.DelegateExecution
import org.flowable.engine.impl.bpmn.behavior.ErrorEndEventActivityBehavior
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Lukasz Frankowski
 */
class TestActivityBehaviorFactory(wrappedActivityBehaviorFactory: ActivityBehaviorFactory): org.flowable.engine.test.TestActivityBehaviorFactory(wrappedActivityBehaviorFactory) {

    override fun createErrorEndEventActivityBehavior(endEvent: EndEvent?, errorEventDefinition: ErrorEventDefinition?): ErrorEndEventActivityBehavior {
        return object: ErrorEndEventActivityBehavior(errorEventDefinition?.errorCode) {
            override fun execute(execution: DelegateExecution?) {
                try {
                    super.execute(execution)
                } catch (e: FlowableException) {
                    errorEventDefinition?.errorCode.let {errorCode ->
                        logger.debug("Re-throwing BPMN error with code: {} on uncaught end event exception", errorCode)
                        throw BpmnError(errorCode)
                    }

                }
            }
        }
    }

    companion object {

        @JvmStatic val logger: Logger = LoggerFactory.getLogger(TestActivityBehaviorFactory::class.java)

    }


}
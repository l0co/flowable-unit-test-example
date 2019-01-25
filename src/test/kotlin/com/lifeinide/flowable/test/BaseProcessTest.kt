package com.lifeinide.flowable.test

import org.flowable.engine.RuntimeService
import org.flowable.engine.test.mock.Mocks
import org.junit.AfterClass
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Lukasz Frankowski
 */
abstract class BaseProcessTest: BaseSpringTest() {

    @Autowired protected lateinit var runtimeService: RuntimeService

    abstract fun processName(): String

    fun startProcess(variables: Map<String, Any> = mapOf()) {
        runtimeService.startProcessInstanceByKey(processName(), variables)
    }

    companion object {

        @AfterClass @JvmStatic
        fun tearDown() {
            Mocks.reset()
        }

    }

}
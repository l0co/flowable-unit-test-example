package com.lifeinide.flowable.test

import org.flowable.engine.RuntimeService
import org.flowable.engine.test.mock.Mocks
import org.junit.After
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Lukasz Frankowski
 */
@RunWith(SpringRunner::class)
@SpringBootTest
abstract class BaseProcessTest {

    @Autowired protected lateinit var runtimeService: RuntimeService

    abstract fun processName(): String

    fun startProcess(variables: Map<String, Any> = mapOf()): ProcessAssertions {
        logger.debug("Starting process: ${processName()}")

        val processTestEnvironment = ProcessTestEnvironment()
        runtimeService.addEventListener(processTestEnvironment)

        try {
            runtimeService.startProcessInstanceByKey(processName(), variables)
        } catch(e: Throwable) {
            processTestEnvironment.exception = e
        } finally {
            runtimeService.removeEventListener(processTestEnvironment)
        }

        return ProcessAssertions(processTestEnvironment) // TODOLF continue here
    }

    @After
    fun tearDown() {
        logger.debug("Unregistering mocks")
        Mocks.reset()
    }

    companion object {

        @JvmStatic val logger: Logger = LoggerFactory.getLogger(BaseProcessTest::class.java)

    }

}
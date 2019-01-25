package com.lifeinide.flowable

import org.flowable.engine.RepositoryService
import org.flowable.engine.RuntimeService
import org.flowable.engine.TaskService
import org.flowable.spring.SpringProcessEngineConfiguration
import org.flowable.spring.boot.EngineConfigurationConfigurer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

/**
 * @author Lukasz Frankowski
 */
@SpringBootApplication
class FlowableTestApplication {

    /** Flowable configuration bean */
    @Bean
    fun flowableSpringProcessEngineConfig() = EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    }

    /** Bean to only log how many processes we've succesfully deployed */
    @Bean
    fun init(repositoryService: RepositoryService,
             runtimeService: RuntimeService,
             taskService: TaskService): CommandLineRunner {

        return CommandLineRunner {
            logger.debug("Found ${repositoryService.createProcessDefinitionQuery().list().size} processes")
        }
    }

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(FlowableTestApplication::class.java)
    }

}

fun main(args: Array<String>) {
    runApplication<FlowableTestApplication>(*args)
}


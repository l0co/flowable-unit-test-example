package com.lifeinide.flowable.test

import org.flowable.engine.test.mock.MockExpressionManager
import org.flowable.spring.SpringProcessEngineConfiguration
import org.flowable.spring.boot.EngineConfigurationConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Lukasz Frankowski
 */
@Configuration
class TestConfiguration {

    /** Flowable configuration bean */
    @Bean
    fun flowableTestSpringProcessEngineConfig() = EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

        // register mock expression manager to make org.flowable.engine.test.mock.Mocks available for processes
        it.expressionManager = MockExpressionManager()

    }


}
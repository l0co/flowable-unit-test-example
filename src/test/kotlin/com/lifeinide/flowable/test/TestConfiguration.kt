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

    /** Custom activity behavior factory for tests **/
    @Bean fun testActivityBehaviorFactory(): TestActivityBehaviorFactory = TestActivityBehaviorFactory()

    /** Flowable configuration bean */
    @Bean
    fun flowableTestSpringProcessEngineConfig(testActivityBehaviorFactory: TestActivityBehaviorFactory) =
            EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

        // register mock expression manager to make org.flowable.engine.test.mock.Mocks available for processes
        it.expressionManager = MockExpressionManager()

        // registers custom activity behavior factory
        it.activityBehaviorFactory = testActivityBehaviorFactory()

    }

}
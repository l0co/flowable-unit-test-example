package com.lifeinide.flowable.test

import org.flowable.engine.RuntimeService
import org.springframework.beans.factory.annotation.Autowired



/**
 * @author Lukasz Frankowski
 */
abstract class BaseProcessTest: BaseSpringTest() {

    @Autowired protected lateinit var runtimeService: RuntimeService

}
package com.lifeinide.flowable.process

import com.lifeinide.flowable.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * A service task for the process of banning user.
 *
 * @author Lukasz Frankowski
 */
@Component(BanUserServiceTask.BEAN_NAME)
class BanUserServiceTask @Autowired constructor(
    val userService: UserService
) {

    fun ban(userId: String) {
        userService.findUser(userId)?.let {
            logger.debug("Banning user: $it")
            userService.ban(it)
        }

        throw IllegalStateException() // shouldn't ever happen in the process
    }

    companion object {

        const val PROCESS_NAME = "BanUser" // process name
        const val BEAN_NAME = "banUserServiceTask" // bean name
        const val VAR_USER_ID = "userId" // process variable
        const val ERR_USER_BAN = "ERR_USER_BAN" // bpmn error code

        @JvmStatic val logger: Logger = LoggerFactory.getLogger(BanUserServiceTask::class.java)

    }

}
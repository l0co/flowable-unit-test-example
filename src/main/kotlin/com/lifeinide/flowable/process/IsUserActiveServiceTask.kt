package com.lifeinide.flowable.process

import com.lifeinide.flowable.service.UserService
import org.flowable.engine.delegate.BpmnError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Example of service task to control example process.
 *
 * @author Lukasz Frankowski
 */
@Component(IsUserActiveServiceTask.BEAN_NAME)
class IsUserActiveServiceTask @Autowired constructor(
    val userService: UserService
) {

    fun checkUserExists(userId: String) {
        if (userService.findUser(userId) == null)
            throw BpmnError(ERR_USER_NOT_EXIST)
    }

    fun isUserActive(userId: String): Boolean {
        userService.findUser(userId)?.let {
            return !userService.isBanned(it)
        }

        return false
    }

    companion object {

        const val PROCESS_NAME = "IsUserActive" // process name
        const val BEAN_NAME = "isUserActiveServiceTask" // service task bean name
        const val VAR_USER_ID = "userId" // process variable
        const val VAR_RESULT = "result" // process variable
        const val ERR_USER_NOT_EXIST = "ERR_USER_NOT_EXIST" // bpmn error code

    }

}
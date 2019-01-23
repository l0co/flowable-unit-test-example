package com.lifeinide.flowable.process

import com.lifeinide.flowable.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Example of service task to control example process.
 *
 * @author Lukasz Frankowski
 */
@Component
class IsUserActiveServiceTask @Autowired constructor(
    val userService: UserService
) {

    fun userExists(userId: String): Boolean {
        return userService.findUser(userId) != null
    }

    fun userIsActive(userId: String): Boolean {
        userService.findUser(userId)?.let {
            return !userService.isBanned(it)
        }

        return false
    }

    companion object {

        const val VAR_USER_ID = "userId" // process variable

    }

}
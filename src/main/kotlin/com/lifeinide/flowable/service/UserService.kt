package com.lifeinide.flowable.service

import com.lifeinide.flowable.model.User
import org.springframework.stereotype.Service

/**
 * Business service for [User]s.
 *
 * @author Lukasz Frankowski
 */
@Service
class UserService {

    fun findUser(id: String): User? {
        throw NotImplementedError()
    }

    fun isBanned(user: User): Boolean {
        throw NotImplementedError()
    }

    fun ban(user: User) {
        throw NotImplementedError()
    }

}
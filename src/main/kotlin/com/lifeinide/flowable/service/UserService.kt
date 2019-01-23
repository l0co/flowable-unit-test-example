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

    private val userRepository: MutableMap<String, User> = mutableMapOf()

    fun findUser(id: String): User? {
        return userRepository[id]
    }

    fun saveUser(user: User) {
        userRepository[user.id] = user
    }

    fun isBanned(user: User): Boolean {
        return user.banned
    }

    fun ban(user: User) {
        user.banned = true
    }

}
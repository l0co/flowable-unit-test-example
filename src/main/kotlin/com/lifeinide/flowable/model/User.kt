package com.lifeinide.flowable.model

import java.io.Serializable
import java.util.*

/**
 * User domain object.
 *
 * @author Lukasz Frankowski
 */
class User(val id: String): Serializable {

    constructor(): this(UUID.randomUUID().toString())

    var banned = false

}
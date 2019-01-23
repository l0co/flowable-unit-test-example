package com.lifeinide.flowable.model

import java.io.Serializable

/**
 * User domain object.
 *
 * @author Lukasz Frankowski
 */
class User(val id: String): Serializable {

    var banned = false

}
package com.lifeinide.flowable.test.process

import com.lifeinide.flowable.model.User
import com.lifeinide.flowable.process.IsUserActiveServiceTask
import com.lifeinide.flowable.test.BaseProcessTest
import org.junit.Test

/**
 * @author Lukasz Frankowski
 */
class IsUserActiveProcessTest: BaseProcessTest() {

    override fun processName(): String = IsUserActiveServiceTask.PROCESS_NAME

    @Test
    fun testUserNotExists() {
        print("Hello from test user not exists") // TODOLF implement me
    }

    @Test
    fun testUserActive() {
        print("Hello from test user active") // TODOLF implement me

        val mockUser = User()
        startProcess(mapOf(IsUserActiveServiceTask.VAR_USER_ID to mockUser.id))
    }

    @Test
    fun testUserInactive() {
        print("Hello from test user inactive") // TODOLF implement me
    }

}
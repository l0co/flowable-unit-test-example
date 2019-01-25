package com.lifeinide.flowable.test.process

import com.lifeinide.flowable.model.User
import com.lifeinide.flowable.process.IsUserActiveServiceTask
import com.lifeinide.flowable.service.UserService
import com.lifeinide.flowable.test.BaseProcessTest
import org.flowable.engine.test.mock.Mocks
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
        val mockUserService = mock(UserService::class.java)
        `when`(mockUserService.findUser(mockUser.id)).thenReturn(mockUser)
        val mockServiceTask = IsUserActiveServiceTask(mockUserService)
        Mocks.register(IsUserActiveServiceTask.BEAN_NAME, mockServiceTask)

        startProcess(mapOf(IsUserActiveServiceTask.VAR_USER_ID to mockUser.id))
    }

    @Test
    fun testUserInactive() {
        print("Hello from test user inactive") // TODOLF implement me
    }

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(IsUserActiveProcessTest::class.java)
    }

}
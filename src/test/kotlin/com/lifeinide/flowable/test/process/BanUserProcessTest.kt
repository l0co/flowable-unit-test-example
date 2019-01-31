package com.lifeinide.flowable.test.process

import com.lifeinide.flowable.model.User
import com.lifeinide.flowable.process.BanUserServiceTask
import com.lifeinide.flowable.process.IsUserActiveServiceTask
import com.lifeinide.flowable.service.UserService
import com.lifeinide.flowable.test.BaseProcessTest
import com.lifeinide.flowable.test.ProcessAssertions
import org.flowable.engine.test.mock.Mocks
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Lukasz Frankowski
 */
class BanUserProcessTest: BaseProcessTest() {

    override fun processName(): String = BanUserServiceTask.PROCESS_NAME

    protected fun prepareEnvAndStartProcess(userExists: Boolean = true, userIsActive: Boolean = true): ProcessAssertions {
        val mockUserService = mock(UserService::class.java)
        val mockUser = User()

        if (userExists) {

            `when`(mockUserService.findUser(mockUser.id)).thenReturn(mockUser)
            `when`(mockUserService.isBanned(mockUser)).thenReturn(!userIsActive)

        } else {

            `when`(mockUserService.findUser(ArgumentMatchers.anyString())).thenReturn(null)

        }

        doNothing().`when`(mockUserService).ban(mockUser)

        Mocks.register(BanUserServiceTask.BEAN_NAME, BanUserServiceTask(mockUserService))
        Mocks.register(IsUserActiveServiceTask.BEAN_NAME, IsUserActiveServiceTask(mockUserService))
        return startProcess(mapOf(BanUserServiceTask.VAR_USER_ID to mockUser.id))
    }

    @Test
    fun testUserNotExists() {
        logger.debug("testUserNotExists()")
        with (prepareEnvAndStartProcess(userExists = false)) {
            assertActivityStarted("isUserActiveCallActivity")
            assertActivityNotCompleted("isUserActiveCallActivity")
            assertBpmnError(BanUserServiceTask.ERR_USER_BAN)
        }
    }

    @Test
    fun testUserActive() {
        logger.debug("testUserActive()")
        with (prepareEnvAndStartProcess(userIsActive = true)) {
            assertActivityCompleted("isUserActiveCallActivity")
            assertActivityCompleted("banUserTask")
        }
    }

    @Test
    fun testUserInactive() {
        logger.debug("testUserInactive()")
        with (prepareEnvAndStartProcess(userIsActive = false)) {
            assertActivityCompleted("isUserActiveCallActivity")
            assertActivityNotStarted("banUserTask")
            assertBpmnError(BanUserServiceTask.ERR_USER_BAN)
        }
    }

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(BanUserProcessTest::class.java)
    }

}
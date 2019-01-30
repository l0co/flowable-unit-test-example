package com.lifeinide.flowable.test.process

import com.lifeinide.flowable.model.User
import com.lifeinide.flowable.process.IsUserActiveServiceTask
import com.lifeinide.flowable.service.UserService
import com.lifeinide.flowable.test.BaseProcessTest
import com.lifeinide.flowable.test.ProcessAssertions
import org.flowable.engine.test.mock.Mocks
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Lukasz Frankowski
 */
class IsUserActiveProcessTest: BaseProcessTest() {

    override fun processName(): String = IsUserActiveServiceTask.PROCESS_NAME

    protected fun prepareEnvAndStartProcess(userExists: Boolean = true, userIsActive: Boolean = true): ProcessAssertions {
        val mockUserService: UserService
        val mockUser = User()

        if (userExists) {

            mockUserService = mock(UserService::class.java)
            `when`(mockUserService.findUser(mockUser.id)).thenReturn(mockUser)
            `when`(mockUserService.isBanned(mockUser)).thenReturn(!userIsActive)

        } else {

            mockUserService = mock(UserService::class.java)
            `when`(mockUserService.findUser(ArgumentMatchers.any())).thenReturn(null)

        }

        Mocks.register(IsUserActiveServiceTask.BEAN_NAME, IsUserActiveServiceTask(mockUserService))
        return startProcess(mapOf(IsUserActiveServiceTask.VAR_USER_ID to mockUser.id))
    }

//    @Test
//    fun testUserNotExists() {
//        logger.debug("testUserNotExists()") // TODOLF implement me
//    }

    @Test
    fun testUserActive() {
        logger.debug("testUserActive()")
        with (prepareEnvAndStartProcess(userIsActive = true)) {
            assertActivityCompleted("checkUserExists")
            assertActivityCompleted("isUserActive")
            assertVariable(IsUserActiveServiceTask.VAR_RESULT, true)
        }
    }

    @Test
    fun testUserInactive() {
        logger.debug("testUserInactive()")
        with (prepareEnvAndStartProcess(userIsActive = false)) {
            assertActivityCompleted("checkUserExists")
            assertActivityCompleted("isUserActive")
            assertVariable(IsUserActiveServiceTask.VAR_RESULT, false)
        }
    }

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(IsUserActiveProcessTest::class.java)
    }

}
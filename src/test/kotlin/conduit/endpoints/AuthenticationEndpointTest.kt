package conduit.endpoints

import conduit.Router
import conduit.handlers.LoggedInUserInfo
import conduit.model.*
import conduit.utils.toJsonTree
import io.mockk.every
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AuthenticationEndpointTest {
    lateinit var router: Router

    @BeforeEach
    fun beforeEach() {
        router = getRouterToTest()
    }

    @Test
    fun `should return a User on successful login`() {
        every { router.loginHandler.invoke(any()) } returns LoggedInUserInfo(
            Email("jake@jake.jake"),
            Token("jwt.token.here"),
            Username("jake"),
            Bio("I work at statefarm"),
            null
        )

        @Language("JSON")
        val requestBody = """
            {
              "user":{
                "email": "jake@jake.jake",
                "password": "jakejake"
              }
            }
        """.trimIndent()
        val request = Request(Method.POST, "/api/users/login").body(requestBody)

        val resp = router()(request)

        @Language("JSON")
        val expectedResponseBody = """
            {
              "user": {
                "email": "jake@jake.jake",
                "token": "jwt.token.here",
                "username": "jake",
                "bio": "I work at statefarm",
                "image": null
              }
            }
        """.trimIndent().toJsonTree()
        assertEquals(expectedResponseBody, resp.bodyString().toJsonTree())
        assertEquals(Status.OK, resp.status)
        assertEquals("application/json; charset=utf-8", resp.header("Content-Type"))
    }

    @Test
    fun `should return 400 if email or password are not available in request body`() {
        @Language("JSON")
        val requestBody = """
            {
              "user":{
              }
            }
        """.trimIndent()
        val request = Request(Method.POST, "/api/users/login").body(requestBody)

        val resp = router()(request)


        assertEquals(Status.BAD_REQUEST, resp.status)
    }

    // TODO: write test for cases that handler throws an exception
}
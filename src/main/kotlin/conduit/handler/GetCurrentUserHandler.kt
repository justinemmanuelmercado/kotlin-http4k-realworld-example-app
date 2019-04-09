package conduit.handler

import conduit.model.extractEmail
import conduit.repository.ConduitDatabase
import conduit.util.HttpException
import conduit.util.TokenAuth
import org.http4k.core.Status

interface GetCurrentUserHandler {
    operator fun invoke(tokenInfo: TokenAuth.TokenInfo): UserDto
}

class GetCurrentUserHandlerImpl(val database: ConduitDatabase) : GetCurrentUserHandler {
    override fun invoke(tokenInfo: TokenAuth.TokenInfo): UserDto {
        val email = tokenInfo.extractEmail()
        val user = database.tx {
            getUser(email) ?: throw HttpException(Status.NOT_FOUND, "User with email ${email.value} not found.")
        }

        return UserDto(
            user.email,
            tokenInfo.token,
            user.username,
            user.bio,
            user.image
        )
    }
}
package com.example.plugins

import Repo.UserRepo
import com.example.Auth.JwtService
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

fun Application.configAuth()
{
    val userDb = UserRepo()
    val jwt = JwtService()

    install(Authentication)
    {
        jwt("jwt"){
            verifier(jwt.verifier)
            realm = "Todo Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("userId")
                val claimInt = claim.asInt()
                val user = userDb.getById(claimInt)
                user
            }
        }
    }
}
package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.serialization.Serializable


@Serializable
data class Session(val userId : Int)


fun Application.Session()
{
    install(Sessions) {
        cookie<Session>("MY_SESSION") {
            cookie.extensions ["SameSite"] = "lax"
        }
    }
}

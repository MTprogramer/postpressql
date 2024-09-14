package com.example.plugins

import Repo.UserRepo
import Routing.user
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val db = UserRepo()
    routing {
        user(db)
    }
}

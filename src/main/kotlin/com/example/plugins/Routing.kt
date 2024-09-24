package com.example.plugins

import Repo.TodoRepo
import Repo.UserRepo
import Routing.user
import com.example.Auth.JwtService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val db = UserRepo()
    val todoRepo = TodoRepo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> s }
    routing {
        user(db , todoRepo , jwtService , hashFunction)
    }
}

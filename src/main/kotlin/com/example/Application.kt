package com.example

import DataBase.DatabaseFactory
import Repo.UserRepo
import com.example.Auth.JwtService
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.sessions.Sessions

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)


}

fun Application.module() {
    DatabaseFactory.init()
    Session()
    configureSerialization()
    configureRouting()
}
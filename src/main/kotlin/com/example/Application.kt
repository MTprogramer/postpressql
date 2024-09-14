package com.example

import DataBase.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)


}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()
}
package com.example.Auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService {
    private val issuer = "Sever"
    private val jwtSecrate = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecrate)

    val verifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(userId: Int): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
        .sign(algorithm)



}
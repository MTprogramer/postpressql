package Routing


import Repo.UserRepo
import io.ktor.http.*
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.put


/**
 * User API_VERSION is change when we change the db structure
 * here is all logics for handling database operations
 */

const val API_VERSION = "v1/"
const val USER_CREATE = "$API_VERSION/user"
const val GET_USER_BY_ID = "$API_VERSION/user/{userId}"

fun Route.user(db: UserRepo) {
    // Create a user
    post(USER_CREATE) {
        val parameters = call.receive<Parameters>()

        val name = parameters["userName"] ?: return@post call.respondText(
            "Missing field: name",
            status = HttpStatusCode.Unauthorized
        )
        val age = parameters["userAge"] ?: return@post call.respondText(
            "Missing field: age",
            status = HttpStatusCode.Unauthorized
        )
        val email = parameters["userEmail"] ?: return@post call.respondText(
            "Missing field: email",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val user = db.inset(email ,name, age.toInt())
            user?.userId?.let {
                call.respond(status = HttpStatusCode.OK , user)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    get(USER_CREATE) {
        try {
            val userList = db.getAll()
            if (userList.isNotEmpty()) {
                call.respond(userList)
            } else {
                call.respondText("No Data found!", status = HttpStatusCode.OK)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to retrieve users", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving users")
        }
    }

    // Get user by ID
    get(GET_USER_BY_ID) {
        val userId = call.parameters["userId"]?.toIntOrNull() ?: return@get call.respondText(
            "Invalid user ID",
            status = HttpStatusCode.BadRequest
        )

        try {
            val user = db.getById(userId) ?: return@get call.respondText(
                "User not found",
                status = HttpStatusCode.NotFound
            )
            call.respond(status = HttpStatusCode.OK, user)
        } catch (e: Throwable) {
            application.log.error("Failed to retrieve user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
        }
    }

    // Delete user by ID
    delete(GET_USER_BY_ID) {
        val userId = call.parameters["userId"]?.toIntOrNull() ?: return@delete call.respondText(
            "Invalid user ID",
            status = HttpStatusCode.BadRequest
        )

        try {
            val deleted = db.deleteById(userId)
            if (deleted) {
                call.respondText("Deleted successfully", status = HttpStatusCode.OK)
            } else {
                call.respondText("User ID not found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to delete user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems deleting user")
        }
    }

    // Update user by ID
    put(GET_USER_BY_ID) {
        val userId = call.parameters["userId"]?.toIntOrNull() ?: return@put call.respondText(
            "Invalid user ID",
            status = HttpStatusCode.BadRequest
        )

        val updateInfo = call.receive<Parameters>()
        val name = updateInfo["userName"] ?: return@put call.respondText(
            "Missing field: name",
            status = HttpStatusCode.Unauthorized
        )
        val email = updateInfo["userEmail"] ?: return@put call.respondText(
            "Missing field: email",
            status = HttpStatusCode.Unauthorized
        )
        val age = updateInfo["userAge"] ?: return@put call.respondText(
            "Missing field: age",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val result = db.updateById(userId,email, name, age.toInt())
            if (result) {
                call.respondText("Updated successfully", status = HttpStatusCode.OK)
            } else {
                call.respondText("User not found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to update user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems updating user")
        }
    }
}

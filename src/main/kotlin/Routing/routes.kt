package Routing


import Repo.TodoRepo
import Repo.UserRepo
import com.example.Auth.JwtService
import com.example.plugins.Session
import io.ktor.http.*
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.put
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set


/**
 * User API_VERSION is change when we change the db structure
 * here is all logics for handling database operations
 */

const val API_VERSION = "v1/"
const val USER_CREATE = "$API_VERSION/user"
const val GET_USER_BY_ID = "$API_VERSION/user/{userId}"

fun Route.user(
    db: UserRepo,
    todoRepo: TodoRepo,
    jwtService: JwtService,
    hash:(String) -> String
) {
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
        val password = parameters["password"] ?: return@post call.respondText(
            "Missing field: password",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val user = db.inset(email ,name, password , age.toInt())
            user?.userId?.let {
                call.respond(status = HttpStatusCode.OK , user)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    // Create a user by auth
    post("$USER_CREATE/create") {
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
        val password = parameters["password"] ?: return@post call.respondText(
            "Missing field: password",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)
        val currentUser = db.inset(name , email , hashPassword , age.toInt())

        try {
            currentUser?.userId?.let {
                call.sessions.set(Session(it))
                call.respondText(
                    jwtService.generateToken(it),
                    status = HttpStatusCode.Created
                )
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    // login user
    post("$USER_CREATE/login") {
        val parameters = call.receive<Parameters>()

        val email = parameters["userEmail"] ?: return@post call.respondText(
            "Missing field: email",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameters["password"] ?: return@post call.respondText(
            "Missing field: password",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)
        val currentUser = db.getByEmail(email)

        try {
            currentUser?.email?.let {
                if (currentUser.password == hashPassword)
                {
                    call.sessions.set(Session(currentUser.userId))
                    call.respondText(
                        jwtService.generateToken(currentUser.userId),
                        status = HttpStatusCode.Created
                    )
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }


    // Delete user by auth
    delete("$USER_CREATE/delete") {

         val user = call.sessions.get<Session>()?.let {
             db.getById(it.userId)
         }
        if (user == null)
        {
            call.respondText(
                "Invalid user ID",
                status = HttpStatusCode.BadRequest
            )
        }

        try {
            user?.userId?.let { todoRepo.deleteAllByUserId(user.userId) }
            val currentUser = user?.userId?.let { db.deleteById(it)}
            if (currentUser!!)
            {
                call.respondText("user deleted ")
            }
            else
            {
                call.respondText("getting problem")
            }
        }catch (e : Exception)
        {
            call.respondText("deleted catch called")
        }

    }


    //update data
    put("$USER_CREATE/update")
    {
        val parameters = call.receive<Parameters>()
        val name = parameters["userName"] ?: return@put call.respondText(
            "Missing field: name",
            status = HttpStatusCode.Unauthorized
        )
        val age = parameters["userAge"] ?: return@put call.respondText(
            "Missing field: age",
            status = HttpStatusCode.Unauthorized
        )
        val email = parameters["userEmail"] ?: return@put call.respondText(
            "Missing field: email",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameters["password"] ?: return@put call.respondText(
            "Missing field: password",
            status = HttpStatusCode.Unauthorized
        )

        val user = call.sessions.get<Session>()?.let {
            db.getById(it.userId)
        }
        if (user == null)
        {
            call.respondText(
                "user not get from session",
                status = HttpStatusCode.BadRequest
            )
        }
        val hashPass = hash(password)

        try {
            val result = user?.userId?.let { db.updateUser(name , email , hashPass , age.toInt() , it) }
            if (result!!)
                call.respondText("Updated successfully")
            else
                call.respondText("error is updating user")
        }catch (e : Exception)
        {
            call.respondText("not updated")
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


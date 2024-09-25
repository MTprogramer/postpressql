package Routing

import Repo.TodoRepo
import Repo.UserRepo
import com.example.plugins.Session
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters

import io.ktor.server.application.log
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions


const val Todo_CREATE = "$API_VERSION/todo"
const val TODO_BY_ID = "$API_VERSION/{id}"

fun Route.todo(
    userRepo: UserRepo,
    todoRepo: TodoRepo
)
{
    authenticate("jwt")
    {
        post(Todo_CREATE)
        {
            val parameters = call.receive<Parameters>()

            val title = parameters["title"] ?: return@post call.respondText(
                "Missing field: title",
                status = HttpStatusCode.Unauthorized
            )
            val completed = parameters["completed"] ?: return@post call.respondText(
                "Missing field: completed",
                status = HttpStatusCode.Unauthorized
            )
            val user = call.sessions.get<Session>()?.let {
                userRepo.getById(it.userId)
            }

            if (user == null)
            {
                call.respondText(
                    "user not get from session",
                    status = HttpStatusCode.BadRequest
                )

            }

            try {
                val todo = todoRepo.insert(title , completed.toBoolean() , user!!.userId)
                todo?.let {
                    call.respond(status = HttpStatusCode.OK  , todo)
                }
            }catch (e : Throwable)
            {
                application.log.error("Failed to register user", e)
                call.respond(HttpStatusCode.BadRequest, "Problems creating User")
            }
        }
    }



    get(TODO_BY_ID)
    {
        val parameters = call.receive<Parameters>()
        val id = parameters["id"] ?: return@get call.respondText(
            "Missing field: id",
            status = HttpStatusCode.Unauthorized
        )

        try
        {
            val todo = todoRepo.getById(id.toInt())
            todo?.let {
                call.respond(status = HttpStatusCode.OK , todo)
            }
        }catch (e:Exception)
        {
            call.respondText("getting problem")
        }

    }
    get(Todo_CREATE)
    {

        val user = call.sessions.get<Session>()?.let {
            userRepo.getById(it.userId)
        }
        if (user == null) {
            call.respondText(
                "user not get from session",
                status = HttpStatusCode.BadRequest
            )
        }
        try
        {
            val todo = user?.let { todoRepo.getAllTodoById(it.userId) }
            todo?.let {
                call.respond(status = HttpStatusCode.OK , todo)
            }
        }catch (e:Exception)
        {
            call.respondText("getting problem")
        }

    }

    delete(TODO_BY_ID)
    {
        val parameters = call.receive<Parameters>()
        val id = parameters["id"]?: return@delete call.respondText (
            "id missing",
            status = HttpStatusCode.Unauthorized
        )

        val user = call.sessions.get<Session>()?.let {
            userRepo.getById(it.userId)
        }

        if (user == null)
            call.respondText (
                "user not found in session", status = HttpStatusCode.Unauthorized
            )

        try {
            val allTodo = user?.let { todoRepo.getAllTodoById(it.userId) }
            if (!allTodo.isNullOrEmpty())
            {
                allTodo.forEach {
                    if (it.id == id.toInt())
                    {
                       val result = todoRepo.deleteById(id.toInt())
                        if (result) call.respond(status = HttpStatusCode.OK , "Deleted Todo successfully")
                    }
                    else
                        call.respond(status = HttpStatusCode.OK , "Not Deleted ")
                }
            }
        }catch (e:Exception)
        {
            call.respond( "Unsuccessful")
        }
    }

    delete(Todo_CREATE)
    {
        val user = call.sessions.get<Session>()?.let {
            userRepo.getById(it.userId)
        }

        if (user == null)
            call.respondText (
                "user not found in session", status = HttpStatusCode.Unauthorized
            )
        try {
            val result = user?.let { todoRepo.deleteAllByUserId(it.userId) }
            if (result!!)
                call.respond(status = HttpStatusCode.OK , "Deleted Todo successfully")
            else
                call.respond(status = HttpStatusCode.OK , "Not Deleted ")

        }catch (e:Exception)
        {
            call.respond( "Unsuccessful")
        }
    }


    put(TODO_BY_ID)
    {
        val parameters = call.receive<Parameters>()

        val title = parameters["title"] ?: return@put call.respondText(
            "Missing field: title",
            status = HttpStatusCode.Unauthorized
        )
        val id = parameters["id"] ?: return@put call.respondText(
            "Missing field: id",
            status = HttpStatusCode.Unauthorized
        )
        val completed = parameters["completed"] ?: return@put call.respondText(
            "Missing field: completed",
            status = HttpStatusCode.Unauthorized
        )
        val user = call.sessions.get<Session>()?.let {
            userRepo.getById(it.userId)
        }

        if (user == null)
        {
            call.respondText(
                "user not get from session",
                status = HttpStatusCode.BadRequest
            )

        }

        try {
            val alltodo = user?.let { todoRepo.getAllTodoById(it.userId) }
            alltodo?.forEach {
                if (it.id == id.toInt())
                {
                    val result = todoRepo.updateById(id.toInt() , title , completed.toBoolean())
                    if (result)
                        call.respond(status = HttpStatusCode.OK , "Updated Todo successfully")
                    else
                        call.respond(status = HttpStatusCode.OK , "Not Updated ")
                }
            }

        }catch (e : Throwable)
        {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

}
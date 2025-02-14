package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    routing {

        suspend fun sendResponse(call: ApplicationCall, code: Int, message: String, data: List<TaskDTO> = emptyList()) {
            call.respond(BaseResponse(code, message, data))
        }

        post("/register") {
            val user = call.receive<UserDTO>()
            val userExists = transaction { Users.select { Users.username eq user.username }.count() > 0 }

            if (userExists) {
                sendResponse(call, -1, "Username already exists")
            } else {

                transaction {
                    Users.insert {
                        it[username] = user.username
                        it[password] = user.password
                    }
                }
                sendResponse(call, 1, "User registered successfully")
            }
        }

        post("/login") {
            val user = call.receive<UserDTO>()
            val userRecord = transaction {
                Users.select { Users.username eq user.username }
                    .map { it[Users.id] to it[Users.password] }
                    .firstOrNull()
            }

            if (userRecord == null) {
                sendResponse(call, -2, "User not found")
            } else if (userRecord.second != user.password) {
                sendResponse(call, -1, "Wrong Password")
            } else {
                sendResponse(call, userRecord.first, "Login Successful")
            }
        }

        get("/getTasks") {
            try {
                val userId = call.request.queryParameters["id"]?.toIntOrNull()
                if (userId == null) {
                    sendResponse(call, -1, "Invalid user ID")
                    return@get
                }

                val tasks = transaction {
                    Tasks.select { Tasks.userId eq userId }
                        .map {
                            TaskDTO(
                                userId = it[Tasks.userId],
                                title = it[Tasks.title],
                                content = it[Tasks.content],
                                lastModifiedDate = it[Tasks.lastModifiedDate],
                                id = it[Tasks.id]
                            )
                        }
                }

                sendResponse(call, 1, "", tasks)
            } catch (e: Exception) {
                sendResponse(call, -1, "Error: ${e.localizedMessage}")
            }
        }


        post("/addTask") {
            try {
                val task = call.receive<TaskDTO>()

                val insertedRows = transaction {
                    Tasks.insert {
                        it[userId] = task.userId
                        it[title] = task.title
                        it[content] = task.content
                        it[lastModifiedDate] = task.lastModifiedDate
                    }
                }.insertedCount

                if (insertedRows > 0) {
                    sendResponse(call, 1, "Task added successfully")
                } else {
                    sendResponse(call, -1, "Task insertion failed")
                }
            } catch (e: Exception) {
                sendResponse(call, -1, e.localizedMessage)
            }
        }

        put("/updateTask") {
            try {
                val taskUpdate = call.receive<TaskDTO>()
                println("Received task update: $taskUpdate")

                val taskId = taskUpdate.id
                if (taskId == 0) {
                    sendResponse(call, -1, "Invalid task ID")
                    return@put
                }

                val updatedRows = transaction {
                    Tasks.update({ Tasks.id eq taskId }) {
                        it[title] = taskUpdate.title
                        it[content] = taskUpdate.content
                        it[lastModifiedDate] = taskUpdate.lastModifiedDate
                    }
                }

                if (updatedRows > 0) {
                    sendResponse(call, 1, "Task updated successfully")
                } else {
                    sendResponse(call, -2, "Task not found or no changes made")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                sendResponse(call, -1, "Error: ${e.localizedMessage}")
            }
        }

        delete("/deleteTask") {
            val taskId = call.request.queryParameters["id"]?.toIntOrNull()

            val response = try {
                if (taskId != null) {
                    val deletedRows = transaction {
                        Tasks.deleteWhere { Tasks.id eq taskId }
                    }
                    if (deletedRows > 0) {
                        BaseResponse(code = 1, message = "Task deleted successfully", data = emptyList())
                    } else {
                        BaseResponse(code = -2, message = "Task not found", data = emptyList())
                    }
                } else {
                    BaseResponse(code = -1, message = "Invalid task ID", data = emptyList())
                }
            } catch (e: Exception) {
                BaseResponse(code = -1, message = "Error occurred: ${e.localizedMessage}", data = emptyList())
            }

            call.respond(response)
        }

    }
}
package com.example.API.Controller2.controllers

import dto.AddTaskDTO
import dto.ResponseTaskDTO
import dto.TaskItemDTO
import dto.TaskListResponseDTO
import jakarta.validation.Valid
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import java.sql.DriverManager
import java.time.LocalDateTime
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/api")
class HelloController (private val jdbcTemplate: JdbcTemplate) {
// Создаем таблицы руками в базе
    init {
        createLogTable()
        createTasksTable()
    }

    //  hello_calls
    // добавляем таблицу hello_calls
    private fun createLogTable() {
        val url = "jdbc:postgresql://localhost:5430/postgres_db"
        val user = "admin"
        val password = "admin12345678"

        DriverManager.getConnection(url, user, password).use { connection ->
            connection.createStatement().use { statement ->
                statement.execute("CREATE TABLE IF NOT EXISTS hello_calls (call_time TIMESTAMP NOT NULL)")
            }
        }
    }
    // функция что бы писать call_time в таблицу hello_calls
    private fun logCall() {
        // Исправил: JdbcTemplate вместо DriverManager
        jdbcTemplate.update(
            "INSERT INTO hello_calls (call_time) VALUES (?)",
            LocalDateTime.now()
        )
    }
//    private fun logCall() {
//        val url = "jdbc:postgresql://localhost:5430/postgres_db"
//        val user = "admin"
//        val password = "admin12345678"
//
//        DriverManager.getConnection(url, user, password).use { connection ->
//            val sql = "INSERT INTO hello_calls (call_time) VALUES (?)"
//            connection.prepareStatement(sql).use { statement ->
//                statement.setObject(1, LocalDateTime.now())
//                statement.executeUpdate()
//            }
//        }
//    }

    // методы для tasks
    // добавляем таблицу
    private fun createTasksTable() {
        // Используем JdbcTemplate
        jdbcTemplate.execute("""
        CREATE TABLE IF NOT EXISTS tasks (
            id SERIAL PRIMARY KEY,
            text TEXT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            is_completed BOOLEAN DEFAULT FALSE
        )
    """.trimIndent())
    }
//    private fun createTasksTable() {
//        val url = "jdbc:postgresql://localhost:5430/postgres_db"
//        val user = "admin"
//        val password = "admin12345678"
//
//        DriverManager.getConnection(url, user, password).use { connection ->
//            connection.createStatement().use { statement ->
//                statement.execute("""
//                    CREATE TABLE IF NOT EXISTS tasks (
//                        id SERIAL PRIMARY KEY,
//                        text TEXT NOT NULL,
//                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
//                        is_completed BOOLEAN DEFAULT FALSE
//                    )
//                """.trimIndent())
//            }
//        }
//    }

    @PostMapping("/task")
    fun addTask(@Valid @RequestBody request: AddTaskDTO): ResponseTaskDTO {
        val taskText = request.text
//        val url = "jdbc:postgresql://localhost:5430/postgres_db"
//        val user = "admin"
//        val password = "admin12345678"
        val params  = listOf(taskText ,LocalDateTime.now()).toTypedArray()
        jdbcTemplate.batchUpdate (
            "INSERT INTO tasks (text, created_at) VALUES (?, ?)",
            listOf(params))
        return ResponseTaskDTO(
            message = "Задача добавлена",
            success = true,
            task = taskText
        )
//        return mapOf(
//            "success" to true,
//            "message" to "Задача добавлена",
//            "task" to taskText
//        )
//        DriverManager.getConnection(url, user, password).use { connection ->
//            val sql = "INSERT INTO tasks (text, created_at) VALUES (?, ?)"
//            connection.prepareStatement(sql).use { statement ->
//                statement.setString(1, taskText)
//                statement.setObject(2, LocalDateTime.now())
//                statement.executeUpdate()
//
//                return mapOf(
//                    "success" to true,
//                    "message" to "Задача добавлена",
//                    "task" to taskText
//                )
//            }
//        }
    }
    @GetMapping("/tasks")
    fun getAllTasks(): TaskListResponseDTO {
        val tasks = jdbcTemplate.query("SELECT * FROM tasks ORDER BY created_at DESC") { resultSet, _ ->
            TaskItemDTO(
                id = resultSet.getInt("id"),
                text = resultSet.getString("text"),
                createdAt = resultSet.getTimestamp("created_at").toString(),
                isCompleted = resultSet.getBoolean("is_completed")
            )
        }

        return TaskListResponseDTO(
            success = true,
            count = tasks.size,
            tasks = tasks
        )
    }
//    fun getAllTasks(): Map<String, Any> {
//        val tasks = jdbcTemplate.query(          // Для SELECT используем query() !!!!!
//            "SELECT * FROM tasks ORDER BY created_at DESC") { resultSet, _ ->
//            mapOf(
//                "id" to resultSet.getInt("id"),
//                "text" to resultSet.getString("text"),
//                "created_at" to resultSet.getTimestamp("created_at").toString(),
//                "is_completed" to resultSet.getBoolean("is_completed")
//            )
//        }
//        return mapOf(
//            "success" to true,
//            "count" to tasks.size,
//            "tasks" to tasks
//        )
//    }
//    @GetMapping("/tasks")
//    fun getAllTasks(): Map<String, Any> {
//        val url = "jdbc:postgresql://localhost:5430/postgres_db"
//        val user = "admin"
//        val password = "admin12345678"
//
//        DriverManager.getConnection(url, user, password).use { connection ->
//            connection.createStatement().use { statement ->
//                val resultSet = statement.executeQuery("SELECT * FROM tasks ORDER BY created_at DESC")
//
//                val tasks = mutableListOf<Map<String, Any>>()
//                while (resultSet.next()) {
//                    tasks.add(mapOf(
//                        "id" to resultSet.getInt("id"),
//                        "text" to resultSet.getString("text"),
//                        "created_at" to resultSet.getTimestamp("created_at").toString(),
//                        "is_completed" to resultSet.getBoolean("is_completed")
//                    ))
//                }
//
//                return mapOf(
//                    "success" to true,
//                    "count" to tasks.size,
//                    "tasks" to tasks
//                )
//            }
//        }
//    }

    // Тут мы будем добавдять роут для удаления задач
    // @DeleteMapping("/task")

    // методы для hello

    @GetMapping("/hello")
    fun helloWorld(): Map<String, String> {
        logCall()
        return mapOf("message" to "Hello World! 🚀")
    }

    @GetMapping("/health")
    fun healthCheck(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "service" to "MVP HelloController Service",
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/greet/{name}")
    fun greetUser(@PathVariable name: String): Map<String, String> {
        return mapOf("greeting" to "Hello, $name!")
    }

    @GetMapping("/info")
    fun serviceInfo(): Map<String, Any> {
        return mapOf(
            "version" to "1.0.0",
            "environment" to "development",
            "uptime" to java.lang.management.ManagementFactory.getRuntimeMXBean().uptime
        )
    }
}
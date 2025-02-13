package com.example.database

import com.example.Tasks
import com.example.Users
import com.example.safe.Constant.DATABASE_URL
import com.example.safe.Constant.PASSWORD
import com.example.safe.Constant.USERNAME
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            DATABASE_URL,
            driver = "org.mariadb.jdbc.Driver",
            user = USERNAME,
            password = PASSWORD
        )

        transaction {
            SchemaUtils.create(Users, Tasks)
        }
    }
}
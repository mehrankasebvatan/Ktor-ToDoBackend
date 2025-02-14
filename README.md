Ktor ToDo Backend
=================

This is a simple backend for a ToDo app built with Ktor and Exposed.

🚀 Features
-----------

*   User authentication (register & login)
*   Create, read, update, and delete tasks
*   Uses MariaDB as the database
*   Structured with MVVM and Clean Architecture principles

⚙️ Setup & Installation
-----------------------

    git clone https://github.com/mehrankasebvatan/Ktor-ToDoBackend.git
    cd Ktor-ToDoBackend
    ./gradlew build
    java -jar build/libs/ktor-sample-all.jar
    

📡 API Endpoints
----------------

*   `POST /register` - Register a new user
*   `POST /login` - Authenticate user
*   `POST /addTask` - Create a new task
*   `GET /getTasks?id=1` - Get tasks for a user
*   `PUT /updateTask` - Update a task
*   `DELETE /deleteTask?id=5` - Delete a task

📜 License
----------

MIT License

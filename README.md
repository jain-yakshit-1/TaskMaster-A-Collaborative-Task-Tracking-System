# TaskMaster

## Overview

TaskMaster is a Spring Boot application designed to manage tasks and facilitate collaboration within teams or projects. The application provides a RESTful API for task creation, assignment, and tracking, allowing users to collaborate through comments and attachments. TaskMaster supports user authentication and authorization, enabling secure access to task management features. It also provides real-time notifications and integrates a generative AI model to automatically generate task descriptions or summaries based on user input.

## Features

- **User Registration and Login**: Secure user authentication and authorization with JWT.
- **Task Management**: Create, update, delete, and view tasks with attributes like title, description, and due date. Tasks can be filtered, sorted, and searched.
- **Team/Project Collaboration**: Create and manage teams or projects. Assign tasks to team members and collaborate through comments and attachments.
- **Real-time Notifications**: Receive real-time updates and notifications when tasks are assigned or updated.
- **Generative AI Integration**: Automatically generate task descriptions or summaries based on user input.
- **Swagger Documentation**: Explore and test all API endpoints via Swagger UI.

## Endpoints

### Authentication

- **POST /api/register**
  - Register a new user.

- **POST /api/login**
  - Log in a user.

- **GET /api/profile**
  - View user profile.

- **PUT /api/profile**
  - Update user profile.

### Task Management

- **POST /api/tasks**
  - Create a new task.

- **GET /api/tasks**
  - Retrieve all tasks assigned to the logged-in user.

- **GET /api/tasks/{id}**
  - Retrieve a specific task by ID.

- **PUT /api/tasks/{id}**
  - Update a task by ID.

- **DELETE /api/tasks/{id}**
  - Delete a task by ID.

- **POST /api/tasks/{id}/complete**
  - Mark a task as completed.

### Task Collaboration

- **POST /api/tasks/{id}/comments**
  - Add a comment to a task.

- **POST /api/tasks/{id}/attachments**
  - Attach a file to a task.

### Team/Project Management

- **POST /api/teams**
  - Create a new team or project.

- **POST /api/teams/{id}/invite**
  - Invite users to join a team or project.

### Notifications

- **GET /api/notifications**
  - Retrieve real-time notifications for task updates and assignments.

## Generative AI Integration

The system integrates a generative AI model to assist with task creation by automatically generating task descriptions or summaries. This feature reduces manual effort and speeds up the process of task management.

## Setup and Installation

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd taskmaster

2. **Build the Project**:
   ```bash
   ./gradlew build
   ```
3. **Run the Application**:
   ```
   ./gradlew bootRun
   ```

## Swagger Documentation

Swagger is integrated for interactive API documentation. You can explore and test the API endpoints using the Swagger UI at: http://localhost:8080/swagger-ui/index.html
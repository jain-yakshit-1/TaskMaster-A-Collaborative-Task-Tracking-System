package com.airtribe.taskmaster.controller;

import com.airtribe.taskmaster.model.Attachment;
import com.airtribe.taskmaster.model.Comment;
import com.airtribe.taskmaster.model.Task;
import com.airtribe.taskmaster.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "Create a new task", description = "This endpoint creates a new task.")
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all tasks", description = "This endpoint retrieves all tasks, with optional filters like status, sortBy, and query.")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String query) {
        List<Task> tasks = taskService.getAllTasks(status, sortBy, query);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @Operation(summary = "Get task by ID", description = "Retrieve task details by task ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return task != null ? new ResponseEntity<>(task, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update a task", description = "Update task details for a specific task ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return updatedTask != null ? new ResponseEntity<>(updatedTask, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Delete a task", description = "Delete a task by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Add a comment to a task", description = "Add a comment to a specific task.")
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId, @Valid @RequestBody Comment comment) {
        Comment createdComment = taskService.addComment(taskId, comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @Operation(summary = "Get comments for a task", description = "Retrieve all comments for a specific task.")
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByTaskId(@PathVariable Long taskId) {
        List<Comment> comments = taskService.getCommentsByTaskId(taskId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Upload an attachment to a task", description = "Upload a file attachment to a specific task.")
    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<Attachment> uploadAttachment(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) throws IOException {
        Attachment attachment = taskService.saveAttachment(taskId, file);
        return new ResponseEntity<>(attachment, HttpStatus.CREATED);
    }

    @Operation(summary = "Get attachments for a task", description = "Retrieve all attachments for a specific task.")
    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<Attachment>> getAttachmentsByTaskId(@PathVariable Long taskId) {
        List<Attachment> attachments = taskService.getAttachmentsByTaskId(taskId);
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }
}
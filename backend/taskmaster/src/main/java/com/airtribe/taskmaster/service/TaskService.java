package com.airtribe.taskmaster.service;

import com.airtribe.taskmaster.model.Task;
import com.airtribe.taskmaster.model.User;
import com.airtribe.taskmaster.model.Comment;
import com.airtribe.taskmaster.model.Notification;
import com.airtribe.taskmaster.model.Attachment;
import com.airtribe.taskmaster.repository.TaskRepository;
import com.airtribe.taskmaster.repository.CommentRepository;
import com.airtribe.taskmaster.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private VertexAiGeminiChatModel vertexAiGeminiChatModel;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Task createTask(Task task) {
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            String generatedDescription = vertexAiGeminiChatModel.call("Generate a description for a task named " + task.getTitle());
            task.setDescription(generatedDescription);
        }
        Task createdTask = taskRepository.save(task);
        return createdTask;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            User previousAssignedUser = task.getAssignedTo();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setDueDate(taskDetails.getDueDate());
            task.setCompleted(taskDetails.isCompleted());
            task.setAssignedTo(taskDetails.getAssignedTo());
            Task updatedTask = taskRepository.save(task);
            
             // Send notification if the task is assigned to a new user
            if (taskDetails.getAssignedTo() != null && !taskDetails.getAssignedTo().equals(previousAssignedUser)) {
                sendNotification(new Notification("Task Assigned", "A task has been assigned to you: " + task.getTitle()), taskDetails.getAssignedTo().getId());
            }

            // Send notification if the task is updated and the user is already assigned
            if (previousAssignedUser != null && previousAssignedUser.equals(taskDetails.getAssignedTo())) {
                sendNotification(new Notification("Task Updated", "A task assigned to you has been updated: " + task.getTitle()), previousAssignedUser.getId());
            }
            return updatedTask;
        }
        return null;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByAssignedToId(userId);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> searchTasks(String query) {
        return taskRepository.findByTitleContainingOrDescriptionContaining(query, query);
    }

    public List<Task> getAllTasks(String status, String sortBy, String query) {
        if (status != null) {
            return getTasksByStatus(status);
        }
        if (query != null) {
            return searchTasks(query);
        }
        if (sortBy != null) {
            return taskRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy));
        }
        return getAllTasks();
    }

    public Comment addComment(Long taskId, Comment comment) {
        Task task = getTaskById(taskId);
        comment.setTask(task);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    public Attachment saveAttachment(Long taskId, MultipartFile file) throws IOException {
        Task task = getTaskById(taskId);
        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setData(file.getBytes());
        return attachmentRepository.save(attachment);
    }

    public List<Attachment> getAttachmentsByTaskId(Long taskId) {
        return attachmentRepository.findByTaskId(taskId);
    }

    private void sendNotification(Notification notification, Long userId) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }
}
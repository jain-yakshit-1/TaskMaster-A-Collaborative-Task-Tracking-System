package com.airtribe.taskmaster.controller;

import com.airtribe.taskmaster.model.Notification;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Operation(summary = "Send a notification", description = "Send a notification message to subscribers.")
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notification send(Notification notification) {
        return notification;
    }
}
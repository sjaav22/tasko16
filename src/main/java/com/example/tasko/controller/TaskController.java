package com.example.tasko.controller;

import com.example.tasko.model.Task;
import com.example.tasko.model.User;
import com.example.tasko.service.TaskService;
import com.example.tasko.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Task task) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            task.setUser(user);
            Task created = taskService.createTask(task);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            if (page != null && size != null) {
                Page<Task> tasks = taskService.getUserTasksPaginated(user, PageRequest.of(page, size));
                return ResponseEntity.ok(tasks);
            } else {
                List<Task> tasks = taskService.getUserTasks(user);
                return ResponseEntity.ok(tasks);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{taskId}/status")
    @ResponseBody
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> status) {
        try {
            Task updated = taskService.updateTaskStatus(taskId, status.get("status"));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/overdue")
    @ResponseBody
    public ResponseEntity<?> getOverdueTasks(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<Task> tasks = taskService.getOverdueTasks(user);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    @ResponseBody
    public ResponseEntity<?> getUpcomingTasks(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<Task> tasks = taskService.getUpcomingTasks(user);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{taskId}")
    @ResponseBody
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task taskDetails) {
        try {
            Task updated = taskService.updateTask(taskId, taskDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}")
    @ResponseBody
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
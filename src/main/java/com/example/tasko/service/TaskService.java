package com.example.tasko.service;

import com.example.tasko.model.Task;
import com.example.tasko.model.User;
import com.example.tasko.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus("PENDING");
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getUserTasks(User user) {
        return taskRepository.findByUserOrderByDueDateAsc(user);
    }

    @Transactional(readOnly = true)
    public Page<Task> getUserTasksPaginated(User user, Pageable pageable) {
        return taskRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Task>> getTasksByStatusGrouped() {
        return taskRepository.findAll().stream()
            .collect(Collectors.groupingBy(Task::getStatus));
    }

    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks(User user) {
        return taskRepository.findByUserAndDueDateBeforeAndStatusNot(
            user,
            LocalDateTime.now(),
            "COMPLETED"
        );
    }

    @Transactional(readOnly = true)
    public List<Task> getUpcomingTasks(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekLater = now.plusDays(7);
        return taskRepository.findByUserAndDueDateBetweenAndStatusNot(
            user,
            now,
            weekLater,
            "COMPLETED"
        );
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long countTotalTasks() {
        return taskRepository.count();
    }

    @Transactional(readOnly = true)
    public long countPendingTasks() {
        return taskRepository.countByStatus("PENDING");
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        if (status.equals("COMPLETED")) {
            task.setCompletedAt(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setPriority(taskDetails.getPriority());
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Map<String, Object> getTaskTrends() {
        return Map.of(
            "labels", List.of("Jan", "Feb", "Mar", "Apr", "May"),
            "completed", List.of(10, 15, 8, 12, 20),
            "new", List.of(15, 10, 12, 8, 15)
        );
    }
}
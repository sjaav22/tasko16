package com.example.tasko.repository;

import com.example.tasko.model.Task;
import com.example.tasko.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByDueDateAsc(User user);
    Page<Task> findByUser(User user, Pageable pageable);
    List<Task> findByStatus(String status);
    List<Task> findByUserAndDueDateBeforeAndStatusNot(User user, LocalDateTime dueDate, String status);
    List<Task> findByUserAndDueDateBetweenAndStatusNot(User user, LocalDateTime start, LocalDateTime end, String status);
    long countByStatus(String status);
}
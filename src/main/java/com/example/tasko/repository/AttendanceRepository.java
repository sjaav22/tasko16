package com.example.tasko.repository;

import com.example.tasko.model.Attendance;
import com.example.tasko.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndCheckInBetween(User user, LocalDateTime start, LocalDateTime end);
    List<Attendance> findByUserAndCheckInBetweenOrderByCheckInDesc(User user, LocalDateTime start, LocalDateTime end);
    int countByUserAndCheckInBetweenAndStatus(User user, LocalDateTime start, LocalDateTime end, String status);
    int countByCheckInBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
}
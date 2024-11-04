package com.example.tasko.service;

import com.example.tasko.model.Attendance;
import com.example.tasko.model.User;
import com.example.tasko.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public Attendance logAttendance(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        LocalDateTime endOfDay = now.with(LocalTime.MAX);

        if (attendanceRepository.findByUserAndCheckInBetween(user, startOfDay, endOfDay).isPresent()) {
            throw new RuntimeException("Attendance already logged for today");
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckIn(now);
        attendance.setStatus("PRESENT");
        return attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public Attendance getTodayAttendance(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        LocalDateTime endOfDay = now.with(LocalTime.MAX);
        return attendanceRepository.findByUserAndCheckInBetween(user, startOfDay, endOfDay).orElse(null);
    }

    @Transactional
    public void checkOut(User user) {
        Attendance attendance = getTodayAttendance(user);
        if (attendance == null) {
            throw new RuntimeException("No attendance found for today");
        }
        attendance.setCheckOut(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getMonthlyAttendance(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return attendanceRepository.findByUserAndCheckInBetweenOrderByCheckInDesc(
            user,
            startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX)
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceStats(User user) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        int workingDays = startOfMonth.until(today).getDays() + 1;
        int presentDays = countPresentDays(user, startOfMonth, today);
        double overtimeHours = calculateOvertimeHours(user, startOfMonth, today);

        return Map.of(
            "workingDays", workingDays,
            "presentDays", presentDays,
            "overtimeHours", overtimeHours
        );
    }

    @Transactional(readOnly = true)
    public int countPresentDays(User user, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.countByUserAndCheckInBetweenAndStatus(
            user,
            startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX),
            "PRESENT"
        );
    }

    @Transactional(readOnly = true)
    public double calculateOvertimeHours(User user, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByUserAndCheckInBetweenOrderByCheckInDesc(
            user,
            startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX)
        );

        double totalOvertime = 0.0;
        for (Attendance attendance : attendances) {
            if (attendance.getCheckOut() != null) {
                LocalDateTime standardEndTime = attendance.getCheckIn().toLocalDate().atTime(17, 0);
                if (attendance.getCheckOut().isAfter(standardEndTime)) {
                    double overtime = java.time.Duration.between(standardEndTime, attendance.getCheckOut()).toHours();
                    totalOvertime += overtime;
                }
            }
        }
        return totalOvertime;
    }

    @Transactional(readOnly = true)
    public int countPresentToday() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        return attendanceRepository.countByCheckInBetweenAndStatus(startOfDay, endOfDay, "PRESENT");
    }

    public Map<String, Object> getAttendanceTrends() {
        // Implementation for attendance trends data
        // This would return data for the charts in the admin dashboard
        return Map.of(
            "labels", List.of("Mon", "Tue", "Wed", "Thu", "Fri"),
            "present", List.of(45, 42, 47, 40, 43),
            "absent", List.of(5, 8, 3, 10, 7)
        );
    }
}
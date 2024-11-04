package com.example.tasko.controller;

import com.example.tasko.model.Attendance;
import com.example.tasko.model.User;
import com.example.tasko.service.AttendanceService;
import com.example.tasko.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    @PostMapping("/log")
    @ResponseBody
    public ResponseEntity<?> logAttendance(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            Attendance attendance = attendanceService.logAttendance(user);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            attendanceService.checkOut(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/monthly")
    @ResponseBody
    public ResponseEntity<?> getMonthlyAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<Attendance> attendance = attendanceService.getMonthlyAttendance(user, year, month);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<?> getAttendanceStats(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
            int presentDays = attendanceService.countPresentDays(user, startDate, endDate);
            double overtimeHours = attendanceService.calculateOvertimeHours(user, startDate, endDate);
            
            Map<String, Object> stats = Map.of(
                "presentDays", presentDays,
                "workingDays", endDate.getDayOfMonth(),
                "overtimeHours", overtimeHours
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
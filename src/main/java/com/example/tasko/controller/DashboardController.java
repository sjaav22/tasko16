package com.example.tasko.controller;

import com.example.tasko.model.User;
import com.example.tasko.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final TaskService taskService;
    private final AttendanceService attendanceService;
    private final EnterpriseService enterpriseService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        return user.getRole().equals("ROLE_ADMIN") ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (!user.getRole().equals("ROLE_USER")) {
            return "redirect:/access-denied";
        }
        
        // User Profile
        model.addAttribute("user", user);
        model.addAttribute("enterprise", user.getEnterprise());

        // Tasks
        model.addAttribute("tasks", taskService.getUserTasks(user));
        model.addAttribute("pendingTasks", taskService.getTasksByStatus("PENDING"));
        model.addAttribute("overdueTasks", taskService.getOverdueTasks(user));
        model.addAttribute("upcomingTasks", taskService.getUpcomingTasks(user));

        // Attendance
        model.addAttribute("todayAttendance", attendanceService.getTodayAttendance(user));
        model.addAttribute("monthlyAttendance", attendanceService.getMonthlyAttendance(
            user, 
            LocalDate.now().getYear(), 
            LocalDate.now().getMonthValue()
        ));
        model.addAttribute("attendanceStats", attendanceService.getAttendanceStats(user));
        
        return "user/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (!user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }
        
        // Admin Profile
        model.addAttribute("user", user);
        model.addAttribute("enterprise", user.getEnterprise());
        
        // User Management
        model.addAttribute("users", userService.findByEnterprise(user.getEnterprise()));
        model.addAttribute("activeUsers", userService.findActiveUsersByEnterprise(user.getEnterprise()));
        
        // Task Management
        model.addAttribute("allTasks", taskService.getEnterpriseTasksByStatus(user.getEnterprise()));
        
        // System Statistics
        model.addAttribute("stats", Map.of(
            "totalUsers", userService.countByEnterprise(user.getEnterprise()),
            "activeUsers", userService.countActiveByEnterprise(user.getEnterprise()),
            "totalTasks", taskService.countByEnterprise(user.getEnterprise()),
            "pendingTasks", taskService.countPendingByEnterprise(user.getEnterprise()),
            "presentToday", attendanceService.countPresentTodayByEnterprise(user.getEnterprise())
        ));

        // Analytics Data
        model.addAttribute("taskTrends", taskService.getTaskTrends(user.getEnterprise()));
        model.addAttribute("attendanceTrends", attendanceService.getAttendanceTrends(user.getEnterprise()));
        
        return "admin/dashboard";
    }
}
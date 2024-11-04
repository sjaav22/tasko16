package com.example.tasko.controller;

import com.example.tasko.model.Payroll;
import com.example.tasko.model.User;
import com.example.tasko.service.PayrollService;
import com.example.tasko.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String payrollDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Payroll> payrollHistory = payrollService.getPayrollHistory(user);
        model.addAttribute("payrollHistory", payrollHistory);
        return "user/payroll";
    }

    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<?> generatePayroll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            Payroll payroll = payrollService.generatePayroll(user, year, month);
            return ResponseEntity.ok(payroll);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<?> getPayrollHistory(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<Payroll> history = payrollService.getPayrollHistory(user);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
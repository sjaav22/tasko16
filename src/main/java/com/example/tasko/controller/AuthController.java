package com.example.tasko.controller;

import com.example.tasko.model.User;
import com.example.tasko.model.Enterprise;
import com.example.tasko.service.UserService;
import com.example.tasko.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final EnterpriseService enterpriseService;
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                             @RequestParam Long enterpriseId,
                             RedirectAttributes redirectAttributes) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/register";
            }
            
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }
            
            if (!user.getRole().equals("ROLE_USER") && !user.getRole().equals("ROLE_ADMIN")) {
                redirectAttributes.addFlashAttribute("error", "Invalid role selected");
                return "redirect:/register";
            }
            
            Enterprise enterprise = enterpriseService.getEnterpriseById(enterpriseId);
            user.setEnterprise(enterprise);
            
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());
            return user.getRole().equals("ROLE_ADMIN") ? 
                "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        }
        return "redirect:/login";
    }
}
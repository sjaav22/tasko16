package com.example.tasko.controller;

import com.example.tasko.model.Enterprise;
import com.example.tasko.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/enterprises")
@RequiredArgsConstructor
public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listEnterprises(Model model) {
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "admin/enterprises";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> createEnterprise(@RequestBody Enterprise enterprise) {
        try {
            Enterprise created = enterpriseService.createEnterprise(enterprise);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> updateEnterprise(@PathVariable Long id, @RequestBody Enterprise enterprise) {
        try {
            Enterprise updated = enterpriseService.updateEnterprise(id, enterprise);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteEnterprise(@PathVariable Long id) {
        try {
            enterpriseService.deleteEnterprise(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
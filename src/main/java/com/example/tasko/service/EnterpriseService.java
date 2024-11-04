package com.example.tasko.service;

import com.example.tasko.model.Enterprise;
import com.example.tasko.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;

    @Transactional
    public Enterprise createEnterprise(Enterprise enterprise) {
        if (enterpriseRepository.existsByName(enterprise.getName())) {
            throw new RuntimeException("Enterprise name already exists");
        }
        return enterpriseRepository.save(enterprise);
    }

    @Transactional(readOnly = true)
    public Enterprise getEnterpriseById(Long id) {
        return enterpriseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Enterprise not found"));
    }

    @Transactional(readOnly = true)
    public List<Enterprise> getAllEnterprises() {
        return enterpriseRepository.findAll();
    }

    @Transactional
    public Enterprise updateEnterprise(Long id, Enterprise enterpriseDetails) {
        Enterprise enterprise = getEnterpriseById(id);
        enterprise.setName(enterpriseDetails.getName());
        enterprise.setDescription(enterpriseDetails.getDescription());
        return enterpriseRepository.save(enterprise);
    }

    @Transactional
    public void deleteEnterprise(Long id) {
        Enterprise enterprise = getEnterpriseById(id);
        enterpriseRepository.delete(enterprise);
    }

    @Transactional(readOnly = true)
    public long countEnterprises() {
        return enterpriseRepository.count();
    }
}
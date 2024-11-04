package com.example.tasko.repository;

import com.example.tasko.model.Payroll;
import com.example.tasko.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByUser(User user);
    List<Payroll> findByPayrollDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<Payroll> findByUserAndPayrollDate(User user, LocalDate payrollDate);
    
    @Query("SELECT COUNT(p) > 0 FROM Payroll p WHERE p.user = ?1 AND YEAR(p.payrollDate) = ?2 AND MONTH(p.payrollDate) = ?3")
    boolean existsByUserAndYearAndMonth(User user, int year, int month);
}
package com.example.tasko.service;

import com.example.tasko.model.Payroll;
import com.example.tasko.model.User;
import com.example.tasko.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final AttendanceService attendanceService;
    private final UserService userService;

    @Transactional
    public Payroll generatePayroll(User user, int year, int month) {
        if (payrollRepository.existsByUserAndYearAndMonth(user, year, month)) {
            throw new RuntimeException("Payroll already generated for this month");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Calculate working days and attendance
        int workingDays = calculateWorkingDays(startDate, endDate);
        int presentDays = attendanceService.countPresentDays(user, startDate, endDate);
        double overtimeHours = attendanceService.calculateOvertimeHours(user, startDate, endDate);

        // Calculate salary components
        BigDecimal basicSalary = calculateBasicSalary(user, presentDays, workingDays);
        BigDecimal overtimePay = calculateOvertimePay(overtimeHours, user);
        BigDecimal deductions = calculateDeductions(basicSalary, presentDays, workingDays);
        BigDecimal netSalary = basicSalary.add(overtimePay).subtract(deductions);

        Payroll payroll = new Payroll();
        payroll.setUser(user);
        payroll.setPayrollDate(startDate);
        payroll.setWorkingDays(workingDays);
        payroll.setPresentDays(presentDays);
        payroll.setOvertimeHours(overtimeHours);
        payroll.setBasicSalary(basicSalary);
        payroll.setOvertimePay(overtimePay);
        payroll.setDeductions(deductions);
        payroll.setNetSalary(netSalary);
        payroll.setStatus("GENERATED");

        return payrollRepository.save(payroll);
    }

    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        // Implement working days calculation logic
        return 22; // Example: standard working days in a month
    }

    private BigDecimal calculateBasicSalary(User user, int presentDays, int workingDays) {
        // Implement basic salary calculation logic
        BigDecimal dailyRate = new BigDecimal("1000"); // Example daily rate
        return dailyRate.multiply(new BigDecimal(presentDays))
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOvertimePay(double overtimeHours, User user) {
        // Implement overtime pay calculation logic
        BigDecimal hourlyRate = new BigDecimal("150"); // Example hourly rate
        return hourlyRate.multiply(new BigDecimal(overtimeHours))
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDeductions(BigDecimal basicSalary, int presentDays, int workingDays) {
        // Implement deductions calculation logic
        BigDecimal deductionRate = new BigDecimal("0.1"); // Example: 10% deduction
        return basicSalary.multiply(deductionRate)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public List<Payroll> getPayrollHistory(User user) {
        return payrollRepository.findByUser(user);
    }
}
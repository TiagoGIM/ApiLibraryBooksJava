package com.libraryapi.libraryapi.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.libraryapi.libraryapi.model.Loan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LOANS_LATE = "0 0 0 1/1 * ?";

    private final ILoanService loanService;

    private final EmailService emailService;

    @Value("${application.mail.lateloans.message}")
    private String message;

    @Scheduled(cron = CRON_LOANS_LATE)
    public void sendEmailToLateLoans(){
        
        List<Loan> lateLoans = loanService.getAllLateLoans();

        List<String> mailList = lateLoans
            .stream()
            .map(loan -> loan.getCustomerEmail())
            .collect(Collectors.toList());

        emailService.sendEmails(message,mailList);
    }
}

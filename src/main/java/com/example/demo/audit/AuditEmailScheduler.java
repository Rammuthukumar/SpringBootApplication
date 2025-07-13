package com.example.demo.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.utils.EmailService;

@Component
public class AuditEmailScheduler {
    
    @Autowired private AuditRepo auditRepo;
    @Autowired private EmailService emailService;

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void emailScheduler(){
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(2);

        List<Audit> auditLogs = auditRepo.findPendingLogs(tenMinutesAgo);

        for(Audit log : auditLogs){
            emailService.sendReminderEmail(log);
            log.setEmailSent(true);
        }

        auditRepo.saveAll(auditLogs);
    }
}

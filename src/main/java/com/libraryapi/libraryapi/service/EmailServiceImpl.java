package com.libraryapi.libraryapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.lateloans.remetent}")
    private String remetent;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmails(String message, List<String> mailList) {

        String[] mails = mailList.toArray(new String[mailList.size()]);
        
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("ta devendo");
        mailMessage.setText(message);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);

    }
    
}

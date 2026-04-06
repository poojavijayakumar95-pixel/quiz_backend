package com.quizapp.quiz.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Explicit constructor
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to the Online Quiz Application!");
        message.setText("Hello " + username + ",\n\n" +
                "Your registration was successful. You can now log in and start taking quizzes!\n\n" +
                "Best regards,\nQuiz App Team");

        mailSender.send(message);
    }

    public void sendQuizResultEmail(String toEmail, Long quizId, int score, int totalQuestions, double percentage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Quiz Results are In!");
        message.setText("Great job completing Quiz #" + quizId + "!\n\n" +
                "Here are your results:\n" +
                "Score: " + score + " out of " + totalQuestions + "\n" +
                "Percentage: " + percentage + "%\n\n" +
                "Log in to view more details or take another quiz.\n\n" +
                "Best regards,\nQuiz App Team");

        mailSender.send(message);
    }
}

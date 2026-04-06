package com.quizapp.quiz.service;

import com.quizapp.quiz.dto.AnswerSubmitDTO;
import com.quizapp.quiz.dto.QuizAttemptDTO;
import com.quizapp.quiz.dto.QuizResultResponse;
import com.quizapp.quiz.dto.QuizSubmitRequest;
import com.quizapp.quiz.model.Option;
import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.model.QuizAttempt;
import com.quizapp.quiz.model.User;
import com.quizapp.quiz.repository.OptionRepository;
import com.quizapp.quiz.repository.QuizAttemptRepository;
import com.quizapp.quiz.repository.QuizRepository;
import com.quizapp.quiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipantService {

    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final EmailService emailService; // NEW: Added EmailService

    public ParticipantService(QuizRepository quizRepository, OptionRepository optionRepository, QuizAttemptRepository quizAttemptRepository, UserRepository userRepository, EmailService emailService) {
        this.quizRepository = quizRepository;
        this.optionRepository = optionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.emailService = emailService; // NEW: Assigned EmailService
    }

    public QuizResultResponse submitQuiz(Long quizId, String userEmail, QuizSubmitRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();

        if (request.getAnswers() != null) {
            for (AnswerSubmitDTO answer : request.getAnswers()) {
                Option selectedOption = optionRepository.findById(answer.getSelectedOptionId())
                        .orElseThrow(() -> new RuntimeException("Option not found"));

                if (selectedOption.getQuestion().getId().equals(answer.getQuestionId()) && selectedOption.isCorrect()) {
                    score++;
                }
            }
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .user(user)
                .score(score)
                .attemptDate(LocalDateTime.now())
                .build();

        quizAttemptRepository.save(attempt);

        double percentage = ((double) score / totalQuestions) * 100;

        // NEW: Send the results email to the user
        emailService.sendQuizResultEmail(userEmail, quizId, score, totalQuestions, percentage);

        return QuizResultResponse.builder()
                .quizId(quizId)
                .totalQuestions(totalQuestions)
                .score(score)
                .percentage(percentage)
                .build();
    }


    public List<QuizAttemptDTO> getQuizHistory(String userEmail) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUser_EmailOrderByAttemptDateDesc(userEmail);

        return attempts.stream().map(attempt -> {
            int totalQuestions = attempt.getQuiz().getQuestions().size();
            double percentage = totalQuestions == 0 ? 0 : ((double) attempt.getScore() / totalQuestions) * 100;

            return QuizAttemptDTO.builder()
                    .attemptId(attempt.getId())
                    .quizTitle(attempt.getQuiz().getTitle())
                    .score(attempt.getScore())
                    .totalQuestions(totalQuestions)
                    .percentage(percentage)
                    .attemptDate(attempt.getAttemptDate())
                    .build();
        }).toList();
    }
}
package com.quizapp.quiz.service;

import com.quizapp.quiz.dto.AnswerSubmitDTO;
import com.quizapp.quiz.dto.QuizAttemptDTO;
import com.quizapp.quiz.dto.QuizResultResponse;
import com.quizapp.quiz.dto.QuizSubmitRequest;
import com.quizapp.quiz.model.Option;
import com.quizapp.quiz.model.Question;
import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.model.QuizAttempt;
import com.quizapp.quiz.model.User;
import com.quizapp.quiz.repository.QuizAttemptRepository;
import com.quizapp.quiz.repository.QuizRepository;
import com.quizapp.quiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParticipantService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ParticipantService(
            QuizRepository quizRepository,
            QuizAttemptRepository quizAttemptRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public QuizResultResponse submitQuiz(Long quizId, String userEmail, QuizSubmitRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int totalQuestions = quiz.getQuestions().size();

        // Build correctAnswerMap from the already-loaded quiz (avoids LazyInitializationException)
        Map<Long, Long> correctAnswerMap = new HashMap<>();
        for (Question question : quiz.getQuestions()) {
            for (Option option : question.getOptions()) {
                if (option.isCorrect()) {
                    correctAnswerMap.put(question.getId(), option.getId());
                    break;
                }
            }
        }

        // Grade submitted answers
        int score = 0;
        if (request.getAnswers() != null) {
            for (AnswerSubmitDTO answer : request.getAnswers()) {
                Long correctOptionId = correctAnswerMap.get(answer.getQuestionId());
                if (correctOptionId != null && correctOptionId.equals(answer.getSelectedOptionId())) {
                    score++;
                }
            }
        }

        // Save attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .user(user)
                .score(score)
                .attemptDate(LocalDateTime.now())
                .build();

        quizAttemptRepository.save(attempt);

        double percentage = totalQuestions == 0 ? 0 : ((double) score / totalQuestions) * 100;

        // FIX: Wrap email in try-catch so a mail server failure NEVER crashes
        // the quiz submission. The score is already saved — email is optional.
        try {
            emailService.sendQuizResultEmail(userEmail, quizId, score, totalQuestions, percentage);
        } catch (Exception e) {
            // Log the error but do NOT rethrow — quiz result must still return successfully
            System.err.println("Warning: Failed to send result email to " + userEmail + ": " + e.getMessage());
        }

        return QuizResultResponse.builder()
                .quizId(quizId)
                .totalQuestions(totalQuestions)
                .score(score)
                .percentage(percentage)
                .build();
    }

    public List<QuizAttemptDTO> getQuizHistory(String userEmail) {
        List<QuizAttempt> attempts = quizAttemptRepository
                .findByUser_EmailOrderByAttemptDateDesc(userEmail);

        return attempts.stream().map(attempt -> {
            int totalQuestions = attempt.getQuiz().getQuestions().size();
            double percentage = totalQuestions == 0
                    ? 0
                    : ((double) attempt.getScore() / totalQuestions) * 100;

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
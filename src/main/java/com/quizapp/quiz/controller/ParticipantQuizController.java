package com.quizapp.quiz.controller;

import com.quizapp.quiz.dto.QuizAttemptDTO;
import com.quizapp.quiz.dto.QuizResultResponse;
import com.quizapp.quiz.dto.QuizSubmitRequest;
import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.service.ParticipantService;
import com.quizapp.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant/quizzes")
public class ParticipantQuizController {

    private final QuizService quizService;
    private final ParticipantService participantService;

    // Explicit constructor to avoid IDE Lombok issues
    public ParticipantQuizController(QuizService quizService, ParticipantService participantService) {
        this.quizService = quizService;
        this.participantService = participantService;
    }

    // GET: View all available quizzes
    @GetMapping
    public ResponseEntity<List<Quiz>> getAvailableQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    // GET: View a specific quiz to take
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizToTake(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }
    @GetMapping("/history")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizHistory(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(participantService.getQuizHistory(userEmail));
    }
    // POST: Submit answers for auto-grading
    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long id,
            @RequestBody QuizSubmitRequest request,
            Authentication authentication // Spring automatically injects the logged-in user here via JWT
    ) {
        // Extract the email of the person taking the quiz from their secure token
        String userEmail = authentication.getName();

        // Pass it to the service to grade the quiz and save the attempt
        QuizResultResponse response = participantService.submitQuiz(id, userEmail, request);
        return ResponseEntity.ok(response);
    }
}

package com.quizapp.quiz.controller;



import com.quizapp.quiz.dto.QuizRequest;
import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    private final QuizService quizService;

    // Explicit constructor
    public AdminQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // POST: Create a new quiz
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizRequest request) {
        Quiz createdQuiz = quizService.createQuiz(request);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    // GET: Get all quizzes
    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    // GET: Get a specific quiz by ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    // DELETE: Delete a quiz by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok("Quiz deleted successfully");
    }
    // ... existing POST, GET, DELETE endpoints ...

    // NEW: PUT: Update an existing quiz by ID
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody QuizRequest request) {
        Quiz updatedQuiz = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(updatedQuiz);
    }
}
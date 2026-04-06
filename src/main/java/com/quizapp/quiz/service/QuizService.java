package com.quizapp.quiz.service;


import com.quizapp.quiz.dto.OptionRequest;
import com.quizapp.quiz.dto.QuestionRequest;
import com.quizapp.quiz.dto.QuizRequest;
import com.quizapp.quiz.model.Option;
import com.quizapp.quiz.model.Question;
import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    // Explicit constructor to avoid IDE Lombok issues
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz createQuiz(QuizRequest request) {
        // 1. Create the base Quiz entity
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimitInMinutes(request.getTimeLimitInMinutes())
                .questions(new ArrayList<>())
                .build();

        // 2. Loop through questions in the request
        if (request.getQuestions() != null) {
            for (QuestionRequest qRequest : request.getQuestions()) {
                Question question = Question.builder()
                        .text(qRequest.getText())
                        .quiz(quiz) // Link question to quiz
                        .options(new ArrayList<>())
                        .build();

                // 3. Loop through options for each question
                if (qRequest.getOptions() != null) {
                    for (OptionRequest oRequest : qRequest.getOptions()) {
                        Option option = Option.builder()
                                .text(oRequest.getText())
                                .isCorrect(oRequest.isCorrect())
                                .question(question) // Link option to question
                                .build();
                        question.getOptions().add(option);
                    }
                }
                quiz.getQuestions().add(question);
            }
        }

        // 4. Save to database. CascadeType.ALL will save questions and options automatically!
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

// ... existing create, get, delete methods ...

    // NEW METHOD: Update an existing quiz
    public Quiz updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        // 1. Update basic details
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimitInMinutes(request.getTimeLimitInMinutes());

        // 2. Clear existing questions (Hibernate will automatically delete the old ones due to orphanRemoval=true)
        quiz.getQuestions().clear();

        // 3. Rebuild the questions and options from the new request
        if (request.getQuestions() != null) {
            for (QuestionRequest qRequest : request.getQuestions()) {
                Question question = Question.builder()
                        .text(qRequest.getText())
                        .quiz(quiz)
                        .options(new ArrayList<>())
                        .build();

                if (qRequest.getOptions() != null) {
                    for (OptionRequest oRequest : qRequest.getOptions()) {
                        Option option = Option.builder()
                                .text(oRequest.getText())
                                .isCorrect(oRequest.isCorrect())
                                .question(question)
                                .build();
                        question.getOptions().add(option);
                    }
                }
                quiz.getQuestions().add(question);
            }
        }

        return quizRepository.save(quiz);
    }
}
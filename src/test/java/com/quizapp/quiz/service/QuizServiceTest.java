package com.quizapp.quiz.service;

import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository; // Create a fake repository

    @InjectMocks
    private QuizService quizService; // Inject the fake repository into our real service

    @Test
    public void testGetQuizById_Success() {
        // Arrange
        Long quizId = 1L;
        Quiz mockQuiz = Quiz.builder().id(quizId).title("Mock Quiz").timeLimitInMinutes(15).build();

        // Tell Mockito: "When the service asks the repo for ID 1, return this mockQuiz"
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(mockQuiz));

        // Act
        Quiz result = quizService.getQuizById(quizId);

        // Assert
        assertNotNull(result);
        assertEquals("Mock Quiz", result.getTitle());
        verify(quizRepository, times(1)).findById(quizId); // Verify the repo was called exactly once
    }

    @Test
    public void testGetQuizById_NotFound() {
        // Arrange
        Long quizId = 99L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            quizService.getQuizById(quizId);
        });

        assertEquals("Quiz not found with id: 99", exception.getMessage());
    }
}
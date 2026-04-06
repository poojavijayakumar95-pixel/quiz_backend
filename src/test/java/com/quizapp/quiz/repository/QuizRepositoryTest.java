package com.quizapp.quiz.repository;

import com.quizapp.quiz.model.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    public void testSaveAndFindQuiz() {
        // Arrange
        Quiz quiz = Quiz.builder()
                .title("Sample Test Quiz")
                .description("Testing repository")
                .timeLimitInMinutes(10)
                .build();

        // Act
        Quiz savedQuiz = quizRepository.save(quiz);
        Quiz fetchedQuiz = quizRepository.findById(savedQuiz.getId()).orElse(null);

        // Assert
        assertNotNull(fetchedQuiz);
        assertEquals("Sample Test Quiz", fetchedQuiz.getTitle());
        assertEquals(10, fetchedQuiz.getTimeLimitInMinutes());
    }
}
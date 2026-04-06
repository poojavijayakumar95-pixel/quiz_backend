package com.quizapp.quiz.controller;

import com.quizapp.quiz.model.Quiz;
import com.quizapp.quiz.security.JwtService; // NEW IMPORT
import com.quizapp.quiz.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminQuizController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypasses JWT security for unit testing the controller
public class AdminQuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    // NEW: We must mock JwtService so the ApplicationContext loads successfully!
    @MockBean
    private JwtService jwtService;

    @Test
    public void testGetAllQuizzes() throws Exception {
        // Arrange
        Quiz quiz1 = Quiz.builder().id(1L).title("Java Basics").timeLimitInMinutes(10).build();
        Quiz quiz2 = Quiz.builder().id(2L).title("Spring Boot").timeLimitInMinutes(20).build();

        when(quizService.getAllQuizzes()).thenReturn(Arrays.asList(quiz1, quiz2));

        // Act & Assert
        mockMvc.perform(get("/api/admin/quizzes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.size()").value(2)) // Expect an array of 2 items
                .andExpect(jsonPath("$[0].title").value("Java Basics")); // Check first item's title
    }
}
package com.quizapp.quiz.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizRequest {
    private String title;
    private String description;
    private Integer timeLimitInMinutes;
    private List<QuestionRequest> questions;
}

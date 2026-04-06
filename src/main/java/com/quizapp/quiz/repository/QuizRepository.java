package com.quizapp.quiz.repository;

import com.quizapp.quiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Standard CRUD operations are automatically provided
}
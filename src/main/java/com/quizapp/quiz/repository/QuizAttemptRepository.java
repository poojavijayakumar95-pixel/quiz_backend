package com.quizapp.quiz.repository;


import com.quizapp.quiz.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);
    List<QuizAttempt> findByQuizId(Long quizId); // Useful for Admin stats
    List<QuizAttempt> findByUser_EmailOrderByAttemptDateDesc(String email);
}
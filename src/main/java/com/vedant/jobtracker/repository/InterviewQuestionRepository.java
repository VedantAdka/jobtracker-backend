package com.vedant.jobtracker.repository;

import com.vedant.jobtracker.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByJobApplicationId(Long jobApplicationId);
}

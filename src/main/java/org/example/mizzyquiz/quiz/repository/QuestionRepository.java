package org.example.mizzyquiz.quiz.repository;

import org.example.mizzyquiz.quiz.enitity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    // to search for a question based on wildcard matching
    @Query("SELECT q FROM Question q WHERE " +
            "LOWER(q.text) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Question> searchQuestions(@Param("search") String search, Pageable pageable);


}

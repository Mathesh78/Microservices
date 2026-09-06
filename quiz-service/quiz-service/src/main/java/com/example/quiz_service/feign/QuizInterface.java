package com.example.quiz_service.feign;

import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "question-service",
        url = "http://localhost:8080"
)
public interface QuizInterface {

    @GetMapping("/question/generate")
    ResponseEntity<List<Integer>> getQuestionsForQuiz(
            @RequestParam("categoryName") String categoryName,
            @RequestParam("numQuestions") Integer numQuestions
    );

    @PostMapping("/question/getQuestions")
    ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(
            @RequestBody List<Integer> questionIds
    );

    @PostMapping("/question/getScore")
    ResponseEntity<Integer> getScore(
            @RequestBody List<Response> responses
    );
}
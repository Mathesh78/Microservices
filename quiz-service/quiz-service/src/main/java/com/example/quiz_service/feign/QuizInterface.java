package com.example.quiz_service.feign;

import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("QUESTION-SERVICE")
public interface QuizInterface {

    @GetMapping("question/generate")
    List<Integer> getQuestionsForQuiz(
            @RequestParam String categoryName,
            @RequestParam Integer numQuestions
    );

    @PostMapping("question/getQuestions")
    List<QuestionWrapper> getQuestionsFromId(
            @RequestBody List<Integer> questionIds
    );

    @PostMapping("question/getScore")
    Integer getScore(
            @RequestBody List<Response> responses
    );
}
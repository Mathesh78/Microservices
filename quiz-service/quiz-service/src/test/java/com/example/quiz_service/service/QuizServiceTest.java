package com.example.quiz_service.service;

import com.example.quiz_service.dao.QuizDao;
import com.example.quiz_service.feign.QuizInterface;
import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Quiz;
import com.example.quiz_service.model.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizDao quizDao;

    @Mock
    private QuizInterface quizInterface;

    @Mock
    private QuizEventPublisher quizEventPublisher;

    @InjectMocks
    private QuizService quizService;


    @Test
    void shouldCreateQuiz() {

        List<Integer> questionIds = List.of(1, 2, 3, 4, 5);

        when(
                quizInterface.getQuestionsForQuiz("Java", 5)
        ).thenReturn(questionIds);

        ResponseEntity<String> response =
                quizService.createQuiz(
                        "Java",
                        5,
                        "Java Quiz"
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Success", response.getBody());

        verify(quizInterface)
                .getQuestionsForQuiz("Java", 5);

        verify(quizDao)
                .save(any(Quiz.class));
    }


    @Test
    void shouldGetQuizQuestions() {

        Quiz quiz = new Quiz();

        quiz.setTitle("Java Quiz");
        quiz.setQuestionIds(List.of(1, 2, 3));

        List<QuestionWrapper> questions = List.of();

        when(quizDao.findById(1))
                .thenReturn(Optional.of(quiz));

        when(
                quizInterface.getQuestionsFromId(
                        List.of(1, 2, 3)
                )
        ).thenReturn(questions);

        ResponseEntity<List<QuestionWrapper>> response =
                quizService.getQuizQuestions(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questions, response.getBody());

        verify(quizDao).findById(1);

        verify(quizInterface)
                .getQuestionsFromId(List.of(1, 2, 3));
    }


    @Test
    void shouldCalculateResultAndPublishEvent() {

        List<Response> responses = List.of();

        when(
                quizInterface.getScore(responses)
        ).thenReturn(4);

        ResponseEntity<Integer> result =
                quizService.calculateResult(
                        3,
                        responses
                );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(4, result.getBody());

        verify(quizInterface)
                .getScore(responses);

        verify(quizEventPublisher)
                .publishQuizSubmitted(3, 4);
    }


    @Test
    void shouldThrowExceptionWhenQuizNotFound() {

        when(quizDao.findById(99))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizService.getQuizQuestions(99)
                );

        assertEquals(
                "Quiz not found with id: 99",
                exception.getMessage()
        );
    }
}
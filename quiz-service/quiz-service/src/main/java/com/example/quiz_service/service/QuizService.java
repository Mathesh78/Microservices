package com.example.quiz_service.service;

import com.example.quiz_service.dao.QuizDao;
import com.example.quiz_service.feign.QuizInterface;
import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Quiz;
import com.example.quiz_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    @Autowired
    QuizEventPublisher quizEventPublisher;

    public ResponseEntity<String> createQuiz(
            String category,
            int numQ,
            String title) {

        List<Integer> questions =
                quizInterface.getQuestionsForQuiz(category, numQ);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);

        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {

        Quiz quiz = quizDao.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quiz not found with id: " + id));

        List<Integer> questionIds =
                quiz.getQuestionIds();

        List<QuestionWrapper> questions =
                quizInterface.getQuestionsFromId(questionIds);

        return ResponseEntity.ok(questions);
    }

    public ResponseEntity<Integer> calculateResult(
            Integer id,
            List<Response> responses) {

        Integer score =
                quizInterface.getScore(responses);

        if (score != null) {
            quizEventPublisher.publishQuizSubmitted(id, score);
        }

        return ResponseEntity.ok(score);
    }
}

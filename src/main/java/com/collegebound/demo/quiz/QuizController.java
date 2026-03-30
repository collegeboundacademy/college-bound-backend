package com.collegebound.demo.quiz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collegebound.demo.user.User;
import com.collegebound.demo.user.UserRepository;



// Example request body for creating a quiz:
// {
//     title: String,
//     description: String,
//     questions: [
//       {
//         question: String,
//         answerChoices: [
//           String,
//           String,
//           String
//         ],
//         correctAnswer: String
//       },
//       {
//         question: String,
//         answerChoices: [
//           String,
//           String,
//           String
//         ],
//         correctAnswer: String
//       }
//     ]
//   };

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private QuizRepository quizRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest body, Authentication auth) {
        System.out.println("Creating quiz for user with githubId: " + auth.getName());
        System.out.println("Received quiz data: " + body.getTitle());
        
        User user = userRepo.findByGithubId(Integer.valueOf(auth.getName())).orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(body.getTitle());
        quiz.setDescription(body.getDescription());
        quiz.setCreator(user);

        List<Question> questions = body.getQuestions();

        for (Question q : questions) {
            q.setQuiz(quiz);
        }

        quiz.setQuestions(questions);

        quizRepo.save(quiz);
        
        return ResponseEntity.ok(quiz);
    }
    
    @GetMapping("/get/{quizId}")
    public ResponseEntity<String> getQuizById(@PathVariable("quizId") Long id) {
        Quiz quiz = quizRepo.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        String quizJson = quiz.convertToJson();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(quizJson);
    }
}

package QuizGame;

/**
 * Host: The quiz game host. Can add questions to a quiz.
 */
public class Host extends User {
    public Host(String name) {
        super(name);
    }

    public void addQuestion(Quiz quiz, Question q) {
        quiz.addQuestion(q);
    }
}

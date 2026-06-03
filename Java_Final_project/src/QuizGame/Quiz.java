package QuizGame;

import java.util.ArrayList;

/**
 * Quiz: An ordered list of Questions with an internal pointer.
 */
public class Quiz {
    private final ArrayList<Question> questions = new ArrayList<>();
    private int index = 0;

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public Question nextQuestion() {
        if (index < questions.size()) {
            return questions.get(index++);
        }
        return null;
    }

    public int size() {
        return questions.size();
    }

    public void reset() {
        index = 0;
    }
}

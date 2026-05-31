package QuizGame;

import java.util.ArrayList;

public class Quiz {
	// fields
	private ArrayList<Question> questions;
	private int index = 0;
	
	// constructor
    public Quiz() {
        questions = new ArrayList<>();
    }
    
	// methods
    public void addQuestion(Question q) {
    	// used by addQuestion in Host class
        questions.add(q);
    }
	
    public Question nextQuestion() {
        if (index < questions.size()) {
            return questions.get(index++);
        }
        return null; // no more questions
	}
}

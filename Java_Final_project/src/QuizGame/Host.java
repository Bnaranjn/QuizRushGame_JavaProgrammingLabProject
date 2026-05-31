package QuizGame;

public class Host extends User{
	
	// constructor
	public Host(String name) {
		super(name);
	}
	
	// methods
	public void addQuestion(Quiz quiz, Question q) {
		// adds already built quiz into given quiz
        quiz.addQuestion(q);
	}
	
}

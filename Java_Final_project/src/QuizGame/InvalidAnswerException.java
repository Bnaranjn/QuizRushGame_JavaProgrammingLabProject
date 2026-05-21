package QuizGame;

public class InvalidAnswerException extends RuntimeException{
	// should it really be a runtime exception?
	
	public InvalidAnswerException(String message) {
		super(message);
	}
}

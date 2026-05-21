package QuizGame;

public class InvalidBetException extends RuntimeException{
	// should it really be a runtime exception?
	
	public InvalidBetException(String message) {
		super(message);
	}
}

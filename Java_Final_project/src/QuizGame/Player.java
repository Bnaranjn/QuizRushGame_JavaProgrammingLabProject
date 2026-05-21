package QuizGame;
// import QuizGame.User;

public class Player extends User{
	
	// fields
	private int selectedAnswer; // could set it to -1 as default
	private int currentBet; // could set it to 0 as default
	
	// constructor 
	public Player(String n) {
		super(n);
		
		// default values
		this.selectedAnswer = -1;
		this.currentBet = 0;
	}
	
	// methods
	public void submitAnswer(int ans) throws InvalidAnswerException{
		// maybe change name to setAnswer
		// to do: idk build the answer class ig
		
		if (ans < 0 || ans > 3) {
			throw new
			InvalidAnswerException("Invalid answer index (Must be 0 to 3, inclusive)");
		} else {
			this.selectedAnswer = ans;
		}
		
	}
	
	public int getSelectedAnswer() {
		// what if this is used before answer is set
		// the default ans index is -1, technically could be a valid indexing in the arr
		// be aware!!
		
		return this.selectedAnswer;
	}
	
	public void resetAnswer() {
		// the answers are 0 indexed, so using -1 as default
		this.selectedAnswer = -1; 
	}
	
	public void setBet(int bet) throws InvalidBetException{
		// to do: idk build the bet class ig 
		// should implement bet  separately??
		
		if (bet > this.score) {
			throw new InvalidBetException("Betting amount exceeds current score.");
		} else if(bet < 0) {
			throw new InvalidBetException("Betting amound cannot be less than 0.");
		} else {
			this.currentBet = bet;
		}
		
	}
	
	public int getBet() {
		return this.currentBet;
	}
	
	
	// exceptions
	// separate for now	
}

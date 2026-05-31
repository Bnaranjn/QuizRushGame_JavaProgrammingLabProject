package QuizGame;

abstract class User {
	// fields
	private String name;
	private int score = 0; // initial score set to 0
	
	// constructor 
	public User(String n ){
		this.name = n;
	}
	
	// methods
	public void updateScore(int s) {
		this.score = s;
	}
	
	public int getScore() {
		// return the score, instead of displaying it
		return this.score;
	}
}

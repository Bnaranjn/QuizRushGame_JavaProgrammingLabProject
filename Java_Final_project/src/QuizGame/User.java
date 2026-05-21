package QuizGame;

abstract class User {
	// fields
	String name;
	int score = 0; // initial score set to 0
	
	// constructor 
	User(String n ){
		this.name = n;
	}
	
	// methods
	void updateScore(int s) {
		this.score = s;
	}
	
	void getScore() {
		// currently prints with new line and the score only
		System.out.println(this.score);
	}
}

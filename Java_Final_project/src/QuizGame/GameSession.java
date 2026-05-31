package QuizGame;
// 06/01 new field currQuestion - not in og UML
	// should each Question have different score
	// should eacH Question have different timer
	// IT IS INCOMPLETE

import java.util.ArrayList;

public class GameSession {
	// fields
	private Quiz quiz;
	private ArrayList<Player> players;
	private int round = 0; // maybe remove it?
	private Question currQuestion; // not included in og UML
	// Constructor
	public GameSession(Quiz quiz) {
		// to do - ??
		this.quiz = quiz;
	    this.players = new ArrayList<>();
	}
	
	// methods
	public void addPlayer(Player p) {
		// to do -- ??
		this.players.add(p);
	}
	
	public void processRounds() {
		//
		currQuestion = quiz.nextQuestion();
			// the the next question is fetched
			// the ui linking should come here??

	    if (currQuestion == null) {
	        System.out.println("Quiz finished!");
	        return;
	    }

	    round++;

	    // The UI linking/hooks ??
	    	// todo -- ??
	    startTimer(currQuestion);
	    // how to handle if all players answered before the timer runs out
	    
	    
	    for (Player p : players) {
	    	checkAnswer(p, currQuestion);
	    }
	    
	    if (round % 3 == 0) { 
	    	startBettingPhase();
	    }
	}
	
	private void checkAnswer(Player p, Question q) {
		// checks the answer of a player and updates their score
		if (q.isCorrect(p.getSelectedAnswer())) {
			// the 1 is placeholder
			// maybe have score field in Question -- so each Q can have different scores
			p.updateScore(1); 
		}
	}
	
	private int calculateScore() {
		return 0;
	}
	
	private void startBettingPhase() {
		
	}
	
	private void startTimer(Question q) {
		
	}
	
}

package QuizGame;
// 06/01 new field currQuestion - not in og UML
	// should each Question have different score
	// should eacH Question have different timer
	// IT IS INCOMPLETE
//6.3 BY NARANJIN

//MANAGES THE FULL GAME
import java.util.ArrayList;

public class GameSession {
	//NEXT EVENT ENUM - used for the switch
	public enum NextEvent {
        NEXT_QUESTION,
        BET_ROUND,
        GAME_OVER
    }
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
	//added- get players
	public ArrayList<Player> getPlayers() {
        return players;
    }
	//game flow 
	//given the index of hte question that was just answered - decideds what should happen next
	
	public static NextEvent getNextEvent(int currentQuestionIndex, int totalQuestions) {
        int next = currentQuestionIndex + 1;
 
        if (next >= totalQuestions) {
            return NextEvent.GAME_OVER;
        }
        if (next % 3 == 0) {
            return NextEvent.BET_ROUND;
        }
        return NextEvent.NEXT_QUESTION;
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
	    
	    //startTimer(currQuestion); - timer n ui da ywna gesen
	    // how to handle if all players answered before the timer runs out
	    
	    
//	    for (Player p : players) {
//	    	checkAnswer(p, currQuestion);
//	    }
	    for (Player p : players) {
            boolean correct = currQuestion.isCorrect(p.getSelectedAnswer());
            //p.resolveAnswer(correct, timeLeft);//!!
            p.resolveAnswer(correct, p.getAnswerTimeLeft());//getting each players own left time
            p.resetAnswer();
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

    public Question getCurrentQuestion() {
        return currQuestion;
    }
 
    public int getRound() {
        return round;
    }
	
    
    
	private int calculateScore() {
		return 0;
	}
	// i think these two functions not necessary
	private void startBettingPhase() {
		
	}
	
	private void startTimer(Question q) {
		
	}
	
}

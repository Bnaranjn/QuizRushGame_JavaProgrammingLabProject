package QuizGame;

/**
 * Player: A quiz participant with answer selection and betting ability.
 */
public class Player extends User {
    private int selectedAnswer = -1;  // -1 = no answer
    private int pendingBetAmount = 0;
    private int pendingBetMultiplier = 1;
    
    
 // Scoring constants 
    //ADDED
    private int answerTimeLeft = 0;
    public static final int BASE_CORRECT_SCORE = 500;
    public static final int POINTS_PER_SECOND  = 33;

    public Player(String name) {
        super(name);
    }

    public void submitAnswer(int ans, int timeLeft) throws InvalidAnswerException {
//        if (ans < 0 || ans > 3) {
//            throw new InvalidAnswerException("Invalid answer index (must be 0â€“3).");
//        }
        if (ans < -1 || ans > 3) {   // -1 is allowed (timeout / no answer)
            throw new InvalidAnswerException("Invalid answer index (must be 0–3, or -1 for no answer).");
        }
        
        this.selectedAnswer = ans;
        this.answerTimeLeft= timeLeft;
    }
    public int getAnswerTimeLeft() { return answerTimeLeft;}

    public int getSelectedAnswer() {
        return this.selectedAnswer;
    }

    public void resetAnswer() {
        this.selectedAnswer = -1;
        //added
        this.answerTimeLeft=0;
    }

    /**
     * Sets a bet. Validates that the amount doesn't exceed current score.
     * @param amount   actual point amount to wager
     * @param multiplier  x1, x2, or x3
     */
    public void setBet(int amount, int multiplier) throws InvalidBetException {
        if (amount < 0) {
            throw new InvalidBetException("Bet amount cannot be negative.");
        }
        if (amount > getScore()) {
            throw new InvalidBetException("Bet exceeds current score.");
        }
        if (multiplier < 1 || multiplier > 3) {
            throw new InvalidBetException("Multiplier must be 1, 2, or 3.");
        }
        this.pendingBetAmount     = amount;
        this.pendingBetMultiplier = multiplier;
    }

    public int getPendingBetAmount()      { return pendingBetAmount; }
    public int getPendingBetMultiplier()  { return pendingBetMultiplier; }
 
    //ADDED
    public int resolveAnswer(boolean correct, int timeLeft) {
        int points = 0;
 
        if (correct) {
            points = BASE_CORRECT_SCORE + (timeLeft * POINTS_PER_SECOND);
        }
 
        //Apply bet on top of the base points
        if (pendingBetAmount > 0) {
            if (correct) {
                points += pendingBetAmount * (pendingBetMultiplier - 1);
            } else {
                points -= pendingBetAmount;
            }
            pendingBetAmount     = 0;
            pendingBetMultiplier = 1;
        }
 
        updateScore(points);   //floor at 0 is handled inside User.updateScore()
        return points;
    }
    /**
     * Resolves a bet after a question is answered.
     * If correct: score += amount * (multiplier - 1)
     * If wrong:   score -= amount
     */
    public void resolveBet(boolean correct) {
        if (pendingBetAmount == 0) return;
        if (correct) {
            updateScore(pendingBetAmount * (pendingBetMultiplier - 1));
        } else {
            updateScore(-pendingBetAmount);
        }
        pendingBetAmount     = 0;
        pendingBetMultiplier = 1;
    }

    public void clearBet() {
        pendingBetAmount     = 0;
        pendingBetMultiplier = 1;
    }
}

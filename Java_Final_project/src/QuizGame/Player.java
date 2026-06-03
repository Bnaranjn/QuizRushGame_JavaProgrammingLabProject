package QuizGame;

/**
 * Player: A quiz participant with answer selection and betting ability.
 */
public class Player extends User {
    private int selectedAnswer = -1;  // -1 = no answer
    private int pendingBetAmount = 0;
    private int pendingBetMultiplier = 1;

    public Player(String name) {
        super(name);
    }

    public void submitAnswer(int ans) throws InvalidAnswerException {
        if (ans < 0 || ans > 3) {
            throw new InvalidAnswerException("Invalid answer index (must be 0–3).");
        }
        this.selectedAnswer = ans;
    }

    public int getSelectedAnswer() {
        return this.selectedAnswer;
    }

    public void resetAnswer() {
        this.selectedAnswer = -1;
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

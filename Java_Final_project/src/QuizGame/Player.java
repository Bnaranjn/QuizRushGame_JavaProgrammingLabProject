package QuizGame;

public class Player extends User {
    private int selectedAnswer = -1;
    private int answerTimeLeft = 0;
    private int currentWager = 0;
    private int betMultiplier = 1;

    public Player(String name) {
        super(name);
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int answerIndex) {
        this.selectedAnswer = answerIndex;
    }

    public int getAnswerTimeLeft() {
        return answerTimeLeft;
    }

    public void setAnswerTimeLeft(int answerTimeLeft) {
        this.answerTimeLeft = answerTimeLeft;
    }

    public void placeBet(int amount, int multiplier) {
        if (amount < 0 || amount > this.score) {
            throw new InvalidBetException("Wager falls outside legal bounds: " + amount);
        }
        this.currentWager = amount;
        this.betMultiplier = multiplier;
    }

    public void resolveAnswer(boolean correct, int secondsLeft) {
        if (correct) {
            int baseGain = 100;
            int timeBonus = secondsLeft * 5;
            int directReward = baseGain + timeBonus;

            if (currentWager > 0) {
                // Incorporate risk gains safely: wager * multiplier base
                directReward += (currentWager * (betMultiplier - 1));
            }
            updateScore(directReward);
        } else {
            if (currentWager > 0) {
                updateScore(-currentWager);
            }
        }
        // Always reset wager metrics following evaluation boundary
        this.currentWager = 0;
        this.betMultiplier = 1;
    }

    public void resetAnswer() {
        this.selectedAnswer = -1;
        this.answerTimeLeft = 0;
    }
}
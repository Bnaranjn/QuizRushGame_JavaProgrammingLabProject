package QuizGame;

public class Player extends User {
    private int selectedAnswer = -1;
    private int answerTimeLeft = 0;
    private int currentWager = 0;
    private int betMultiplier = 1;
    private boolean isHost = false;

    public Player(String name) {
        super(name);
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
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
            this.currentWager = 0;
            this.betMultiplier = 1;
            return;
        }
        this.currentWager = amount;
        this.betMultiplier = multiplier;
    }

    public void resolveAnswer(boolean correct, int secondsLeft) {
        if (isHost) return; // Defensive check

        if (correct) {
            int baseGain = 100;
            int timeBonus = secondsLeft * 5;
            int directReward = baseGain + timeBonus;

            if (currentWager > 0) {
                directReward += (currentWager * (betMultiplier - 1));
            }
            updateScore(directReward);
        } else {
            if (currentWager > 0) {
                updateScore(-currentWager);
            }
        }
        this.currentWager = 0;
        this.betMultiplier = 1;
    }

    public void resetAnswer() {
        this.selectedAnswer = -1;
        this.answerTimeLeft = 0;
    }
}
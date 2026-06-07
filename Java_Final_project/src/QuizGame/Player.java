package QuizGame;

public class Player extends User {

    // chosen answer for current question
    private int selectedAnswer = -1;

    // time remaining when answer was submitted
    private int answerTimeLeft = 0;

    // current bet amount
    private int currentWager = 0;

    // selected multiplier for bet
    private int betMultiplier = 1;

    // true if this player is the host
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

    // saves player's bet info
    public void placeBet(int amount, int multiplier) {

        // reset bet if amount is invalid
        if (amount < 0 || amount > this.score) {
            this.currentWager = 0;
            this.betMultiplier = 1;
            return;
        }

        this.currentWager = amount;
        this.betMultiplier = multiplier;
    }

    // updates score after answer is revealed
    public void resolveAnswer(boolean correct, int secondsLeft) {

        // host should never receive points
        if (isHost) return;

        if (correct) {

            // base score for correct answer
            int baseGain = 100;

            // extra pts for answering quicker
            int timeBonus = secondsLeft * 5;

            int directReward = baseGain + timeBonus;

            // add bet winnings if bet was placed
            if (currentWager > 0) {
                directReward += (currentWager * (betMultiplier - 1));
            }

            updateScore(directReward);

        } else {

            // lose wager if answer was wrong
            if (currentWager > 0) {
                updateScore(-currentWager);
            }
        }

        // clear bet data for next round
        this.currentWager = 0;
        this.betMultiplier = 1;
    }

    // reset answer values before next question
    public void resetAnswer() {
        this.selectedAnswer = -1;
        this.answerTimeLeft = 0;
    }
}
package QuizGame;

/**
 * User: Abstract base for Host and Player.
 * Fixed: updateScore now ADDS to score instead of replacing it.
 */
public abstract class User {
    private final String name;
    private int score = 0;

    public User(String name) {
        this.name = name;
    }

    /** Adds 'points' to the current score. */
    public void updateScore(int points) {
        this.score += points;   // Bug fix: was "= points", now "+= points"
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public String getName() {
        return this.name;
    }
}

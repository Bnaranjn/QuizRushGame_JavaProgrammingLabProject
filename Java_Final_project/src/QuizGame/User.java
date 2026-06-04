package QuizGame;

public abstract class User {
    private final String name;
    protected int score = 0;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateScore(int points) {
        this.score += points;
    }
}
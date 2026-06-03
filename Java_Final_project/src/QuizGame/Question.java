package QuizGame;

/**
 * Question: Holds a quiz question, its four options, and the correct answer index.
 */
public class Question {
    private final String text;
    private final String[] options;
    private final int correctAnswer;

    public Question(String text, String[] options, int correctAnswer) {
        this.text          = text;
        this.options       = options;
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect(int answer) {
        return this.correctAnswer == answer;
    }

    public String getText() {
        return this.text;
    }

    public String[] getOptionsArr() {
        return this.options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getOptionsFormatted() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            sb.append((i + 1)).append(". ").append(options[i]).append("\n");
        }
        return sb.toString();
    }
}

package QuizGame;

//stores a quiz question, answer choices and correct answer
public class Question {
    private final String questionText;
    private final String[] options;
    private final int correctOptionIndex;

    public Question(String questionText, String[] options, int correctOptionIndex) {
        if (options == null || options.length != 4) {
            throw new InvalidAnswerException("A question must provide exactly four options.");
        }
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOption(int index) {
        return options[index];
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public boolean isCorrect(int index) {
        return index == correctOptionIndex;
    }
}
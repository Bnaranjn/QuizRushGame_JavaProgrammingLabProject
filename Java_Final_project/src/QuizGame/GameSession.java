package QuizGame;

public class GameSession {
    public enum NextEvent {
        NEXT_QUESTION,
        BET_ROUND,
        GAME_OVER
    }

    public static NextEvent getNextEvent(int currentQuestionIndex, int totalQuestions) {
        int nextIndex = currentQuestionIndex + 1;
        if (nextIndex >= totalQuestions) {
            return NextEvent.GAME_OVER;
        }
        // Inserts a betting round after every 3rd question (index 2, 5, 8...)
        if (nextIndex > 0 && nextIndex % 3 == 0) {
            return NextEvent.BET_ROUND;
        }
        return NextEvent.NEXT_QUESTION;
    }
}
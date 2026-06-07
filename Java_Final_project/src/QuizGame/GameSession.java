package QuizGame;

public class GameSession {

    // possible game actions
    public enum NextEvent {
        NEXT_QUESTION,
        BET_ROUND,
        GAME_OVER
    }

    // decides what happens nxt
    public static NextEvent getNextEvent(int currentQuestionIndex, int totalQuestions) {
        int nextIndex = currentQuestionIndex + 1;

        // no more questions left
        if (nextIndex >= totalQuestions) {
            return NextEvent.GAME_OVER;
        }

        // betting round every 3 questions
        if (nextIndex > 0 && nextIndex % 3 == 0) {
            return NextEvent.BET_ROUND;
        }

        return NextEvent.NEXT_QUESTION;
    }
}
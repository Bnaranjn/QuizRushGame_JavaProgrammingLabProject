package QuizGame;

/**
 * QuestionBank: Static store of quiz questions.
 * Format per row: {questionText, optA, optB, optC, optD, correctIndex (0-based)}
 */
public class QuestionBank {
    // correctIndex is 0-based: 0=A, 1=B, 2=C, 3=D
    public static final String[][] QUESTIONS = {
        {"Which planet is known as the Red Planet?",
            "Venus", "Mars", "Jupiter", "Saturn", "1"},
        {"How many sides does a hexagon have?",
            "5", "6", "7", "8", "1"},
        {"What is the capital of Japan?",
            "Beijing", "Seoul", "Bangkok", "Tokyo", "3"},
        {"Which element has the chemical symbol O?",
            "Gold", "Oxygen", "Osmium", "Oganesson", "1"},
        {"Who painted the Mona Lisa?",
            "Picasso", "Van Gogh", "Da Vinci", "Rembrandt", "2"},
        {"What is 12 x 12?",
            "124", "144", "132", "156", "1"},
        {"Which ocean is the largest?",
            "Atlantic", "Indian", "Arctic", "Pacific", "3"},
        {"How many bones are in the adult human body?",
            "186", "206", "226", "246", "1"},
        {"What language is most spoken in Brazil?",
            "Spanish", "English", "Portuguese", "French", "2"},
        {"Which gas do plants absorb from the atmosphere?",
            "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", "2"},
    };

    public static String getText(int index) {
        return QUESTIONS[index][0];
    }

    public static String[] getOptions(int index) {
        return new String[]{
            QUESTIONS[index][1],
            QUESTIONS[index][2],
            QUESTIONS[index][3],
            QUESTIONS[index][4]
        };
    }

    /** Returns the correct answer index (0-based). */
    public static int getCorrectIndex(int index) {
        return Integer.parseInt(QUESTIONS[index][5]);
    }

    public static int getTotalQuestions() {
        return QUESTIONS.length;
    }
}

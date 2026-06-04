package QuizGame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private final List<Question> questionBank = new ArrayList<>();

    public void addQuestion(Question q) {
        questionBank.add(q);
    }

    public Question getQuestionAt(int index) {
        if (index < 0 || index >= questionBank.size()) return null;
        return questionBank.get(index);
    }

    public int getTotalQuestions() {
        return questionBank.size();
    }

    public void loadQuizFromFile(String filePath) {
        questionBank.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    String qText = parts[0];
                    String[] opts = { parts[1], parts[2], parts[3], parts[4] };
                    int correctIdx = Integer.parseInt(parts[5].trim());
                    questionBank.add(new Question(qText, opts, correctIdx));
                }
            }
        } catch (IOException e) {
            System.err.println("Default fallback used. Failed loading configuration input descriptor: " + e.getMessage());
            loadFallbackQuestions();
        }
    }

    private void loadFallbackQuestions() {
        questionBank.add(new Question("Which programming language uses JVM execution routines?", 
                new String[]{"Python", "C++", "Java", "Ruby"}, 3));
        questionBank.add(new Question("What is the time complexity of looking up an item in a balanced HashMap?", 
                new String[]{"O(n)", "O(1)", "O(log n)", "O(n log n)"}, 2));
        questionBank.add(new Question("Which protocol provides reliable, ordered communication streams?", 
                new String[]{"UDP", "TCP", "ICMP", "DNS"}, 2));
        questionBank.add(new Question("What design pattern ensures a class has only one instance?", 
                new String[]{"Factory", "Observer", "Singleton", "Adapter"}, 3));
    }
}
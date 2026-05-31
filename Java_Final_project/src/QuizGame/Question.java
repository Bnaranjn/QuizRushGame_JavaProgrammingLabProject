package QuizGame;

// 06/01 12 am - may need few additional methods
// added 2 different getOptions methods - additional 1 from the og UML

public class Question {
	// fields
	private String text;
	private String[] options;
	private int correctAnswer;
	
	// constructor
	public Question(String text, String[] options, int correctAnswer){
		this.text = text;
		this.options = options;
		this.correctAnswer = correctAnswer;
	}
	
	// methods
	public boolean isCorrect(int answer) {
		return(this.correctAnswer == answer);
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getOptionsFormated() {
		// all of the options are merged into one string
		//		maybe useful when displaying the whole options under the questions
		// may or may not change the format
	    String result = "";
	    for (int i = 0; i < options.length; i++) {
	        result += (i + 1) + ". " + options[i] + "\n";
	    }
	    return result;
	}
	
	public String[] getOptionsArr() {
		// return the options arr
		// used to get text on each buttons
		return this.options;
	}
	
}

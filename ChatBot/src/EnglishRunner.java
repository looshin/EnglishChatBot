// Anna F, Ulee K, Iris T, Meira C
// CS 3 Summer 2022-2023
// Final Project: Language Chatbot
// 7/25/22
// Runs an intelligent chatbot that converses with a Spanish learner
// SpanishRunner

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class EnglishRunner {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		int DifficultyLevel = 1;
		English.difficultyLevel(DifficultyLevel);
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("greetings");
		categories.add("endconvo");
		
		English spanishBot = new English(categories);
		spanishBot.runBot();
		English.createAnswers();
		
		
	}

}
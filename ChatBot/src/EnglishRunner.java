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
	/*
	 * 	The main method displays everything onto the console
	 *  @param args console input
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int DifficultyLevel = 1; ///setting difficulty level
		English.difficultyLevel(DifficultyLevel); 
		ArrayList<String> categories = new ArrayList<String>(); //adding categories
		categories.add("greetings");
		categories.add("endconvo");
		
		//Creating a new english chat bot
		English spanishBot = new English(categories);
		spanishBot.runBot();
		English.createAnswers(); //Creating answers
		
		
	}

}
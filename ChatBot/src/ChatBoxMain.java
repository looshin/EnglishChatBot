import java.util.*;

public class ChatBoxMain {
	
	
	//Need to detect if a word is a noun, preposition, verb, or adjective


	
	
	public static void main(String[] args) {
		
	}
	
	//Need a way to detect new words
	public static ArrayList<String> currentWords(Queue<String> newWords){
		ArrayList<String> currentWords = new ArrayList<>();
		
		currentWords.add("Cow");
		currentWords.add("Red"); //Default structure to gerund rn
		currentWords.add("Sleeping");
		currentWords.add("is");
		
		while(!newWords.isEmpty()) {
			currentWords.add(newWords.poll());
		}
		
		return currentWords;
	}
}

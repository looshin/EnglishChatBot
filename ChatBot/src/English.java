// Anna F, Ulee K, Iris T, Meira C
// CS 3 Summer 2022-2023
// Final Project: Language Chatbot
// 7/25/22
// An intelligent chatbot that converses with a Spanish learner
// Language processing API: Apache OpenNPL - https://opennlp.apache.org/
// Spanish Parts of Speech ML Model - https://cavorite.com/labs/nlp/opennlp-models-es/
// Spanish

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

public class English {

	private static Map<String, String> questionAnswer = new HashMap<>();
	private static ArrayList<String> categories;
	private static ArrayList<String> words = new ArrayList<>(); 
	private static boolean currentConvo;
	private static Queue<String> PartsOfSpeech = new LinkedList<>();
	private static Queue<String> CurrentWords = new LinkedList<>();

	public English(ArrayList<String> answerCategories) {
		questionAnswer = new HashMap<>();
		categories = answerCategories;
		currentConvo = true;
	}

	/**
	 * Runs the language bot by starting a conversation and responding appropriately
	 * to the user.
	 *
	 * @throws FileNotFoundException if file is not found
	 * @throws IOException           if an input or output exception occurs
	 */
	public void runBot() throws FileNotFoundException, IOException {
		try {
			createAnswers();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		DoccatModel model = trainSpanishModel();
		

		Scanner scan = new Scanner(System.in);

		
		Pronouns.add("It"); //not necessary anymore i would say
		while (currentConvo) {
			// getting user input
			System.out.print("You: ");
			String userInput = scan.nextLine();

			String[] sentences = detectSentences(userInput);

			String response = "";

			for (String s : sentences) {
				response += getResponse(s, model);

			}
			

			System.out.println("Paul: " + response);

		}

		scan.close();

	}

	/**
	 * Define all possible answers for each phrase category.
	 */
	public static void createAnswers() throws FileNotFoundException {

		for (String s : categories) {
			File possibleAnswers = new File(String.valueOf(s) + ".txt"); //Getting default text extension
			questionAnswer.put(s, getRandomAnswer(possibleAnswers)); //picks a random answer in psosible answers
		}

	}

	private static ArrayList<String> possibleAnswerList = new ArrayList<>();
	
	/**
	 * Gets a random answer for each possible response category based on the bot's
	 * current vocabulary.
	 *
	 * @param possibleAnswers the file of possible answers for a given category
	 * @return a randomly chosen response from the file
	 */
	public static String getRandomAnswer(File possibleAnswers) {
		Scanner fileReader = null;
		try {
			fileReader = new Scanner(possibleAnswers);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		
		while (fileReader.hasNextLine()) {

			// each possible response has its own line
			possibleAnswerList.add(fileReader.nextLine());
		}
		fileReader.close();

		Random rand = new Random();
		int index = rand.nextInt(possibleAnswerList.size()); //chooses a random index
		return possibleAnswerList.get(index);
	}
	
	
	
	
	private static ArrayList<String> Pronouns = new ArrayList<>();
//	private static String old;
	private static ArrayList<Noun> NounsSingular = new ArrayList<>();
	private static ArrayList<Noun> NounsPlural = new ArrayList<>();
	private static ArrayList<Verb> Verbs = new ArrayList<>();
	private static ArrayList<Adjective> Adjectives = new ArrayList<>();
	private static ArrayList<Preposition> Preposition = new ArrayList<>();
	private static ArrayList<Adverb> Adverbs = new ArrayList<>();
	private static ArrayList<Verb> VerbPastTense = new ArrayList<>();
	private static ArrayList<Verb> VerbSingular3 = new ArrayList<>();
	private static ArrayList<Verb> VerbSingularNon3 = new ArrayList<>();
	private static String subject = "yourself";
	private static String subjectPlural;
	private static Map<String, String> wordTypes = new HashMap<String, String>();
	

	
	
	public static void  whatWord(Queue<String> pos, Queue<String> currentWords) {
		//Get subject, below plus s is in subject 
//		if(s.charAt(s.length()-1) == 's') {
//			//We know the word is a verb - present tense
//			//Classify to verb
//			Verb v = new Verb(s);
//			Verbs.add(v);
//			for (Verb verb : Verbs) {
//				System.out.print(verb.getWord()+" ");
//			}
//		}
//		
//		System.out.println(getSubject(s));
		
//		if (s.equals("NN") || s.equals("NNP") || s.equals("NS") || s.equals("NNPS")) {
//			
//		}
			
//		String old = s;
		while (!pos.isEmpty() || (!currentWords.isEmpty())) {
			String key = pos.remove();
			String value = currentWords.remove();
			wordTypes.put(key, value);
		}
		System.out.println(wordTypes);
		classifyWords(wordTypes);
	}
	
	public static void classifyWords(Map<String, String> words) {
		for (String key: words.keySet()) {
			if (key.equals("NN") || key.equals("NNP")) {
				SingularNoun n = new SingularNoun(words.get(key));
				NounsSingular.add(n);
				subject  = "The "+ words.get(key);
//				System.out.println("Singular Noun Successfully added");
			}
			if (key.equals("NNS") || key.equals("NNPS")) {
				PluralNouns n = new PluralNouns(words.get(key));
				NounsPlural.add(n);
				subjectPlural = "The " + words.get(key);
				System.out.println("Plural Noun Successfully added");
			}
			if (key.equals("VB")|| key.equals("VBG") || key.equals("VBN")) {
				Verb v = new Verb(words.get(key));
				Verbs.add(v);
//				System.out.println("Verb sucessfully added");
			}
			if (key.equals("VBD")) {
				VerbPastTense v = new VerbPastTense(words.get(key));
				VerbPastTense.add(v);
//				System.out.println("Past tense verb added");
			}
			if (key.equals("VBP") && !words.get(key).equals("am") && !words.get(key).equals("are")) {
				VerbSingularNon3 v = new VerbSingularNon3(words.get(key));
				VerbSingularNon3.add(v);
//				System.out.println("Non Third Person singular present verb added");
			}
			if (key.equals("VBZ") && !words.get(key).equals("is")) {
				VerbSingular3 v = new VerbSingular3(words.get(key));
				VerbSingular3.add(v);
//				System.out.println("Third Person singular present verb added");
			}
			
			if (key.equals("JJ") || key.equals("JJR") || key.equals("JJS")) {
				Adjective a = new Adjective(words.get(key));
				Adjectives.add(a);
//				System.out.println("Adjective sucessfully added");
			}
			if (key.equals("RB")||key.equals("RBR") || key.equals("RBS")) {
				if (!words.get(key).equals(words)) {
					Adverb a = new Adverb(words.get(key));
					Adverbs.add(a);
//					System.out.println("Adverb added sucessfully");
				}
			}
		}
	}
	
	public static String getSubject(String s) {
		
		String subject = "";
		if (!Pronouns.contains(s)) {
			System.out.println(Pronouns.contains(s));
		}
		
		return subject;
	}
	/**
	 * Trains the chatbot model using the traning data provided in a txt file.
	 *
	 * @return the trained model of the bot
	 * @throws IOException if input or output exception occurs
	 */
	public static DoccatModel trainSpanishModel() throws IOException {
		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
				new File("categorizer.txt"));
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory,
				StandardCharsets.UTF_8);
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

		DoccatFactory factory = new DoccatFactory(
				new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

		TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
		params.put(TrainingParameters.CUTOFF_PARAM, 0);

		// Train a model with classifications from above file. //might be es
		DoccatModel model = DocumentCategorizerME.train("eng", sampleStream, params, factory);
		return model;

	}

	/**
	 * Splits the user input up into sentences.
	 *
	 * @param userInput the user input to the bot
	 * @return an array of sentences that the user inputted
	 * @throws IOException           if input or output exception occurs
	 * @throws FileNotFoundException if file is not found
	 */
	public static String[] detectSentences(String userInput)
			throws IOException, FileNotFoundException {

		InputStream modelIn = new FileInputStream("en-sent.bin");
		SentenceDetectorME myCategorizer = new SentenceDetectorME(new SentenceModel(modelIn));
		String[] sentences = myCategorizer.sentDetect(userInput);
		return sentences;
	}

	
	private static int num = 0;
	
	/**
	 * Tokenizes a user inputted-sentence, analyzes the parts of speech, finds the
	 * lemma for each token, and detects the category of the user's input. Finds an
	 * appropriate response based on the category.
	 *
	 * @param s     one sentence that the user inputted
	 * @param model the machine learning model being used
	 * @return an appropriate response to the user
	 * @throws FileNotFoundException if file is not found
	 * @throws IOException           if input or output exception occured
	 */
	public static String getResponse(String s, DoccatModel model)
			throws FileNotFoundException, IOException {

		// Splits a sentence into tokens (words and punctuation)
		String[] tokenizedSentence = tokenizeSentence(s);
		
		

		// Detects the part of speech of each token
		String[] partsOfSpeech = detectPOSTags(tokenizedSentence);

		// Finds the lemma or each token (for example, "break" is the lemma of broke,
		// break, broken...)
		String[] lemmas = lemmatizeTokens(tokenizedSentence, partsOfSpeech);

		// Determines which category its response should fall under
		String responseCateg = detectCategory(model, lemmas);
		
		
		
		System.out.println(responseCateg);
		if (responseCateg.equals("endconvo")) {
			English.currentConvo = false;
			String goodbyeResponse = questionAnswer.get("endconvo");
			return " " + goodbyeResponse;
		} else if (responseCateg.equals("greetings") && num == 0)  {
			num++;
			return " " + questionAnswer.get("greetings");
		} else {
			return generateResponse();
		}

	}
	
	public static String generateResponse() {
		System.out.println("                        "+ subject);
		String response ="";
		int randNum = (int)Math.floor(Math.random()*(2-2+1)+1);
		System.out.println(words);
		if (wordTypes.containsValue("?") && (wordTypes.containsValue("Is") || wordTypes.containsValue("Am") || wordTypes.containsValue("Do"))) {
			wordTypes.remove(".");
			wordTypes.remove("VBZ");
			if (randNum == 1) {
				response = "yes";
			} else {
				response = "no";
			}
			return response;
		} 
		if (wordTypes.containsValue("?") && wordTypes.containsValue("Why")) {
			wordTypes.remove(".");
			int randNoun = (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			int randVerb = (int)Math.floor(Math.random()*(VerbSingular3.size()-2+1)+1);
			int randAdv = (int)Math.floor(Math.random()*(Adverbs.size()-2+1)+1);
			return "Because "+ subject + " " + Verbs.get(randVerb).getWord() + " " + Adverbs.get(randAdv).getWord();
		}
		
		if (wordTypes.containsValue("?") && wordTypes.containsValue("What")) {
			wordTypes.remove(".");
			wordTypes.remove("");
			return "I don't know. How am I supposed to know. Literally Ulee Klebeck programmed me. Ask him.";
		}
		
		if (wordTypes.containsValue("?") && wordTypes.containsValue("How") && wordTypes.containsValue("are")) {
			wordTypes.remove(".");
			int randAdj = (int)Math.floor(Math.random()*(Adjectives.size()-2+1)+1);
			return "I am " + Adjectives.get(randAdj).getWord();
		}
		
		if (wordTypes.containsValue("?") && wordTypes.containsValue("How")) {
			wordTypes.remove(".");
			wordTypes.remove("WP");
			int randNoun = (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			int randVerb = (int)Math.floor(Math.random()*(VerbSingular3.size()-2+1)+1);
//			int randAdv = (int)Math.floor(Math.random()*(Adverbs.size()-2+1)+1);
			return "Because "+ subject + " " + VerbSingularNon3.get(randVerb).getWord();
		}
		if (wordTypes.containsValue("?")) {
			wordTypes.remove(".");
			return "Idk";
		}
		int randomStructure = (int)Math.floor(Math. random()*(8-1+1)+1);
		if (randomStructure == 1) {
			//Noun + verb + adjective
			int randNoun = (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			int randVerb = (int)Math.floor(Math.random()*(VerbSingular3.size()-2+1)+1);
			int randAdv = (int)Math.floor(Math.random()*(Adverbs.size()-2+1)+1);
			return subject + " " + VerbSingular3.get(randVerb).getWord() + " " + Adverbs.get(randAdv).getWord();
		} else if (randomStructure == 2){
			//Noun verb
			int randNoun = (int)Math.floor(Math.random()*(NounsPlural.size()-2+1)+1);
			
			int randVerb = (int)Math.floor(Math.random()*(VerbSingularNon3.size()-2+1)+1);
			return subjectPlural+ " " + VerbSingularNon3.get(randVerb).getWord();
		} else if (randomStructure == 3) {
			//Noun verb Noun
			int randNoun1 = (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			int randVerb = (int)Math.floor(Math.random()*(VerbSingular3.size()-2+1)+1);
			int randNoun2 =  (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			return subject + " " + VerbSingular3.get(randVerb).getWord() + " " + NounsSingular.get(randNoun2).getWord();
		} else if (randomStructure == 4) {
			//Noun verb Noun Verb
			int randNoun1 = (int)Math.floor(Math.random()*(NounsPlural.size()-2+1)+1);
			int randVerb1 = (int)Math.floor(Math.random()*(VerbSingularNon3.size()-2+1)+1);
			int randNoun2 =  (int)Math.floor(Math.random()*(NounsPlural.size()-2+1)+1);
			int randAdv = (int)Math.floor(Math.random()*(Adverbs.size()-2+1)+1);
			return "I " + VerbSingularNon3.get(randVerb1).getWord()+" " + NounsPlural.get(randNoun1).getWord()+" "+ Adverbs.get(randAdv).getWord();
//			return NounsPlural.get(randNoun1).getWord()+ " " + VerbSingularNon3.get(randVerb1).getWord() + " " + NounsSingular.get(randNoun2).getWord() + " " + Adverbs.get(randAdv).getWord();
		} else if (randomStructure == 5){
			//Noun verb noun adjective
			int randNoun1 = (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			int randVerb = (int)Math.floor(Math.random()*(VerbSingular3.size()-2+1)+1);
			int randNoun2 =  (int)Math.floor(Math.random()*(NounsSingular.size()-2+1)+1);
			int randAdverb = (int)Math.floor(Math.random()*(Adverbs.size()-2+1)+1);
			return "I dislike " + NounsSingular.get(randNoun1).getWord(); 
//			return NounsSingular.get(randNoun1).getWord()+ " "+ VerbSingular3.get(randVerb).getWord()+" "+ NounsSingular.get(randNoun2).getWord() + " "+ Adverbs.get(randAdverb).getWord();
		} else if (randomStructure == 6){
			int randNoun =  (int)Math.floor(Math.random()*(NounsPlural.size()-2+1)+1);
			return "Tell me more about " + subject;
		} else {
			int randAdj = (int)Math.floor(Math.random()*(Adjectives.size()-2+1)+1);
			return "I am "  + Adjectives.get(randAdj).getWord();
		}
	}

	
	public static void difficultyLevel(int level) {
		if (level == 1) {
			NounsSingular.add(new SingularNoun("Horse"));
			NounsSingular.add(new SingularNoun("World"));
			NounsSingular.add(new SingularNoun("Art"));
			
			NounsPlural.add(new PluralNouns("Horses"));
			NounsPlural.add(new PluralNouns("Apples"));
			NounsPlural.add(new PluralNouns("People"));
			
			Verbs.add(new Verb("Kisses"));
			Verbs.add(new Verb("Races"));
			Verbs.add(new Verb("Kills"));
			
			VerbPastTense.add(new VerbPastTense("Went"));
			VerbPastTense.add(new VerbPastTense("Sang"));
			VerbPastTense.add(new VerbPastTense("Drank"));
			
			VerbSingular3.add(new VerbSingular3("runs"));
			VerbSingular3.add(new VerbSingular3("fights"));
			VerbSingular3.add(new VerbSingular3("loves"));
			
			VerbSingularNon3.add(new VerbSingularNon3("run"));
			VerbSingularNon3.add(new VerbSingularNon3("help"));
			VerbSingularNon3.add(new VerbSingularNon3("make"));
			
			Adjectives.add(new Adjective("Green"));
			Adjectives.add(new Adjective("Large"));
			Adjectives.add(new Adjective("Fast"));
			
			Adverbs.add(new Adverb("Quckly"));
			Adverbs.add(new Adverb("Quickly"));
			Adverbs.add(new Adverb("Suddenly"));
		}
	}
	/**
	 * Splits a sentence into tokens, such as individual words and punctuation
	 * marks.
	 */
	private static String[] tokenizeSentence(String sentence)
			throws FileNotFoundException, IOException {
		InputStream modelIn = new FileInputStream("en-token.bin");

		TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(modelIn));

		// tokenize sentence
		System.out.println();
//			for (String s: words) {
////				System.out.println(s);
//				English.whatWord(s);
//			}
		
		String[] tokens = myCategorizer.tokenize(sentence);
		System.out
				.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));
	
		
		for (String s: tokens) {
//			System.out.println(s);
//			if (!words.contains(s) && words.isEmpty() == true) {
//				words.add(s);
//			}
			CurrentWords.add(s);
			words.add(s);
		}
		
		return tokens;

	}

	/**
	 * Detects the parts of speech (noun, adjective, etc.) of each token using a
	 * English POS model.
	 */
	private static String[] detectPOSTags(String[] tokens) throws IOException {
		InputStream modelIn = new FileInputStream("en-pos-perceptron.bin");

		POSTaggerME myCategorizer = new POSTaggerME(new POSModel(modelIn));

		String[] posTokens = myCategorizer.tag(tokens);
		System.out.println(
				"POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));
		
		for (String posToken : posTokens) {
			PartsOfSpeech.add(posToken);
//			whatWord(posToken);
		}
		
		whatWord(PartsOfSpeech, CurrentWords);
		
		return posTokens;

	}

	/**
	 * Finds the lemma of each token. For example, the lemma of "broken" and "broke"
	 * in English is "break".
	 *
	 * @param tokens  the array of tokens in the sentence
	 * @param posTogs the array of parts of speech for each token in the sentence
	 * @return an array of lemmatized tokens
	 * @throws InvalidFormatException if format is invalid
	 * @throws IOException            if input or output exception occurs
	 */
	private static String[] lemmatizeTokens(String[] tokens, String[] posTags)
			throws InvalidFormatException, IOException {

		// Using English lemmatizer for now - will look for Spanish one
		InputStream modelIn = new FileInputStream("en-lemmatizer.bin");

		LemmatizerME myCategorizer = new LemmatizerME(new LemmatizerModel(modelIn));
		String[] lemmaTokens = myCategorizer.lemmatize(tokens, posTags);

		return lemmaTokens;

	}

	/**
	 * Detects which category the user's input is most likely to fall under, such as
	 * a greeting or a conversation closer.
	 *
	 * @param model       the machine learning model being user
	 * @param finalTokens the fully analyzed, lemmatized tokens
	 * @throws IOException if input or output exception occurs
	 * @return the category of the user's input that the model will use to determine
	 *         a response
	 */
	private static String detectCategory(DoccatModel model, String[] finalTokens)
			throws IOException {

		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

		// find most likely category
		double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
		String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
//		System.out.println(category);
		return category;
//		for (int i = 0;  i < finalTokens.length; i++) {
//			System.out.println(finalTokens[i]);
//		}
//		return " ";

	}

}
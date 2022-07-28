
public class PluralNouns extends Noun{


	public PluralNouns(String word) {
		super(word);
	}

	public String getWord() {
		return "the " + this.word;
	}
	
}

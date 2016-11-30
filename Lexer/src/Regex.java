import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.*;


public class Regex {
	public Pattern pattern;
	public String RegexName;
	
	
	
	public Regex(){
		super();
		this.pattern = null;
		this.RegexName = null;
	}
	
	public Regex(Pattern pattern, String RegexName){
		super();
		this.pattern = pattern;
		this.RegexName = RegexName;
	}
	
	public static ArrayList<Token> createRegexList(String input) {
		ArrayList<Regex> RegexList = new ArrayList<Regex>();
		RegexList.add(new Regex(Pattern.compile("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\""),"LITERAL"));
		RegexList.add(new Regex(Pattern.compile("(println|print)", Pattern.CASE_INSENSITIVE),"RESERVED"));
		RegexList.add(new Regex(Pattern.compile("(?!\".*)(?![=|>|<|(|)|+|\\-|\\*|/|%|,|\\!|\\||\\&|\\[|\\]|{|}|#])[^(println|print)][a-zA-Z][a-zA-Z0-9_]+(?!.*\")"),"IDENTIFIER"));
		RegexList.add(new Regex(Pattern.compile("[0-9]+"),"CONSTANT"));
		RegexList.add(new Regex(Pattern.compile("(?!\".*\")[\\=|\\>|\\<|(|)|\\+|\\-|\\*|/|%|,|\\!|\\||\\&|\\[|\\]|\\{|\\}|#](?!\".*\")"),"SPECIALCHAR"));
		RegexList.add(new Regex(Pattern.compile("[ \t\f\r\n]+"),"WHITESPACE"));
		
		StringBuffer tokenPatternsBuffer = new StringBuffer();
	    for (Regex regex : RegexList)
	      tokenPatternsBuffer.append(String.format("|(?<%s>%s)", regex.RegexName, regex.pattern));
		
	    Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));
	    ArrayList<Token> tokens = new ArrayList<Token>();
	    // Begin matching tokens
	    Matcher matcher = tokenPatterns.matcher(input);
//	    while (matcher.find()) {
//	      if (matcher.group(RegexList.get(1).RegexName) != null && matcher.group(RegexList.get(1).RegexName) == "RESERVED") {
//	        tokens.add(new Token(RegexList.get(1).RegexName, matcher.group(RegexList.get(1).RegexName)));
//	        continue;
//	      } else if (matcher.group(RegexList.get(2).RegexName) != null && matcher.group(RegexList.get(2).RegexName) == "IDENTIFIER") {
//	    	  tokens.add(new Token(RegexList.get(2).RegexName, matcher.group(RegexList.get(2).RegexName)));
//	          continue;
//	      } else if (matcher.group(RegexList.get(4).RegexName) != null && matcher.group(RegexList.get(4).RegexName) == "SPECIALCHAR") {
//	    	  tokens.add(new Token(RegexList.get(4).RegexName, matcher.group(RegexList.get(4).RegexName)));
//	          continue;
//	      } else if (matcher.group(RegexList.get(3).RegexName) != null && matcher.group(RegexList.get(3).RegexName) == "CONSTANT") {
//	    	  tokens.add(new Token(RegexList.get(3).RegexName, matcher.group(RegexList.get(3).RegexName)));
//	          continue;
//	      } else if (matcher.group(RegexList.get(0).RegexName) != null && matcher.group(RegexList.get(0).RegexName) == "LITERAL") {
//	    	  tokens.add(new Token(RegexList.get(0).RegexName, matcher.group(RegexList.get(0).RegexName)));
//	          continue;
//	      } else if (matcher.group(RegexList.get(5).RegexName) != null)
//	        continue;
//	    }

	    return tokens;
	}
}



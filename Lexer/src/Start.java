import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

public class Start {
	public static Pattern tokenPattern;
	public static int totalLines = 0;
	public static ArrayList<LineNumberMapper> lineMap = new ArrayList<LineNumberMapper>();
	public static HashMap<Integer, String> program = new HashMap<Integer, String>();
	/* Function : To read file */
	public static BufferedReader readFile(String inputFilePath){
		BufferedReader br=null;
		try {
			FileReader inputFile = new FileReader(inputFilePath);
			 br = new BufferedReader(inputFile);
			 return br;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}
	/* Function : To create Token Pattern to be compiled */
	public static Pattern createTokenPattern(){
		StringBuffer tokenBuffer = new StringBuffer();
	    tokenBuffer.append(String.format("|(?<IDENTIFIER>%s)", "(?!\".*\")(?!(println|print|let|integer|input|end|if|then|goto|gosub|ret))[a-zA-Z]+(?!\".*\")"));
	    tokenBuffer.append(String.format("|(?<RESERVED>%s)", "println|print|let|integer|input|end|if|then|goto|gosub|ret"));
	    tokenBuffer.append(String.format("|(?<CONSTANT>%s)", "[0-9]+"));
	    tokenBuffer.append(String.format("|(?<SPACE>%s)", "[ \t\f\r\n]+"));
	    tokenBuffer.append(String.format("|(?<COMMENT>%s)", "(//.*?$)"));
	    tokenBuffer.append(String.format("|(?<LITERAL>%s)", "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\""));
	    tokenBuffer.append(String.format("|(?<SPECIALCHAR>%s)", "(?!\".*\")(?!(//.*?$))[\\=|\\>|\\<|(|)|\\+|\\-|\\*|/|%|,|\\!|\\||\\&|\\[|\\]|\\{|\\}|#](?!(,\".*\"))"));
	    tokenPattern = Pattern.compile(new String(tokenBuffer.substring(1)),Pattern.CASE_INSENSITIVE);
		return tokenPattern;
	}
	/* Function : To tokenize the input string into lexemes */
	public static ArrayList<Token> tokenize(String input){
		ArrayList<Token> tokens = new ArrayList<Token>();
		int checkLength = 0;
		tokenPattern = createTokenPattern();
	    Matcher matcher = tokenPattern.matcher(input);
	    while(matcher.find()){
	    	if (matcher.group("RESERVED") != null) {
	    		tokens.add(new Token(matcher.group("RESERVED"), "RESERVED"));
	            continue;
	        } else  if (matcher.group("LITERAL") != null) {
	    		tokens.add(new Token(matcher.group("LITERAL"), "LITERAL"));
	            continue;
	        } else if (matcher.group("IDENTIFIER") != null) {
	        	checkLength = matcher.group("IDENTIFIER").length();
	        	if(checkLength >=1 && checkLength <=32)
	        		tokens.add(new Token(matcher.group("IDENTIFIER"), "IDENTIFIER"));
	            continue;
	        } else if (matcher.group("SPECIALCHAR") != null) {
	    		tokens.add(new Token(matcher.group("SPECIALCHAR"), "SPECIALCHAR"));
	            continue;
	        } else if (matcher.group("CONSTANT") != null) {
	    		tokens.add(new Token(matcher.group("CONSTANT"), "CONSTANT"));
	            continue;
	        } else if (matcher.group("COMMENT") != null) {
	    		tokens.add(new Token(matcher.group("COMMENT"), "COMMENT"));
	            continue;
	        } else if (matcher.group("SPACE") != null) {
	            continue;
	        }
	    }
	    return tokens;
	}
	
	public void storeLineNumers(){
		
	}
	
	public static void main(String[] args) {
		String inputFilePath = "E:\\MS in CS\\Sem1 - Fall\\Adv topics in Prog Languages(Hoffman)\\nestedloop.txt";
		int line = 0;
		ArrayList<Token> tokens = new ArrayList<Token>();
		LineNumberMapper lineMapper = new LineNumberMapper();
		Parser p = new Parser();
		try {
			BufferedReader br= readFile(inputFilePath);
			String input = "";
			while ((input = br.readLine()) != null) {
				line++;
				program.put(line, input);
				
				tokens = tokenize(input);
				lineMapper.storeLineNumber(lineMap, tokens, line);
			}
			totalLines = line;
			p.parse(lineMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

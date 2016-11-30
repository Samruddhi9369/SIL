import java.util.ArrayList;

public class LineNumberMapper {
	public int index;
	public int lineNumber;
	public ArrayList<Token> tokens;
	
	public LineNumberMapper() {
		index=0;
		lineNumber = 0;
		tokens = null;
	}
	
	public LineNumberMapper(int index, int lineNumber, ArrayList<Token> tokens){
		this.index = index;
		this.lineNumber = lineNumber;
		this.tokens = tokens;
	}
	
	public void storeLineNumber(ArrayList<LineNumberMapper> lineMap, ArrayList<Token> tokens, int index){
		
		int lineNumber;
		try{
			lineNumber = Integer.parseInt(tokens.get(0).token);
			tokens.remove(0);
			if(checkifLineNumberAlreadyExists(lineMap, lineNumber) == false)
				lineMap.add(new LineNumberMapper(index, lineNumber, tokens));
			else {
				System.err.println("\nError in program: Line Number already exists.");
				System.exit(0);
			}
		}
		catch(java.lang.NumberFormatException e){
			System.err.println("\nError in program: Line Number does not exist.");
			System.exit(0);
		}
		
	}
	
	public boolean checkifLineNumberAlreadyExists(ArrayList<LineNumberMapper> lineMap, int lineNumber){
		boolean exists = false;
		for(LineNumberMapper lm: lineMap){
			if(lm.lineNumber == lineNumber){
				exists = true;
				break;
			}
		}
		return exists;
	}
}

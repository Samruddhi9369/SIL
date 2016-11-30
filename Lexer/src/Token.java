import java.util.ArrayList;

public class Token {
	String token;
	String tokentype;
	
	public Token(){
		super();
		this.tokentype = null;
		this.token = null;
	}
	
	public Token(String token, String tokentype){
		super();
		this.tokentype = tokentype;
		this.token = token;
	}
	/* Function : To display tokens */
	public void display(ArrayList<Token> tokens){
		for(Token token : tokens){
			System.out.println(token.token+"\t"+token.tokentype+" \n");
		}
		
	}
	
}

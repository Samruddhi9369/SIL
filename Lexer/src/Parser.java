import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Stack;

public class Parser {
	ArrayList<Token> tokens;
	Token lookahead;
	int lineNumber;
	public static ArrayList<LineNumberMapper> lineMap2 = new ArrayList<LineNumberMapper>();
	public static Stack<Integer> goSubStack;
	
	public void parse(ArrayList<LineNumberMapper> lineMap){
		
		ListIterator<LineNumberMapper> itLineMap = lineMap.listIterator();
		lineMap2 = new ArrayList<LineNumberMapper>();
		lineMap2 = lineMap;
		int currentIndex = 0, index = 0;
		goSubStack = new Stack<Integer>();
		ArrayList<Token> tokenslist = new ArrayList<Token>();
		while(itLineMap.hasNext()){
			LineNumberMapper lineNumMap = itLineMap.next();
			
			tokenslist =lineNumMap.tokens;
			currentIndex = lineNumMap.index;
			index = parseLine(tokenslist, currentIndex);
			if(index != 0){
				if(currentIndex<index){
					for (; index > currentIndex + 1; --index) {
				        itLineMap.next(); // ignore/skip
				    }
				}
				else {
					for (; index < currentIndex + 1; ++index) {
				        itLineMap.previous(); // ignore/skip
				    }
				}
			}
		}
	}
	

	public int getIndex(int lineNumber, ArrayList<LineNumberMapper> lineMap){
		int index = 0;
		boolean flag = false;
		for(LineNumberMapper lmap : lineMap){
			if(lmap.lineNumber == lineNumber){
				index = lmap.index;
				flag = true;
				break;
			}else{
				flag = false;
			}
		}
		if(flag == false){
			System.err.println("Error: Line Number "+lineNumber+" does not exist");
			System.exit(0);
		}
		return index;
	}
	
	
	public int getLineNumber(int index, ArrayList<LineNumberMapper> lineMap){
		int lineNumber = 0;
		boolean flag = false;
		for(LineNumberMapper lmap : lineMap){
			if(lmap.index == index){
				lineNumber = lmap.lineNumber;
				flag = true;
				break;
			}else{
				flag = false;
			}
		}
		if(flag == false){
			System.err.println("Error: Line Number "+index+" does not exist");
			System.exit(0);
		}
		return lineNumber;
	}
	
	
	public int parseLine(ArrayList<Token> tokens,int currentIndex){
		
		int line = 0, index = 0;
	    int totalTokens = 0;
		Token lastToken;
	    SymbolTable sym = new SymbolTable();
	    Scanner reader;
	    int expressionValue = 0;
	    String printStatement="";
		ArrayList<Token> expression = new ArrayList<Token>();
		ListIterator<Token> it = tokens.listIterator();
		lookahead = tokens.get(0);
		String nextToken = "";
		Token NextAvailableToken = it.next();
		if(lookahead.tokentype.toUpperCase() == "RESERVED") {
		    switch(lookahead.token.toUpperCase()){
		    case "END" :
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							if(lastToken.tokentype.matches("COMMENT")){
								tokens.remove(totalTokens);
							}
							if(totalTokens == 0){
			    				System.out.println("\nTerminating the program..");
			    				System.exit(0);
							}
							else {
								System.out.println("Invalid END statement at Line "+getLineNumber(currentIndex,lineMap2));
							}
		    				break;
		    case "PRINT":
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							if(lastToken.tokentype.matches("COMMENT")){
								tokens.remove(totalTokens);
							}
							totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
		    				if(lastToken.token.equalsIgnoreCase("PRINT")){
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
							if(tokens.get(0).token.matches(",")||tokens.get(tokens.size()-1).token.matches(",")){
								System.err.println("\nError in Grammar at \",\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
							}
							lastToken = tokens.get(totalTokens);
							totalTokens = tokens.size()-1;
							ListIterator<Token> it1 = tokens.listIterator();
							it1= it;
							nextToken = "";
							while(it1.hasNext()){
								Token current = it1.next();
								if(current.tokentype.matches("LITERAL")){
									if(!expression.isEmpty()){
										System.err.println("Error: Missing ',' at "+current.token + " at Line "+getLineNumber(currentIndex,lineMap2));
										System.exit(0);
									}
									if(it1.hasNext()){
										nextToken = it1.next().token;
										if(!nextToken.matches(",")){
											System.err.println("Error: Missing ',' at "+nextToken + " at Line "+getLineNumber(currentIndex,lineMap2));
											System.exit(0);
										}
									}
									String literal = current.token;
					    			printStatement += literal.substring(1, literal.length()-1);
								}else{
									if(!current.token.matches(",")){
										nextToken+=current.token;
										expression.add(current);
									}
									else{
										if(!nextToken.isEmpty() && current.token.matches(",")){
											nextToken="";
											char currentOp;
											for(Token tok : expression){
									    		if(tok.tokentype.matches("SPECIALCHAR")){
										    		currentOp = tok.token.charAt(0);
										    		if(currentOp == '+' || currentOp == '-' || currentOp == '*' || currentOp == '/'||currentOp == '(' || currentOp == ')'){
										    			//isValidOp=1;
										    		}else{
										    			//isValidOp=0;
										    			System.err.println("Error: Invalid Operator \""+currentOp+"\" in expression at Line "+getLineNumber(currentIndex,lineMap2));
									    				System.exit(0);
										    		}
									    		}
									    	}
											expressionValue = evaluateExpression(expression);
											expression.clear();
							    			printStatement += expressionValue;
										}else{
											System.err.println("Error");
											System.exit(0);
										}
									}
								}
							}
							if(!expression.isEmpty()){
								char currentOp;
								for(Token tok : expression){
						    		if(tok.tokentype.matches("SPECIALCHAR")){
							    		currentOp = tok.token.charAt(0);
							    		if(currentOp == '+' || currentOp == '-' || currentOp == '*' || currentOp == '/'||currentOp == '(' || currentOp == ')'){
							    			//isValidOp=1;
							    		}else{
							    			//isValidOp=0;
							    			System.err.println("Error: Invalid Operator \""+currentOp+"\" in expression at Line "+getLineNumber(currentIndex,lineMap2));
						    				System.exit(0);
							    		}
						    		}
						    	}
					    		expressionValue = evaluateExpression(expression);
					    		expression = null;
				    			printStatement += expressionValue;
					    	}
					    	System.out.print(printStatement);
		    				break;
		    case "PRINTLN":
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							if(lastToken.tokentype.matches("COMMENT")){
								tokens.remove(totalTokens);
							}
							totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
		    				if(lastToken.token.equalsIgnoreCase("PRINTLN")){
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
							if(tokens.get(0).token.matches(",")||tokens.get(tokens.size()-1).token.matches(",")){
								System.err.println("\nError in Grammar at \",\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
							}
							lastToken = tokens.get(totalTokens);
							totalTokens = tokens.size()-1;
							ListIterator<Token> it2 = tokens.listIterator();
							it2 = it;
							nextToken = "";
							while(it2.hasNext()){
								Token current = it2.next();
								if(current.tokentype.matches("LITERAL")){
									if(!expression.isEmpty()){
										System.err.println("Error: Missing ',' at "+current.token + " at Line "+getLineNumber(currentIndex,lineMap2));
										System.exit(0);
									}
									if(it2.hasNext()){
										nextToken = it2.next().token;
										if(!nextToken.matches(",")){
											System.err.println("Error: Missing ',' at "+nextToken + " at Line "+getLineNumber(currentIndex,lineMap2));
											System.exit(0);
										}
									}
									String literal = current.token;
					    			printStatement += literal.substring(1, literal.length()-1);
								}else{
									if(!current.token.matches(",")){
										nextToken+=current.token;
										expression.add(current);
									}
									else{
										if(!nextToken.isEmpty() && current.token.matches(",")){
											nextToken="";
											char currentOp;
											for(Token tok : expression){
									    		if(tok.tokentype.matches("SPECIALCHAR")){
										    		currentOp = tok.token.charAt(0);
										    		if(currentOp == '+' || currentOp == '-' || currentOp == '*' || currentOp == '/'||currentOp == '(' || currentOp == ')'){
										    			
										    		}else{
										    			
										    			System.err.println("Error: Invalid Operator \""+currentOp+"\" in expression at Line "+getLineNumber(currentIndex,lineMap2));
									    				System.exit(0);
										    		}
									    		}
									    	}
											expressionValue = evaluateExpression(expression);
											expression.clear();
							    			printStatement += expressionValue;
										}else{
											System.err.println("Error");
											System.exit(0);
										}
									}
								}
							}
							if(!expression.isEmpty()){
								char currentOp;
								for(Token tok : expression){
						    		if(tok.tokentype.matches("SPECIALCHAR")){
							    		currentOp = tok.token.charAt(0);
							    		if(currentOp == '+' || currentOp == '-' || currentOp == '*' || currentOp == '/'||currentOp == '(' || currentOp == ')'){
							    			//isValidOp=1;
							    		}else{
							    			//isValidOp=0;
							    			System.err.println("Error: Invalid Operator \""+currentOp+"\" in expression at Line "+getLineNumber(currentIndex,lineMap2));
						    				System.exit(0);
							    		}
						    		}
						    	}
					    		expressionValue = evaluateExpression(expression);
					    		expression = null;
				    			printStatement += expressionValue;
					    	}
					    	System.out.println(printStatement);
							break;
		    case "INPUT" :
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							reader = new Scanner(System.in);
		    				if(lastToken.tokentype.matches("COMMENT")){
		    					tokens.remove(totalTokens);
		    				}
		    				totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
		    				if(lastToken.token.equalsIgnoreCase("INPUT")){
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
							if(lastToken.tokentype.matches("IDENTIFIER")){
						    	for(Token token : tokens){
						    		if(token.tokentype.matches("IDENTIFIER")){
						    			int inputValue = 0;
						    			Long number;
							    		try{
							    			while (!reader.hasNextLong()) {
							    				System.out.println("Please enter a valid number");
							    				reader.next(); 
						    			    	}
							    				number = reader.nextLong();
						    			    	while(number < -2147483648 || number > 2147483647){
						    			    		System.out.println("Please enter number within range -2147483648 to +2147483647");
						    			    		number = reader.nextLong();
						    			    		
						    			    	}
									    	inputValue= number.intValue();
									    	sym.editIntegerNameValue(token.token, inputValue);
									    	
							    		}catch(java.util.InputMismatchException e){
						    				System.out.println("Please enter number within range -2147483648 to +2147483647\n");
						    				System.exit(0);
						    			}
						    			sym.editIntegerNameValue(token.token, inputValue);
						    		}
								}
							}
		    				else{
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
		    				break;
		    case "INTEGER" : 
		    				totalTokens = tokens.size()-1;
		    				lastToken = tokens.get(totalTokens);
		    				if(lastToken.tokentype.matches("IDENTIFIER") || lastToken.tokentype.matches("COMMENT")){
		    					if(lastToken.tokentype.matches("COMMENT")){
		    						tokens.remove(totalTokens);
		    					}
		    					totalTokens = tokens.size()-1;
			    				lastToken = tokens.get(totalTokens);
			    				if(lastToken.token.equalsIgnoreCase("INTEGER")){
			    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
				    				System.exit(0);
			    				}
			    				if(lastToken.tokentype.matches("SPECIALCHAR")){
			    					System.err.println("\nError in Grammar at "+lastToken.token+ " at Line "+getLineNumber(currentIndex,lineMap2));
					    			System.exit(0);
		    					}
						    	
			    				while(it.hasNext()){
			    		    		Token token = it.next();
			    		    		if(token.tokentype.matches("IDENTIFIER")){
			    		    			sym.addIntegerName(token.token);
			    		    		}
			    		    		if(token.tokentype.matches("CONSTANT")){
			    		    			System.out.println("\nError in Grammar at "+token.token + " at Line "+getLineNumber(currentIndex,lineMap2));
			    		    			System.exit(0);
			    		    		}
			    		    		if(token.tokentype.matches("SPECIALCHAR")){
			    		    			if(!token.token.matches(",")){
			    		    				System.err.println("\nError in Grammar at "+token.token+" at Line "+getLineNumber(currentIndex,lineMap2));
			    		    				System.exit(0);
			    		    			}
			    		    		}
			    		    		if(token.tokentype.matches("COMMENT")){
			    			    			//This is single line comment
			    		    		}
			    		    	}
		    				}
		    				else{
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
		    				break;
		    case "LET" :
		    				String name ="";
		    				totalTokens = tokens.size()-1;
		    				lastToken = tokens.get(totalTokens);
		    				if(lastToken.tokentype.matches("COMMENT")){
		    					tokens.remove(totalTokens);
		    				}
		    				totalTokens = tokens.size()-1;
		    				lastToken = tokens.get(totalTokens);
		    				if(lastToken.token.equalsIgnoreCase("LET")){
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
		    				if(lastToken.tokentype.matches("SPECIALCHAR")){
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
		    				while(it.hasNext()){
		    					Token token = it.next();
		    					if(token.tokentype.matches("IDENTIFIER")){
					    			name= token.token;
					    			break;
					    		}
					    		else if(token.tokentype.matches("SPECIALCHAR")){
					    			if(token.token.matches("=")){
					    				break;
					    			}
					    			else
					    			{
					    				System.err.println("\nError in Grammar at \""+token.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
					    				System.exit(0);
					    			}
					    		}
					    		else {
					    			System.err.println("\nError in Grammar at \""+token.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
				    				System.exit(0);
					    		}
		    				}
					    	
					    	if(tokens.get(0).token.matches("=")){
			    				it.next();
			    			}
					    	it.next();
					    	char currentOp;
					    	//int isValidOp = 0;
					    	while(it.hasNext()){
		    					Token token = it.next();
		    					if(token.tokentype.matches("SPECIALCHAR")){
						    		currentOp = token.token.charAt(0);
						    		if(currentOp == '+' || currentOp == '-' || currentOp == '*' || currentOp == '/'||currentOp == '(' || currentOp == ')'){
						    			//isValidOp=1;
						    		}else{
						    			
						    			System.err.println("Operator \""+currentOp+"\" is not supported at Line "+getLineNumber(currentIndex,lineMap2));
					    				System.exit(0);
						    		}
					    		}

					    	}
					    	
					    	expressionValue = evaluateExpression(tokens);
					    	sym.editIntegerNameValue(name,expressionValue);
							break;
		    case "IF" : 
		    				int num1=0, num2=0;
		    				boolean hasThen = false;
		    				ArrayList<Token> compareExpression = new ArrayList<Token>();
		    				ArrayList<Token> compareExpression2 = new ArrayList<Token>();
		    				char operator = 0;
		    				totalTokens = tokens.size()-1;
		    				lastToken = tokens.get(totalTokens);
		    				if(lastToken.tokentype.matches("COMMENT")){
		    					tokens.remove(totalTokens);
		    				}
		    				totalTokens = tokens.size()-1;
		    				lastToken = tokens.get(totalTokens);
		    				checkforNestedIfs(tokens,getLineNumber(currentIndex,lineMap2));
		    				checkforThens(tokens,getLineNumber(currentIndex,lineMap2));
		    				if(lastToken.token.equalsIgnoreCase("THEN") || lastToken.token.equalsIgnoreCase("IF")){
		    					System.err.println("\nError in Grammar at \""+lastToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
		    				checkforOperator(tokens,getLineNumber(currentIndex,lineMap2));
		    				while(it.hasNext()){
		    					Token token = it.next();
		    					if(token.tokentype.matches("SPECIALCHAR")){
		    						if(token.token.matches("<") || token.token.matches(">") || token.token.matches("=")){
			    						operator = token.token.charAt(0);
			    						//hasOp = true;
			    						break;	
		    						}
		    						else {
		    							System.err.println("\nError in Grammar at \""+token.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
					    				System.exit(0);
		    						}
		    					}
		    					else{
		    						compareExpression.add(token);
		    					}
		    				}
		    				if(compareExpression.size() == 0){
		    					System.err.println("\nError in Grammar: Missing Expression at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
    						num1 = evaluateExpression(compareExpression);
    						while(it.hasNext()){
		    					Token token = it.next();
		    					if(token.tokentype.matches("RESERVED")){
		    						if(token.token.equalsIgnoreCase("THEN")){
		    							hasThen = true;
			    						break;
		    						}
		    					}
		    					else if(token.token.equalsIgnoreCase(">") || token.token.equalsIgnoreCase("<") || token.token.equalsIgnoreCase("=")){
		    						System.err.println("\nError in Grammar: Invalid Expression at Line "+getLineNumber(currentIndex,lineMap2));
				    				System.exit(0);
	    						}
		    					else{
		    						compareExpression2.add(token);
		    					}
		    				}
    						if(compareExpression2.size() == 0){
		    					System.err.println("\nError in Grammar: Missing Expression at Line "+getLineNumber(currentIndex,lineMap2));
			    				System.exit(0);
		    				}
							num2 = evaluateExpression(compareExpression2);
							compareExpression.clear();
							
							if(performCompareOperation(operator, num1, num2) == true){
								ArrayList<Token> remaining = new ArrayList<Token>();
								while(it.hasNext()){
									remaining.add(it.next());
								}
								index = parseLine(remaining, currentIndex);
							}else{
								index = 0;
							}
		    				break;
		    case "GOTO"	   :
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							if(lastToken.tokentype.matches("COMMENT")){
								tokens.remove(totalTokens);
							}
							checkLoopStatements(tokens,"GOTO",getLineNumber(currentIndex, lineMap2));
							totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
		    				line = Integer.parseInt(tokens.get(1).token);
		    				index = getIndex(line,lineMap2);
		    				break;
		    case "GOSUB"   :
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							if(lastToken.tokentype.matches("COMMENT")){
								tokens.remove(totalTokens);
							}
							checkLoopStatements(tokens,"GOSUB",getLineNumber(currentIndex, lineMap2));
							line = Integer.parseInt(tokens.get(1).token);
							index = getIndex(line,lineMap2);
							int returnAddress = currentIndex+1;
							goSubStack.push(returnAddress);
							break;
		    case "RET"	   :
					    	totalTokens = tokens.size()-1;
							lastToken = tokens.get(totalTokens);
							if(lastToken.tokentype.matches("COMMENT")){
								tokens.remove(totalTokens);
							}
		    				if(!goSubStack.empty()){
		    					index = goSubStack.pop();
		    				}else{
		    					System.err.println("Error: Invalid Return Statement at Line "+getLineNumber(currentIndex,lineMap2));
		    					System.exit(0);
		    				}
		    				break;
		    case "COMMENT" :
					    	//This is single line comment
							break;
		    default: System.err.println("Invalid Syntax at Line "+getLineNumber(currentIndex,lineMap2));
		    		 break;
		    }
	    } else {
	    	System.err.println("Invalid Syntax at \""+NextAvailableToken.token+"\" at Line "+getLineNumber(currentIndex,lineMap2));
	    	System.exit(0);
	    }
		return index;
	}
	
	public int evaluateExpression(ArrayList<Token> tokenslist){
		
		ArrayList<Token> tokens = tokenslist;
		SymbolTable sym = new SymbolTable();
        // Store values
		Stack<Integer> values = new Stack<Integer>();
		// Store Operators
		Stack<Character> ops = new Stack<Character>();
		int tokenValue = 0;
		for (Token token : tokens)
        {
            // Current token is a constant or an identifier
            if (token.tokentype.matches("CONSTANT"))
            {
            	if(token.token.charAt(0) == '-')
            		tokenValue = -1 * Integer.parseInt(token.token.substring(1));
            	else
            		tokenValue = Integer.parseInt(token.token);
                values.push(tokenValue);
            }
            else if (token.tokentype.matches("IDENTIFIER"))
            {
                values.push(sym.getIntegerValue(token.token));
            }
            else if(token.tokentype.matches("SPECIALCHAR")){
            	char currentOp = token.token.charAt(0);
	            if (currentOp == '('){
	            	ops.push('(');
	            }
	            else if (currentOp == ')'){
	                while (ops.peek() != '(')
		                  values.push(performOperation(ops.pop(), values.pop(), values.pop()));
		                ops.pop();
	            }
	            // Current token is an operator.
	            else if (currentOp == '+' || currentOp == '-' || currentOp == '*' || currentOp == '/')
	            {
	                while (!ops.empty() && checkforPrecedence(currentOp, ops.peek())){
	                  values.push(performOperation(ops.pop(), values.pop(), values.pop()));
	                }
	                ops.push(currentOp);
	            }
            }
        }
		char op = 0;
        while (!ops.empty()) {
        	try{
        		values.push(performOperation(op=ops.pop(), values.pop(), values.pop()));
        	}
        	catch(java.util.EmptyStackException e){
        		System.err.println("\nInvalid Expression at "+op);
        		System.exit(0);
        	}
        }
        return values.pop();
	}
	
    public boolean checkforPrecedence(char op1, char op2)
    {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }
 
    public int performOperation(char operator, int num2, int num1)
    {
        switch (operator)
        {
        case '+':
            return num2 + num1;
        case '-':
            return num1 - num2;
        case '*':
            return num2 * num1;
        case '/':
            if (num1 == 0)
                throw new
                UnsupportedOperationException("Arithmetic Error: Divide by Zero");
            return num1 / num2;
        }
        return 0;
    }
    
    public boolean performCompareOperation(char operator, int num1, int num2)
    {
        switch (operator)
        {
        case '<':
            return num1 < num2;
        case '>':
            return num1 > num2;
        case '=':
            return num1 == num2;
        }
        return false;
    }
    
    public void checkforOperator(ArrayList<Token> tokens, int lineNumber){
    	boolean hasOp = false;
    	ListIterator<Token> it = tokens.listIterator();
    	while(it.hasNext()){
			Token token = it.next();
			if(token.token.equalsIgnoreCase("THEN")){
				break;
			}
			if(token.tokentype.matches("SPECIALCHAR")){
				if(token.token.equalsIgnoreCase(">") || token.token.equalsIgnoreCase("<") || token.token.equalsIgnoreCase("=")){
					hasOp = true;
					break;
				}
				else {
					System.err.println("Error: Invalid Operator in statement at Line "+lineNumber);
					System.exit(0);
				}
			}
		}
    	if(!hasOp){
    		System.err.println("Error: Missing Operator in statement at Line "+lineNumber);
			System.exit(0);
    	}
	}
    

    public void checkforExpression(ArrayList<Token> tokens, int lineNumber){
		if(tokens.size()-1 == 0){
			System.err.println("Error: Missing expression after 'IF' in statement at Line "+lineNumber);
			System.exit(0);
		}
	}
    
    public void checkforNestedIfs(ArrayList<Token> tokens, int lineNumber){
    	int countIF = 0;
    	for(Token tok : tokens){
    		if(tok.tokentype.matches("RESERVED") && tok.token.equalsIgnoreCase("IF")){
    			countIF++;
    			if(countIF > 1){
    				System.err.println("Error: Nested 'IF' is not allowed in statement at Line "+lineNumber);
    				System.exit(0);
    			}
    		}
		}
	}

    public void checkforThens(ArrayList<Token> tokens, int lineNumber){
    	int countTHEN = 0;
    	for(Token tok : tokens){
    		if(tok.tokentype.matches("RESERVED") && tok.token.equalsIgnoreCase("THEN")){
    			countTHEN++;
    			if(countTHEN > 1){
    				System.err.println("Error: 'THEN' is occured 2 times in statement at Line "+lineNumber);
    				System.exit(0);
    			}
    		}
		}
	}
    
    public void checkLoopStatements(ArrayList<Token> tokens, String loopVar, int lineNumber){
    	if(tokens.size()-1 > 1){
    		System.err.println("Error: Invalid "+loopVar+" statement at Line "+lineNumber);
			System.exit(0);
    	} else if(tokens.size()-1 == 0){
    		System.err.println("Error: Missing Line Number in "+ loopVar +" statement at Line "+lineNumber);
			System.exit(0);
    	} else {
    		
    	}
    	
	}
}

import java.util.ArrayList;

public class SymbolTable {
	public String name;
	public String type;
	public int value;
	
	public static ArrayList<SymbolTable> st = new ArrayList<SymbolTable>();
	
	public SymbolTable(){
		this.name= "";
		this.type="";
		this.value=0;
	}
	
	public SymbolTable(String name, String type, int value){
		this.name= name;
		this.type=type;
		this.value=value;
	}
	
	public void addIntegerName(String name){
		int flag = 0;
		for(SymbolTable sym : st){
			if(sym.name.equals(name)){
				flag = 1;
				break;
			}
		}
		if(flag == 1){
			System.err.println("Error: Already defined variable");
			System.exit(0);
		}
		else 
			st.add(new SymbolTable(name,"INTEGER",0));
	}
	
	public void editIntegerNameValue(String name, int value){
		for(SymbolTable sym : st){
			if(sym.name.equals(name)){
				sym.value = value;
			}else{
			}
		}
	}
	
	public int getIntegerValue(String name){
		int flag = 0;
		for(SymbolTable sym : st){
			if(sym.name.equals(name)){
				value = sym.value;
				flag = 0;
				break;
			} else{
				flag = 1;
			}
		}
		if(flag == 1){
			System.err.println("Error: Variable \""+name+"\" is not declared.");
			System.exit(0);
		}
		return value;
	}
	
	public void displayTable(ArrayList<SymbolTable> st){
		System.out.println("\nName\tType\tValue");
		for(SymbolTable symtable : st){
			System.out.println(symtable.name+"\t"+symtable.type+"\t"+symtable.value+"\n");
		}
	}
	
}

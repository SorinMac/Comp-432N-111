import java.util.ArrayList;
import java.util.HashMap;

//the start of the spagetti code
//this is all super home grown

//depth first throught the ast as a for loop
//then goes wide when it see a block
public class Comp_SymbolTable {
    Comp_AST Comp_AST = new Comp_AST();
    static Comp_CodeGen Comp_CodeGen = new Comp_CodeGen();
    int Semantic_Num_Errors = 0;
    int Scope = 0;
    int printOut = 0;

    //what a item is that can be added
    public class item {
        String name;
        Boolean IsUsed;
        Boolean IsInitialized;

        item(String name) {
            this.name = name;
            this.IsUsed = false;
            this.IsInitialized = false;
        }
    }

    //a specific scope 
    public class Symbole_Node {
        int scope;
        HashMap<String, item> values;
        Comp_AST.Tree_Node parent;

        Symbole_Node(int scope) {
            this.scope = scope;
            this.values = new HashMap<>();
        }
    }

    //how to store all the blocks
    public class Symbol_Scope {
        ArrayList<Symbole_Node> Scopes;

        Symbol_Scope() {
            this.Scopes = new ArrayList<>();
        }
    }

    //global value to get hold all the blocks
    Symbol_Scope Blocks = new Symbol_Scope();
    Comp_AST.Tree_Node current;
    boolean scopeAdded = false;

    //this will do all the checking and stuff
    public int Start_Symbole_Table(Comp_AST.Tree_Node Abstract_Syntax_Tree) {
        //makes the first new scope
        Symbole_Node Values_At_Block = new Symbole_Node(Scope);
        Semantic_Num_Errors = 0;

        //goes through everything and start checking it all
        for (int i = 0; i < Abstract_Syntax_Tree.children.size(); i++) {
            if (Abstract_Syntax_Tree.children.get(i).name.equals("var_decl")) { //if there is a var decl do this
                //gets the variable as the value 
                item value = new item(Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                //then puts it in the scope that it is associated too
                Values_At_Block.values.put(Abstract_Syntax_Tree.children.get(i).children.get(1).name, value);
            } else if (Abstract_Syntax_Tree.children.get(i).name.equals("assignment_statment")) { //if there is a assignment statment
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.equals("+")){ //if its a plus (intop)
                    //grabs the type of the variable from the hashmap of the scope
                    String type1;

                    //telling what the type is
                    if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[0-9]?")){
                        type1 = "int";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.contains("\"")){
                        type1 = "string";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[a-z]?")){
                        type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("true|false")){
                        type1 = "boolean";
                    }else{
                        type1 = "";
                    }

                    //if it return "" thats means was not found
                    if (type1 ==  ""){ //error
                        Semantic_Num_Errors++;
                        System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(0).name  + " line num " + Abstract_Syntax_Tree.children.get(i).children.get(0).line_num + ".");
                    }

                    //sets up second type array list to hold all the possible types (more than one number)
                    String type2 = "";

                    type2 = intop_check(Abstract_Syntax_Tree.children.get(i).children.get(1), Values_At_Block);

                    if (!type1.equals(type2)) { //if error then cannot change the initiliazed
                        Semantic_Num_Errors++;
                        System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + " at " +
                        Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                    }
                }else{ //if it is not a plus one

                    //gets the string value of the first one (the variable)
                    String type1;

                    //telling what the type is
                    if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[0-9]?")){
                        type1 = "int";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.contains("\"")){
                        type1 = "string";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[a-z]?")){
                        type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("true|false")){
                        type1 = "boolean";
                    }else{
                        type1 = "";
                    }
                    
                    //if not found error returns "" if not found
                    if (type1 ==  ""){
                        Semantic_Num_Errors++;
                        System.out.println("Not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(0).name  + " line number " + Abstract_Syntax_Tree.children.get(i).children.get(0).line_num + ".");
                    }
                    
                    //this is the looking for type 2
                    String type2 = "";

                    //telling what the type is
                    if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[0-9]?")){
                        type2 = "int";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.contains("\"")){
                        type2 = "string";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[a-z]?")){
                        type2 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).name);
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("true|false")){
                        type2 = "boolean";
                    }else{
                        //logic to compare bool ops
                        type2 = compare_boolop(Abstract_Syntax_Tree.children.get(i), Values_At_Block);

                        if(type2.matches("int|string|boolean")){
                            type2 = "boolean";
                        }else{
                            type2 = "";
                        }

                    }

                    //if not found error returns "" if not found
                    if (type2 ==  ""){
                        Semantic_Num_Errors++;
                        System.out.println("Not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).name  + " line number " + Abstract_Syntax_Tree.children.get(i).children.get(1).line_num + ".");
                    }

                    if (!type1.equals(type2)) {//check if types match
                        if(type1 != ""){
                            Semantic_Num_Errors++;
                            System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).line_num + " at " +
                            Abstract_Syntax_Tree.children.get(i).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                        }
                    }else{//if they do match then make the intalized true
                        if (Values_At_Block.values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(0).name)) {
                            Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).IsInitialized = true;
                        } else { //if they match then change the initialized to true
                            int test = 0;
                            for(int s = Scope-1; s >= 0; s--){ //goes through the previous scopes
                                if(Blocks.Scopes.get(s).values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(0).name)){
                                    Symbole_Node temp = Blocks.Scopes.get(s);
                                    temp.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).IsInitialized = true;
                                    test = 1;
                                }
                            }

                            if(test == 0){ //back up error just in case
                                Semantic_Num_Errors++;
                                System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(0).name + " used in print statement not found.");
                            }
                        }
                    }
                }
                
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("if_statment")){ //handel the instance of a if statment
                //will check to make sure that the whole thing is the same
                String if_type = compare_boolop(Abstract_Syntax_Tree.children.get(i), Values_At_Block);

                //retrun "" if there was something not matching in the boolop
                if(if_type.equals("")){
                    Semantic_Num_Errors++;
                    System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).line_num + " at " +
                    Abstract_Syntax_Tree.children.get(i).place_num + ".");
                }else{

                }

                //handles the new scope for if
                if (Abstract_Syntax_Tree.children.get(i).children.get(Abstract_Syntax_Tree.children.get(i).children.size()-1).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(Abstract_Syntax_Tree.children.get(i).children.size()-1));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("while_statment")){//while statment condition
                //will check the whole thing making sure that they are all the same
                String if_type = compare_boolop(Abstract_Syntax_Tree.children.get(i), Values_At_Block);

                //if they miss match will return "" then know its a error
                if(if_type.equals("")){
                    Semantic_Num_Errors++;
                    System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).line_num + " at " +
                    Abstract_Syntax_Tree.children.get(i).place_num + ".");
                }else{

                }

                //handles the new scope after  a while
                if (Abstract_Syntax_Tree.children.get(i).children.get(Abstract_Syntax_Tree.children.get(i).children.size()-1).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(Abstract_Syntax_Tree.children.get(i).children.size()-1));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("print_statment")){//for the print statments
                //what is the variable of the print statment
                String printedVariable = Abstract_Syntax_Tree.children.get(i).children.get(1).name;

                if(printedVariable.matches("[a-z]?")){
                    String test = getVariableType(Values_At_Block, printedVariable);
    
                    if(test == ""){//if not found then error
                        Semantic_Num_Errors++;
                        System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(1).name + " line num " + Abstract_Syntax_Tree.children.get(i).children.get(1).line_num + " used in print statement not found.");
                    }
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("block")){ //if therer is a block will add it
                Scope++;
                Blocks.Scopes.add(Values_At_Block);
                Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i));
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("$")){//siginfies the end print out all scopes and clear the block for the next program
                for (int s = 0; s < Blocks.Scopes.size(); s++){
                    int scope_test = Blocks.Scopes.get(s).scope;
                    for(int o = s+1; o < Blocks.Scopes.size(); o++){
                        if(scope_test == Blocks.Scopes.get(o).scope){
                            Blocks.Scopes.remove(o);
                        }
                    }
                }

                if(Blocks.Scopes.size() == 0){
                    Blocks.Scopes.add(Values_At_Block);
                }

                printAllScopes();
                System.out.println("The number of errors in semantic anaylsis is " + Semantic_Num_Errors + " .");
                Blocks.Scopes.clear();
                Scope = 0;

                if(Semantic_Num_Errors > 0){

                }else{
                    //Comp_CodeGen.start_codegen(Abstract_Syntax_Tree, Blocks);
                }

                return Semantic_Num_Errors;
            }
        } 

        Blocks.Scopes.add(Values_At_Block);
        return Semantic_Num_Errors;
    }

    //function to get the value at the variable
    private String getVariableType(Symbole_Node Values_At_Block, String variableName) {
        Symbole_Node currentScope = Values_At_Block;
        
        //if in current scope then just return
        if(currentScope.values.containsKey(variableName)) {
            if(Values_At_Block.values.get(variableName).IsInitialized == false){
                System.out.println("Warning " + variableName + " has not been initlized.");
            }
            currentScope.values.get(variableName).IsUsed = true;
            return currentScope.values.get(variableName).name;
        }
        
        //will go back if needed
        for (int i = currentScope.scope-1; i >= 0; i--) {
            currentScope = Blocks.Scopes.get(i);
            if (currentScope.values.containsKey(variableName)) {
                if(Values_At_Block.values.get(variableName).IsInitialized == false){
                    System.out.println("Warning " + variableName + " has not been initlized.");
                }
                currentScope.values.get(variableName).IsUsed = true;
                return currentScope.values.get(variableName).name;
            }
        }
        
        //if nothing found then return ""
        return "";
    }

    public String compare_boolop(Comp_AST.Tree_Node Bool_Node, Symbole_Node Values_At_Block){//comparison for a whole boolop
        String bool_type = ""; //will hold the type
        ArrayList<String> types = new ArrayList<>(); //will hold all the types

        //will go through all the children of the bool node checking what type they are and adding them to the types array list
        for(int i = 0; i < Bool_Node.children.size(); i++){
            if(Bool_Node.children.get(i).name.matches("!=|==")){
                if(Bool_Node.children.get(i-1).name.equals(")")){

                }else{
                   //telling what the type is
                    if(Bool_Node.children.get(i-1).name.matches("[0-9]?")){
                        types.add("int");
                    }else if(Bool_Node.children.get(i-1).name.contains("\"")){
                        types.add("string");
                    }else if(Bool_Node.children.get(i-1).name.matches("[a-z]?")){
                        types.add(getVariableType(Values_At_Block, Bool_Node.children.get(i-1).name));
                        if(Values_At_Block.values.get(Bool_Node.children.get(i-1).name).IsInitialized == false){
                            System.out.println("Warning " + Bool_Node.children.get(i-1).name + " has not been initlized.");
                        }
                    }else if(Bool_Node.children.get(i-1).name.matches("true|false")){
                        types.add("boolean");
                    }
                }

                if(Bool_Node.children.get(i+1).name.equals("(")){
                    
                }else{
                    //telling what the type is
                    if(Bool_Node.children.get(i+1).name.matches("[0-9]?")){
                        types.add("int");
                    }else if(Bool_Node.children.get(i+1).name.contains("\"")){
                        types.add("string");
                    }else if(Bool_Node.children.get(i+1).name.matches("[a-z]?")){
                        types.add(getVariableType(Values_At_Block, Bool_Node.children.get(i+1).name));
                    }else if(Bool_Node.children.get(i+1).name.matches("true|false")){
                        types.add("boolean");
                    }
                }
            }
        }

        //will grab one type
        bool_type = types.get(0);

        //if the type is any different then error or ""
        for(int j = 0; j < types.size(); j++){
            if(!bool_type.equals(types.get(j))){
                bool_type = "";
            }
        }

        //return found type to the if or while check
        return bool_type;
    }

    //this will check for intops liek 1+2+3+4
    public String intop_check(Comp_AST.Tree_Node Intop_Node, Symbole_Node Values_At_Block){
        String int_type = ""; //will hold the type
        ArrayList<String> types = new ArrayList<>(); //will hold the types of all 

        //mainly used to check the variables since parse will handle the rest
        for(int i = 0; i < Intop_Node.children.size(); i++){
            if(Intop_Node.children.get(i).name.matches("[0-9]?")){
                types.add("int");
            }else if(Intop_Node.children.get(i).name.contains("\"")){
                types.add("string");
            }else if(Intop_Node.children.get(i).name.matches("[a-z]?")){
                types.add(getVariableType(Values_At_Block, Intop_Node.children.get(i).name));
                if(Values_At_Block.values.get(Intop_Node.children.get(i-1).name).IsInitialized == false){
                    System.out.println("Warning " + Intop_Node.children.get(i-1).name + " has not been initlized.");
                }
            }else if(Intop_Node.children.get(i).name.matches("true|false")){
                types.add("boolean");
            }else{
                types.add("");
            }
        }

        //grab one
        int_type = types.get(0);

        //make sure they are all the same type
        for(int j = 0; j < types.size(); j++){
            if(!int_type.equals(types.get(j))){
                int_type = "";
            }
        }

        //return the correct type
        return int_type;

    }

    //will print out all values at the scopes
    public void printAllScopes() {
        for (Symbole_Node scope : Blocks.Scopes) {
            //if there is nothing in the scope (varibales) then do not print out
            if(scope.values.size() != 0){
                System.out.println("Scope " + scope.scope + ":");
                for (String variableName : scope.values.keySet()) { //printing out everything
                    item currentItem = scope.values.get(variableName);
                    System.out.println("Variable Name: " + variableName + ", Type: " + currentItem.name + ", IsUsed: " + currentItem.IsUsed + ", IsInitialized: " + currentItem.IsInitialized);
                }
            }
        }
    }
}

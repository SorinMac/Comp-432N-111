import java.util.ArrayList;
import java.util.HashMap;

//get a == b working recusoion yet again through the blocks checking left and right side && b = true == (true == false)
//got to get the ast to make the right tree stuff then it should be good to go
//test cases
//labs

//the start of the spagetti code
//this is all super home grown

//depth first throught the ast as a for loop
//then goes wide when it see a block
public class Comp_SymbolTable {
    Comp_AST Comp_AST = new Comp_AST();
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
    public void Start_Symbole_Table(Comp_AST.Tree_Node Abstract_Syntax_Tree) {
        //makes the first new scope
        Symbole_Node Values_At_Block = new Symbole_Node(Scope);

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
                    if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[0-9]+")){
                        type1 = "int";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.contains("\"")){
                        type1 = "string";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[a-z]+")){
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
                    ArrayList<String> type2 = new ArrayList<>();

                    for(int q = 0; q < Abstract_Syntax_Tree.children.get(i).children.size(); q++){
                        //telling what the type is
                        if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(q).name.matches("[0-9]+")){
                            type2.add("int");
                        }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(q).name.contains("\"")){
                            type2.add("string");
                        }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(q).name.matches("[a-z]+")){
                            type2.add(getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name));
                        }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(q).name.matches("true|false")){
                            type2.add("boolean");
                        }else{
                            type2.add("");
                        }
                    }

                    for(int w = 0; w < type2.size(); w ++){//goes through all the types in type2 and checks them
                        //checks if the two types are the same if so then good if not then error
                        if (!type1.equals(type2.get(w))) { //if error then cannot change the initiliazed
                            if(type1 != ""){
                                Semantic_Num_Errors++;
                                System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + " at " +
                                Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                            }
                        }
                }
                    
                }else{ //if it is not a plus one

                    //gets the string value of the first one (the variable)
                    String type1;

                    //telling what the type is
                    if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[0-9]+")){
                        type1 = "int";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.contains("\"")){
                        type1 = "string";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(0).name.matches("[a-z]+")){
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
                    if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[0-9]+")){
                        type2 = "int";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.contains("\"")){
                        type2 = "string";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[a-z]+")){
                        type2 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("true|false")){
                        type2 = "boolean";
                    }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("!=|==")){
                        type2 = compare_boolop(Abstract_Syntax_Tree.children.get(i).children.get(1), Values_At_Block);

                        if(type2.matches("int|string|boolean")){
                            type2 = type1;
                        }else{
                            type2 = "";
                        }
                    }else{
                        type2 = "";
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
                            for(int s = Blocks.Scopes.size()-1; s >= 0; s--){ //goes through the previous scopes
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
                //the first type
                String type1 = "";
                
                //telling what the type is
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[0-9]+")){
                    type1 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.contains("\"")){
                    type1 = "string";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){
                    type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("true|false")){
                    type1 = "boolean";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("!=|==")){
                    type1 = compare_boolop(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0), Values_At_Block);

                    if(type1.matches("int|string|boolean")){
                        
                    }else{
                        type1 = "";
                    }
                }

                //error if the type is not identified
                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name  + " line num " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).line_num + ".");
                }

                //second type
                String type2 = "";

                //telling what the type is
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("[0-9]+")){
                    type2 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.contains("\"")){
                    type2 = "string";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("[a-z]+")){
                    type2 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("true|false")){
                    type2 = "boolean";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("!=|==")){
                    type2 = compare_boolop(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0), Values_At_Block);

                    if(type2.matches("int|string|boolean")){
                        type2 = type1;
                    }else{
                        type2 = "";
                    }
                }

                //if they do not equal error
                if (!type1.equals(type2)) {
                    if(type1 != ""){
                        Semantic_Num_Errors++;
                        System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + " at " +
                        Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                    }else{

                    }
                }else{//if they do equal block
                    //if it is just a number do not do anything
                    if(!Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){

                    }else{//if a varaible
                        if (Values_At_Block.values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)) { //set us to true (current scope)
                            Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                        } else { //if not current scope then go back until you find it
                            int test = 0;
                            for(int s = Blocks.Scopes.size()-1; s == 0; s--){
                                if(Blocks.Scopes.get(s).values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)){
                                    Symbole_Node temp = Blocks.Scopes.get(s);
                                    temp.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                                    test = 1;
                                }
                            }
                            //if not found then error
                            if(test == 0){
                                Semantic_Num_Errors++;
                                System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + " used in print statement not found.");
                            }
                        }
                    }
                }

                //since there is always a scope after then just go to the new scope
                if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(2).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(2));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("while_statment")){//while statment condition
                //first type
                String type1 = "";
                
                //telling what the type is
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[0-9]+")){
                    type1 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.contains("\"")){
                    type1 = "string";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){
                    type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("true|false")){
                    type1 = "boolean";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("!=|==")){
                    type1 = compare_boolop(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0), Values_At_Block);

                    if(type1.matches("int|string|boolean")){
                        
                    }else{
                        type1 = "";
                    }
                }

                //if the first type is nothing error
                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name  + " line num " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).line_num + ".");
                }

                //second type
                String type2 = "";

                //telling what the type is
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("[0-9]+")){
                    type2 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.contains("\"")){
                    type2 = "string";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("[a-z]+")){
                    type2 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("true|false")){
                    type2 = "boolean";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("!=|==")){
                    type2 = compare_boolop(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0), Values_At_Block);

                    if(type2.matches("int|string|boolean")){
                        type2 = type1;
                    }else{
                        type2 = "";
                    }
                }

                //if not equal error
                if (!type1.equals(type2)) {
                    if(type1 != ""){
                        Semantic_Num_Errors++;
                        System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).line_num + " at " +
                        Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                    }else{
                        
                    }
                }else{//if the types do match
                    if(!Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){//if its a number vlaue do nothing

                    }else{//if it a variable
                        if (Values_At_Block.values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)) {//in current scope do not go back
                            Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                        } else {//go backwords through the scopes to see if the value is there
                            int test = 0;
                            for(int s = Blocks.Scopes.size()-1; s == 0; s--){
                                if(Blocks.Scopes.get(s).values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)){
                                    Symbole_Node temp = Blocks.Scopes.get(s);
                                    temp.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                                    test = 1;
                                }
                            }

                            if(test == 0){ //if not there then error 
                                Semantic_Num_Errors++;
                                System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + " used in print statement not found.");
                            }
                        }
                    }
                }

                //since there is always a scope after then just go to the new scope
                if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(2).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(2));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("print_statment")){//for the print statments
                //what is the variable of the print statment
                String printedVariable = Abstract_Syntax_Tree.children.get(i).children.get(1).name;

                if(printedVariable.matches("[a-z]+")){
                    if (Values_At_Block.values.containsKey(printedVariable)) {//if its on the current scope then good
                        Values_At_Block.values.get(printedVariable).IsUsed = true;
                    } else {//if  not go through the scopes in order to find it
                        int test = 0;
                        for(int s = Blocks.Scopes.size()-1; s >= 0; s--){
                            if(Blocks.Scopes.get(s).values.containsKey(printedVariable)){
                                Symbole_Node temp = Blocks.Scopes.get(s);
                                temp.values.get(printedVariable).IsUsed = true;
                                test = 1;
                            }
                        }
    
                        if(test == 0){//if not found then error
                            Semantic_Num_Errors++;
                            System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(1).name + " line num " + Abstract_Syntax_Tree.children.get(i).children.get(1).line_num + " used in print statement not found.");
                        }
                    }
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("block")){
                Scope++;
                Blocks.Scopes.add(Values_At_Block);
                Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i));
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("$")){//siginfies the end print out all scopes and clear the block for the next program
                if(Scope == 0){
                    Blocks.Scopes.add(Values_At_Block); 
                    printAllScopes();
                    Blocks.Scopes.clear();
                }else{
                    printAllScopes();
                    Blocks.Scopes.clear();
                }
            }
        }  
    }

    //function to get the value at the variable
    private String getVariableType(Symbole_Node Values_At_Block, String variableName) {
        Symbole_Node currentScope = Values_At_Block;
        
        //if in current scope then just return
        if(currentScope.values.containsKey(variableName)) {
            return currentScope.values.get(variableName).name;
        }
        
        //will go back if needed
        for (int i = currentScope.scope - 1; i >= 0; i--) {
            currentScope = Blocks.Scopes.get(i);
            if (currentScope.values.containsKey(variableName)) {
                return currentScope.values.get(variableName).name;
            }
        }
        
        //if nothing found then return ""
        return "";
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

    public String compare_boolop(Comp_AST.Tree_Node Bool_Node, Symbole_Node Values_At_Block){
        String value1 = Bool_Node.children.get(0).name;
        String value2 = Bool_Node.children.get(1).name;

        if(Bool_Node.children.get(1).name.matches("!=|==")){
            value2 = compare_boolop(Bool_Node.children.get(1), Values_At_Block);
        }

        String bool_type1 = "";
        String bool_type2 = "";

        if(value1.matches("[0-9]+")){
            bool_type1 = "int";
        }else if(value1.matches("\"")){
            bool_type1 = "string";
        }else if(value1.matches("[a-z]+")){
            bool_type1 = getVariableType(Values_At_Block, value1);
        }else if(value1.matches("true|false")){
            bool_type1 = "boolean";
        }else{
            bool_type1 = "";
        }

        if(value2.matches("[0-9]+")){
            bool_type2 = "int";
        }else if(value2.matches("\"")){
            bool_type2 = "string";
        }else if(value2.matches("[a-z]?")){
            bool_type2 = getVariableType(Values_At_Block, value1);
        }else if(value2.matches("true|false")){
            bool_type2 = "boolean";
        }else if(value2.matches("int|string|boolean")){
            bool_type2 = value2;
        }else{
            bool_type2 = "";
        }

        if (!bool_type1.equals(bool_type2)) {
            bool_type1 = "";
        }

        return bool_type1;
    }
}

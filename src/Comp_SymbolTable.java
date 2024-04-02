import java.util.ArrayList;
import java.util.HashMap;

//fix ast

//ie
/*
 {
    int a
    boolean b
    {
        string c
        a = 5
        b = true
        c = "inta"
        print(c)
    }
    print(b)
    print(a)
}$ 

becuse the block is just there on its own and there is no real pattern for it to fallow in the recusion to get it set up right
*/

//the start of the spagetti code
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

                if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.equals("+")){ //if its a plus
                    //grabs the type of the variable from the hashmap of the scope
                    String type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name);

                    //if it return "" thats means was not found
                    if (type1 ==  ""){ //error
                        Semantic_Num_Errors++;
                        System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name  + " " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + ".");
                    }

                    //sets up second type
                    String type2 = "";

                    //based of what was added as the thing after the + 
                    if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[0-9]+")) {
                        type2 = "int";
                    } else if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("true|false")) {
                        type2 = "boolean";
                    } else {
                        type2 = "string";
                    }

                    //checks if the two types are the same if so then good if not then error
                    if (!type1.equals(type2)) { //if error then cannot change the initiliazed
                        if(type1 != ""){
                            Semantic_Num_Errors++;
                            System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + " at " +
                            Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                        }
                    }else{ //if no error then can chaneg the inistliazed
                        if (Values_At_Block.values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(0).name)) { //if current scope has the value then change
                            Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).IsInitialized = true;
                        } else { //go through previous scopes looking for the variable and change when you find it
                            int test = 0;
                            for(int s = Blocks.Scopes.size()-1; s >= 0; s--){
                                if(Blocks.Scopes.get(s).values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(0).name)){
                                    Symbole_Node temp = Blocks.Scopes.get(s);
                                    temp.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).IsInitialized = true;
                                    test = 1;
                                }
                            }
                            
                            //back up error if not found
                            if(test == 0){
                                Semantic_Num_Errors++;
                                System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(0).name + " used in print statement not found.");
                            }
                        }
                    }
                    
                }else{ //if it is not a plus one

                    //gets the string value of the first one (the variable)
                    String type1;
                    type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                    
                    //if not found error returns "" if not found
                    if (type1 ==  ""){
                        Semantic_Num_Errors++;
                        System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name  + " " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + ".");
                    }
                    
                    //this is the looking for type 2
                    String type2 = "";

                    //basaed on what is on the other side of equal will tell what sign
                    if (Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[0-9]+")) {
                        type2 = "int";
                    } else if (Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("true|false")) {
                        type2 = "boolean";
                    } else {
                        type2 = "string";
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
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("if_statment")){
                String type1 = "";
                
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[0-9]+")){
                    type1 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("true|false")){
                    type1 = "boolean";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){
                    type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);
                }else{
                    type1 = "string";
                }

                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name  + " " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + ".");
                }

                String type2 = "";

                if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("[0-9]+")) {
                    type2 = "int";
                } else if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("true|false")) {
                    type2 = "boolean";
                } else {
                    type2 = "string";
                }

                if (!type1.equals(type2)) {
                    if(type1 != ""){
                        Semantic_Num_Errors++;
                        System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).line_num + " at " +
                        Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                    }else{

                    }
                }else{
                    if(!Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){

                    }else{
                        if (Values_At_Block.values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)) {
                            Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                        } else {
                            int test = 0;
                            for(int s = Blocks.Scopes.size()-1; s == 0; s--){
                                if(Blocks.Scopes.get(s).values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)){
                                    Symbole_Node temp = Blocks.Scopes.get(s);
                                    temp.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                                    test = 1;
                                }
                            }
                            if(test == 0){
                                Semantic_Num_Errors++;
                                System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + " used in print statement not found.");
                            }
                        }
                    }
                }

                if (Abstract_Syntax_Tree.children.get(i).children.get(3).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(2));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("while_statment")){
                String type1 = "";
                
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[0-9]+")){
                    type1 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("true|false")){
                    type1 = "boolean";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){
                    type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);
                }else{
                    type1 = "string";
                }

                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name  + " " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + ".");
                }

                String type2 = "";

                if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("[0-9]+")) {
                    type2 = "int";
                } else if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name.matches("true|false")) {
                    type2 = "boolean";
                } else {
                    type2 = "string";
                }

                if (!type1.equals(type2)) {
                    if(type1 != ""){
                        Semantic_Num_Errors++;
                        System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).line_num + " at " +
                        Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                    }else{
                        
                    }
                }else{
                    if(!Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[a-z]+")){

                    }else{
                        if (Values_At_Block.values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)) {
                            Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                        } else {
                            int test = 0;
                            for(int s = Blocks.Scopes.size()-1; s == 0; s--){
                                if(Blocks.Scopes.get(s).values.containsKey(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name)){
                                    Symbole_Node temp = Blocks.Scopes.get(s);
                                    temp.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
                                    test = 1;
                                }
                            }

                            if(test == 0){
                                Semantic_Num_Errors++;
                                System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + " used in print statement not found.");
                            }
                        }
                    }
                }

                if (Abstract_Syntax_Tree.children.get(i).children.get(2).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(2));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("print_statment")){
                String printedVariable = Abstract_Syntax_Tree.children.get(i).children.get(1).name;

                if (Values_At_Block.values.containsKey(printedVariable)) {
                    Values_At_Block.values.get(printedVariable).IsUsed = true;
                } else {
                    int test = 0;
                    for(int s = Blocks.Scopes.size()-1; s >= 0; s--){
                        if(Blocks.Scopes.get(s).values.containsKey(printedVariable)){
                            Symbole_Node temp = Blocks.Scopes.get(s);
                            temp.values.get(printedVariable).IsUsed = true;
                            test = 1;
                        }
                    }

                    if(test == 0){
                        Semantic_Num_Errors++;
                        System.out.println("Variable " + Abstract_Syntax_Tree.children.get(i).children.get(0).name + " used in print statement not found.");
                    }
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("$")){
                printAllScopes();
                Blocks.Scopes.clear();
            }
        }

        Blocks.Scopes.add(Values_At_Block);
        
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
                    System.out.println("Variable Name: " + variableName + ", IsUsed: " + currentItem.IsUsed + ", IsInitialized: " + currentItem.IsInitialized);
                }
            }
        }
    }
}

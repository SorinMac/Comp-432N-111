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

//print out


public class Comp_SymbolTable {
    Comp_AST Comp_AST = new Comp_AST();
    int Semantic_Num_Errors = 0;
    int Scope = 0;
    int printOut = 0;

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

    public class Symbole_Node {
        int scope;
        HashMap<String, item> values;
        Comp_AST.Tree_Node parent;

        Symbole_Node(int scope) {
            this.scope = scope;
            this.values = new HashMap<>();
        }
    }

    public class Symbol_Scope {
        ArrayList<Symbole_Node> Scopes;

        Symbol_Scope() {
            this.Scopes = new ArrayList<>();
        }
    }

    Symbol_Scope Blocks = new Symbol_Scope();
    Comp_AST.Tree_Node current;

    public void Start_Symbole_Table(Comp_AST.Tree_Node Abstract_Syntax_Tree) {
        Symbole_Node Values_At_Block = new Symbole_Node(Scope);

        for (int i = 0; i < Abstract_Syntax_Tree.children.size(); i++) {
            if (Abstract_Syntax_Tree.children.get(i).name.equals("var_decl")) {
                item value = new item(Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                Values_At_Block.values.put(Abstract_Syntax_Tree.children.get(i).children.get(1).name, value);
                
            } else if (Abstract_Syntax_Tree.children.get(i).name.equals("assignment_statment")) {
                if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.equals("+")){
                    String type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name);

                    if (type1 ==  ""){
                        Semantic_Num_Errors++;
                        System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name  + " " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + ".");
                    }

                    String type2 = "";

                    if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("[0-9]+")) {
                        type2 = "int";
                    } else if (Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name.matches("true|false")) {
                        type2 = "boolean";
                    } else {
                        type2 = "string";
                    }

                    if (!type1.equals(type2)) {
                        if(type1 != ""){
                            Semantic_Num_Errors++;
                            System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + " at " +
                            Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                        }
                    }else{
                        Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name).IsInitialized = true;
                    }
                    
                }else{
                    String type1;
                    type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                    
                    if (type1 ==  ""){
                        Semantic_Num_Errors++;
                        System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).name  + " " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(1).line_num + ".");
                    }

                    String type2 = "";

                    if (Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[0-9]+")) {
                        type2 = "int";
                    } else if (Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("true|false")) {
                        type2 = "boolean";
                    } else {
                        type2 = "string";
                    }

                    if (!type1.equals(type2)) {
                        if(type1 != ""){
                            Semantic_Num_Errors++;
                            System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).line_num + " at " +
                                    Abstract_Syntax_Tree.children.get(i).children.get(1).place_num + " declared " + type1 + " but comparing " + type2 + ".");
                        }
                    }else{
                        Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).IsInitialized = true;
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
                        Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;

                        if (Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsInitialized == false && Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed == true) {
                            System.out.println("Used but not Initialized " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + ".");
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
                        Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;

                        if (Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsInitialized == false && Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed == true) {
                            System.out.println("Used but not Initialized " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + ".");
                        }
                    }
                }

                if (Abstract_Syntax_Tree.children.get(i).children.get(2).name.equals("block")) {
                    Scope++;
                    Blocks.Scopes.add(Values_At_Block);
                    Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i).children.get(2));
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("print_statment")){
                String printedVariable = Abstract_Syntax_Tree.children.get(i).children.get(0).name;
                if (Values_At_Block.values.containsKey(printedVariable)) {
                    Values_At_Block.values.get(printedVariable).IsUsed = true;
                } else {
                    Semantic_Num_Errors++;
                    System.out.println("Variable " + printedVariable + " used in print statement not found.");
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("$")){
                printAllScopes();
            }
        }

        Blocks.Scopes.add(Values_At_Block);
        
    }

    //can not go backwords yet since the block is not made
    private String getVariableType(Symbole_Node Values_At_Block, String variableName) {
        Symbole_Node currentScope = Values_At_Block;
    
        // Check the current scope
        if(currentScope.values.containsKey(variableName)) {
            return currentScope.values.get(variableName).name;
        }
        
        // Traverse through parent scopes
        for (int i = currentScope.scope - 1; i >= 0; i--) {
            currentScope = Blocks.Scopes.get(i);
            if (currentScope.values.containsKey(variableName)) {
                return currentScope.values.get(variableName).name;
            }
        }
    
        // Variable not found in any scope
        return "";
    }

    public void printAllScopes() {
        for (Symbole_Node scope : Blocks.Scopes) {
            System.out.println("Scope " + scope.scope + ":");
            for (String variableName : scope.values.keySet()) {
                item currentItem = scope.values.get(variableName);
                System.out.println("Variable Name: " + variableName + ", IsUsed: " + currentItem.IsUsed + ", IsInitialized: " + currentItem.IsInitialized);
            }
        }
    }
}

import java.util.ArrayList;
import java.util.HashMap;

//need to get the scope stuff back up when needed (fix it up)
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
*/

//waring for unused variables at the end
//print out

//notes
//at the end if back to block and not the parent
//doing a depth first in order search of the AST from the root of the node
//make a array list for the scope and add other scopes after it

public class Comp_SymbolTable {
    Comp_AST Comp_AST = new Comp_AST();
    int Semantic_Num_Errors = 0;
    int Scope = 0;

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
        //could we use a array list so that we have all the scopes there and the scope would just be the one you are currenntly on until 0 and just go back
        //have to do it all in one shot
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

                //testing if things are being added
                System.out.println(Values_At_Block.scope);
                System.out.println(Abstract_Syntax_Tree.children.get(i).children.get(1).name + " " + value.name);

            } else if (Abstract_Syntax_Tree.children.get(i).name.equals("assignment_statment")) {
                String type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(0).name);

                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(0).name + ".");
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
                    }else{
                        
                    }
                }else{
                    Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).IsInitialized = true;
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("if_statment")){
                String type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);

                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + ".");
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
                    Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;

                    if (Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsInitialized == false && Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed == true) {
                        System.out.println("Used but not Initialized " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + ".");
                    }
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("while_statment")){
                String type1 = getVariableType(Values_At_Block, Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name);

                if (type1 ==  ""){
                    Semantic_Num_Errors++;
                    System.out.println("Variable not found error variable: " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name  + ".");
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
                    Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;

                    if (Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsInitialized == false && Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed == true) {
                        System.out.println("Used but not Initialized " + Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name + ".");
                    }
                }
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("print_statment")){
                Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(1).children.get(0).name).IsUsed = true;
            }

            //find a way to check for the end without having the end block there
            //needs to go back 
            if (Abstract_Syntax_Tree.children.get(i).name.equals("block")) {
                Scope++;
                Blocks.Scopes.add(Values_At_Block);
                Start_Symbole_Table(Abstract_Syntax_Tree.children.get(i));
            }
        }
    }

    //goes back to early 
    //first needs to check the current scope then go through the for loop to go backwords and shit
    private String getVariableType(Symbole_Node Values_At_Block, String variableName) {
        
        if(Values_At_Block.values.containsKey(variableName)){
            return Values_At_Block.values.get(variableName).name;
        }else{
            if(Values_At_Block.scope == 0){
                Symbole_Node currentScope = Values_At_Block;
                if (currentScope.values.containsKey(variableName)) {
                    return currentScope.values.get(variableName).name;
                }
            }else{
                for (int i = Values_At_Block.scope; i >= 0; i--) {
                    Symbole_Node currentScope = Blocks.Scopes.get(i);
                    if (currentScope.values.containsKey(variableName)) {
                        return currentScope.values.get(variableName).name;
                    }
                }
            }
        }

        return "";
    }
}

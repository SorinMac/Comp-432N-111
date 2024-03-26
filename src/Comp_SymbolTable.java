import java.util.HashMap;
import java.util.Map;


//need to add the rest of the logic for the print, while, if, boolop, addition
//need to get the scope stuff down

public class Comp_SymbolTable {
    Comp_AST Comp_AST = new Comp_AST();
    int Semantic_Num_Errors = 0;
    int Scope = 0;

    public class Symbole_Node{
        int scope;
        HashMap<String, String> values;
        Comp_AST.Tree_Node parent;

        Symbole_Node(int scope){
            this.scope = scope;
            this.values = new HashMap<>();
        }
    }

    public class Symbole_Scope{
        Symbole_Node parent;
        Symbole_Node child;

        Symbole_Scope(){
            this.parent = null;
            this.child = null;
        }
    }

    public void Start_Symbole_Table(Comp_AST.AST Abstract_Syntax_Tree){
        Symbole_Scope Block  = new Symbole_Scope();
        Symbole_Node Values = new Symbole_Node(Scope);

        if(Abstract_Syntax_Tree.root.children.size() == 0){
            //nothing in the AST
        }else{
            for(int i = 0; i < Abstract_Syntax_Tree.root.children.size(); i++){
                if(Abstract_Syntax_Tree.root.children.get(i).name.equals("var_decl")){
                    Values.values.put(Abstract_Syntax_Tree.root.children.get(i).children.get(1).name, Abstract_Syntax_Tree.root.children.get(i).children.get(0).name);
                }else if(Abstract_Syntax_Tree.root.children.get(i).name.equals("assignment_statment")){
                    String type1 = Values.values.get(Abstract_Syntax_Tree.root.children.get(i).children.get(0).name);
                    String type2 = "";

                    if(Abstract_Syntax_Tree.root.children.get(i).children.get(1).name.matches("[0-9]+")){
                        type2 = "int";
                    }else if(Abstract_Syntax_Tree.root.children.get(i).children.get(1).name.matches("true|false")){
                        type2 = "boolean";
                    }else{
                        type2 = "string";
                    }

                    if(!type1.equals(type2)){
                        Semantic_Num_Errors++;
                        System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.root.children.get(i).children.get(1).line_num + " at " + Abstract_Syntax_Tree.root.children.get(i).children.get(1).place_num + " delcared " + type1 + " but assigning " + type2 + ".");
                    }
                }
            }

            for(Map.Entry<String, String> entry: Values.values.entrySet()){
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
            

            Block.parent = Values;
            Scope++;
        }
        //as we go through the AST add values as necessary
        //when you see a new block make a new Symbole_Scope
            //first add one to scope
            //then start adding the values
    }
}

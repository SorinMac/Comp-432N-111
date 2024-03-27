import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//need to add the rest of the logic for the print, while, if, boolop, addition
//need to get the scope stuff down

public class Comp_SymbolTable {
    Comp_AST Comp_AST = new Comp_AST();
    int Semantic_Num_Errors = 0;
    int Scope = 0;

    public class item{
        String name;
        Boolean IsUsed;
        Boolean IsInitalized;

        item(String name){
            this.name = name;
            this.IsUsed = false;
            this.IsInitalized = false;
        }
    }

    public class Symbole_Node{
        int scope;
        HashMap<String, item> values;
        Comp_AST.Tree_Node parent;

        Symbole_Node(int scope){
            this.scope = scope;
            this.values = new HashMap<>();
        }
    }

    public class Symbol_Scope {
        //could we use a array list so that we have all the scopes there and the scope would just be the one you are currenntly on until 0 and just go back
        ArrayList<Symbole_Node> Scope;

        Symbol_Scope(){
            this.Scope = new ArrayList<>();
        }
    }

    Symbol_Scope Blocks = new Symbol_Scope();

    public void Start_Symbole_Table(Comp_AST.Tree_Node Abstract_Syntax_Tree){
        int children_place = 0;
        Symbole_Node Values_At_Block = new Symbole_Node(Scope);

        for(int i = 0; i < Abstract_Syntax_Tree.children.size(); i++){
            if(Abstract_Syntax_Tree.children.get(i).name.equals("var_decl")){
                item value = new item(Abstract_Syntax_Tree.children.get(i).children.get(0).name);
                Values_At_Block.values.put(Abstract_Syntax_Tree.children.get(i).children.get(1).name, value);
            }else if(Abstract_Syntax_Tree.children.get(i).name.equals("assignment_statment")){
                String type1 = Values_At_Block.values.get(Abstract_Syntax_Tree.children.get(i).children.get(0).name).name;
                String type2 = "";

                if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("[0-9]+")){
                    type2 = "int";
                }else if(Abstract_Syntax_Tree.children.get(i).children.get(1).name.matches("true|false")){
                    type2 = "boolean";
                }else{
                    type2 = "string";
                }

                if(!type1.equals(type2)){
                    Semantic_Num_Errors++;
                    System.out.println("Type mis-match error at " + Abstract_Syntax_Tree.children.get(i).children.get(1).line_num + " at " + 
                    Abstract_Syntax_Tree.children.get(i).children.get(1).place_num + " delcared " + type1 + " but assigning " + type2 + ".");
                }
            }
            
            //find a way to check for the end without having the end block there
            if(Abstract_Syntax_Tree.children.get(i).name.equals("block") || Abstract_Syntax_Tree.children.size() == i){

                for (Map.Entry<String, item> entry : Values_At_Block.values.entrySet()) {
                    String key = entry.getKey();
                    item value = entry.getValue();
                    System.out.println("Key: " + key + ", Value: " + value.name);
                }

                Scope++;
                children_place++;
                Blocks.Scope.add(Values_At_Block);

                Start_Symbole_Table(Abstract_Syntax_Tree.children.get(children_place));


                
            }
        }
    }
}
//things to do 
//only works one line at a time (fix this for the Wheatley Line 50)
//rest of logic for AST

import java.util.ArrayList;
import java.util.List;

public class Comp_AST {
    Comp_Lexer Comp_Lexer = new Comp_Lexer();

    public class AST_Node{
        String item;
        int line_num;
        int line_place;

        AST_Node(String item, int line_num, int line_place){
            this.item = item;
            this.line_num = line_num;
            this.line_place = line_place;
        }

    }

    public class AST {
        AST_Node root;
        ArrayList<AST_Node> objects;

        AST(){
            this.root = null;
            this.objects = new ArrayList<>();
        }
        
    }

    public void Start_AST_Build(List<Comp_Lexer.TokenBuilder> Token_List){

        AST Abstract_Syntax_Tree = new AST();

        for(int i = 0; i < Token_List.size(); i++){
            if (Token_List.get(i).unknown_item.equals("{") && Abstract_Syntax_Tree.root == null){
                AST_Node node = new AST_Node("Block", Token_List.get(i).line_num, Token_List.get(i).place_num);
                Abstract_Syntax_Tree.root = node;
            }else{
                //this will be the logic to handle what else needs to be added
                if(Token_List.get(i).unknown_item.equals("{")){
                    AST_Node node = new AST_Node("Block", Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(node);
                }else if(Token_List.get(i).unknown_item.equals("}")){
                    AST_Node node = new AST_Node("End_Block", Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(node);
                }else if(Token_List.get(i).unknown_item.equals("=")){
                    AST_Node temp_node0 = new AST_Node("Assignment", Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node0);
                    AST_Node temp_node1 = new AST_Node(Token_List.get(i-1).unknown_item, Token_List.get(i-1).line_num, Token_List.get(i-1).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node1);
                    AST_Node temp_node2 = new AST_Node(Token_List.get(i+1).unknown_item, Token_List.get(i+1).line_num, Token_List.get(i+1).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node2);
                }else if(Token_List.get(i).unknown_item.equals("string")){
                    AST_Node temp_node0 = new AST_Node("Variable_Decleration", Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node0);
                    AST_Node temp_node1 = new AST_Node(Token_List.get(i).unknown_item, Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node1);
                    AST_Node temp_node2 = new AST_Node(Token_List.get(i+1).unknown_item, Token_List.get(i+1).line_num, Token_List.get(i+1).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node2);
                }else if(Token_List.get(i).unknown_item.equals("int")){
                    AST_Node temp_node0 = new AST_Node("Variable_Decleration", Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node0);
                    AST_Node temp_node1 = new AST_Node(Token_List.get(i).unknown_item, Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node1);
                    AST_Node temp_node2 = new AST_Node(Token_List.get(i+1).unknown_item, Token_List.get(i+1).line_num, Token_List.get(i+1).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node2);
                }else if(Token_List.get(i).unknown_item.equals("boolean")){
                    AST_Node temp_node0 = new AST_Node("Variable_Decleration", Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node0);
                    AST_Node temp_node1 = new AST_Node(Token_List.get(i).unknown_item, Token_List.get(i).line_num, Token_List.get(i).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node1);
                    AST_Node temp_node2 = new AST_Node(Token_List.get(i+1).unknown_item, Token_List.get(i+1).line_num, Token_List.get(i+1).place_num);
                    Abstract_Syntax_Tree.objects.add(temp_node2);
                }
            }
        }
    }
}

/*else if(Token_List.get(i).unknown_item.equals("$")){
    AST_Node temp_node0 = new AST_Node("End_Of_Program", Token_List.get(i).line_num, Token_List.get(i).place_num);
    Abstract_Syntax_Tree.objects.add(temp_node0);
}*/

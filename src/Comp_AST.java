import java.util.ArrayList;
import java.util.List;

//after the first line the other stuff it not being added as children of the first block
//still not making assigning to the right block very annouying bugg
//over all is getting the right data for everything else so other than parent child should be working fine

public class Comp_AST {
    //lots of global values that will be explained later in the program
    Comp_Lexer Comp_Lexer = new Comp_Lexer();
    static Comp_Lexer.TokenBuilder current_Token;
    static List<Comp_Lexer.TokenBuilder> AST_Token_List;
    static int token_place = 0;
    static int debugg_mode_token = 0;
    static int debugg_mode_function = 0;
    static int Program_Num = 0;


    //class for the nodes of the CST
    public class Tree_Node{
        String name;
        Tree_Node parent;
        ArrayList<Tree_Node> children;

        Tree_Node(){
            this.name = "";
            this.parent = null;
            this.children = new ArrayList<>();
        }

    }

    //class for the actual CST and the root and current all that stuff
    public class AST{
        Tree_Node root;
        Tree_Node current;

        AST(){
            this.root = null;
            this.current = null;
        }

        //how we add node taken from https://www.labouseur.com/
        public void addNode(String kind, String label){
            //creates a temp node to be used for placment on the cst
            Tree_Node Temp_Node = new Tree_Node();
            Temp_Node.name = label;

            //if this is the first run then we just make it the root
            if(this.root == null){
                root = Temp_Node;
            //else we have to add it so that we can account for it 
            }else {
                //the parent of this will be the one before or the current current ಠ_ಠ
                Temp_Node.parent = current;
                //then the child
                Temp_Node.parent.children.add(Temp_Node);
            }

            //if it is not a leaf node then that means we are still traversing down so we make the new current what the temp
            //node was
            if (!kind.equals("leaf")){
                current = Temp_Node;
            }
        }

        //this will be used to go back up the tree
        public void end_all_children(){
            if(current.parent != null){
                current = current.parent;
            }
        }

        //this is to give the output of the tree
        public void grow(Tree_Node Node, int depth){
            //default 
            String output =  "";

            //will make the first set of dashes
            for(int i = 0; i < depth; i++){
                output += "-";
            }

            //counting anything in child as a leaf node
            //should be only things that are leaf
            //how to check for that ??
            if(Node.children.size() == 0){
                output += "[" + Node.name + "]";
                output += "\n";
                System.out.println(output);
            }else{
                //else its a branch so just output it
                output += "<" + Node.name + "> \n";
                System.out.println(output);
                for(int i = 0; i < Node.children.size(); i++){
                    grow(Node.children.get(i), depth + 1);
                }
            }

        }
        
    }

    //decleration of the CST that will be used
    static AST Abstract_Syntax_Tree;


    public void AST_Start(List<Comp_Lexer.TokenBuilder> Token_List){
        //some default values
        token_place = 0;
        Abstract_Syntax_Tree = new AST();
        AST_Token_List = Token_List;
        Program_Num++;
        current_Token = Token_List.get(token_place);

        AST_Program();
    }

    static void AST_Program(){
        //then calls all the approeratie functions needed for the LL(1) based on the BNF
        AST_Block();
        Abstract_Syntax_Tree.addNode("leaf", "$");
        //if it gets to here thats the end of the current program so check for errors and print out tree
        System.out.println("AST for Program " + Program_Num + " Done");
        Abstract_Syntax_Tree.grow(Abstract_Syntax_Tree.root, 0);

        //go back 
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Block(){
        //add the node when it is made
        Abstract_Syntax_Tree.addNode("root", "block");
        
        //reset of things that are needed in order to do the rest of the work
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        AST_Statement_List();
        token_place++;
        if(token_place == AST_Token_List.size()){
            token_place--;
        }
        current_Token = AST_Token_List.get(token_place);
        //gpo back
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Statement_List(){
       
        //this will check to see what procedure the ASTr should take
        if(current_Token.unknown_item.equals("print") || current_Token.unknown_item.matches("[a-z]") 
            || current_Token.unknown_item.matches("int|string|boolean") || current_Token.unknown_item.equals("while") 
            || current_Token.unknown_item.equals("if") || current_Token.unknown_item.equals("{")){
            AST_Statement();
            AST_Statement_List();
        }else{
            // it’s a ɛ (empty)
        }
        //go back
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Statement(){
        
        //this is more checking to see what kind of statment that it is
        //so that the ASTr cna figure out where to go with ll1
        if(current_Token.unknown_item.equals("print")){
            AST_Print_Statment();
        }else if(current_Token.unknown_item.matches("[a-z]")){
            AST_Assignment_Statment();
        }else if(current_Token.unknown_item.matches("int|string|boolean")){
            AST_Var_Decl();
        }else if(current_Token.unknown_item.equals("while")){
            AST_While_Statment();
        }else if(current_Token.unknown_item.equals("if")){
            AST_If_Statment();
        }else if(current_Token.unknown_item.equals("{")){
            AST_Block();
        }
        //go back
        //Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Print_Statment(){
        //add the node
        Abstract_Syntax_Tree.addNode("branch", "print_statment");
        
        //the other steps that need to be taken based on the bnf
        Abstract_Syntax_Tree.addNode("leaf", "print");
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        AST_Expr();
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        //go back
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Assignment_Statment(){
        //adds the node 
        Abstract_Syntax_Tree.addNode("branch", "assignment_statment");
        
        //rest of the stuff that needs to be checked
        Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place).unknown_item);
        token_place++;
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        AST_Expr();
        //go back
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Var_Decl(){    
        //adds the node 
        Abstract_Syntax_Tree.addNode("branch", "var_decl");

        //rest of the stuff to check
        Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place).unknown_item);
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place).unknown_item);
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        //go back
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_While_Statment(){
        //add to the node
        Abstract_Syntax_Tree.addNode("branch", "while_statment");
        
        //rest of the stuff that needs to be checked
        Abstract_Syntax_Tree.addNode("leaf", "while");
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        AST_Boolean_Expr();
        AST_Block();
        //go back 
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_If_Statment(){
        //add the node
        Abstract_Syntax_Tree.addNode("branch", "if_statment");
        
        //rest of the stuff to check
        Abstract_Syntax_Tree.addNode("leaf", "if");
        token_place++;
        current_Token = AST_Token_List.get(token_place);
        AST_Boolean_Expr();
        AST_Block();
        //gpo back
        Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Expr(){
        
        //check whcih expr it is from the BNF
        if(current_Token.unknown_item.matches("[0-9]+")){
            AST_Int_Expr();
        }else if(current_Token.unknown_item.equals("\"")){
            AST_String_Expr();
        }else if(current_Token.unknown_item.equals("(") || current_Token.unknown_item.equals("true") || current_Token.unknown_item.equals("false")){
            AST_Boolean_Expr();
        }else if(current_Token.unknown_item.matches("[a-z]")){
            Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place).unknown_item);
            token_place++;
            current_Token = AST_Token_List.get(token_place);
        }
        //go back
        //Abstract_Syntax_Tree.end_all_children();
    }

    //this causes error in the case that when it does not see the plus but there is still more than one thing there
    static void AST_Int_Expr(){
        
        //int is not strictly ll1 more like ll2 so did this to check for intop or just digit
        if(AST_Token_List.get(token_place+1).unknown_item.equals("+")){
            Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place+1).unknown_item);
            token_place++;
            current_Token = AST_Token_List.get(token_place);
            Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place-1).unknown_item);
            token_place++;
            current_Token = AST_Token_List.get(token_place);
            AST_Expr();
        }else if(current_Token.unknown_item.matches("[0-9]+")){
            Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place).unknown_item);
            token_place++;
            current_Token = AST_Token_List.get(token_place);
        }
        //go back
        //Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_String_Expr(){

        token_place++;
        String holder = "";

        for(int k = token_place; k < AST_Token_List.size(); k++){

            if(AST_Token_List.get(k).unknown_item.equals("\"")){
                token_place = k;
                break;
            }

            holder = holder + AST_Token_List.get(k).unknown_item;
        }

        Abstract_Syntax_Tree.addNode("leaf", holder);

        token_place++;
        current_Token = AST_Token_List.get(token_place);
        //go back
        //Abstract_Syntax_Tree.end_all_children();
    }

    static void AST_Boolean_Expr(){
        
        //check to see which path to take from the BNF
        if(current_Token.unknown_item.equals("(")){
            Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place+2).unknown_item);
            token_place++;
            current_Token = AST_Token_List.get(token_place);
            AST_Expr();
            token_place++;
            current_Token = AST_Token_List.get(token_place);
            AST_Expr();
            token_place++;
            current_Token = AST_Token_List.get(token_place);
        }else if (current_Token.unknown_item.matches("true") ||current_Token.unknown_item.matches("false")) {
            Abstract_Syntax_Tree.addNode("leaf", AST_Token_List.get(token_place).unknown_item);
            token_place++;
            current_Token = AST_Token_List.get(token_place);
        }
        
        //go back
        Abstract_Syntax_Tree.end_all_children();
    }

}

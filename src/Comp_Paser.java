import java.util.ArrayList;
import java.util.List;

//things to do
//test to see if the output is correct for larger cases
//test for mutiple program files

//go back = going back on the node tree for other programs and such

public class Comp_Paser {
    //lots of global values that will be explained later in the program
    Comp_Lexer Comp_Lexer = new Comp_Lexer();
    static Comp_Lexer.TokenBuilder current_Token;
    static int token_place = 0;
    static List<Comp_Lexer.TokenBuilder> Parser_Token_List;
    static int parse_num_errors = 0;
    static int debugg_mode_token = 0;
    static int debugg_mode_function = 1;
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
    public class CST{
        Tree_Node root;
        Tree_Node current;

        CST(){
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
            }else{
                //the parent of this will be the one before or the current current ಠ_ಠ
                Temp_Node.parent = current;
                //then the child
                Temp_Node.parent.children.add(Temp_Node);
            }

            //if it is not a leaf node then that means we are still traversing down so we make the new current what the temp
            //node was
            if (kind != "leaf"){
                current = Temp_Node;
            }
        }

        //this will be used to go back up the tree
        public void end_all_children(){
            current = current.parent;
        }

        //this is to give the output of the tree
        public void grow(Tree_Node Node, int depth){
            //default 
            String output =  "";

            //will make the first set of dashes
            for(int i = 0; i < depth; i++){
                output += "-";
            }

            //something to check if the node is a leaf node (still needs to be fixed i think)
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
    static CST Concreat_Syntax_Tree;


    public void Parser_Start(List<Comp_Lexer.TokenBuilder> Token_List){
        //some default values
        Concreat_Syntax_Tree = new CST();
        Parser_Token_List = Token_List;
        token_place = 0;
        parse_num_errors = 0;
        Program_Num++;
        current_Token = Token_List.get(token_place);

        //gives the node to the CST
        //to set or stop the debug mode
        if(debugg_mode_token == 1){
            System.out.println("Parseing for token " + current_Token.unknown_item);
        }

        //this will check if the start is valid or not
        switch (Token_List.get(token_place).unknown_item) {
            case "{":
                Parse_Program();
                break;
            default:
                Parse_Match("Block");
                break;
        }
        
    }

    static void Parse_Program(){
        //same kind of pattern
        //this is the call for the node to be built
        Concreat_Syntax_Tree.addNode("root", "program");
        //debug stuff
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Program()");
        }
        //then calls all the approeratie functions needed for the LL(1) based on the BNF
        Parse_Block();
        Parse_Match("$");
        //if it gets to here thats the end of the current program so check for errors and print out tree
        System.out.println("Parse for Program " + Program_Num + " Done");
        if(parse_num_errors > 0){
            //this will be displayed if there are errors letting the user know
            System.out.println("Parse has " + parse_num_errors + " errors :(");
            System.out.println("No CST will be made :(");
        }else{
            //this will be displayed if there are no errors
            System.out.println("Parse No errors :)");
            //and start the cst build and display
            System.out.println("CST will be displayed :)");
            Concreat_Syntax_Tree.grow(Concreat_Syntax_Tree.root, 0);
        }
        //go back 
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Block(){
        //add the node when it is made
        Concreat_Syntax_Tree.addNode("branch", "block");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Block()");
        }
        //reset fo things that are needed in order to do the rest of the work
        Parse_Match("{");
        Parse_Statement_List();
        Parse_Match("}");
        //gpo back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Statement_List(){
        //add the new node
        Concreat_Syntax_Tree.addNode("branch", "statement_list");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Statement_List()");
        }
        //this will check to see what procedure the parser should take
        if(current_Token.unknown_item.equals("print") || current_Token.unknown_item.matches("[a-z]") || current_Token.unknown_item.matches("int|string|boolean") || current_Token.unknown_item.equals("while") || current_Token.unknown_item.equals("if") || current_Token.unknown_item.equals("{")){
            Parse_Statement();
            Parse_Statement_List();
        }else{
            // it’s a ɛ (empty)
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Statement(){
        //add the new node
        Concreat_Syntax_Tree.addNode("branch", "statement");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Statement()");
        }
        //this is more checking to see what kind of statment that it is
        //so that the parser cna figure out where to go with ll1
        if(current_Token.unknown_item.equals("print")){
            Parse_Print_Statment();
        }else if(current_Token.unknown_item.matches("[a-z]")){
            Parse_Assignment_Statment();
        }else if(current_Token.unknown_item.matches("int|string|boolean")){
            Parse_Var_Decl();
        }else if(current_Token.unknown_item.equals("while")){
            Parse_While_Statment();
        }else if(current_Token.unknown_item.equals("if")){
            Parse_If_Statment();
        }else if(current_Token.unknown_item.equals("{")){
            Parse_Block();
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Print_Statment(){
        //add the node
        Concreat_Syntax_Tree.addNode("branch", "print_statment");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Print_Statment()");
        }
        //the other steps that need to be taken based on the bnf
        Parse_Match("print");
        Parse_Match("(");
        Parse_Expr();
        Parse_Match(")");
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Assignment_Statment(){
        //adds the node 
        Concreat_Syntax_Tree.addNode("branch", "assignment_statment");
        //debugginh
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Assignment_Statment()");
        }
        //rest of the stuff that needs to be checked
        Parse_Id();
        Parse_Match("=");
        Parse_Expr();
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Var_Decl(){
        //add the new node
        Concreat_Syntax_Tree.addNode("branch", "var_decl");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Var_Decl()");
        }
        //rest of the stuff to check
        Parse_Type();
        Parse_Id();
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_While_Statment(){
        //add to the node
        Concreat_Syntax_Tree.addNode("branch", "while_statment");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_While_Statment()");
        }
        //rest of the stuff that needs to be checked
        Parse_Match("while");
        Parse_Boolean_Expr();
        Parse_Block();
        //go back 
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_If_Statment(){
        //add the node
        Concreat_Syntax_Tree.addNode("branch", "if_statment");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_If_Statment()");
        }
        //rest of the stuff to check
        Parse_Match("if");
        Parse_Boolean_Expr();
        Parse_Block();
        //gpo back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Expr(){
        //add the node
        Concreat_Syntax_Tree.addNode("branch", "expr");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Expr()");
        }
        //check whcih expr it is from the BNF
        if(current_Token.unknown_item.matches("[0-9]+")){
            Parse_Int_Expr();
        }else if(current_Token.unknown_item.equals("\"")){
            Parse_String_Expr();
        }else if(current_Token.unknown_item.equals("(") || current_Token.unknown_item.equals("true") || current_Token.unknown_item.equals("false")){
            Parse_Boolean_Expr();
        }else{
            Parse_Id();
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Int_Expr(){
        //add the new node
        Concreat_Syntax_Tree.addNode("branch", "int_expr");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Int_Expr()");
        }
        //int is not strictly ll1 more like ll2 so did this to check for intop or just digit
        if(Parser_Token_List.get(token_place+1).unknown_item.equals("+")){
            Parse_Digit();
            Parse_intop();
            Parse_Expr();
        }else{
            Parse_Digit();
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_String_Expr(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "string_expr");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_String_Expr()");
        }
        //check the rest of the stuff
        Parse_Match("\"");
        Parse_Char_List();
        Parse_Match("\"");
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Boolean_Expr(){
        //add the node
        Concreat_Syntax_Tree.addNode("branch", "boolean_expr");
        //debugging
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Boolean_Expr()");
        }
        //check to see which path to take from the BNF
        if(current_Token.unknown_item.equals("(")){
            Parse_Match("(");
            Parse_Expr();
            Parse_Boolop();
            Parse_Expr();
            Parse_Match(")");
        }else{
            Parse_Boolval();
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Id(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "id");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Id()");
        }
        //rest of stuff to check
        Parse_Char();
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Char_List(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "char_list");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Char_List()");
        }
        //check which path to take based on BNF
        if (current_Token.unknown_item.matches("[a-z]")){
            Parse_Char();
            Parse_Char_List();
        }else if (current_Token.unknown_item.equals(" ")){
            Parse_Space();
            Parse_Char_List();
        }else{
            // it’s a ɛ (empty)
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Type(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "type");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Type()");
        }
        //to get the expected value
        switch (current_Token.unknown_item) {
            case "int":
                Parse_Match("int");
                break;
            case "string":
                Parse_Match("string");
                break;
            case "boolean":
                Parse_Match("boolean");
                break;
            default:
                Parse_Match("Type");
                break;
        }
        //go back 
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Char(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "char");
        //debug
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Char()");
        }
        //get expected
        switch (current_Token.unknown_item) {
            case "a":
                Parse_Match("a");
                break;
            case "b":
                Parse_Match("b");
                break;
            case "c":
                Parse_Match("c");
                break;
            case "d":
                Parse_Match("d");
                break;
            case "e":
                Parse_Match("e");
                break;
            case "f":
                Parse_Match("f");
                break;
            case "g":
                Parse_Match("g");
                break;
            case "h":
                Parse_Match("h");
                break;
            case "i":
                Parse_Match("i");
                break;
            case "j":
                Parse_Match("j");
                break;
            case "k":
                Parse_Match("k");
                break;
            case "l":
                Parse_Match("l");
                break;
            case "m":
                Parse_Match("m");
                break;
            case "n":
                Parse_Match("n");
                break;
            case "o":
                Parse_Match("o");
                break;
            case "p":
                Parse_Match("p");
                break;
            case "q":
                Parse_Match("q");
                break;
            case "r":
                Parse_Match("r");
                break;
            case "s":
                Parse_Match("s");
                break;
            case "t":
                Parse_Match("t");
                break;
            case "u":
                Parse_Match("u");
                break;
            case "v":
                Parse_Match("v");
                break;
            case "w":
                Parse_Match("w");
                break;
            case "x":
                Parse_Match("x");
                break;
            case "y":
                Parse_Match("y");
                break;
            case "z":
                Parse_Match("z");
                break;
            default:
                Parse_Match("Char");
                break;
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Space(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "space");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Space()");
        }
        //get the expected
        switch (current_Token.unknown_item) {
            case " ":
                Parse_Match(" ");
                break;
            default:
                Parse_Match("Space");
                break;
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Digit(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "digit");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Digit()");
        }
        //get the expected
        switch (current_Token.unknown_item) {
            case "0":
                Parse_Match("0");
                break;
            case "1":
                Parse_Match("1");
                break;
            case "2":
                Parse_Match("2");
                break;
            case "3":
                Parse_Match("3");
                break;
            case "4":
                Parse_Match("4");
                break;
            case "5":
                Parse_Match("5");
                break;
            case "6":
                Parse_Match("6");
                break;
            case "7":
                Parse_Match("7");
                break;
            case "8":
                Parse_Match("8");
                break;
            case "9":
                Parse_Match("9");
                break;
            default:
                Parse_Match("Digit");
                break;
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Boolop(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "boolop");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Boolop()");
        }
        //get expected
        switch (current_Token.unknown_item) {
            case "==":
                Parse_Match("==");
                break;
            case "!=":
                Parse_Match("!=");
                break;
            default:
                Parse_Match("Bool Op");
                break;
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Boolval(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "boolval");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Boolval()");
        }
        //get expected
        switch (current_Token.unknown_item) {
            case "false":
                Parse_Match("false");
                break;
            case "true":
                Parse_Match("true");
                break;
            default:
                Parse_Match("Boolean Value");
                break;
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_intop(){
        //add node
        Concreat_Syntax_Tree.addNode("branch", "intop");
        //debugg
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_intop()");
        }
        //get expected
        switch (current_Token.unknown_item) {
            case "+":
                Parse_Match("+");
                break;
            default:
                Parse_Match("Int Op");
                break;
        }
        //go back
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Match(String expected_Token){
        //will match the expected passed with the current and see
        if(current_Token.unknown_item.equals(expected_Token)){
            //if match then move to next pointer and add the leaf node to the tree
            token_place++;
            //end of list was causing problme had to do this to fix
            //just says at end -1 to see what the last thing is
            if(token_place > Parser_Token_List.size()-1){
                current_Token = Parser_Token_List.get(token_place-1);
            }else{
                current_Token = Parser_Token_List.get(token_place);
            }
            //will print out the current token on
            if(debugg_mode_token == 1){
                System.out.println("Parseing for token " + current_Token.unknown_item);
            }
            //add the leaf
            Concreat_Syntax_Tree.addNode("leaf", expected_Token);
        }else{
            //error message as well as keeping track of the amount of error for output
            System.out.println("Parse Error: Expected " + expected_Token + " but found " + current_Token.unknown_item + " at " + + current_Token.line_num + " : " + current_Token.place_num);
            parse_num_errors++;
        }
    }

}

import java.util.ArrayList;
import java.util.List;

public class Comp_Paser {
    Comp_Lexer Comp_Lexer = new Comp_Lexer();
    static Comp_Lexer.TokenBuilder current_Token;
    static int token_place = 0;
    static List<Comp_Lexer.TokenBuilder> Parser_Token_List;
    static int parse_num_errors = 0;
    static int debugg_mode_token = 0;
    static int debugg_mode_function = 1;


    //test to see if this all works or not
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

    public class CST{
        Tree_Node root;
        Tree_Node current;

        CST(){
            this.root = null;
            this.current = null;
        }

        public void addNode(String kind, String label){
            Tree_Node Temp_Node = new Tree_Node();
            Temp_Node.name = label;

            if(this.root == null){
                root = Temp_Node;
            }else{
                Temp_Node.parent = current;
                Temp_Node.parent.children.add(Temp_Node);
            }

            if (kind != "leaf"){
                current = Temp_Node;
            }
        }

        public void end_all_children(){
            current = current.parent;
        }

        public void grow(Tree_Node Start_Node, int depth){

        }
    
        public String CST_String_Output(Tree_Node Start_Node){
            String output =  "";
    
            this.grow(Start_Node, 0);

            return output;
    
        }
        
    }

    static CST Concreat_Syntax_Tree;


    public void Parser_Start(List<Comp_Lexer.TokenBuilder> Token_List){
        Concreat_Syntax_Tree = new CST();
        Parser_Token_List = Token_List;
        token_place = 0;
        parse_num_errors = 0;
        current_Token = Token_List.get(token_place);

        Concreat_Syntax_Tree.addNode("root", "start");
        if(debugg_mode_token == 1){
            System.out.println("Parseing for token " + current_Token.unknown_item);
        }
        switch (Token_List.get(token_place).unknown_item) {
            case "{":
                Parse_Program();
                break;
            default:
                Parse_Match("Block");
                break;
        }
        Concreat_Syntax_Tree.end_all_children();
        
    }

    static void Parse_Program(){
        Concreat_Syntax_Tree.addNode("branch", "program");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Program()");
        }
        Parse_Block();
        Parse_Match("$");
        if(parse_num_errors > 0){
            System.out.println("Parse has " + parse_num_errors + " errors :(");
        }else{
            System.out.println("Parse No errors :)");
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Block(){
        Concreat_Syntax_Tree.addNode("branch", "block");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Block()");
        }
        Parse_Match("{");
        Parse_Statement_List();
        Parse_Match("}");
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Statement_List(){
        Concreat_Syntax_Tree.addNode("branch", "statement_list");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Statement_List()");
        }
        if(current_Token.unknown_item.equals("print") || current_Token.unknown_item.matches("[a-z]") || current_Token.unknown_item.matches("int|string|boolean") || current_Token.unknown_item.equals("while") || current_Token.unknown_item.equals("if") || current_Token.unknown_item.equals("{")){
            Parse_Statement();
            Parse_Statement_List();
        }else{
            // it’s a ɛ
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Statement(){
        Concreat_Syntax_Tree.addNode("branch", "statement");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Statement()");
        }
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
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Print_Statment(){
        Concreat_Syntax_Tree.addNode("branch", "print_statment");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Print_Statment()");
        }
        Parse_Match("print");
        Parse_Match("(");
        Parse_Expr();
        Parse_Match(")");
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Assignment_Statment(){
        Concreat_Syntax_Tree.addNode("branch", "assignment_statment");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Assignment_Statment()");
        }
        Parse_Id();
        Parse_Match("=");
        Parse_Expr();
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Var_Decl(){
        Concreat_Syntax_Tree.addNode("branch", "var_decl");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Var_Decl()");
        }
        Parse_Type();
        Parse_Id();
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_While_Statment(){
        Concreat_Syntax_Tree.addNode("branch", "while_statment");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_While_Statment()");
        }
        Parse_Match("while");
        Parse_Boolean_Expr();
        Parse_Block();
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_If_Statment(){
        Concreat_Syntax_Tree.addNode("branch", "if_statment");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_If_Statment()");
        }
        Parse_Match("if");
        Parse_Boolean_Expr();
        Parse_Block();
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Expr(){
        Concreat_Syntax_Tree.addNode("branch", "expr");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Expr()");
        }
        if(current_Token.unknown_item.matches("[0-9]+")){
            Parse_Int_Expr();
        }else if(current_Token.unknown_item.equals("\"")){
            Parse_String_Expr();
        }else if(current_Token.unknown_item.equals("(") || current_Token.unknown_item.equals("true") || current_Token.unknown_item.equals("false")){
            Parse_Boolean_Expr();
        }else{
            Parse_Id();
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Int_Expr(){
        Concreat_Syntax_Tree.addNode("branch", "int_expr");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Int_Expr()");
        }
        if(Parser_Token_List.get(token_place+1).unknown_item.equals("+")){
            Parse_Digit();
            Parse_intop();
            Parse_Expr();
        }else{
            Parse_Digit();
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_String_Expr(){
        Concreat_Syntax_Tree.addNode("branch", "string_expr");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_String_Expr()");
        }
        Parse_Match("\"");
        Parse_Char_List();
        Parse_Match("\"");
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Boolean_Expr(){
        Concreat_Syntax_Tree.addNode("branch", "boolean_expr");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Boolean_Expr()");
        }
        if(current_Token.unknown_item.equals("(")){
            Parse_Match("(");
            Parse_Expr();
            Parse_Boolop();
            Parse_Expr();
            Parse_Match(")");
        }else{
            Parse_Boolval();
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Id(){
        Concreat_Syntax_Tree.addNode("branch", "id");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Id()");
        }
        Parse_Char();
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Char_List(){
        Concreat_Syntax_Tree.addNode("branch", "char_list");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Char_List()");
        }
        if (current_Token.unknown_item.matches("[a-z]")){
            Parse_Char();
            Parse_Char_List();
        }else if (current_Token.unknown_item.equals(" ")){
            Parse_Space();
            Parse_Char_List();
        }else{
            // it’s a ɛ
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Type(){
        Concreat_Syntax_Tree.addNode("branch", "type");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Type()");
        }
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
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Char(){
        Concreat_Syntax_Tree.addNode("branch", "char");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Char()");
        }
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
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Space(){
        Concreat_Syntax_Tree.addNode("branch", "space");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Space()");
        }
        switch (current_Token.unknown_item) {
            case " ":
                Parse_Match(" ");
                break;
            default:
                Parse_Match("Space");
                break;
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Digit(){
        Concreat_Syntax_Tree.addNode("branch", "digit");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Digit()");
        }
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
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Boolop(){
        Concreat_Syntax_Tree.addNode("branch", "boolop");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Boolop()");
        }
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
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Boolval(){
        Concreat_Syntax_Tree.addNode("branch", "boolval");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_Boolval()");
        }
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
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_intop(){
        Concreat_Syntax_Tree.addNode("branch", "intop");
        if(debugg_mode_function == 1){
            System.out.println("Parseing: Parse_intop()");
        }
        switch (current_Token.unknown_item) {
            case "+":
                Parse_Match("+");
                break;
            default:
                Parse_Match("Int Op");
                break;
        }
        Concreat_Syntax_Tree.end_all_children();
    }

    static void Parse_Match(String expected_Token){
        if(current_Token.unknown_item.equals(expected_Token)){
            token_place++;
            if(token_place > Parser_Token_List.size()-1){
                current_Token = Parser_Token_List.get(token_place-1);
            }else{
                current_Token = Parser_Token_List.get(token_place);
            }

            if(debugg_mode_token == 1){
                System.out.println("Parseing for token " + current_Token.unknown_item);
            }
            Concreat_Syntax_Tree.addNode("leaf", expected_Token);
        }else{
            //this is the wrong error you need to ouptut what should be there and then what is expected not the null shit
            //test to see if this works or not
            System.out.println("Parse Error: Expected " + expected_Token + " but found " + current_Token.unknown_item + " at " + + current_Token.line_num + " : " + current_Token.place_num);
            parse_num_errors++;
        }
    }

}

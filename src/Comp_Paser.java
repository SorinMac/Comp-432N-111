import java.util.List;

public class Comp_Paser {
    Comp_Lexer Comp_Lexer = new Comp_Lexer();
    static Comp_Lexer.TokenBuilder current_Token;
    static int token_place = 0;
    static List<Comp_Lexer.TokenBuilder> Parser_Token_List;
    static int parse_num_errors = 0;
    static int debugg_mode = 0;
    //this will return if parse was done correct or not 
    static boolean Parse_Done;

    //should the parser be allowed to start at anything
    //or is it only allowed to start with a {}
    //like can is start with a a Char
    //how does this need to kick off?
    public void Parser_Start(List<Comp_Lexer.TokenBuilder> Token_List){
        Parser_Token_List = Token_List;
        token_place = 0;
        parse_num_errors = 0;
        current_Token = Token_List.get(token_place);
        if(debugg_mode == 1){
            System.out.println("Parseing for token " + current_Token.unknown_item);
        }
        switch (Token_List.get(token_place).unknown_item) {
            case "{":
                Parse_Program();
                break;
            default:
                Parse_Match("null");
                break;
        }
    }

    static void Parse_Program(){
        Parse_Block();
        Parse_Match("$");
        if(parse_num_errors > 0){
            System.out.println("Parse has " + parse_num_errors + " errors :(");
        }else{
            System.out.println("Parse No errors :)");
        }
    }

    static void Parse_Block(){
        Parse_Match("{");
        Parse_Statement_List();
        Parse_Match("}");
    }

    static void Parse_Statement_List(){
        if(current_Token.unknown_item.equals("print") || current_Token.unknown_item.matches("[a-z]") || current_Token.unknown_item.matches("int|string|boolean") || current_Token.unknown_item.equals("while") || current_Token.unknown_item.equals("if") || current_Token.unknown_item.equals("{")){
            Parse_Statement();
            Parse_Statement_List();
        }else{
            // it’s a ɛ
        }
        
    }

    static void Parse_Statement(){
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
    }

    static void Parse_Print_Statment(){
        Parse_Match("print");
        Parse_Match("(");
        Parse_Expr();
        Parse_Match(")");
    }

    static void Parse_Assignment_Statment(){
        Parse_Id();
        Parse_Match("=");
        Parse_Expr();

    }

    static void Parse_Var_Decl(){
        Parse_Type();
        Parse_Id();
    }

    static void Parse_While_Statment(){
        Parse_Match("while");
        Parse_Boolean_Expr();
        Parse_Block();
    }

    static void Parse_If_Statment(){
        Parse_Match("if");
        Parse_Boolean_Expr();
        Parse_Block();
    }

    static void Parse_Expr(){
        //this one right now does not work
        if(current_Token.unknown_item.matches("[0-9]+")){
            Parse_Int_Expr();
        }else if(current_Token.unknown_item.equals("\"")){
            Parse_String_Expr();
        }else if(current_Token.unknown_item.equals("(") || current_Token.unknown_item.equals("true") || current_Token.unknown_item.equals("false")){
            Parse_Boolean_Expr();
        }else{
            Parse_Id();
        }
    }

    //what is the best way to differentiate the two possibilities for this one
    static void Parse_Int_Expr(){
        if(Parser_Token_List.get(token_place+1).unknown_item.equals("+")){
            Parse_Digit();
            Parse_intop();
            Parse_Expr();
        }else{
            Parse_Digit();
        }
    }

    static void Parse_String_Expr(){
        Parse_Match("\"");
        Parse_Char_List();
        Parse_Match("\"");
    }

    static void Parse_Boolean_Expr(){
        if(current_Token.unknown_item.equals("(")){
            Parse_Match("(");
            Parse_Expr();
            Parse_Boolop();
            Parse_Expr();
            Parse_Match(")");
        }else{
            Parse_Boolval();
        }

    }

    static void Parse_Id(){
        Parse_Char();
    }

    static void Parse_Char_List(){
        if (current_Token.unknown_item.matches("[a-z]")){
            Parse_Char();
            Parse_Char_List();
        }else if (current_Token.unknown_item.equals(" ")){
            Parse_Space();
            Parse_Char_List();
        }else{
            // it’s a ɛ
        }

    }

    static void Parse_Type(){
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
                break;
        }
    }

    static void Parse_Char(){
        //ask him if this is correct
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
                Parse_Match("null");
                break;
        }
    }

    static void Parse_Space(){
        Parse_Match(" ");
    }

    static void Parse_Digit(){
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
                Parse_Match("null");
                break;
        }
    }

    static void Parse_Boolop(){
        switch (current_Token.unknown_item) {
            case "==":
                Parse_Match("==");
                break;
            case "!=":
                Parse_Match("!=");
                break;
            default:
                Parse_Match("null");
                break;
        }
    }

    static void Parse_Boolval(){
        switch (current_Token.unknown_item) {
            case "false":
                Parse_Match("false");
                break;
            case "true":
                Parse_Match("true");
                break;
            default:
                Parse_Match("null");
                break;
        }
    }

    static void Parse_intop(){
        Parse_Match("+");
    }

    static void Parse_Match(String expected_Token){
        if(current_Token.unknown_item.equals(expected_Token)){
            token_place++;
            if(token_place > Parser_Token_List.size()-1){
                current_Token = Parser_Token_List.get(token_place-1);
            }else{
                current_Token = Parser_Token_List.get(token_place);
            }

            if(debugg_mode == 1){
                System.out.println("Parseing for token " + current_Token.unknown_item);
            }
            //building tree logic i think
            //do not want to cosnume token (get rid of it/loose the memory for it)
            //inc program counter is done with for loop
        }else{
            System.out.println("Parse Error: Expected " + expected_Token + " but found " + current_Token.unknown_item + " at " + + current_Token.line_num + " : " + current_Token.place_num);
            parse_num_errors++;
        }
    }

}

import java.util.ArrayList;
import java.util.List;

public class Comp_Paser {
    Comp_Lexer Comp_Lexer = new Comp_Lexer();
    static Comp_Lexer.TokenBuilder current_Token;

    //should the parser be allowed to start at anything
    //or is it only allowed to start with a {}
    //like can is start with a a Char
    //how does this need to kick off?
    public void Parser_Start(List<Comp_Lexer.TokenBuilder> Token_List){
        for(int i = 0; i < Token_List.size(); i++){
            current_Token = Token_List.get(i);
            switch (Token_List.get(i).unknown_item) {
                case "{":
                    break;
                case "(":
                    break;
                case "print":
                    break;
                case "while":
                    break;
                case "if":
                    break;
                case "int":
                    break;
                case "string":
                    break;
                case "boolean":
                    break;
                default:
                    break;
            }
        }
    }

    static void Parse_Program(){

    }

    static void Parse_Block(){
        
    }

    static void Parse_Statement_List(){
        
    }

    static void Parse_Statement(){

    }

    static void Parse_Print_Statment(){

    }

    static void Parse_Assignment_Statment(){

    }

    static void Parse_Var_Decl(){

    }

    static void Parse_While_Statment(){

    }

    static void Parse_If_Statment(){

    }

    static void Parse_Expr(){

    }

    static void Parse_Int_Expr(){

    }

    static void Parse_String_Expr(){

    }

    static void Parse_Boolean_Expr(){

    }

    static void Parse_Id(){

    }

    static void Parse_Char_List(){

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
                break;
        }
    }

    static void Parse_intop(){
        Parse_Match("+");
    }

    static void Parse_Match(String expected_Token){
        if(current_Token.unknown_item.equals(expected_Token)){
            //building tree logic i think
            //do not want to cosnume token (get rid of it/loose the memory for it)
            //inc program counter is done with for loop
        }else{
            System.out.println("Parse Error: Expected " + expected_Token + " but found " + current_Token.unknown_item + " at " + + current_Token.line_num + " : " + current_Token.place_num);
        }
    }

}

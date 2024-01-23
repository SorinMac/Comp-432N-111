import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.List;


public class Compiler {

    //class for the tokens
    class TokenBuilder{

        //in my searches across the internet i learned of enums in Java
        //will make it easier to assign the description when i need to do that 
        Type description;
        String unknown_item;

        //simple constructor for building the tokens out when i need that to be done
        TokenBuilder(Type description, String unknown_item){
            this.description = description;
            this.unknown_item = unknown_item;
        }

    }

    //my type I believe this will be everything not fully sure yet
    enum Type{
        LeftBracket, RightBracket, LeftParan, RightParan, PrintStatement, WhileStatement, IfStatement, TYPE, CHAR, SPACE, DIGIT, Boolop, Boolval, Intop
    }

    //this will be where the Lexer work happens
    static List<TokenBuilder> Lexer(String code){

    }

    //this will find out what the correct discription will be for the particual unknow object
    static Type GetDescription(String unknown_item){

    }

    public static void main(String[] args){
        //sets the value up
        String line = "";

        try {
            //gets the file ready for reading
            File commandTXT = new File("test.txt");
            Scanner reader = new Scanner(commandTXT);

            //makes is a long string (is that okay or should i have it with the tabs)
            while (reader.hasNextLine()) {
                line = line + reader.nextLine();
            }

            reader.close();
        
        //error checking to see if the file was open or not
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //going to store all the Tokens into a arraylist so that i do not have to worry about the amount i need alocated to the array
        //Lexer will also be the function in which i will do the Lexer's work
        List<TokenBuilder> Tokens = Lexer(line);

        //simple way to print out all the tokens in the array list
        for(TokenBuilder Token: Tokens){
            System.out.println(Token);
        }
    }
}

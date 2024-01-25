import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Compiler {

    //class for the tokens
    static class TokenBuilder{

        //in my searches across the internet i learned of enums in Java
        //will make it easier to assign the description when i need to do that 
        String description;
        String unknown_item;

        //simple constructor for building the tokens out when i need that to be done
        TokenBuilder(String description, String unknown_item){
            this.description = description;
            this.unknown_item = unknown_item;
        }

    }

    //this will be where the Lexer work happens
    static List<TokenBuilder> Lexer(String code){
        List<TokenBuilder> Token = new ArrayList<>();
        int Check_Doubel_Quote = 0;

        //this while check for white space have not made somehing to handle it only being in the string
        //String check = "\\b(if)\\b|[a-z]+|[0-9]+|[+(){}]|[=]+|\\s|";

        String check = "\\b(if)\\b|[a-z]+|[0-9]+|[+(){}]|[=]+";
        Pattern tokenCheck = Pattern.compile(check);
        Matcher tokenFinder = tokenCheck.matcher(code);

        while(tokenFinder.find()){
            if(tokenFinder.group() == String.valueOf('"')){
                Check_Doubel_Quote = 1;
                continue;
            }else{
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Doubel_Quote);
                Token.add(new TokenBuilder(item_decloration, item));
            }
        }

        return Token;
    }

    //this will find out what the correct discription will be for the particual unknow object
    static String GetDescription(String unknown_item, int If_Double_Quote){
        String TokenDisc = "";

        //not get around comments
        //and does not have quotes ready
        //have when in string do it as all single chars (count space only here as well)
        //blocks

        if(unknown_item.equals("{")){
            TokenDisc = "Left_Bracket";
        }else if(unknown_item.equals("}")){
            TokenDisc = "Right_Bracket";
        }else if(unknown_item.equals("(")){
            TokenDisc = "Left_Paran";
        }else if(unknown_item.equals(")")){
            TokenDisc = "Right_Paran";
        }else if(unknown_item.equals("print")){
            TokenDisc = "Print_Statment";
        }else if(unknown_item.equals("while")){
            TokenDisc = "While_Statment";
        }else if(unknown_item.equals("if")){
            TokenDisc = "If_Statment";
        }else if(unknown_item.equals(" ") && If_Double_Quote == 1){
            TokenDisc = "SPACE";
        }else if(unknown_item.equals("+")){
            TokenDisc = "InTop";
        }else if(unknown_item.equals("=")){
            TokenDisc = "AssignmentStatement";
        }else if(unknown_item == String.valueOf('"')){
            TokenDisc = "QUOTE";
        }else if(unknown_item.matches("int|string|boolean")){
            TokenDisc = "TYPE";
        }else if(unknown_item.matches("[a-z]+")){
            TokenDisc = "CHAR";
        }else if(unknown_item.matches("[0-9]+")){
            TokenDisc = "DIGIT";
        }

        return TokenDisc;

    }

    public static void main(String[] args){
        //sets the value up
        List<String> code = new ArrayList<>();
        List<TokenBuilder> Tokens_List = new ArrayList<>();
        List<TokenBuilder> Temp_Token_Holder = new ArrayList<>();

        try {
            //gets the file ready for reading
            File commandTXT = new File("test.txt");
            Scanner reader = new Scanner(commandTXT);

            //makes is a long string (is that okay or should i have it with the tabs)
            while (reader.hasNextLine()) {
                code.add(reader.nextLine());
            }

            reader.close();
        
        //error checking to see if the file was open or not
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //going to store all the Tokens into a arraylist so that i do not have to worry about the amount i need alocated to the array
        //Lexer will also be the function in which i will do the Lexer's work
        for(int i = 0; i < code.size(); i++){
            Temp_Token_Holder = Lexer(code.get(i));

            if(Temp_Token_Holder.size() > 0){
                for(int k  = 0; k < Temp_Token_Holder.size(); k++){
                    Tokens_List.add(Temp_Token_Holder.get(k));
                }
            }
        }

        //simple way to print out all the tokens in the array list
        for(TokenBuilder Token: Tokens_List){
            System.out.println(Token.description + " [ " + Token.unknown_item + " ] " + "Found at: ");
        }

        System.out.println("Done");

        System.exit(0);
    }
}

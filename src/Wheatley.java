
import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Wheatley {

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

    //global Token array list so that you can add things when needed
    static List<TokenBuilder> Token = new ArrayList<>();

    //this will be where the Lexer work happens
    static List<TokenBuilder> Lexer(String code){   
        int Check_Comment = 0;
        int Check_Quote = 0;

        //block code
        //line num and place
        //debugg mode and non debugg mode
        //original one
        //String check = "if|string|boolean|int|[a-z]+|while|!=|==|=|True|False|[$]|[0-9]+|[+(){}]|/\\\\*|\\\"|\\s";
        //i think it is all correct double check all the toekns are there or not

        //this is my java regualar expression tokenization string
        String check = "if|string|boolean|int|while|print|[a-z]|!=|==|=|True|False|[$]|[0-9]+|[+(){}]|/\\\\*|\\\"|\\s";
        Pattern tokenCheck = Pattern.compile(check);
        Matcher tokenFinder = tokenCheck.matcher(code);

        while(tokenFinder.find()){

            //testing
            //System.out.println(tokenFinder.group());

            //logic for if i am inside quotes or not
            if (tokenFinder.group().equals("\"")) {
                Check_Quote = (Check_Quote == 0) ? 1 : 0;
            }

            //logic to handle if  i am in inside of comments
            if(tokenFinder.group().equals("/") && Check_Comment == 1){
                Check_Comment = 0;
                continue;
            }else if(tokenFinder.group().equals("/") || Check_Comment == 1){
               Check_Comment = 1;
            //logic to handle types
            }else if(tokenFinder.group().matches("int|string|boolean")){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote);
                Token.add(new TokenBuilder(item_decloration, item));
            //logic for if's
            }else if(tokenFinder.group().matches("if")){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote);
                Token.add(new TokenBuilder(item_decloration, item));
            //logic for while
            }else if(tokenFinder.group().matches("while")){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote);
                Token.add(new TokenBuilder(item_decloration, item));
            //and logic for print stuff
            }else if(tokenFinder.group().matches("print")){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote);
                Token.add(new TokenBuilder(item_decloration, item));
            //this is seperate so that when i go to the .mathces for a-z i can check if i am in quotes or not and do stuff apporoeratly
            }else if(tokenFinder.group().matches("[a-z]+")){
                GetDescription(tokenFinder.group(), Check_Quote);
                continue;
            //logic for spaces
            }else if(tokenFinder.group().matches("\s") && Check_Quote == 1){
                GetDescription(tokenFinder.group(), Check_Quote);
                continue;
            //logic for everything else
            }else if(tokenFinder.group().matches("[0-9]+|[+(){}]|==|!=|=|\\\"|True|False|[$]|")){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote);
                Token.add(new TokenBuilder(item_decloration, item));
            }
        }

        return Token;
    }

    //this will find out what the correct discription will be for the particual unknow object
    static String GetDescription(String unknown_item, int Quote){
        String TokenDisc = "";

        //lots of logic to handle what the discription of the token should be 
        //for the tokens that where found
        if(unknown_item.equals("{")){
            TokenDisc = "Begin_Block";
        }else if(unknown_item.equals("}")){
            TokenDisc = "End_Block";
        }else if(unknown_item.equals("(")){
            TokenDisc = "Open_Expression";
        }else if(unknown_item.equals(")")){
            TokenDisc = "Close_Expression";
        }else if(unknown_item.equals("print")){
            TokenDisc = "Print_Statment";
        }else if(unknown_item.equals("while")){
            TokenDisc = "While_Statment";
        }else if(unknown_item.equals("if")){
            TokenDisc = "If_Statment";
        }else if(unknown_item.equals("==")){
            TokenDisc = "Equal";
        }else if(unknown_item.equals("!=")){
            TokenDisc = "Not_Equal";
        }else if(unknown_item.equals("True")){
            TokenDisc = "Boolean";
        }else if(unknown_item.equals("False")){
            TokenDisc = "Boolean";
        }else if(unknown_item.equals("$")){
            TokenDisc = "END_OF_PROGRAM";
        }else if(unknown_item.matches("\s")){
            String item = unknown_item;
            String item_decloration = "SPACE";
            Token.add(new TokenBuilder(item_decloration, item));
        }else if(unknown_item.equals("+")){
            TokenDisc = "InTop";
        }else if(unknown_item.equals("=")){
            TokenDisc = "AssignmentStatement";
        }else if(unknown_item.matches("\"")){
            TokenDisc = "QUOTE";
        }else if(unknown_item.matches("int|string|boolean")){
            TokenDisc = "TYPE";
        }else if(unknown_item.matches("[a-z]+")){
            //special logic

            //if i am in quotes then you have to print out every character and the spaces
            if(Quote == 1){
                String[] char_Holder = unknown_item.split("");

                for(int i = 0; i < char_Holder.length; i++){
                    String item = char_Holder[i];
                    String item_decloration = "CHAR";
                    Token.add(new TokenBuilder(item_decloration, item));
                }
            
            //if not then its an ID
            }else{
                String item = unknown_item;
                String item_decloration = "ID";
                Token.add(new TokenBuilder(item_decloration, item));
            }

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

        //have the lexer output as a option for a debugg mode
        int Lexer_Output_Boolean = 1;

        try {
            //gets the file ready for reading
            File commandTXT = new File("src/test.txt");
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
            //temp holder
            //since we go in line by line (as best test case) then i need a temp holder to add to the total holder
            Temp_Token_Holder = Lexer(code.get(i));

            if(Temp_Token_Holder.size() > 0){
                for(int k  = 0; k < Temp_Token_Holder.size(); k++){
                    //adding to the total holder
                    Tokens_List.add(Temp_Token_Holder.get(k));
                }

                //then clearing the temp before i the compiler pulls in a new string
                Temp_Token_Holder.clear();
            }
        }

        //simple way to print out all the tokens in the array list
        if(Lexer_Output_Boolean == 1){
            for(TokenBuilder Token: Tokens_List){
                System.out.println(Token.description + " [ " + Token.unknown_item + " ] " + "Found at: ");

                if(Token.unknown_item.equals("$")){
                    System.out.println("End of Current Program :)" + "\n");
                }
            }
        }else{
            System.out.println("Done :)");
        }

        System.exit(0);
    }
}

package src;

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
        int line_num;
        int place_num;

        //simple constructor for building the tokens out when i need that to be done
        TokenBuilder(String description, String unknown_item, int num, int place){
            this.description = description;
            this.unknown_item = unknown_item;
            this.line_num = num;
            this.place_num = place;
        }

    }

    //global Token array list so that you can add things when needed
    static List<TokenBuilder> Token = new ArrayList<>();
    static int place = 0;
    static int Check_Quote = 0;

    //this will be where the Lexer work happens
    static List<TokenBuilder> Lexer(String code, int line_num){   
        int Check_Comment = 0;

        //block code
        //place

        //this is my java regualar expression tokenization string
        String check = "if|string|boolean|int|while|print|true|false|[a-z]|!=|==|=|[$]|[0-9]+|[+(){}]|/\\\\*|\\\"|\\s|$|.";
        Pattern tokenCheck = Pattern.compile(check);
        Matcher tokenFinder = tokenCheck.matcher(code);

        while(tokenFinder.find()){

            //testing
            //System.out.println(tokenFinder.group());

            //this is how it get the index of the token
            place = tokenFinder.end();


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
            }else if(tokenFinder.group().matches("int|string|boolean") && Check_Quote == 0){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote, line_num);
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            //logic for if's
            }else if(tokenFinder.group().matches("if") && Check_Quote == 0){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote, line_num);
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            //logic for while
            }else if(tokenFinder.group().matches("while") && Check_Quote == 0){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote, line_num);
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            //and logic for print stuff
            }else if(tokenFinder.group().matches("print") && Check_Quote == 0){
                String item = tokenFinder.group();
                String item_decloration = GetDescription(item, Check_Quote, line_num);
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            //this is seperate so that when i go to the .mathces for a-z i can check if i am in quotes or not and do stuff apporoeratly
            }else if(tokenFinder.group().matches("[a-z]+")){
                GetDescription(tokenFinder.group(), Check_Quote, line_num);
                continue;
            //logic for spaces
            }else if(tokenFinder.group().matches("\s") && Check_Quote == 1){
                GetDescription(tokenFinder.group(), Check_Quote, line_num);
                continue;
            //logic for everything else
            }else if(tokenFinder.group().matches("[0-9]+|[+(){}]|==|!=|=|\\\"|true|false|[$]|$|.")){
                String item = tokenFinder.group();
                GetDescription(item, Check_Quote, line_num);
            }
        }

        return Token;
    }

    //this will find out what the correct discription will be for the particual unknow object
    static String GetDescription(String unknown_item, int Quote, int line_num){
        String TokenDisc = "";

        //lots of logic to handle what the discription of the token should be 
        //for the tokens that where found
        if(unknown_item.equals("{") && Quote == 0){
            TokenDisc = "Begin_Block";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("}") && Quote == 0){
            TokenDisc = "End_Block";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("(") && Quote == 0){
            TokenDisc = "Open_Expression";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals(")")&& Quote == 0){
            TokenDisc = "Close_Expression";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("print") && Quote == 0){
            TokenDisc = "Print_Statment";
        }else if(unknown_item.equals("while") && Quote == 0){
            TokenDisc = "While_Statment";
        }else if(unknown_item.equals("if") && Quote == 0){
            TokenDisc = "If_Statment";
        }else if(unknown_item.equals("==") && Quote == 0){
            TokenDisc = "Equal";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("!=") && Quote == 0){
            TokenDisc = "Not_Equal";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("true") && Quote == 0){
            TokenDisc = "Boolean_Value";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("false") && Quote == 0){
            TokenDisc = "Boolean_Value";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("$") && Quote == 0){
            TokenDisc = "END_OF_PROGRAM";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.matches("\s") && Quote == 1){
            String item = unknown_item;
            String item_decloration = "SPACE";
            Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
        }else if(unknown_item.equals("+") && Quote == 0){
            TokenDisc = "InTop";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.equals("=") && Quote == 0){
            TokenDisc = "AssignmentStatement";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.matches("\"")){
            TokenDisc = "QUOTE";
            Token.add(new TokenBuilder(TokenDisc, unknown_item, line_num+1, place));
        }else if(unknown_item.matches("int|string|boolean") && Quote == 0){
            TokenDisc = "TYPE";
        }else if(unknown_item.matches("[a-z]+")){
            //special logic
            //if i am in quotes then you have to print out every character and the spaces
            if(Quote == 1){
                String[] char_Holder = unknown_item.split("");

                for(int i = 0; i < char_Holder.length; i++){
                    String item = char_Holder[i];
                    String item_decloration = "CHAR";
                    Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
                }
            
            //if not then its an ID
            }else{
                String item = unknown_item;
                String item_decloration = "ID";
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            }

        }else if(unknown_item.matches("[0-9]+") && Quote == 0){
            String[] int_Holder = unknown_item.split("");

            for(int i = 0; i < int_Holder.length; i++){
                String item = int_Holder[i];
                TokenDisc = "DIGIT";
                Token.add(new TokenBuilder(TokenDisc, item, line_num+1, place));
            }

        }else{
            if(unknown_item.matches("\s")){
                
            }else if(unknown_item.matches("[0-9]+")){
                String item = unknown_item;
                String item_decloration = "Error: int not allowed in string";
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            }else if (unknown_item.matches("") && Check_Quote == 1){
                String item = unknown_item;
                String item_decloration = "Error: new line not allowed in string";
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));
            }else if(!unknown_item.matches("")){
                String item = unknown_item;
                String item_decloration = "Error: non recognized symbol";
                Token.add(new TokenBuilder(item_decloration, item, line_num+1, place));

            }
        }

        return TokenDisc;

    }

    public static void main(String[] args){
        //sets the value up
        List<String> code = new ArrayList<>();
        List<TokenBuilder> Tokens_List = new ArrayList<>();
        List<TokenBuilder> Temp_Token_Holder = new ArrayList<>();
        //nums of the error and number of programs
        int num_of_program = 1;
        int lexer_num_of_error = 0;

        //have the lexer output as a option for a debugg mode
        int Lexer_Output_Boolean = 1;

        try {
            //gets the file ready for reading
            //args[0] for when you need to take in a argurment from the command line
            // "src/test.txt" when you want to use the break points
            File commandTXT = new File("src/test2.txt");
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
            if(code.get(i).isEmpty() && i == code.size()-1){
                if(!code.get(i-1).contains("$")){
                    System.out.println("EOP Error: Your program does not end with $ at line " + (i+1));
                }
            }

            Temp_Token_Holder = Lexer(code.get(i), i);

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
            //output
            for(int i = 0; i < Tokens_List.size(); i++){
                System.out.println(Tokens_List.get(i).description + " [ " + Tokens_List.get(i).unknown_item + " ] " + "Found at line " + Tokens_List.get(i).line_num + " : " + Tokens_List.get(i).place_num);

                if(Tokens_List.get(i).description.contains("Error")){
                    lexer_num_of_error++;
                }

                if(Tokens_List.get(i).unknown_item.equals("$")){
                    System.out.println("Number of Errors is " + lexer_num_of_error + " :(");
                    if(lexer_num_of_error > 0){
                        System.out.println("Lexer failed :(");
                    }
                    lexer_num_of_error = 0;
                    System.out.println("End of program " + num_of_program + "\n");
                    num_of_program++;
                }
            }

            //checking for the error tokens and then removing them 
            //makes the for loop get out of order
            for(int i = 0; i < Tokens_List.size(); i++){
                TokenBuilder Token = Tokens_List.get(i);
                if(Token.description.contains("Error")){
                    Tokens_List.remove(Token);
                }
            }

        }else{
            for(int i = 0; i < Tokens_List.size(); i++){
                if(Tokens_List.get(i).description.contains("Error")){
                    lexer_num_of_error++;
                }
            }
            
            if(lexer_num_of_error > 0){
                System.out.println("Lexer Error did not fully compile :( \n");
            }else{
                System.out.println("Done \n");
            }
        }

        System.exit(0);
    }
}
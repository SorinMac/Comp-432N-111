import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Wheatley {

    //class for the tokens
    public class TokenBuilder{

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

    public static void main(String[] args){
        Comp_Lexer Comp_Lexer = new Comp_Lexer();
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

            Temp_Token_Holder = Comp_Lexer.Lexer(code.get(i), i);

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
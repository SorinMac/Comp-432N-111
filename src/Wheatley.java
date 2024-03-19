import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.List;


public class Wheatley {

    public static void main(String[] args){
        Comp_Lexer Comp_Lexer = new Comp_Lexer();
        Comp_Parser Comp_Paser = new Comp_Parser();

        //sets the value up
        List<String> code = new ArrayList<>();
        List<Comp_Lexer.TokenBuilder> Tokens_List = new ArrayList<>();
        //nums of the error and number of programs
        int num_of_program = 1;
        int lexer_num_of_error = 0;

        //have the lexer output as a option for a debugg mode
        //will make it do all tokens all at once in one shot
        int Lexer_Output_Boolean = 1;

        try {
            //gets the file ready for reading
            //args[0] for when you need to take in a argurment from the command line
            // "src/test.txt" when you want to use the break points
            File commandTXT = new File("src/test.txt"); //"src/parser_test.txt" or args[0]
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
                    System.out.println("EOP Warning: Your program does not end with $ at line " + (i+1));
                }
            }

            Tokens_List = Comp_Lexer.Lexer(code.get(i), i);

            //make this so that it only happens
            if(Tokens_List.size() > 0){
                    if(Lexer_Output_Boolean == 1){
                        //output
                        for(int l = 0; l < Tokens_List.size(); l++){
                            System.out.println(Tokens_List.get(l).description + " [ " + Tokens_List.get(l).unknown_item + " ] " + "Found at line " + Tokens_List.get(l).line_num + " : " + Tokens_List.get(l).place_num);

                            if(Tokens_List.get(l).description.contains("Error")){
                                lexer_num_of_error++;
                            }

                            if(Tokens_List.get(l).unknown_item.equals("$")){
                                System.out.println("Lexer Number of Errors is " + lexer_num_of_error);
                                if(lexer_num_of_error > 0){
                                    System.out.println("Lexer failed :(");
                                }else{
                                    System.out.println("Paser starting :)");
                                    Comp_Paser.Parser_Start(Tokens_List);
                                }
                                lexer_num_of_error = 0;
                                System.out.println("End of program " + num_of_program + "\n");
                                num_of_program++;
                            }
                        }

                        //checking for the error tokens and then removing them 
                        //makes the for loop get out of order
                        for(int l = 0; l < Tokens_List.size(); l++){
                            Comp_Lexer.TokenBuilder Token = Tokens_List.get(l);
                            if(Token.description.contains("Error")){
                                Tokens_List.remove(Token);
                            }
                        }

                        }else{
                            for(int l = 0; l < Tokens_List.size(); l++){
                                if(Tokens_List.get(l).description.contains("Error")){
                                    lexer_num_of_error++;
                                }
                            }
                            
                            if(lexer_num_of_error > 0){
                                System.out.println("Lexer Error did not fully compile :( \n");
                            }else{
                                System.out.println("Done");
                                System.out.println("Paser starting :)");
                                Comp_Paser.Parser_Start(Tokens_List);
                            } 
                }

                //then clearing the temp before i the compiler pulls in a new string
                Tokens_List.clear();
            }
        }

        System.exit(0);
    }
}
//as in the lab 0 build enviorment there is a txt file that we have to be able to get info from, using the command line
//these are the classes used to so that i can give output based on what is in the test.txt file

import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 

public class Compiler{
    public static void main(String[] args){
        try {
            //takes in the argument from command line as the file name
            File commandTXT = new File(args[0]);

            //gets the reading ready for the txt file useing a scanner breaking the data up into primative data types
            Scanner reader = new Scanner(commandTXT);

            //while there is data prints it all out
            while (reader.hasNextLine()) {
              String line = reader.nextLine();
              System.out.println(line);
            }

            //closses it all out makeing sure its all good
            reader.close();
        
        //have to add the FileNotFoundException in order for this to work/compile :)
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
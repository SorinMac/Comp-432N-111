import java.util.ArrayList;
import java.util.HashMap;

//plan is to go through the list again and do the for loop like semantic anaylsis
//when see a delcare, print, assign (go to appropreate function)
//then right the code as hex ints

//need to figure out
//how to find and replace?
    //go through array and look for the key then replace with address
//how to save the pointers to the values?
    //the address part of the hashmap gets changed to the address that it is actuall at
    //how do i do that then?

public class Comp_CodeGen {
    static int CODE_SIZE = 256;

    public class address_dets {
        String name;
        int address;

        address_dets(){
            this.name = "";
            this.address = 0;
        }
        
    }

    public void start_codegen(){
        int[] code_array = new int[CODE_SIZE];
        HashMap<String, address_dets> variables = new HashMap<>();

        initialize_code(code_array);

    }

    public void initialize_code(int[] code_array){
        for(int i = 0; i < CODE_SIZE; i++){
            code_array[i] = 0x00;
        }
    }

    //need a way to create unique names for the hash map and pass it in 
    public void declare(){

    }

    //slide 11
    public void assign(){

    }
    
    //slide 11  
    public void print(){

    }
}

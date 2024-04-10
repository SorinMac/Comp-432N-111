import java.util.Arrays;
import java.util.HashMap;

//plan is to go through the list again and do the for loop like semantic anaylsis
//when see a delcare, print, assign (go to appropreate function)
//then right the code as hex ints

//need to figure out
//how to find and replace?
    //go through array and look for the key then replace with address
    //have to swithc how the hashmap works in orde to get this to work

    //swithc from the key value being the temp and not the varianle itself seem
//heap and stack crash
    //values to hold the code_place at the points in which there at the end 

public class Comp_CodeGen {
    static int CODE_SIZE = 256;
    static Comp_SymbolTable Comp_SymbolTable = new Comp_SymbolTable();
    Comp_AST Comp_AST = new Comp_AST();
    HashMap<String, address_dets> variables = new HashMap<>();
    String[] code_array = new String[CODE_SIZE];
    int unique_number = 0;
    int code_place = 0;
    

    public class address_dets {
        String temp_name;
        int address;

        address_dets(String temp_name){
            this.temp_name = temp_name;
            this.address = 0;
        }
        
    }

    public void start_codegen(Comp_AST.Tree_Node AST, Comp_SymbolTable.Symbol_Scope SymboleTable){
        int stack_end = 0;
        int varaibles_decl_end = 0;
        int heap_end = 0;

        initialize_code(code_array);

        for(int i = 0; i < AST.children.size(); i++){
            if(AST.children.get(i).name.equals("var_decl")){
                declare(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("assignment_statment")){
                assign(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("print_statment")){
                print(AST.children.get(i));
            }
        }

        System.out.println(Arrays.toString(code_array));
        

    }

    public void initialize_code(String[] code_array){
        for(int i = 0; i < CODE_SIZE; i++){
            code_array[i] = "00";
        }
    }

    //need a way to create unique names for the hash map and pass it in 
    public void declare(Comp_AST.Tree_Node AST_Node){
        String uniqueValue = "";

        code_array[code_place] = "A9";
        code_place++;
        code_array[code_place] = "00";
        code_place++;
        code_array[code_place] = "8D";
        code_place++;

        uniqueValue = "T" + unique_number;
        unique_number++;

        code_array[code_place] = uniqueValue;
        code_place++;

        code_array[code_place] = "XX";
        code_place++;

        address_dets temp  = new address_dets(uniqueValue);

        variables.put(AST_Node.children.get(1).name, temp);
    }

    //slide 11
    public void assign(Comp_AST.Tree_Node AST_Node){
        String uniqueValue = "";

        code_array[code_place] = "A9";
        code_place++;

        if(AST_Node.children.get(1).name.matches("[a-z]?")){
            uniqueValue = variables.get(AST_Node.children.get(1).name).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;
        }else{
            code_array[code_place] = "0" + AST_Node.children.get(1).name;
            code_place++;
        }
        code_array[code_place] = "8D";
        code_place++;

        uniqueValue = variables.get(AST_Node.children.get(0).name).temp_name;
        code_array[code_place] = uniqueValue;
        code_place++;
        code_array[code_place] = "XX";
        code_place++;
    }
    
    //slide 11  
    public void print(Comp_AST.Tree_Node AST_Node){
        String uniqueValue = "";

        code_array[code_place] = "AC";
        code_place++;

        uniqueValue = variables.get(AST_Node.children.get(1).name).temp_name;
        code_array[code_place] = uniqueValue;
        code_place++;
        code_array[code_place] = "XX";
        code_place++;

        code_array[code_place] = "A2";
        code_place++;

        code_array[code_place] = "01";
        code_place++;

        code_array[code_place] = "FF";
        code_place++;

    
    }
}

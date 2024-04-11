import java.util.HashMap;

//plan is to go through the list again and do the for loop like semantic anaylsis
//when see a delcare, print, assign (go to appropreate function)
//then right the code as hex ints

//need to figure out
//heap and stack crash
    //values to hold the code_place at the points in which there at the end 
//add the scopes to the variables
//handle scoop?

//if is very wrong seems to be missing a lot of stuff

public class Comp_CodeGen {
    static int CODE_SIZE = 256;
    static Comp_SymbolTable Comp_SymbolTable = new Comp_SymbolTable();
    Comp_AST Comp_AST = new Comp_AST();
    HashMap<String, address_dets> variables = new HashMap<>();
    String[] code_array = new String[CODE_SIZE];
    boolean check  = true;
    int unique_number = 0;
    int code_place = 0;
    

    public class address_dets {
        String temp_name;
        String address;

        address_dets(String temp_name){
            this.temp_name = temp_name;
            this.address = "0";
        }
        
    }

    public void start_codegen(Comp_AST.Tree_Node AST, Comp_SymbolTable.Symbol_Scope SymboleTable){
        int stack_end = 0;
        int varaibles_decl_end = 0;
        int heap_end = 0;

        if(check == true){
            initialize_code(code_array);
            check = false;
        }

        for(int i = 0; i < AST.children.size(); i++){
            if(AST.children.get(i).name.equals("var_decl")){
                declare(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("assignment_statment")){
                assign(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("print_statment")){
                print(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("if_statment")){
                if_state(AST.children.get(i));
                start_codegen(AST.children.get(i).children.get(AST.children.get(i).children.size()-1), SymboleTable);
            }else if(AST.children.get(i).name.equals("$")){
                stack_end = code_place;
                if(stack_end >= 256){
                    System.out.println("Error to many bit not able to be ran :(");
                }
                initialize_varables_place();
                varaibles_decl_end = code_place;
                if(varaibles_decl_end >= 256){
                    System.out.println("Error to many bit not able to be ran :(");
                }
                find_and_replace();

                for(int s = 0 ; s < code_array.length; s ++){
                    code_array[i] = code_array[i].toUpperCase();
                }

                System.out.println("[" + String.join(" ", code_array) + "]");
                initialize_code(code_array);
            }
        }
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
            code_array[code_place-1] = "AD";
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

    public void if_state(Comp_AST.Tree_Node AST_Node){
        String uniqueValue = "";
        int if_place = 0;

        code_array[code_place] = "AE";
        code_place++;

        for(int i = 0; i < AST_Node.children.size(); i++){
            if(AST_Node.children.get(i).name.matches("[a-z]?")){
                uniqueValue = variables.get(AST_Node.children.get(i).name).temp_name;
                if_place = i;
                code_array[code_place] = uniqueValue;
                code_place++;
                code_array[code_place] = "XX";
                code_place++;
                break;
            }else if(AST_Node.children.get(i).name.matches("[0-9]?")){
                code_array[code_place] = "0" + AST_Node.children.get(i).name;
                code_place++;
                if_place = i;
                break;
            }
        }

        code_array[code_place] = "EC";
        code_place++;

        for(int i = if_place+1; i < AST_Node.children.size(); i++){
            if(AST_Node.children.get(i).name.matches("[a-z]?")){
                uniqueValue = variables.get(AST_Node.children.get(i).name).temp_name;
                if_place = 0;
                code_array[code_place] = uniqueValue;
                code_place++;
                code_array[code_place] = "XX";
                code_place++;
                break;
            }else if(AST_Node.children.get(i).name.matches("[0-9]?")){
                code_array[code_place] = "0" + AST_Node.children.get(i).name;
                code_place++;
                if_place = 0;
                break;
            }
        }
        code_array[code_place] = uniqueValue;
        code_place++;
        code_array[code_place] = "XX";
        code_place++;

        code_array[code_place] = "D0";
        code_place++;

        code_array[code_place] = "J0";
        code_place++;
    }

    public void initialize_varables_place(){
        code_place++;
        String[] lookup_varaibles = new String[variables.keySet().size()];
        lookup_varaibles = variables.keySet().toArray(lookup_varaibles);

        for(int i = 0; i < lookup_varaibles.length; i++){
            String check = Integer.toHexString(code_place);

            if(check.length() < 2){
                check = "0" + Integer.toHexString(code_place);
            }

            variables.get(lookup_varaibles[i]).address = check;
            code_place++;
        }
    }

    public void find_and_replace(){
        String[] lookup_varaibles = new String[variables.keySet().size()];
        lookup_varaibles = variables.keySet().toArray(lookup_varaibles);

        for(int i = 0; i < CODE_SIZE; i++){
            if(code_array[i].equals("XX")){
                code_array[i] = "00";
            }else{
                for(int s = 0; s < variables.keySet().size(); s++){
                    if(variables.get(lookup_varaibles[s]).temp_name.equals(code_array[i])){
                        code_array[i] = variables.get(lookup_varaibles[s]).address;
                    }
                }
            }
        }
    }
}

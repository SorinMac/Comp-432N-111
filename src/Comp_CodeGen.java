import java.util.HashMap;

//plan is to go through the list again and do the for loop like semantic anaylsis
//when see a delcare, print, assign (go to appropreate function)
//then right the code as hex ints

//need to figure out
//add the scopes to the variables
//handle scoop?
//does not update distance correctly yet see line 83

//heap and stack crash
    //values to hold the code_place at the points in which there at the end 

public class Comp_CodeGen {
    static int CODE_SIZE = 256;
    static Comp_SymbolTable Comp_SymbolTable = new Comp_SymbolTable();
    Comp_AST Comp_AST = new Comp_AST();
    HashMap<String, address_dets> variables = new HashMap<>();
    HashMap<String, distance_dets> distance = new HashMap<>();
    String[] code_array = new String[CODE_SIZE];
    String[] bool_setup = {"74", "72", "75", "65" , "00", "66", "61", "6C", "73", "65", "00"};
    boolean check  = true;
    int unique_number = 0;
    int distance_traveled_number = 0;
    int code_place = 0;
    int heap_start = 0;
    String true_pointer = "";
    String false_pointer = "";
    

    public class address_dets {
        String temp_name;
        String address;

        address_dets(String temp_name){
            this.temp_name = temp_name;
            this.address = "0";
        }
        
    }

    public class distance_dets {
        int place;
        boolean porcessed;

        distance_dets(int place){
            this.place = place;
            porcessed = false;
        }
    }

    public void start_codegen(Comp_AST.Tree_Node AST, Comp_SymbolTable.Symbol_Scope SymboleTable){
        String printout = "";
        int stack_end = 0;
        int varaibles_decl_end = 0;
        int heap_end = 0;

        if(check == true){
            initialize_code(code_array);
            check = false;
        }

        set_bool();

        for(int i = 0; i < AST.children.size(); i++){
            if(AST.children.get(i).name.equals("var_decl")){
                declare(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("assignment_statment")){
                assign(AST.children.get(i));
            }else if(AST.children.get(i).name.equals("print_statment")){
                print(AST.children.get(i), SymboleTable);
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

                find_and_replace_variables();
                find_and_replace_distances();
                output_code(printout);

                initialize_code(code_array);
            }
        }

        String[] distance_values = new String[distance.keySet().size()];
        distance_values = distance.keySet().toArray(distance_values);

        for(int o = 0; o < distance_values.length; o++){
            if(distance.get(distance_values[o]).porcessed == false){
                distance.get(distance_values[o]).place = code_place - distance.get(distance_values[o]).place;
                distance.get(distance_values[o]).porcessed = true;
            }
        }
    }

    public void initialize_code(String[] code_array){
        for(int i = 0; i < CODE_SIZE; i++){
            code_array[i] = "00";
        }
    }

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
    //need this to assign for bools and intops
    public void assign(Comp_AST.Tree_Node AST_Node){
        if(AST_Node.children.get(1).name.matches("[0-9]?")){
            String uniqueValue = "";

            code_array[code_place] = "A9";
            code_place++;
            code_array[code_place] = "0" + AST_Node.children.get(1).name;
            code_place++;
            code_array[code_place] = "8D";
            code_place++;
            uniqueValue = variables.get(AST_Node.children.get(0).name).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;

        }else if(AST_Node.children.get(1).name.contains("\"")){
            String uniqueValue = "";
            String assign_setup = AST_Node.children.get(1).name;
            StringBuilder assign_string = new StringBuilder();
            assign_string.append(assign_setup);
            assign_string.reverse();

            code_array[code_place] = "A9";
            code_place++;

            set_string_heap(assign_string);

            code_array[code_place] = Integer.toHexString(heap_start);
            code_place++;

            heap_start = heap_start - assign_string.length()+1;
            
            code_array[code_place] = "8D";
            code_place++;

            uniqueValue = variables.get(AST_Node.children.get(0).name).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;

        }else if(AST_Node.children.get(1).name.matches("[a-z]?")){
            String uniqueValue = "";

            code_array[code_place] = "A9";
            code_place++;
            code_array[code_place-1] = "AD";
            uniqueValue = variables.get(AST_Node.children.get(1).name).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;
            code_array[code_place] = "8D";
            code_place++;
            uniqueValue = variables.get(AST_Node.children.get(0).name).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;
            
        }else if(AST_Node.children.get(1).name.matches("true|false")){
            
        }
    }
    

    //print intops
    public void print(Comp_AST.Tree_Node AST_Node, Comp_SymbolTable.Symbol_Scope SymboleTable){

        if(AST_Node.children.get(1).name.matches("[0-9]?")){
            code_array[code_place] = "A0";
            code_place++;
            code_array[code_place] = "0" + AST_Node.children.get(1).name;
            code_place++;
            code_array[code_place] = "A2";
            code_place++;
            code_array[code_place] = "01";
            code_place++;
            code_array[code_place] = "FF";
            code_place++;
        }else if(AST_Node.children.get(1).name.contains("\"")){
            String print_out_string = AST_Node.children.get(1).name;
            StringBuilder print_string = new StringBuilder();
            print_string.append(print_out_string);
            print_string.reverse();

            code_array[code_place] = "A0";
            code_place++;

            set_string_heap(print_string);
            
            heap_start = heap_start - print_string.length()+1;

            code_array[code_place] = Integer.toHexString(heap_start);
            code_place++;
            code_array[code_place] = "A2";
            code_place++;
            code_array[code_place] = "02";
            code_place++;
            code_array[code_place] = "FF";
            code_place++;
        }else if(AST_Node.children.get(1).name.matches("[a-z]?")){
            String check = getVariableType(SymboleTable, AST_Node.children.get(1).name);

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

            if(check.matches("string|boolean")){
                code_array[code_place] = "02";
                code_place++;
            }else{
                code_array[code_place] = "01";
                code_place++;
            }

            code_array[code_place] = "FF";
            code_place++;
        }else if(AST_Node.children.get(1).name.matches("true|false")){
            if(AST_Node.children.get(1).name.matches("true")){
                code_array[code_place] = "A0";
                code_place++;
                code_array[code_place] = true_pointer;
                code_place++;
                code_array[code_place] = "A2";
                code_place++;
                code_array[code_place] = "02";
                code_place++;
                code_array[code_place] = "FF";
                code_place++;
            }else{
                code_array[code_place] = "A0";
                code_place++;
                code_array[code_place] = false_pointer;
                code_place++;
                code_array[code_place] = "A2";
                code_place++;
                code_array[code_place] = "02";
                code_place++;
                code_array[code_place] = "FF";
                code_place++;
            }
        }
    }

    //will get to next week
    public void if_state(Comp_AST.Tree_Node AST_Node){
        String uniqueValue = "";
        String distance_variable = "";
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

        code_array[code_place] = "D0";
        code_place++;

        distance_dets temp_distance = new distance_dets(code_place);
        distance_variable = "J" + distance_traveled_number;
        distance.put(distance_variable, temp_distance);
        code_array[code_place] = distance_variable;
        distance_traveled_number++;
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

    public void find_and_replace_variables(){
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

    public void find_and_replace_distances(){
        String[] lookup_distance = new String[distance.keySet().size()];
        lookup_distance = distance.keySet().toArray(lookup_distance);

        for(int i = 0; i < CODE_SIZE; i++){
            for(int s = 0; s < distance.keySet().size(); s++){
                if(lookup_distance[s].equals(code_array[i])){
                    code_array[i] = "0" + Integer.toString(distance.get(lookup_distance[s]).place);
                }
            }
        }
    }

    public void set_bool(){
        for(int i = 0; i < bool_setup.length; i++){
            if(bool_setup[(bool_setup.length-1)-i].equals("74")){
                true_pointer = Integer.toHexString((CODE_SIZE-1)-i);
            }else if(bool_setup[(bool_setup.length-1)-i].equals("66")){
                false_pointer = Integer.toHexString((CODE_SIZE-1)-i);
            }
            code_array[(CODE_SIZE-1)-i] = bool_setup[(bool_setup.length-1)-i];
        }

        heap_start = CODE_SIZE - bool_setup.length;
    }

    public void set_string_heap(StringBuilder set_this_string){
        for(int i = 0; i < set_this_string.length(); i++){
            if(set_this_string.charAt(i) != '\"'){
                char temp = set_this_string.charAt(i);
                code_array[heap_start-(i+1)] = Integer.toHexString((int) temp);
            }
        }
        
        heap_start = heap_start - set_this_string.length()+1;
    }

    public void output_code(String printout){
        printout = "This is the code for program: ";
        for(int s = 0; s < CODE_SIZE; s++){
            if(s % 8 == 0){
                printout = printout + "\n";
            }
            printout = printout + code_array[s] + " ";
        }
        System.out.println(printout);
    }

    private String getVariableType(Comp_SymbolTable.Symbol_Scope Blocks, String variableName){  
        String type = "";

        for(int i = 0; i < Blocks.Scopes.size(); i++){
            if(Blocks.Scopes.get(i).values.containsKey(variableName)){
                type = Blocks.Scopes.get(i).values.get(variableName).name;
            }
        }    
        return type;
    }
}

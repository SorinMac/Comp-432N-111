import java.util.HashMap;

//need to figure out

//heap and stack crash
    //values to hold the code_place at the points in which there at the end 
//if and while
//intop assignment


public class Comp_CodeGen {
    //constant for the size of the code
    static final int CODE_SIZE = 256;

    //allows this file to get the symbole table from the Comp_SymbolTable file
    //allows this file to get the AST from the Comp_AST file
    static Comp_SymbolTable Comp_SymbolTable = new Comp_SymbolTable();
    Comp_AST Comp_AST = new Comp_AST();

    //hashmaps to hold the values stuff like temp name and other data
    //the second one is to old the distance for if and while statments
    HashMap<String, address_dets> variables = new HashMap<>();
    HashMap<String, distance_dets> distance = new HashMap<>();

    //the code array itself
    String[] code_array = new String[CODE_SIZE];
    String[] bool_setup = {"74", "72", "75", "65" , "00", "66", "61", "6C", "73", "65", "00"}; //set up for true and false static values

    boolean check  = true; //this is to set up the array only once
    int unique_number = 0; //this is used to make the unique names
    int distance_traveled_number = 0; //this is used to make the distance travled unqiue value
    int code_place = 0; //the place that you are at in the code array
    int heap_start = 0; //where the heap will start
    String true_pointer = ""; //static for true pointer to the start of the true ouput in the heap
    String false_pointer = ""; //static for false pointer to the start of the false ouput in the heap
    int scope_place = 0;
    int intop_place = 0;
    int intop_run = 0;
    
    //class to hold all the details of the address stuff for variables hashmap
    public class address_dets {
        String temp_name;
        String address;

        address_dets(String temp_name){
            this.temp_name = temp_name;
            this.address = "0";
        }
        
    }

    //class to hold all the details of the distance stuff for distance hashmap
    public class distance_dets {
        int place;
        boolean porcessed;

        distance_dets(int place){
            this.place = place;
            porcessed = false;
        }
    }

    //start of the code gen
    public void start_codegen(Comp_AST.Tree_Node AST, Comp_SymbolTable.Symbol_Scope SymboleTable){
        String printout = ""; //the code that will need to get printed out
        int stack_end = 0; //keep track of the stack end
        int varaibles_decl_end = 0; //keep track of the variable declare end
        int heap_end = 0; //keep track of the heap

        //will do the first intilize
        if(check == true){
            initialize_code(code_array);
            check = false;
            //sets up where the bools need to be
            set_bool();
        }

        //goes through the AST and starts making code for everything
        for(int i = 0; i < AST.children.size(); i++){
            if(AST.children.get(i).name.equals("var_decl")){
                declare(AST.children.get(i), SymboleTable.Scopes.get(scope_place), SymboleTable);
            }else if(AST.children.get(i).name.equals("assignment_statment")){
                assign(AST.children.get(i), SymboleTable.Scopes.get(scope_place), SymboleTable);
            }else if(AST.children.get(i).name.equals("print_statment")){
                if(scope_place == SymboleTable.Scopes.size()){
                    print(AST.children.get(i), SymboleTable, SymboleTable.Scopes.get(scope_place-1));
                }else{
                    print(AST.children.get(i), SymboleTable, SymboleTable.Scopes.get(scope_place));
                }
            }else if(AST.children.get(i).name.equals("if_statment")){
                if_state(AST.children.get(i));
                start_codegen(AST.children.get(i), SymboleTable); //work in progress
            }else if(AST.children.get(i).name.equals("block")){
                //go foward in scope but not backwordws
                scope_place++;
                start_codegen(AST.children.get(i), SymboleTable);
                scope_place--;
            }else if(AST.children.get(i).name.equals("$")){

                HashMap<String, address_dets> a = variables;
                //work in progress
                /*stack_end = code_place;
                if(stack_end >= 256){
                    System.out.println("Error to many bit not able to be ran :(");
                }*/

                initialize_varables_place(); //gets the true places of the values

                //work in progress
                /*varaibles_decl_end = code_place;
                if(varaibles_decl_end >= 256){
                    System.out.println("Error to many bit not able to be ran :(");
                }*/

                find_and_replace_variables(); //replaces the temp name with the real ones

                //work in progress
                /*heap_end = heap_start;
                if(heap_end <= 256){
                    System.out.println("Error to many bit not able to be ran :(");
                }*/

                //work in progress
                find_and_replace_distances();

                output_code(printout); //prints out all the code
                
                //resets all necessary values
                initialize_code(code_array); 
                check = true; 
                unique_number = 0; 
                distance_traveled_number = 0;
                code_place = 0; 
                heap_start = 0; 
                true_pointer = ""; 
                false_pointer = ""; 
                variables.clear();
                distance.clear();
                scope_place = 0;
    
            }
            
        }

        //work in progess
        String[] distance_values = new String[distance.keySet().size()];
        distance_values = distance.keySet().toArray(distance_values);

        for(int o = 0; o < distance_values.length; o++){
            if(distance.get(distance_values[o]).porcessed == false){
                distance.get(distance_values[o]).place = code_place - distance.get(distance_values[o]).place;
                distance.get(distance_values[o]).porcessed = true;
            }
        }
    }

    //sets all thing in the code_array array to 00
    public void initialize_code(String[] code_array){
        for(int i = 0; i < CODE_SIZE; i++){
            code_array[i] = "00";
        }
    }

    //how to declare something
    public void declare(Comp_AST.Tree_Node AST_Node, Comp_SymbolTable.Symbole_Node current_scope, Comp_SymbolTable.Symbol_Scope SymboleTable){
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

        variables.put(AST_Node.children.get(1).name + "@" + getScope(SymboleTable, AST_Node.children.get(1).name, scope_place), temp);
    }

    //need this to assign intops
    public void assign(Comp_AST.Tree_Node AST_Node, Comp_SymbolTable.Symbole_Node current_scope, Comp_SymbolTable.Symbol_Scope SymboleTable){
        if(AST_Node.children.get(1).name.matches("[0-9]?") || AST_Node.children.get(1).name.equals("+")){
            if(AST_Node.children.get(1).name.equals("+")){
                String uniqueValue = "";
                intop_Code(AST_Node.children.get(1), current_scope, SymboleTable);

                code_array[code_place] = "8D";
                code_place++;

                uniqueValue = variables.get(AST_Node.children.get(0).name + "@" + getScope(SymboleTable, AST_Node.children.get(0).name, scope_place)).temp_name;
                code_array[code_place] = uniqueValue;
                code_place++;
                code_array[code_place] = "XX";
                code_place++;
            }else{
                String uniqueValue = "";

                code_array[code_place] = "A9";
                code_place++;
                code_array[code_place] = "0" + AST_Node.children.get(1).name;
                code_place++;
                code_array[code_place] = "8D";
                code_place++;
                uniqueValue = variables.get(AST_Node.children.get(0).name + "@" + getScope(SymboleTable, AST_Node.children.get(0).name, scope_place)).temp_name;
                code_array[code_place] = uniqueValue;
                code_place++;
                code_array[code_place] = "XX";
                code_place++;
            }

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
            
            code_array[code_place] = "8D";
            code_place++;

            uniqueValue = variables.get(AST_Node.children.get(0).name + "@" + getScope(SymboleTable, AST_Node.children.get(0).name, scope_place)).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;

        }else if(AST_Node.children.get(1).name.matches("[a-z]?")){
            String uniqueValue = "";

            code_array[code_place] = "A9";
            code_place++;
            code_array[code_place-1] = "AD";
            uniqueValue = variables.get(AST_Node.children.get(1).name + "@" + getScope(SymboleTable, AST_Node.children.get(1).name, scope_place)).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;
            code_array[code_place] = "8D";
            code_place++;
            uniqueValue = variables.get(AST_Node.children.get(0).name + "@" + getScope(SymboleTable, AST_Node.children.get(0).name, scope_place)).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;
            
        }else if(AST_Node.children.get(1).name.matches("true|false")){
            String uniqueValue = "";

            code_array[code_place] = "A9";
            code_place++;
            
            if(AST_Node.children.get(1).name.matches("true")){
                code_array[code_place] = true_pointer;
                code_place++;
            }else{
                code_array[code_place] = false_pointer;
                code_place++;
            }

            code_array[code_place] = "8D";
            code_place++;
            uniqueValue = variables.get(AST_Node.children.get(0).name + "@" + getScope(SymboleTable, AST_Node.children.get(0).name, scope_place)).temp_name;
            code_array[code_place] = uniqueValue;
            code_place++;
            code_array[code_place] = "XX";
            code_place++;
        }
    }
    

    //print intops
    public void print(Comp_AST.Tree_Node AST_Node, Comp_SymbolTable.Symbol_Scope SymboleTable, Comp_SymbolTable.Symbole_Node current_scope){

        if(AST_Node.children.get(1).name.matches("[0-9]?") || AST_Node.children.get(1).name.equals("+")){

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
            }else{
                intop_Code(AST_Node.children.get(1), current_scope, SymboleTable);

                code_array[code_place] = "8D";
                code_place++;
                code_array[code_place] = "00";
                code_place++;
                code_array[code_place] = "00";
                code_place++;

                code_array[code_place] = "AC";
                code_place++;
                code_array[code_place] = "00";
                code_place++;
                code_array[code_place] = "00";
                code_place++;

                code_array[code_place] = "A2";
                code_place++;
                code_array[code_place] = "01";
                code_place++;
                code_array[code_place] = "FF";
                code_place++;
            }
        }else if(AST_Node.children.get(1).name.contains("\"")){
            String print_out_string = AST_Node.children.get(1).name;
            StringBuilder print_string = new StringBuilder();
            print_string.append(print_out_string);
            print_string.reverse();

            code_array[code_place] = "A0";
            code_place++;

            set_string_heap(print_string);

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
            uniqueValue = variables.get(AST_Node.children.get(1).name + "@" + getScope(SymboleTable, AST_Node.children.get(1).name, scope_place)).temp_name;
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

    public void if_state(Comp_AST.Tree_Node AST_Node){ //will handle the if statment stuff
        //8D 00 00 EC 00 00 D0 05
        //what is the 8D 00 00 and EC 00 00 DO 05 what does that do
            //can this be static or need to change with the program as well as the A2
        //the scope afterwords is just completely breaking 

        String distance_variable = "";
        int if_place = 0;

        //is this static or changing
        code_array[code_place] = "A2";
        code_place++;

        for(int i = 0; i < AST_Node.children.size(); i++){
            if(AST_Node.children.get(i).name.matches("[0-9]?")){
                code_array[code_place] = "0" + AST_Node.children.get(i).name;
                code_place++;
                if_place = i;
                break;
            }
        }

        code_array[code_place] = "A9";
        code_place++;

        for(int i = if_place+1; i < AST_Node.children.size(); i++){
           if(AST_Node.children.get(i).name.matches("[0-9]?")){
                code_array[code_place] = "0" + AST_Node.children.get(i).name;
                code_place++;
                if_place = 0;
                break;
            }
        }

        code_array[code_place] = "8D";
        code_place++;
        code_array[code_place] = "00";
        code_place++;
        code_array[code_place] = "00";
        code_place++;
        code_array[code_place] = "EC";
        code_place++;
        code_array[code_place] = "00";
        code_place++;
        code_array[code_place] = "00";
        code_place++;


        code_array[code_place] = "D0";
        code_place++;

        distance_dets temp_distance = new distance_dets(code_place);
        distance_variable = "J" + distance_traveled_number;
        distance.put(distance_variable, temp_distance);
        code_array[code_place] = distance_variable;
        distance_traveled_number++;
        code_place++;
    }

    //will set the place to its true place rather than the temp varaibles
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

    //will perform the find a repalce for the temp to there true place
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

    //will perfrom the find and replace of the distance that is need to go to if there is a not equals or equals
    public void find_and_replace_distances(){
        String[] lookup_distance = new String[distance.keySet().size()];
        lookup_distance = distance.keySet().toArray(lookup_distance);

        for(int i = 0; i < CODE_SIZE; i++){
            for(int s = 0; s < distance.keySet().size(); s++){
                if(lookup_distance[s].equals(code_array[i])){
                    if(Integer.toString(distance.get(lookup_distance[s]).place).length() >= 2){
                        code_array[i] = Integer.toString(distance.get(lookup_distance[s]).place-1);
                    }else{
                        code_array[i] = "0" + Integer.toString(distance.get(lookup_distance[s]).place-1);
                    }
                }
            }
        }
    }

    //set up where the bools are and then change the heap_start place to match the change
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

    //set up the string and then change the heap_start place to make the change
    public void set_string_heap(StringBuilder set_this_string){
        for(int i = 0; i < set_this_string.length(); i++){
            if(set_this_string.charAt(i) != '\"'){
                char temp = set_this_string.charAt(i);
                code_array[heap_start-(i+1)] = Integer.toHexString((int) temp);
            }
        }

        heap_start = heap_start - set_this_string.length()+1;
    }


    //will output the code 
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

    //will get the varaible type to check wheather load a 02 or 01
    private String getVariableType(Comp_SymbolTable.Symbol_Scope Blocks, String variableName){  
        String type = "";

        for(int i = 0; i < Blocks.Scopes.size(); i++){
            if(Blocks.Scopes.get(i).values.containsKey(variableName)){
                type = Blocks.Scopes.get(i).values.get(variableName).name;
            }
        }    
        return type;
    }

    //will get the scope of the variable
    private int getScope(Comp_SymbolTable.Symbol_Scope Blocks, String variableName, int scope){  
        int start_Scope = scope;

        for(int i = start_Scope; i >= 0; i--){
            if(Blocks.Scopes.get(i).values.containsKey(variableName)){
                scope = Blocks.Scopes.get(i).scope;
            }
        }    
        return scope;
    }

    private void intop_Code(Comp_AST.Tree_Node AST_Node, Comp_SymbolTable.Symbole_Node current_scope, Comp_SymbolTable.Symbol_Scope SymboleTable){
        
        if(intop_run == 0){
            code_array[code_place] = "A9";
            code_place++;

            code_array[code_place] = "0" + AST_Node.children.get(intop_place).name;
            intop_place++;
            code_place++;

            code_array[code_place] = "8D";
            code_place++;

            code_array[code_place] = "00";
            code_place++;
            code_array[code_place] = "00";
            code_place++;

        }else if(intop_run > 0){
            if(AST_Node.children.get(intop_place).name.matches("[a-z]?")){
                code_array[code_place] = "6D";
                code_place++;

                String uniqueValue = "";

                uniqueValue = variables.get(AST_Node.children.get(1).name + "@" + getScope(SymboleTable, AST_Node.children.get(1).name, scope_place)).temp_name;
                code_array[code_place] = uniqueValue;
                code_place++;
                code_array[code_place] = "XX";
                code_place++;

                intop_place++;
            }else{
                code_array[code_place] = "A9";
                code_place++;

            
                code_array[code_place] = "0" + AST_Node.children.get(intop_place).name;
                intop_place++;
                code_place++;
                

                code_array[code_place] = "6D";
                code_place++;

                code_array[code_place] = "00";
                code_place++;
                code_array[code_place] = "00";
                code_place++;

                code_array[code_place] = "8D";
                code_place++;

                code_array[code_place] = "00";
                code_place++;
                code_array[code_place] = "00";
                code_place++;
            }
        }

        if(intop_place == AST_Node.children.size()){
            intop_place = 0;
            intop_run = 0;
        }else{
            if(AST_Node.children.get(intop_place).name.matches("[0-9]?") || AST_Node.children.get(intop_place).name.equals("+") || AST_Node.children.get(intop_place).name.matches("[a-z]?")){
                if(AST_Node.children.get(intop_place).name.equals("+")){
                    intop_place++;
                    intop_run++;
                    intop_Code(AST_Node, current_scope, SymboleTable);
                }else{
                    intop_run++;
                    intop_Code(AST_Node, current_scope, SymboleTable);
                }
            }
        }
    }
}

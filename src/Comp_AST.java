//go through the token list only if parse was okau
//when you see a { make a new block and when you see other symbols that is the thing that you want to add
//the rest is dependent on what you see if its a = you want to get the two things around the =
//other things like string and char and print statment are going to be whole list of stuff


//make it a class where there is a parent and a child and then you just make the next block a child of the parent and then have leaf node
//(array list that will have all things in it)

import java.util.ArrayList;
import java.util.List;

public class Comp_AST {
    public class AST_Node{
        String item;
        int line_num;
        int line_place;

    }

    public class AST {
        AST_Node parent;
        AST_Node child;
        ArrayList<AST_Node> children;
        
    }
}

import java.util.HashMap;
import java.util.Map;

public class test {
    int Semantic_Num_Errors = 0;
    int Scope = 0;

    public class SymbolTableEntry {
        String name;
        boolean isUsed;
        boolean isInitialized;

        SymbolTableEntry(String name) {
            this.name = name;
            this.isUsed = false;
            this.isInitialized = false;
        }
    }

    public class SymbolTable {
        int scope;
        Map<String, SymbolTableEntry> entries;

        SymbolTable(int scope) {
            this.scope = scope;
            this.entries = new HashMap<>();
        }
    }

    public class SymbolScope {
        SymbolTable table;
        SymbolScope parent;

        SymbolScope(SymbolTable table, SymbolScope parent) {
            this.table = table;
            this.parent = parent;
        }
    }

    public void buildSymbolTable(Comp_AST.Tree_Node abstractSyntaxTree) {
        SymbolScope globalScope = new SymbolScope(new SymbolTable(Scope), null);
        processTree(abstractSyntaxTree, globalScope);
        printSymbolTable(globalScope);
    }

    private void processTree(Comp_AST.Tree_Node node, SymbolScope currentScope) {
        for (Comp_AST.Tree_Node child : node.children) {
            if (child.name.equals("var_decl")) {
                String varName = child.children.get(1).name;
                SymbolTableEntry entry = new SymbolTableEntry(varName);
                currentScope.table.entries.put(varName, entry);
            } else if (child.name.equals("block")) {
                Scope++;
                SymbolTable blockTable = new SymbolTable(Scope);
                SymbolScope blockScope = new SymbolScope(blockTable, currentScope);
                processTree(child, blockScope);
                currentScope = currentScope.parent;
            } else if (child.name.equals("assignment_statement")) {
                String varName = child.children.get(0).name;
                if (!isVariableDeclared(varName, currentScope)) {
                    // Check if the variable is declared in parent scopes
                    if (!isVariableDeclared(varName, currentScope.parent)) {
                        Semantic_Num_Errors++;
                        System.out.println("Error: Variable " + varName + " not declared.");
                    }
                }
            }
        }
    }

    private boolean isVariableDeclared(String name, SymbolScope scope) {
        SymbolScope currentScope = scope;
        while (currentScope != null) {
            if (currentScope.table.entries.containsKey(name)) {
                return true;
            }
            currentScope = currentScope.parent;
        }
        return false;
    }

    private void printSymbolTable(SymbolScope scope) {
        SymbolScope currentScope = scope;
        while (currentScope != null) {
            System.out.println("Scope: " + currentScope.table.scope);
            for (Map.Entry<String, SymbolTableEntry> entry : currentScope.table.entries.entrySet()) {
                String varName = entry.getKey();
                SymbolTableEntry symbol = entry.getValue();
                System.out.println("Variable: " + varName + ", Used: " + symbol.isUsed + ", Initialized: " + symbol.isInitialized);
            }
            // Recursively print symbol tables for inner scopes
            printSymbolTable(currentScope.parent);
            currentScope = currentScope.parent;
        }
    }
}

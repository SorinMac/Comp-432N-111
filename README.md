# Comp-432N-111

I have decided to develop this compiler in Java.

The project is stuctered by have the src file be the main file where all the code will be. Then inside this main folder is the
Compiler.Java file, this file is where the compiler will start out. Also since this is Java this is where the "main" method will
be. This function is inside of the class Compiler which matched the file name and is in the correct sytax for Java. How the file
take in input from the command line is through the args. When you run the commands anything after is in args and I can call either
args[0], args[1] and so on to get the data.

Steps to Compiling/Running:

1) run command javac src/Compiler.java

   This will compile the Compiler.Java file I have in the src folder and make a class file of the same name that can be ran at
   command line.

2) run command java -cp src Compiler

   This will run the class file of the same name

3) if you have arguments on command line it will be handled as so

   i.e.

   Command: java Compiler Hello World

   When this command is ran the Hello is args[0], and World is args[1]. I can then use this fact to do as I please with the arguments from command line.

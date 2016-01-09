/* Linnea Kirby
 * 7 January 2016
 * 
 * VirtualMachine
 * Solves the Synacor Challenge
 * Takes the binary file, parses it, and executes its instructions
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Stack;

public class VirtualMachine {
    protected int[] memory;
    protected int[] register;
    protected Stack<Integer> stack;
    protected int index;
    
    /*
     * Constructor 
     * Initializes the 3 storage regions and the counter
     */
    VirtualMachine(){
	memory = new int[(int) Math.pow(2,15)];
	register = new int[8];
	stack = new Stack<Integer>();
	index = 0;
    }
    
    /*
     * parseBin
     * Parses the bin file
     */
    void parseBin(){
	int counter = 0;
	try {
	    FileInputStream input = new FileInputStream("challenge.bin");
	    int c1 = input.read();
	    int c2 = input.read();
	    while((c1  != -1) && (c2 != -1)) {
		memory[counter] = c1+(c2*256);
		counter++;
		c1 = input.read();
		c2 = input.read();
	    }
	    input.close();
	} catch (FileNotFoundException e) {
	    System.out.println("File not found!");
	    e.printStackTrace();
	} catch (IOException e) {
	    System.out.println("I/O Problem!");
	    e.printStackTrace();
	}
    }
    
    /*
     * execute
     * Executes the commands parsed from the bin file
     */
    void execute() {
	while(true) {
	    //System.out.println("\nChoosing case "+memory[index]);
	    switch(memory[index]) {
	    	case 0: halt();
	    		break;
	    	case 1: set(memory[++index], memory[++index]);
	    	    	break;
	    	case 2: push(memory[++index]);
	    	    	break;
	    	case 3: pop(memory[++index]);
	    	    	break;
	    	case 4: eq(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 5: gt(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 6: jmp(memory[++index]);
	    	    	break;
	    	case 7: jt(memory[++index], memory[++index]);;
	    	    	break;
	    	case 8: jf(memory[++index], memory[++index]);
	    	    	break;
	    	case 9: add(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 10: mult(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 11: mod(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 12: and(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 13: or(memory[++index], memory[++index], memory[++index]);
	    	    	break;
	    	case 14: not(memory[++index], memory[++index]);
	    	    	break;
	    	case 15: rmem(memory[++index], memory[++index]);
	    	    	break;
	    	case 16: wmem(memory[++index], memory[++index]);
	    	    	break;
	    	case 17: call(memory[++index]);
	    	    	break;
	    	case 18: ret();
	    	    	break;
	    	case 19: out(memory[++index]);
	    	    	break;
	    	case 20: in(memory[++index]);
	    	    	break;
	    	case 21: noop();
	    	    	break;
	    	default: System.exit(0);
	    }
	}
    }
    
    /*
     * Halt - 0
     * Stops execution and terminates the program
     */
    void halt() {
	System.exit(0);
    }
    
    /*
     * set - 1
     * Sets register <a> to the value of <b>
     */
    void set(int a, int b) {
	//System.out.println("Case 1!");
	a = a%32768;
	b = checkReg(b);
	//System.out.println("Choosing register "+a+" which used to have "+register[a]+" and b is: "+b);
	register[a] = b;
	//System.out.println("In register "+a+" there is now: "+register[a]);
	index++;
    }
    
    /* 
     * push - 2
     * Pushes <a> onto the stack
     */
    void push(int a) {
	a = checkReg(a);
	stack.push(a);
	index++;
    }
    
    /* 
     * pop - 3
     * Removes the top element from the stack and writes it into <a>
     * Empty stack = error
     */
    void pop(int a) {
	a = a%32768;
	register[a] = stack.pop();
	index++;
    }
    
    /* 
     * eq - 4
     * Sets <a> to 1 if <b> is equal to <c>
     * Sets it to 0 otherwise
     */
    void eq(int a, int b, int c) {
	//System.out.println("Case 4!");
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	//System.out.println("a is: "+a+" b is: "+b+" and c is: "+c);
	if(b == c) {
	    register[a] = 1;
	}
	else {
	    register[a] = 0;
	}
	index++;
    }
    
    /* 
     * gt - 5
     * Sets <a> to 1 if <b> is greater than <c>
     * Sets it to 0 otherwise
     */
    void gt(int a, int b, int c) {
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	if(b > c) {
	    register[a] = 1;
	}
	else {
	    register[a] = 0;
	}
	index++;
    }
    
    /* 
     * jmp - 6
     * Jump to <a>
     */
    void jmp(int a) {
	index = a;
	//System.out.println(index);
    }
    
    /* 
     * jt - 7
     * If <a> is nonzero, jump to <b>
     */
    void jt(int a, int b) {
	//System.out.print("Index was: "+index+" ");
	a = checkReg(a);
	b = checkReg(b);
	if(a != 0) {
	    //System.out.println("Changing index!");
	    index = b;
	}
	else {
	    index++;
	}
	//System.out.println("a is: "+a+" b is: "+b+" and index is now: "+index);
    }
    
    /* 
     * jf - 8
     * If <a> is zero, jump to <b>
     */
    void jf(int a, int b) {
	//System.out.println("Case 8!");
	//System.out.print("Index was: "+index+" ");
	a = checkReg(a);
	b = checkReg(b);
	if(a == 0) {
	    //System.out.println("Changing index!");
	    index = b;
	}
	else {
	    index++;
	}
	//System.out.println("a is: "+a+" b is: "+b+" and index is now: "+index);
    }
    
    /*
     * add - 9
     * Assigns into <a> the sum of <b> and <c> (modulo 32768)
     */
    void add(int a, int b, int c) {
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	register[a] = (b+c)%32768;
	index++;
    }
    
    /* 
     * mult - 10
     * Stores into <a> the product of <b> and <c> (modulo 32768)
     */
    void mult(int a, int b, int c) {
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	register[a] = (b*c)%32768;
	index++;
    }
    
    /* 
     * mod - 11
     * Stores into <a> the remainder of <b> divided by <c>
     */
    void mod(int a, int b, int c) {
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	register[a] = b%c;
	index++;
    }
    
    /* 
     * and - 12
     * Stores into <a> the bitwise and of <b> and <c>
     */
    void and(int a, int b, int c) {
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	register[a] = (b&c)%32768;
	index++;
    }
    
    /* 
     * or - 13
     * Stores into <a> the bitwise or of <b> and <c>
     */
    void or(int a, int b, int c) {
	a = a%32768;
	b = checkReg(b);
	c = checkReg(c);
	register[a] = (b|c)%32768;
	index++;
    }
    
    /* 
     * not - 14
     * Stores 15-bit bitwise inverse of <b> in <a>
     */
    void not(int a, int b) {
	//System.out.println("Case 14!");
	a = a%32768;
	b = checkReg(b);
	//System.out.println("b is: "+b+" and bitwise not of b is: "+~b);
	b = ~b;
	if(b<0) {
	    b = 32768+b;
	}
	register[a] = b;
	index++;
    }
    
    /* 
     * rmem - 15
     * Reads memory address at <b> and writes it to <a>
     */
    void rmem(int a, int b) {
	//System.out.println("Case 15!");
	//System.out.println("a is: "+a+" and b is: "+b);
	a = a%32768;
	b = checkReg(b);
	register[a] = memory[b];
	//System.out.println("Register "+a+" now contains "+register[a]);
	index++;
    }
    
    /* 
     * wmem - 16
     * Writes the value from <b> into memory at address <a>
     */
    void wmem(int a, int b) {
	a = checkReg(a);
	b = checkReg(b);
	//System.out.println("a is: "+a+" and b is: "+b);
	memory[a] = b;
	index++;
    }
    
    /* 
     * call - 17
     * Writes the address of the next instruction to the stack and jumps to <a>
     */
    void call(int a) {
	a = checkReg(a);
	stack.push(++index);
	jmp(a);
    }
    
    /* 
     * ret - 18
     * Removes the top element from the stack and jumps to it
     * Empty stack = halt
     */
    void ret() {
	if(stack.isEmpty()) {
	    halt();
	}
	int e = stack.pop();
	jmp(e);
	
    }
    
    /*
     * out - 19
     * Writes the character represented by ascii code <a> to the terminal
     */
    void out(int a) {
	a = checkReg(a);
	System.out.print(Character.toString((char)a));
	index++;
    }
    
    /* 
     * in - 20
     * Reads a character from the terminal and writes its ascii code to <a>
     * It can be assumed that once input starts, it will continue until a newline is encountered
     * This means that you can safely read whole lines from the keyboard and trust they will be fully read
     */
    static boolean nextline = true;
    int c = 0;
    Scanner input = new Scanner(System.in);
    StringReader readline;
    void in(int a) {
	//System.out.println("\nCase 20!");
	a = a%32768;
	//System.out.println(a);
	//System.out.println(nextline);
	if(nextline) {
	    String line = input.nextLine();
	    line = line+"\n";
	    readline = new StringReader(line);
	    nextline = false;
	}
	try {
	    //System.out.println(line);
	    if(c != 10) {
		c = readline.read();
		//System.out.println(c);
		register[a] = (char)c;
		//System.out.println(a);
	    }
	    if(c == 10) {
		nextline = true;
		readline.close();
		//input.close();
		//break;
		//System.out.println((char)c);
		//c = input.read();
	    }
	    else {
		//register[a] = (char)c;
	    }
	    index++;
	} catch (IOException e) {
	    System.out.println("I/O Error!");
	    e.printStackTrace();
	}
    }
    
    /*
     * noop - 21
     * No operation
     */
    void noop() {
	index++;
    }
    
    /*
     * checkReg
     * Checks to see if r indicates a value store register
     * Returns either r or the value stored in the corresponding register
     */
    int checkReg(int r) {
	if(r >= 32768 && r <= 32775) {
	    r = register[r%32768];
	}
	//System.out.println("r is: "+r);
	return r;
    }
    
    /*
     * checkMem
     * Test to see how many numbers are out of bounds
     */
    void checkMem() {
	int count = 0;
	for(int i=0; i<memory.length; i++) {
	    if(memory[i] > 32776) {
		count++;
	    }
	}
	System.out.println(count+" numbers higher than allowed!");
    }
    
    public static void main(String[] args) {
	VirtualMachine vm = new VirtualMachine();
	vm.parseBin();
	//vm.checkMem();
	vm.execute();
    }

}

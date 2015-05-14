package paser;

import ast5.ProgramTool;
/**
 * Project6
 * @author Zhongshan Lu
 *
 */
public class Main
{
    public static void main(String[] args)
    {
    	visitorOne myVisitorOne=new visitorOne();
    	visitorTwo myVisitorTwo=new visitorTwo();
        ProgramTool tool = new ProgramTool();
        String code = "class A { int y; int a;int y; void main() { x = 4; y = 5; print(x); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        
        
        code = "class A { int y; void main() { x = 4; y = 5; print(x); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        
        code = "class A { int x; void main() { x = 4; y = 5; print(x); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        
        code = "class A { int x; void main() { x = 4; y = 5; print(x); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        code = "class A { int one; int one; int two; int three; void main() { one = 1; four = 4; five = 5; print(one); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        code = "class A { int x; void main() { x = 4; x = x + 3; print(x); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        code = "class A { void main() { x = 4; x = x + 3; this.foo(x); } void foo(int y) { print(y); }}";
        System.out.println(code);
        myVisitorOne.read(code);
        System.out.println(myVisitorOne.analyseVariables());
        myVisitorOne.clear();
        myVisitorTwo.read(code);
        System.out.println(myVisitorTwo.analyseVariables());
        myVisitorTwo.clear();
        
        //Add new test case here

    }
}



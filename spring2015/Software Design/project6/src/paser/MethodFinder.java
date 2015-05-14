/*
 * File: MethodFinder.java
 */
package paser;

import ast5.node.AssignStatement;
import ast5.node.Block;
import ast5.node.ClassDeclaration;
import ast5.node.EmptyStatement;
import ast5.node.Expression;
import ast5.node.IntLiteral;
import ast5.node.MethodCall;
import ast5.node.MethodDeclaration;
import ast5.node.NewAssignmentStatement;
import ast5.node.Node;
import ast5.node.PrintStatement;
import ast5.node.VarDeclaration;
import ast5.node.Visitor;
import ast5.node.WhileStatement;

/**
 * This class is able to search code for the existence of a particular method
 * in a particular class.  If class A has an inner class named B, then the
 * MethodFinder refers to the inner class as "A.B".
 */
public class MethodFinder implements Visitor
{
    private String className;
    private String methodName;

    /**
     * determines whether a program contains a method by the given name
     * in a class with the given name.
     * If the class is an inner class, then its full name including the outer
     * class names separated by periods is used.  For example, if class A
     * contains class B which contains class C, then the innermost class is
     * referred to by "A.B.C".
     *
     * @param methodName the name of the method we are searching for
     * @param className  the full name of the class containing the desired method
     * @param root       the root Node of the AST representing the program to be
     *                   searched
     * @return true if a method with the desired name is found in the desired
     *         class in the program represented by the AST with the given
     *         root.
     */
    public boolean findMethod(String methodName, String className, Node root)
    {
        this.methodName = methodName;
        this.className = className;
        return (Boolean) root.accept(this, "");
    }

    //--------------------------------------
    //In each visitClassDeclaration method, the second parameter o contains the outerclass
    //name.
    //--------------------------------------

    public Object visitClassDeclaration(ClassDeclaration node, Object o)
    {
        String outerClass = (String) o;
        for (Node node1 : node.getDeclarations()) {
            if((Boolean) node1.accept(this,
                                      (outerClass.equals("") ? node.getName() :
                                           outerClass + "." + node.getName())))
                return true;
        }
        return false;
    }

    public Object visitMethodDeclaration(MethodDeclaration node, Object o)
    {
        String fullClassName = (String) o;
        return (fullClassName.equals(this.className) &&
                node.getName().equals(this.methodName));
    }

    public Object visitVarDeclaration(VarDeclaration node, Object o)
    {
        return false;
    }

    public Object visitAssignStatement(AssignStatement node, Object o)
    {
        return false;
    }

    public Object visitBlock(Block node, Object o)
    {
        return false;
    }

    public Object visitEmptyStatement(EmptyStatement node, Object o)
    {
        return false;
    }

    public Object visitWhileStatement(WhileStatement node, Object o)
    {
        return false;
    }

    public Object visitMethodCall(MethodCall node, Object o)
    {
        return false;
    }

    public Object visitIntLiteral(IntLiteral node, Object o)
    {
        return false;
    }
    
    public Object visitPrintStatement(PrintStatement node, Object o)
    {
        return false;
    }  
    
    public Object visitExpression(Expression node, Object o)
    {
        return false;
    }
    
    public Object visitNewAssignmentStatement(NewAssignmentStatement node, Object o)
    {
        return false;
    }    
}

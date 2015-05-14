/*
 * File: DefaultBuilder.java
 */
package paser;

import java.util.List;

import ast5.node.Block;
import ast5.node.Expression;
import ast5.node.IntLiteral;
import ast5.node.NewAssignmentStatement;
import ast5.node.Node;
import ast5.node.VarDeclaration;

public class DefaultBuilder
{
    public IntLiteral buildIntLiteral(int value)
    {
        return null;  //stub
    }

    public Node buildAssignStatement(String name, Expression value)
    {
        return null;  //stub
    }

    public Node buildMethodCall(String name, List<String> args)
    {
        return null;  //stub
    }
    
    public Node buildMethodCall(String objName, String name, List<String> args)
    {
        return null;  //stub
    }

    public Node buildEmptyStatement()
    {
        return null;  //stub
    }

    public Node buildWhileStatement(Expression cond, Block body)
    {
        return null;  //stub
    }

    public Block buildBlock(List<Node> statements)
    {
        return null;  //stub
    }

    public Node buildClassDeclaration(String name, List<Node> children)
    {
        return null;  //stub
    }

    public Node buildMethodDeclaration(String type, String name, List<VarDeclaration> params, Block body)
    {
        return null;  //stub
    }

    public Node buildVarDeclaration(String type, String name)
    {
        return null;  //stub
    }
    
    public Node buildPrintStatement(String value) {
    	return null;
    }
    
    public Node buildNewAssignmentStatement(String varName, String className, List<String> args) {
    	return new NewAssignmentStatement(varName,className,args);
    }
}

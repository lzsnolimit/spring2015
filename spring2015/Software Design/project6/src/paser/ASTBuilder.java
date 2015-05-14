/*
 * File: ASTBuilder.java
 */
package paser;

import java.util.List;

import ast5.node.AssignStatement;
import ast5.node.Block;
import ast5.node.ClassDeclaration;
import ast5.node.EmptyStatement;
import ast5.node.Expression;
import ast5.node.IntLiteral;
import ast5.node.MethodCall;
import ast5.node.MethodDeclaration;
import ast5.node.Node;
import ast5.node.PrintStatement;
import ast5.node.VarDeclaration;
import ast5.node.WhileStatement;

public class ASTBuilder extends DefaultBuilder
{
    public IntLiteral buildIntLiteral(int value)
    {
        return new IntLiteral(value);
    }

    public Node buildAssignStatement(String name, Expression value)
    {
        return new AssignStatement(name, value);
    }

    public Node buildMethodCall(String name,List<String> args)
    {
        return new MethodCall(name,args);
    }
    
    public Node buildMethodCall(String objName, String name,List<String> args)
    {
        return new MethodCall(objName, name,args);
    }

    public Node buildEmptyStatement()
    {
        return new EmptyStatement();
    }

    public Node buildWhileStatement(Expression cond, Block body)
    {
        return new WhileStatement(cond, body);
    }

    public Block buildBlock(List<Node> statements)
    {
        return new Block(statements);
    }

    public Node buildClassDeclaration(String name, List<Node> children)
    {
        return new ClassDeclaration(name, children);
    }

    public Node buildMethodDeclaration(String type, String name, List<VarDeclaration> params, Block body)
    {
        return new MethodDeclaration(type, name, params, body);
    }

    public Node buildVarDeclaration(String type, String name)
    {
        return new VarDeclaration(type, name);
    }
    
    public Node buildPrintStatement(String value) {
    	return new PrintStatement(value);
    }
}

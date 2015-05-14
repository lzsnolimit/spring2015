/*
 * File: Parser.java
 */
package paser;

import java.util.ArrayList;
import java.util.List;

import ast5.Token;
import ast5.node.AbsAssignStatement;
import ast5.node.Block;
import ast5.node.ClassDeclaration;
import ast5.node.Expression;
import ast5.node.IntLiteral;
import ast5.node.Node;
import ast5.node.VarDeclaration;

public class Parser
{
    private Scanner scanner;
    private Token currentToken;
    private DefaultBuilder builder;

    public Parser()
    {
        this.builder = null;
        this.scanner = null;
        this.currentToken = null;
    }

    public Node parse(String code, DefaultBuilder builder)
    {
        this.builder = builder;
        this.scanner = new Scanner(code);
        advance(); //get the first token

        Node root = parseDeclaration();  //build the whole AST

        //do final error checking
        if (! (root instanceof ClassDeclaration))
            error("A program must consist of a class declaration.");
        else if (currentToken.getKind() != Token.EOF)
            //There are leftover tokens
            error("The end of the program expected at position: " +
                    currentToken.getPosition());
        return root;
    }
    
    private VarDeclaration parseVarDeclaration() {
        String type = currentToken.getSpelling();
        advance();
        String name = currentToken.getSpelling();
        return (VarDeclaration)builder.buildVarDeclaration(type, name);
    }

    private Node parseDeclaration()
    {
        if (currentToken.getKind() != Token.SYMBOL &&
            currentToken.getKind() != Token.CLASS) {
            error("The wrong kind of token (" + currentToken +
                    ") was found at position " +
                    currentToken.getPosition());
        }
        String type = currentToken.getSpelling();
        advance();
        String name = currentToken.getSpelling();
        matchAndAdvance(Token.SYMBOL);
        if( currentToken.getKind() == Token.SEMICOLON) {
            advance();
            return builder.buildVarDeclaration(type, name);
        }
        else if( currentToken.getKind() == Token.LEFTPAREN) {
            advance();
            List<VarDeclaration>params = parseParamList();
            matchAndAdvance(Token.RIGHTPAREN);
            Block body = parseBlock();
            return builder.buildMethodDeclaration(type, name, params, body);
        }
        else {
            matchAndAdvance(Token.LEFTBRACE);
            List<Node> children = new ArrayList<Node>();
            while( currentToken.getKind() != Token.RIGHTBRACE) {
                children.add(parseDeclaration());
            }
            advance();
            return builder.buildClassDeclaration(name, children);
        }
    }

    private Block parseBlock()
    {
        matchAndAdvance(Token.LEFTBRACE);
        List<Node> statements = new ArrayList<Node>();
        while( currentToken.getKind() != Token.RIGHTBRACE) {
            statements.add(parseStatement());
        }
        matchAndAdvance(Token.RIGHTBRACE);
        return builder.buildBlock(statements);
    }

    private Node parseStatement()
    {
        if( currentToken.getKind() == Token.WHILE ) {
            advance();
            matchAndAdvance(Token.LEFTPAREN);
            Expression cond = parseExpression();
            matchAndAdvance(Token.RIGHTPAREN);
            Block body = parseBlock();
            return builder.buildWhileStatement(cond, body);
        }
        else if (currentToken.getKind() == Token.PRINT) {
        	advance();
        	matchAndAdvance(Token.LEFTPAREN);
        	String value = currentToken.getSpelling();
        	advance();
        	matchAndAdvance(Token.RIGHTPAREN);
        	return builder.buildPrintStatement(value);
        }
        else if (currentToken.getKind() == Token.SEMICOLON) {
            advance();
            return builder.buildEmptyStatement();
        }
        else if( currentToken.getKind() == Token.SYMBOL) {
            String name = currentToken.getSpelling();
            advance();
            if(currentToken.getKind() == Token.LEFTPAREN) {
                advance();
                List<String> args = parseArgList();
                matchAndAdvance(Token.RIGHTPAREN);
                matchAndAdvance(Token.SEMICOLON);
                return builder.buildMethodCall(name,args);
            }
            else if(currentToken.getKind() == Token.EQUALS) {
                return parseAssignmentStatement(name);
            }
            else if (currentToken.getKind() == Token.DOT) {
            	advance();
            	if (currentToken.getKind() != Token.SYMBOL)
            		error("Ill-formed method call at " + currentToken.getPosition());
            	String methodName = currentToken.getSpelling();
            	advance();
            	if (currentToken.getKind() == Token.LEFTPAREN) {
            		advance();
	                List<String> args = parseArgList();
	                matchAndAdvance(Token.RIGHTPAREN);
	                matchAndAdvance(Token.SEMICOLON);
	                return builder.buildMethodCall(name,methodName,args);
            	} else if (currentToken.getKind() == Token.EQUALS) {
            		return parseAssignmentStatement(name,methodName);
            	} else return null;
             }
            else {
                error("Incorrect Token: " + currentToken);
                return null;
            }
        }
        else {
            error("Incorrect Token: " + currentToken);
            return null;
        }
    }
    
    private Node parseAssignmentStatement(String instName, String name) {
    	AbsAssignStatement stmt = (AbsAssignStatement)parseAssignmentStatement(name);
    	stmt.setInstanceVariable(instName);
    	return stmt;
    }

	private Node parseAssignmentStatement(String name) {
		advance();
		if (currentToken.getKind() == Token.NEW) {
			advance();
			String className = currentToken.getSpelling();
			advance();
			matchAndAdvance(Token.LEFTPAREN);
			List<String> args = parseArgList();
			matchAndAdvance(Token.RIGHTPAREN);
			matchAndAdvance(Token.SEMICOLON);
			return builder.buildNewAssignmentStatement(name,className,args);
		} else {
			Expression value = parseExpression();
			matchAndAdvance(Token.SEMICOLON);
			return builder.buildAssignStatement(name, value);
		}
	}
    
    private List<VarDeclaration> parseParamList() {
    	List<VarDeclaration> result = new ArrayList<VarDeclaration>();
    	while (currentToken.getKind() != Token.RIGHTPAREN) {
    		result.add(parseVarDeclaration());
    		advance();
    		if (currentToken.getKind() != Token.COMMA) return result;
    		advance();
    	}
    	return result;
    }
    
    private List<String> parseArgList() {
    	List<String>result = new ArrayList<String>();
    	while (currentToken.getKind() != Token.RIGHTPAREN) {
    		result.add(currentToken.getSpelling());
    		advance();
    		if (currentToken.getKind() != Token.COMMA) return result;
    		advance();
    	}
    	return result;
    }
    
    private Expression parseExpression() {
    	String lhs = currentToken.getSpelling();
    	String op = null;
    	advance();
    	if (currentToken.getKind() == Token.RIGHTPAREN || currentToken.getKind() == Token.SEMICOLON)
    		return new Expression(lhs,null,null);
    	if (currentToken.getKind() == Token.EQUALS || currentToken.getKind() == Token.LESSTHAN || currentToken.getKind() == Token.PLUS)
    		op = currentToken.getSpelling();
    	else error("Illegal expression operator " + currentToken);
    	advance();
    	IntLiteral rhs = parseIntLiteral();
    	return new Expression(lhs,op,rhs);
    }

    private IntLiteral parseIntLiteral()
    {
        int value = 0;
        try {
            value = Integer.parseInt(currentToken.getSpelling());
            advance();
        } catch(NumberFormatException e) {
            error("Illegal integer value: " + currentToken);
        }
        return builder.buildIntLiteral(value);
    }

    private void advance()
    {
        currentToken = scanner.getNextToken();
    }

    private void matchAndAdvance(int kind)
    {
        if (currentToken.getKind() != kind)
            error("The wrong kind of token (" + currentToken +
                    ") was found at position " +
                    currentToken.getPosition());
        advance();
    }

    private void error(String message)
    {
        throw new ParseException("Parse error: " + message);
    }

    private class ParseException extends RuntimeException
    {
        public ParseException(String message)
        {
            super(message);
        }
    }
}

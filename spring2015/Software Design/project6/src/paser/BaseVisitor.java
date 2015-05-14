package paser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ast5.Interpreter;
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
 * This class can take VSSJ code (as a String) and pretty print it-- that is, it
 * can create a new copy of the program with proper formatting according to Java
 * conventions. The following output is an example of pretty-printed code: class
 * A { int x;
 *
 * void foo() { ; x = 3; while(x) { foo(); } }
 *
 * class B { int y;
 *
 * void bar() { } } }
 */
public abstract class BaseVisitor implements Visitor {

	static ArrayList<String> usedVars = new ArrayList<String>();
	static ArrayList<String> declaredVars = new ArrayList<String>();

	/**
	 * constructs a new copy of the program with proper formatting according to
	 * Java conventions.
	 *
	 * @param root
	 *            the root Node of the ASt representing the program to be
	 *            reformatted
	 * @param indent
	 *            the String of whitespace with which the whole program is to be
	 *            indented
	 * @return the properly formatted version of the program
	 */
	public String prettyPrint(Node root, String indent) {
		return (String) root.accept(this, indent);
	}

	@Override
	public Object visitClassDeclaration(ClassDeclaration node, Object o) {
		String indent = (String) o;
		String result = indent + "class " + node.getName() + " {\n";
		for (Iterator it = node.getDeclarations().iterator(); it.hasNext();) {
			Node node1 = (Node) it.next();
			result += node1.accept(this, indent + "    ") + "\n";
			if (it.hasNext())
				result += "\n"; // add blank line between declarations
		}
		result += indent + "}";
		return result;
	}

	@Override
	public Object visitMethodDeclaration(MethodDeclaration node, Object o) {
		String indent = (String) o;
		String result = indent + node.getType() + " " + node.getName() + "(";
		boolean first = true;
		for (Iterator iter = node.getParams().iterator(); iter.hasNext();) {
			Node node1 = (Node) iter.next();
			if (first)
				first = false;
			else
				result += ",";
			result += node1.accept(this, null);
		}
		result += ") ";
		result += node.getBody().accept(this, indent);
		return result;
	}

	@Override
	public Object visitVarDeclaration(VarDeclaration node, Object o) {
		if (o == null)
			return node.getType() + " " + node.getName();
		String indent = (String) o;

		// System.out.println("THIS IS A VARIABLE: (put in list) " +
		// node.getName() + "\n");
		String tmp = node.getName();
		declaredVars.add(tmp);

		return indent + node.getType() + " " + node.getName() + ";";
	}

	@Override
	public Object visitAssignStatement(AssignStatement node, Object o) {
		String indent = (String) o;
		String result = indent + node.getVariable() + " = ";
		result += node.getValue().accept(this, "") + ";";
		String tmp = node.getVariable();
		// System.out.println("THIS IS A VARIABLE ASSIGNED: (put in list) " +
		// node.getVariable());
		usedVars.add(tmp);

		return result;
	}

	@Override
	public Object visitBlock(Block node, Object o) {
		String indent = (String) o; // indent for right brace
		String result = "{";
		if (node.getStatements().size() == 0)
			result += " }";
		else {
			result += "\n";
			for (Node node1 : node.getStatements()) {
				result += node1.accept(this, indent + "    ") + "\n";
			}
			result += indent + "}";
		}

		return result;
	}

	@Override
	public Object visitEmptyStatement(EmptyStatement node, Object o) {
		return o + ";";
	}

	@Override
	public Object visitWhileStatement(WhileStatement node, Object o) {
		String indent = (String) o;
		String result = indent + "while("
				+ node.getCondition().accept(this, indent) + ") ";
		result += node.getBody().accept(this, indent);
		return result;
	}

	@Override
	public Object visitMethodCall(MethodCall node, Object o) {
		String indent = (String) o;
		String result = node.getName() + "(";
		if (node.getObjName() != null) {
			result = node.getObjName() + "." + result;
		}
		result = indent + result;
		result = printStringList(node.getArgs(), result);
		return result;
	}

	@Override
	public Object visitIntLiteral(IntLiteral node, Object o) {
		String indent = (String) o;
		return indent + node.getValue();
	}

	@Override
	public Object visitPrintStatement(PrintStatement node, Object o) {
		String indent = (String) o;
		return indent + "print(" + node.getValue() + ")";
	}

	@Override
	public Object visitExpression(Expression node, Object o) {
		String result = node.getLhs();
		if (node.getOp() != null)
			result += " " + node.getOp() + " " + node.getRhs().getValue();
		return result;
	}

	@Override
	public Object visitNewAssignmentStatement(NewAssignmentStatement node,
			Object o) {
		String indent = (String) o;
		String result = indent + node.getVariable() + " = new "
				+ node.getClassName() + "(";
		return printStringList(node.getArgs(), result);
	}

	public String read(String program) {
		Node root = new Parser().parse(program, new ASTBuilder());
		return prettyPrint(root, "");
	}

	public boolean findMethod(String methodName, String className,
			String program) {
		Node root = new Parser().parse(program, new ASTBuilder());
		return new MethodFinder().findMethod(methodName, className, root);
	}

	public void interpret(String program, String mainClass) {
		Node root = new Parser().parse(program, new ASTBuilder());
		new Interpreter().interpret(root, mainClass);
	}

	private String printStringList(List<String> args, String result) {
		boolean first = true;
		for (Iterator iter = args.iterator(); iter.hasNext();) {
			if (!first)
				result += ",";
			else
				first = false;
			String arg = (String) iter.next();
			result += arg;
		}
		result += ");";
		return result;
	}

	/**
	 * abstract method return variables analysing
	 * 
	 * @return String
	 */
	abstract String analyseVariables();

	/**
	 * clear the variables return void
	 */
	public void clear() {
		declaredVars.clear();
		usedVars.clear();
	}
}
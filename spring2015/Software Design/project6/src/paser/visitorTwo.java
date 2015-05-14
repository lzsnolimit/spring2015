package paser;

import java.util.Collections;

import ast5.node.Visitor;

/**
 * 
 * visitorTwo implement visitor interface 
 *
 */
public class visitorTwo extends BaseVisitor implements Visitor{

	@Override
	String analyseVariables() {
		String strOutput = "Visitor Two:\n";
		if (super.declaredVars.size()<=1) {
			strOutput +="No Errors Found!\n";
			return strOutput;
		}
		int strCount = 0;
		Collections.sort(declaredVars);
		String lastRepeatStr="";
		for (int i = 0; i < super.declaredVars.size()-1; i++) {
			if (super.declaredVars.get(i).equals(super.declaredVars.get(i+1))&&!super.declaredVars.get(i).equals(lastRepeatStr)) {
				strCount++;
				strOutput +=super.declaredVars.get(i)+" is decleared more than once! \n";
				lastRepeatStr=super.declaredVars.get(i);
			}
		}
		if (strCount <= 0) {
			strOutput +="No Errors Found!\n";
		}
		return strOutput;
	}
}

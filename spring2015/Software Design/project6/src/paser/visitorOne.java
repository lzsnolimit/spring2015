package paser;

import java.util.Collections;

import ast5.node.Visitor;

/**
 * 
 * visitorOne implement visitor interface 
 *
 */
public class visitorOne extends BaseVisitor implements Visitor {

	@Override
	String analyseVariables() {
		String strOutput = "Visitor One:\n";
		int strCount = 0;
		String lastRepeatStr="";
		Collections.sort(super.usedVars);
		for (int i = 0; i < super.usedVars.size(); i++) {
			if (!super.declaredVars.contains(super.usedVars.get(i))&&!super.usedVars.get(i).equals(lastRepeatStr)) {
				strOutput += "The variable " + super.usedVars.get(i)
						+ " is initialized but is not being declared \n";
				strCount++;
				lastRepeatStr=super.usedVars.get(i);
			}

		}
		if (strCount <= 0) {
			strOutput += "No Errors Found!\n";

		}
		return strOutput;
	}
}

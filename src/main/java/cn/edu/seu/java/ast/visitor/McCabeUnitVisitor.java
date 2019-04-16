package cn.edu.seu.java.ast.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import cn.edu.seu.java.node.ASTMethod;
import cn.edu.seu.java.util.ASTNodeUtil;

/**
 * @author Huihui Liu
 * @Time 2016-01-02
 */
public class McCabeUnitVisitor extends ASTVisitor {

  private int complexity;

  public McCabeUnitVisitor() {
  }
	
	/*@Override
	public boolean visit(InfixExpression node) {
		if (node.getOperator() == InfixExpression.Operator.CONDITIONAL_AND
				|| node.getOperator() == InfixExpression.Operator.CONDITIONAL_OR) {
			this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": InfixExpression");
			increaseClassComplexity();
		}
		return true;
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": ConditionalExpression");
		increaseClassComplexity();
		return true;
	}*/

  @Override
  public boolean visit(ForStatement node) {
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(EnhancedForStatement node) {
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(IfStatement node) {
    increaseMethodComplexity();
    return true;
  }

	/*@Override
	public boolean visit(ReturnStatement node) {
		this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": ReturnStatement");
		increaseClassComplexity();
		return true;
	}*/

  @Override
  public boolean visit(SwitchStatement node) {
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(SwitchCase node) {
    increaseMethodComplexity();
    return true;
  }

	/*@Override
	public boolean visit(ThrowStatement node) {
		this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": ThrowStatement");
		increaseClassComplexity();
		return true;
	}*/

  @Override
  public boolean visit(DoStatement node) {
    this.increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(WhileStatement node) {
    increaseMethodComplexity();
    return true;
  }

  private void increaseMethodComplexity() {
    this.complexity = this.complexity + 1;

  }

  public int getCyclomaticComplexity() {
    return complexity + 1;
  }
}
package cn.edu.seu.java.ast.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
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
public class McCabeMethodVisitor extends ASTVisitor {

  //private Queue<String> queueOfMethodCC;
  //private Set<String> setOfVisitedMethod;
  //public List<String> predicateList;
  //private String pathOfSourceFile;
  private int methodComplexity;
  private String activeFullyMethodName;

  public McCabeMethodVisitor(ASTMethod method) {
    this(method.getMethodDeclaration());
  }

  public McCabeMethodVisitor(MethodDeclaration node) {
    //this.pathOfSourceFile = methodElement.getPathOfSourceFile();
    //this.queueOfMethodCC = new LinkedList<String>();
    //this.predicateList = new ArrayList<String>();
    //this.setOfVisitedMethod = new HashSet<String>();

    this.activeFullyMethodName = ASTNodeUtil.getFullyQualifedName(node);
    node.accept(this);
  }

  /**
   * for simplicity, we only consider member method inside class or enum class or member class,
   * ignoring methods in local class or anonymous class.
   */
  @Override
  public boolean visit(MethodDeclaration node) {
    if (node.getBody() == null) {
      return false;
    }
    return true;
  }

  @Override
  public boolean visit(ConditionalExpression node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": ConditionalExpression");
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(ForStatement node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": ForStatement");
    //this.predicateList.add("ForStatement");
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(EnhancedForStatement node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": EnhancedForStatement");
    //this.predicateList.add("EnhancedForStatement");
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(IfStatement node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": IfStatement");
    //this.predicateList.add("IfStatement");
    String expr = node.getExpression().toString().replace("&&", "&").replace("||", "|");
    int ands = StringUtils.countMatches(expr, "&");
    int ors = StringUtils.countMatches(expr, "|");
    increaseMethodComplexity(ands + ors + 1);
    increaseMethodComplexity();
    return true;
  }

	/*@Override
	public boolean visit(ReturnStatement node) {
		this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": ReturnStatement");
		increaseClassComplexity();
		return true;
	}*/

	/*@Override
	public boolean visit(SwitchStatement node) {
		//this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": SwitchStatement");
		//this.predicateList.add("SwitchStatement");
		//increaseMethodComplexity();
		return true;
	}*/

  @Override
  public boolean visit(SwitchCase node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": SwitchCase");
    //this.predicateList.add("SwitchCase");
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(Initializer node) {
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
  public boolean visit(CatchClause node) {
    increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(DoStatement node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": DoStatement");
    //this.predicateList.add("DoStatement");
    this.increaseMethodComplexity();
    return true;
  }

  @Override
  public boolean visit(WhileStatement node) {
    //this.queueOfMethodCC.add("Line" + Helper.getStartLine(node) + ": WhileStatement");
    //this.predicateList.add("WhileStatement");
    increaseMethodComplexity();
    return true;
  }

  private void increaseMethodComplexity() {
    if (activeFullyMethodName != null) {
      this.methodComplexity = this.methodComplexity + 1;
    }
  }

  private void increaseMethodComplexity(int i) {
    this.methodComplexity = this.methodComplexity + i;
  }

  public int getMethodCyclomaticComplexity() {
    return methodComplexity + 1;
  }
}
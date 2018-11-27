package cn.edu.seu.java.ast.visitor;

import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

import cn.edu.seu.java.util.ASTNodeUtil;

public class NestingLevelExtractor extends ASTVisitor {

  Stack<String> stack = new Stack<String>();
  int maxNestingLevel = 0;

  public NestingLevelExtractor(MethodDeclaration node) {
    node.accept(this);
  }

  @Override
  public boolean visit(DoStatement node) {
    stack.push("DoStatement");
    if (maxNestingLevel < stack.size()) {
      maxNestingLevel = stack.size();
    }
    return true;
  }

  @Override
  public void endVisit(DoStatement node) {
    this.stack.pop();
  }

  @Override
  public boolean visit(EnhancedForStatement node) {
    stack.push("EnhancedForStatement");
    if (maxNestingLevel < stack.size()) {
      maxNestingLevel = stack.size();
    }
    return true;
  }

  @Override
  public void endVisit(EnhancedForStatement node) {
    this.stack.pop();
  }

  @Override
  public boolean visit(ForStatement node) {
    stack.push("ForStatement");
    if (maxNestingLevel < stack.size()) {
      maxNestingLevel = stack.size();
    }
    return true;
  }

  @Override
  public void endVisit(ForStatement node) {
    this.stack.pop();
  }

  @Override
  public boolean visit(IfStatement node) {
    stack.push("IfStatement");
    if (maxNestingLevel < stack.size()) {
      maxNestingLevel = stack.size();
    }

    Statement thenStmt = node.getThenStatement();
    if (thenStmt != null) {
      thenStmt.accept(this);
    }
    stack.pop();

    Statement elseStmt = node.getElseStatement();
    if (elseStmt != null) {
      if (elseStmt.getNodeType() == ASTNode.IF_STATEMENT) {
        elseStmt.accept(this); // is equivalent to recursively call
      } else {
        stack.push("IfStatement");
        elseStmt.accept(this);
        stack.pop();
      }
    }

    return false;
  }

  @Override
  public boolean visit(WhileStatement node) {
    stack.push("WhileStatement");
    if (maxNestingLevel < stack.size()) {
      maxNestingLevel = stack.size();
    }
    return true;
  }

  @Override
  public void endVisit(WhileStatement node) {
    this.stack.pop();
  }

  public int getMaxNestingLevel() {
    return maxNestingLevel;
  }
}
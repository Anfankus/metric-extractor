package cn.edu.seu.java.ast.visitor;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class CodeFormalizationVisitor extends ASTVisitor {

  public CodeFormalizationVisitor() {

  }

  public boolean visit(IfStatement node) {
    this.bracifyIfStatement(node);
    //System.err.println(node.toString());
    return true;
  }

  public boolean visit(ForStatement node) {
    Statement s = node.getBody();
    if (s.getNodeType() != ASTNode.BLOCK) {
      Block block = createBracifiedCopy(node.getAST(), node.getBody());
      node.setBody(block);
    }
    //System.err.println(node.toString());
    return true;
  }

  public boolean visit(EnhancedForStatement node) {
    Statement s = node.getBody();
    if (s.getNodeType() != ASTNode.BLOCK) {
      Block block = createBracifiedCopy(node.getAST(), node.getBody());
      node.setBody(block);
    }
    //System.err.println(node.toString());
    return true;
  }

  public boolean visit(DoStatement node) {

    Statement s = node.getBody();
    if (s.getNodeType() != ASTNode.BLOCK) {
      Block block = createBracifiedCopy(node.getAST(), node.getBody());
      node.setBody(block);
    }
    return true;
  }

  public boolean visit(WhileStatement node) {
    Statement s = node.getBody();
    if (s.getNodeType() != ASTNode.BLOCK) {
      Block block = createBracifiedCopy(node.getAST(), node.getBody());
      node.setBody(block);
    }
    return true;
  }

  /**
   * delete Javadoc node in Compilation Unit
   */
  public boolean visit(Javadoc node) {
    node.delete();
    return false;
  }

  /**
   * recursivly bracify if-statement.
   */
  private void bracifyIfStatement(IfStatement ifStatement) {
    // change the then statement to a block if necessary
    if (!(ifStatement.getThenStatement() instanceof Block)) {
      if (ifStatement.getThenStatement() instanceof IfStatement) {
        bracifyIfStatement((IfStatement) ifStatement.getThenStatement());
      }
      Block block = createBracifiedCopy(ifStatement.getAST(), ifStatement.getThenStatement());
      ifStatement.setThenStatement(block);
    }

    // check the else statement if it is a block
    Statement elseStatement = ifStatement.getElseStatement();
    if (elseStatement != null && !(elseStatement instanceof Block)) {

      // in case the else statement is an further if statement
      // (else if) do the recursion
      if (elseStatement instanceof IfStatement) {
        bracifyIfStatement((IfStatement) elseStatement);
      } else {
        Block block = createBracifiedCopy(ifStatement.getAST(), ifStatement.getElseStatement());
        ifStatement.setElseStatement(block);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Block createBracifiedCopy(AST ast, Statement body) {
    Block block = ast.newBlock();
    block.statements().add(ASTNode.copySubtree(ast, body));
    return block;
  }
}

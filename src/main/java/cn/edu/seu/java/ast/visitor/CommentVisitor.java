package cn.edu.seu.java.ast.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import cn.edu.seu.java.util.ASTNodeUtil;
import data.io.util.IOUtil;

public class CommentVisitor extends ASTVisitor {

  private CompilationUnit unit;

  public CommentVisitor(CompilationUnit node) {
    this.unit = node;
    @SuppressWarnings("unchecked")
    List<Comment> comments = unit.getCommentList();
    for (Comment comment : comments) {
      comment.accept(this);
    }
  }

  public boolean visit(LineComment node) {
    long startLineNumber = ASTNodeUtil.getStartLine(node);
    IOUtil.println("LineComment start: " + startLineNumber);
    return true;
  }

  public boolean visit(MethodDeclaration node) {
    long startLineNumber = ASTNodeUtil.getStartLine(node);
    IOUtil.println("MethodDeclaration start: " + startLineNumber);
    return true;
  }

  public boolean visit(Javadoc node) {
    long startLineNumber = ASTNodeUtil.getStartLine(node);
    int endLineNumber = unit.getLineNumber(node.getStartPosition() + node.getLength() - 1);

    IOUtil.println("Javadoc start: " + startLineNumber);
    IOUtil.println("Javadoc end: " + endLineNumber);

    return true;
  }

  public boolean visit(BlockComment node) {
    long startLineNumber = ASTNodeUtil.getStartLine(node);
    int endLineNumber = unit.getLineNumber(node.getStartPosition() + node.getLength() - 1);

    IOUtil.println("BlockComment start: " + startLineNumber);
    IOUtil.println("BlockComment end: " + endLineNumber);
    return true;
  }
}

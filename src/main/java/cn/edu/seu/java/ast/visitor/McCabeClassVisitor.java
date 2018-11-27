package cn.edu.seu.java.ast.visitor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.node.ASTMethod;
import cn.edu.seu.java.util.ASTNodeUtil;

/**
 * @author Huihui Liu
 * @Time 2016-01-31
 */
public class McCabeClassVisitor {

  private int classComplexity;

  public McCabeClassVisitor(ASTClass classNode) {
    List<ASTMethod> methods = ASTNodeUtil.getASTMethods(classNode);
    for (ASTMethod methodNode : methods) {
      if (methodNode.isAbstractMethod() != true) {
        McCabeMethodVisitor methodCCVisitor = new McCabeMethodVisitor(methodNode);
        classComplexity = classComplexity + methodCCVisitor.getMethodCyclomaticComplexity();
      }
    }
  }

  /**
   * @return sum of class CC, where abstract methods are excluded
   */
  public int getClassCyclomaticComplexity() {
    return this.classComplexity;
  }
}
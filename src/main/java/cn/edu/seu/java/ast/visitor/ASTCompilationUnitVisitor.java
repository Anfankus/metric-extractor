package cn.edu.seu.java.ast.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.node.ASTEnum;
import cn.edu.seu.java.node.ASTMethod;
import cn.edu.seu.java.util.ASTNodeUtil;

/**
 * extract classes including top-level class, member class, but except enum class and local class
 * appeared in a method, if-statement, etc.
 */
public class ASTCompilationUnitVisitor extends ASTVisitor {

  private ASTCompilationUnit unit;
  private List<ASTClass> listOfASTClass;
  private List<ASTMethod> listOfASTMethod;

  public ASTCompilationUnitVisitor(List<ASTCompilationUnit> units) {
    this.listOfASTClass = new ArrayList<ASTClass>();
    this.listOfASTMethod = new ArrayList<ASTMethod>();
    for (ASTCompilationUnit unit : units) {
      this.unit = unit;
      unit.getCompilationUnit().accept(this);
    }
  }

  public ASTCompilationUnitVisitor(ASTCompilationUnit unit) {
    this.listOfASTClass = new ArrayList<ASTClass>();
    this.listOfASTMethod = new ArrayList<ASTMethod>();
    this.unit = unit;
    unit.getCompilationUnit().accept(this);
  }

  /**
   * Bug: how to fix?? If the top-level class has several member classes, this method will collect
   * this top-level class and their member classes at the same time.
   */
  @Override
  public boolean visit(TypeDeclaration node) {
    if (node.isLocalTypeDeclaration() != true) {
      ASTClass classNode = new ASTClass(unit.getSourceFilePath(), node);
      classNode.setProjectName(unit.getProjectName());
      classNode.setVersionNo(unit.getVersionNum());
      classNode.setVersionPath(unit.getVersionPath());
      classNode.setPackageName(unit.getPackageName());
      classNode.setUnitName(unit.getCompilationUnitName());
      classNode.setSourceDirectory(unit.getSourceDirectory());
      classNode.setRelativeSourcePath(unit.getRelativeSourceFilePath());
      listOfASTClass.add(classNode);
    }
    return true;
  }

  @Override
  public boolean visit(MethodDeclaration node) {
    if (ASTNodeUtil.isInLocalClass(node) != true && ASTNodeUtil.isInEnumClass(node) != true) {
      String packageName = unit.getPackageName();
      String unitName = unit.getCompilationUnitName();
      String sourceFilePath = unit.getSourceFilePath();
      ASTMethod methodNode = new ASTMethod(packageName, unitName, sourceFilePath, node);
      methodNode.setProjectName(unit.getProjectName());
      methodNode.setVersionNum(unit.getVersionNum());
      methodNode.setVersionPath(unit.getVersionPath());
      methodNode.setSourceDirectory(unit.getSourceDirectory());
      methodNode.setRelativeSourcePath(unit.getRelativeSourceFilePath());
      methodNode.setFullyQualifiedTopClassName(ASTNodeUtil.getFullyQualifiedTopClassName(node));
      listOfASTMethod.add(methodNode);
    }
    return false;
  }

  /**
   * extract classes including top-level class, member class, but except enum class and local class
   * appeared in a method, if-statement, etc.
   */
  public List<ASTClass> getListOfASTClass() {
    return listOfASTClass;
  }

  /**
   * extract methods in class (but except local or enum class)
   */
  public List<ASTMethod> getListOfASTMethod() {
    return listOfASTMethod;
  }
}

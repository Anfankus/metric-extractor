package cn.edu.seu.java.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.node.ASTEnum;
import cn.edu.seu.java.util.ASTNodeUtil;

/**
 * extract classes including top-level class, member class, but except enum class and local class
 * located in a statement (e.g., method, if-statement)
 */
public class ASTClassVisitor {

  private List<ASTClass> listOfASTClass;
  //private List<ASTEnum> listOfASTEnum;
  private ASTCompilationUnit unitNode;
  private String packageName;
  private List<ASTClass> listOfTopASTClass;


  public ASTClassVisitor(ASTCompilationUnit node) {
    this.listOfASTClass = new ArrayList<ASTClass>();
    this.listOfTopASTClass = new ArrayList<ASTClass>();
    //this.listOfASTEnum = new ArrayList<ASTEnum>();

    this.packageName = node.getPackageName();
    this.unitNode = node;
    JavaASTClassVisitor visitor = new JavaASTClassVisitor();
    node.getCompilationUnit().accept(visitor);
  }

  private class JavaASTClassVisitor extends ASTVisitor {

    /**
     * extract classes including top-level class, member class, but except enum class and local
     * class enclosed within a method or statement
     */
    public boolean visit(TypeDeclaration node) {
      if (node.isLocalTypeDeclaration() != true) {
        ASTClass classNode = new ASTClass(unitNode.getSourceFilePath(), node);
        classNode.setProjectName(unitNode.getProjectName());
        classNode.setVersionNo(unitNode.getVersionNum());
        classNode.setVersionPath(unitNode.getVersionPath());
        classNode.setPackageName(packageName);
        classNode.setUnitName(unitNode.getCompilationUnitName());
        classNode.setSourceDirectory(unitNode.getSourceDirectory());
        classNode.setRelativeSourcePath(unitNode.getRelativeSourceFilePath());
        listOfASTClass.add(classNode);
      }
      return true;
    }
  }

  /**
   * extract classes including top-level class, member class, but except enum class and local class
   * enclosed within a method or statement
   */
  public List<ASTClass> getListOfASTClass() {
    return listOfASTClass;
  }

  public List<ASTClass> getListOfTopASTClass() {
    return listOfTopASTClass;
  }
}

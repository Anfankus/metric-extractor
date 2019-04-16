package cn.edu.seu.java.ast.visitor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.node.ASTMethod;
import cn.edu.seu.java.util.ASTNodeUtil;

public class ASTMethodVisitor extends ASTVisitor {

  private String sourceFilePath;
  private String projectName;
  private String versionNum;
  private LinkedList<ASTMethod> listOfASTMethod;
  private String packageName;
  private String unitName;
  private String versionPath;
  private String sourceDirectory;
  private String relativeSourcePath;

  public ASTMethodVisitor(List<ASTCompilationUnit> unitList) {
    this.listOfASTMethod = new LinkedList<ASTMethod>();
    for (ASTCompilationUnit unit : unitList) {
      this.projectName = unit.getProjectName();
      this.versionNum = unit.getVersionNum();
      this.versionPath = unit.getVersionPath();
      this.packageName = unit.getPackageName();
      this.unitName = unit.getCompilationUnitName();
      this.sourceFilePath = unit.getSourceFilePath();
      this.sourceDirectory = unit.getSourceDirectory();
      this.relativeSourcePath = unit.getRelativeSourceFilePath();
      unit.getCompilationUnit().accept(this);
    }
  }

  public ASTMethodVisitor(ASTCompilationUnit unit) {
    this.listOfASTMethod = new LinkedList<ASTMethod>();
    this.projectName = unit.getProjectName();
    this.versionNum = unit.getVersionNum();
    this.versionPath = unit.getVersionPath();
    this.packageName = unit.getPackageName();
    this.unitName = unit.getCompilationUnitName();
    this.sourceFilePath = unit.getSourceFilePath();
    this.sourceDirectory = unit.getSourceDirectory();
    this.relativeSourcePath = unit.getRelativeSourceFilePath();
    unit.getCompilationUnit().accept(this);
  }

  public ASTMethodVisitor(ASTClass classNode) {
    this.listOfASTMethod = new LinkedList<ASTMethod>();
    this.projectName = classNode.getProjectName();
    this.versionNum = classNode.getVersionNum();
    this.versionPath = classNode.getVersionPath();
    this.packageName = classNode.getPackageName();
    this.unitName = classNode.getUnitName();
    this.sourceFilePath = classNode.getSourceFilePath();
    this.sourceDirectory = classNode.getSourceDirectory();
    this.relativeSourcePath = classNode.getRelativeSourceFilePath();
    classNode.getTypeDeclaration().accept(this);
  }

  /**
   * extract all ASTMethods which are not nested in local class, enum class and anonymous class
   */
  @Override
  public boolean visit(MethodDeclaration node) {
    if (ASTNodeUtil.isInLocalClass(node) != true && ASTNodeUtil.isInEnumClass(node) != true) {
      ASTMethod methodNode = new ASTMethod(packageName, unitName, sourceFilePath, node);
      methodNode.setProjectName(projectName);
      methodNode.setVersionNum(versionNum);
      methodNode.setVersionPath(versionPath);
      methodNode.setSourceDirectory(this.sourceDirectory);
      methodNode.setRelativeSourcePath(this.relativeSourcePath);
      methodNode.setFullyQualifiedTopClassName(ASTNodeUtil.getFullyQualifiedTopClassName(node));
      this.listOfASTMethod.add(methodNode);
    }
    return true;
  }

  /**
   * @return extract all ASTMethods which are not nested in local class, enum class and anonymous
   * class
   */
  public List<ASTMethod> getASTMethodsNotEnclosedInLocalClassAndEnumClassAndAnonymousClass() {
    return this.listOfASTMethod;
  }
}

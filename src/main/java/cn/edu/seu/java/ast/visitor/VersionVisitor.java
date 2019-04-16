package cn.edu.seu.java.ast.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.node.VersionNode;
import cn.edu.seu.java.util.ASTNodeUtil;

public class VersionVisitor extends ASTVisitor {

  // <key, value>=<packageName, LOCInPackage>
  private HashMap<String, Long> mapOfPackageNameToLOC;
  private long projectVersionLOC;
  private String versionPath;
  private String versionNo;
  private long numOfClass;
  private long numOfMethod;
  private String projectName;
  private VersionNode versionNode;
  private HashSet<String> setOfPackageName;
  private long currentCompilationUnitLOC;

  public VersionVisitor(String projectVersionPath) {
    this.versionPath = projectVersionPath;
    this.versionNode = new VersionNode(projectVersionPath);

    this.projectName = this.extractProjectName(projectVersionPath);
    this.versionNo = this.extractVersionName(projectVersionPath);

    this.setOfPackageName = new HashSet<String>();
    this.mapOfPackageNameToLOC = new HashMap<String, Long>();
  }


  public String extractProjectName(String projectVersionPath) {
    String lastStringInPath = "";
    String projectName = "";

    String temp[] = projectVersionPath.trim().split("\\\\");
    if (temp.length > 0) {
      lastStringInPath = temp[temp.length - 1];
    }

    char[] array = lastStringInPath.toCharArray();
    final String digitStr = "0123456789";
    int count = 0;
    for (int i = 0; i < array.length; i++) {
      if (digitStr.contains(String.valueOf(array[i]))) {
        count = i;
        break;
      }
    }
    if (0 < count && count < lastStringInPath.length()) {
      projectName = lastStringInPath.substring(0, count);
    }
    return projectName;
  }

  public String extractVersionName(String projectVersionPath) {
    String lastStringInPath = "";
    String versionNo = "0.0.0";

    String temp[] = projectVersionPath.trim().split("\\\\");
    if (temp.length > 0) {
      lastStringInPath = temp[temp.length - 1];
    }

    char[] array = lastStringInPath.toCharArray();
    final String digitStr = "0123456789";
    int count = 0;
    for (int i = 0; i < array.length; i++) {
      if (digitStr.contains(String.valueOf(array[i]))) {
        count = i;
        break;
      }
    }

    if (0 < count && count < lastStringInPath.length()) {
      versionNo = lastStringInPath.substring(count, lastStringInPath.length());
    }
    return versionNo;
  }

  public boolean visit(CompilationUnit node) {
    this.currentCompilationUnitLOC = ASTNodeUtil.getLinesOfCode(node);
    this.projectVersionLOC = this.projectVersionLOC + this.currentCompilationUnitLOC;
    updatePackageSizeInProject(ASTNodeUtil.getPackageName(node));
    return true;
  }

  public boolean visit(TypeDeclaration node) {
    this.numOfClass++;
    return true;
  }

  public boolean visit(MethodDeclaration node) {
    this.numOfMethod++;
    return true;
  }

  private void updatePackageSizeInProject(String packageName) {
    this.setOfPackageName.add(packageName);
    if (this.mapOfPackageNameToLOC.containsKey(packageName)) {
      Long oldValue = this.mapOfPackageNameToLOC.get(packageName);
      Long newValue = oldValue + this.currentCompilationUnitLOC;
      this.mapOfPackageNameToLOC.replace(packageName, newValue);
    } else {
      this.mapOfPackageNameToLOC.put(packageName, this.currentCompilationUnitLOC);
    }
  }

  public HashMap<String, Long> getIndividualPackageLOCInProject() {
    return this.mapOfPackageNameToLOC;
  }

  public Set<String> getSetOfPackageName() {
    return this.setOfPackageName;
  }

  public void travel(List<ASTCompilationUnit> unitElements) {
    for (ASTCompilationUnit unitElement : unitElements) {
      CompilationUnit unit = unitElement.getCompilationUnit();
      unit.accept(this);
    }
    this.versionNode.setProjectVersionPath(this.versionPath);
    this.versionNode.setProjectName(this.projectName);
    this.versionNode.setVersionNo(this.versionNo);
    this.versionNode.setNumOfPackage(this.setOfPackageName.size());
    this.versionNode.setNumOfVersionLOC(this.projectVersionLOC);
    this.versionNode.setNumOfClass(this.numOfClass);
    this.versionNode.setNumOfMethod(this.numOfMethod);
  }

  public VersionNode getVersionRecorder() {
    return this.versionNode;
  }
}

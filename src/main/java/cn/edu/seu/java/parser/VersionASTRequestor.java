package cn.edu.seu.java.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import cn.edu.seu.java.ast.visitor.CodeFormalizationVisitor;
import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.util.StringUtil;
import data.io.util.IOUtil;

public class VersionASTRequestor extends FileASTRequestor {

  private String projectName;
  private String versionNo;
  private String versionPath;
  private List<ASTCompilationUnit> correctASTUnits;
  private List<CompilationUnit> correctCompilationUnits;

  /**
   * @param versionPath e.g., F:/commit-corpus/guava2.1.3
   */
  public VersionASTRequestor(String versionPath) {
    this("", "", versionPath);
  }

  /**
   * @param projectName e.g., guava
   * @param versionNum e.g., a194b64, where a194b64 is short SHA
   * @param versionPath e.g., F:/commit-corpus/guava/guava2009-10-06-00-31-24-a194b64
   */
  public VersionASTRequestor(String projectName, String versionNum, String versionPath) {
    this.projectName = projectName;
    this.versionNo = versionNum;
    this.versionPath = versionPath;
    this.correctASTUnits = new ArrayList<ASTCompilationUnit>();
    this.correctCompilationUnits = new ArrayList<CompilationUnit>();
  }

  @Override
  public void acceptAST(String sourceFilePath, CompilationUnit node) {
    sourceFilePath = sourceFilePath.replace("\\", "/");
    if (this.isValidPackage(sourceFilePath, node)) {
      node.setProperty("projectName", projectName);
      node.setProperty("versionNo", versionNo);
      node.setProperty("versionPath", versionPath);
      node.setProperty("sourceFilePath", sourceFilePath);
      this.correctCompilationUnits.add(node);

      ASTCompilationUnit unit = new ASTCompilationUnit(projectName, versionNo, versionPath,
          sourceFilePath, node);
      this.correctASTUnits.add(unit);
    } else {
      //System.err.println("error:compilation unit has no or wrong package");
      //System.err.println("path:" + sourceFilePath + "\n");
      IOUtil.catchError("error: compilation unit has no package" + "(" + sourceFilePath + ")\n");
    }
  }

  // very low efficiency
	/*private boolean isDuplicated(ASTCompilationUnit node) {
		boolean flag = false;
		for (ASTCompilationUnit unit : correctASTUnits) {
			if (node.toString().equals(unit.getCodeEntity().toString())) {
				flag = true;
				break;
			}
		}
		return flag;
	}*/

  /**
   * @return false if there is no package, or package is invalid, e.g., ${package}.a.b
   */
  private boolean isValidPackage(String sourceFilePath, CompilationUnit node) {
    boolean isValid = false;
    if (node.getPackage() != null) {
      String dotSeperatedPath = sourceFilePath.replace('/', '.');
      String packageName = node.getPackage().getName().getFullyQualifiedName();
      isValid = dotSeperatedPath.contains(packageName);
    }
    return isValid;
  }

  public String getVersionPath() {
    return this.versionPath;
  }

  public List<ASTCompilationUnit> getASTCompilationUnits() {
    return correctASTUnits;
  }

  public List<CompilationUnit> getCompilationUnits() {
    return correctCompilationUnits;
  }

}
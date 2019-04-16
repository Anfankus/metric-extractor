package cn.edu.seu.java.node;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import cn.edu.seu.java.ast.visitor.CodeFormalizationVisitor;
import cn.edu.seu.java.util.ASTNodeUtil;
import cn.edu.seu.java.util.StringUtil;
import data.io.util.IOUtil;

public class ASTCompilationUnit {

  private CompilationUnit node;
  private String projectName;
  private String versionNum;
  private String versionPath;
  private String sourceFilePath;
  private String compilationUnitName;
  private String packageName;
  private String packagePath;
  private String sourceDirectory;
  private String relativeSourceFilePath;

  public ASTCompilationUnit(String projectName, String versionNo, String versionPath,
      String sourceFilePath, CompilationUnit node) {
    this.projectName = projectName;
    this.versionNum = versionNo;
    this.versionPath = versionPath;
    this.node = node;
    this.sourceFilePath = sourceFilePath.replace("\\", "/");

    this.compilationUnitName = StringUtil.getJavaFileName(sourceFilePath);
    this.packageName = ASTNodeUtil.getPackageName(node);

    this.extractSourceDiretoryAndRelativePath();
  }

  private void extractSourceDiretoryAndRelativePath() {
    this.packagePath = this.packageName.replace('.', '/');
    int beginIndex = sourceFilePath.indexOf(this.versionNum); // versionNum = SHA

    //BugFix: the length of "packagePath" is short, should be replaced with "packagePath+"/"+unitName"
    int endIndex = sourceFilePath.indexOf(packagePath + "/" + compilationUnitName) - 1;
    if (beginIndex + versionNum.length() + 1 < endIndex) {
      sourceDirectory = sourceFilePath
          .substring(beginIndex + versionNum.length() + 1, endIndex); // end with "/"
    } else {
      sourceDirectory = "";
    }

    relativeSourceFilePath =
        sourceDirectory + "/" + packagePath + "/" + compilationUnitName + ".java";
		
		/*IOUtil.println(sourceFilePath);
		IOUtil.println(sourceDirectory);
		IOUtil.println(relativeSourceFilePath);
		IOUtil.println("");*/
  }

  /**
   * @return ast node of CompilationUnit
   */
  public CompilationUnit getCompilationUnit() {
    return node;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getVersionNum() {
    return versionNum;
  }

  public String getSourceFilePath() {
    return sourceFilePath;
  }

  public String getCompilationUnitName() {
    return this.compilationUnitName;
  }

  public String getPackageName() {
    return this.packageName;
  }

  public String getPackagePath() {
    return packagePath;
  }

  public String getSourceDirectory() {
    return sourceDirectory;
  }

  public String getVersionPath() {
    return versionPath;
  }

  public String getRelativeSourceFilePath() {
    return relativeSourceFilePath;
  }
}

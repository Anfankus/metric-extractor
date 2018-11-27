package cn.edu.seu.java.node;

public class JavaNode {

  protected String projectName;
  protected String versionNo;
  protected String versionPath;
  protected String sourceFilePath;
  protected String packageName;
  protected String unitName; // i.e., compilation unit name

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public void setVersionNo(String versionNo) {
    this.versionNo = versionNo;
  }

  public void setVersionPath(String versionPath) {
    this.versionPath = versionPath;
  }

  public void setSourceFilePath(String sourceFilePath) {
    this.sourceFilePath = sourceFilePath;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getVersionNum() {
    return versionNo;
  }

  public String getVersionPath() {
    return versionPath;
  }

  public String getSourceFilePath() {
    return sourceFilePath;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getUnitName() {
    return unitName;
  }
}

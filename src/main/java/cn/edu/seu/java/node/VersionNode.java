package cn.edu.seu.java.node;


public class VersionNode {

  private String pathOfProjectVersion;
  private String projectName;
  private String versionNo;
  private long numOfVersionLOC;
  private long numOfEmptyLinesOfCode;
  private long numOfPacakge;
  private long numOfClass;
  private long numOfMethod;

  public VersionNode(String pathOfProjectVersion) {
    this.pathOfProjectVersion = pathOfProjectVersion;
  }

  public void setPathOfVersion(String pathOfVersion) {
    this.pathOfProjectVersion = pathOfVersion;
  }

  public String getPathOfVersion() {
    return pathOfProjectVersion;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setVersionNo(String versionNo) {
    this.versionNo = versionNo;
  }

  public String getVersionNo() {
    return versionNo;
  }

  public void setNumOfVersionLOC(long numOfLOC) {
    this.numOfVersionLOC = numOfLOC;
  }

  public long getNumOfVersionLOC() {
    return this.numOfVersionLOC;
  }

  public void setEmptyLinesOfCode(long emptyLinesOfCode) {
    this.numOfEmptyLinesOfCode = emptyLinesOfCode;
  }

  public long getEmptyLinesOfCode() {
    return numOfEmptyLinesOfCode;
  }

  public void setNumOfPacakge(long numOfPacakge) {
    this.numOfPacakge = numOfPacakge;
  }

  public long getNumOfPacakge() {
    return numOfPacakge;
  }

  public void setNumOfClass(long numOfClass) {
    this.numOfClass = numOfClass;
  }

  public long getNumOfClass() {
    return numOfClass;
  }

  public void setNumOfMethod(long numOfMethod) {
    this.numOfMethod = numOfMethod;
  }

  public long getNumOfMethod() {
    return numOfMethod;
  }

  public void setNumOfPackage(int size) {
    this.numOfPacakge = size;
  }

  public void setNumOfEmptyLinesOfCode(long emptyLinesOfCode) {
    this.numOfEmptyLinesOfCode = emptyLinesOfCode;
  }

  public void setProjectVersionPath(String pathOfVersion) {
    this.pathOfProjectVersion = pathOfVersion;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("ProjectVersionPath: " + pathOfProjectVersion + "\n");
    b.append("ProjectName: " + projectName + "\n");
    b.append("VersionNo: " + versionNo + "\n");
    b.append("NumOfVersionLOC: " + numOfVersionLOC + "\n");
    b.append("NumOfEmptyLineOfCode: " + numOfEmptyLinesOfCode + "\n");
    b.append("NumOfPackage: " + numOfPacakge + "\n");
    b.append("NumOfClass: " + numOfClass + "\n");
    b.append("NumOfMethod: " + numOfMethod + "\n");
    return b.toString();
  }
}

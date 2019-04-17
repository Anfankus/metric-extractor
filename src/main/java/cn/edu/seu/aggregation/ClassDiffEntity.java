package cn.edu.seu.aggregation;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.TreeMultiset; // from guava

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class ClassDiffEntity {

  public String oldFullClassName;
  public String newFullClassName;

  //public String oldSourceFilePath;
  //public String newSourceFilePath;

  public HighLevelDiffEntityChangeType changeType;

  public List<MethodDiffEntity> listMethodDiffEntity;

  public List<SourceCodeChange> allSourceCodeChanges;
  public String oldSourceFilePath;
  public String newSourceFilePath;

  public ClassDiffEntity(String oldFullClassName, String newFullClassName,
      HighLevelDiffEntityChangeType changeType,
      List<MethodDiffEntity> methodDiffEntities, List<SourceCodeChange> allSourceCodeChanges,
      String oldSourceFilePath, String newSourceFilePath) {
    this.oldFullClassName = oldFullClassName;
    this.newFullClassName = newFullClassName;
    this.changeType = changeType;
    this.listMethodDiffEntity = new ArrayList<MethodDiffEntity>(methodDiffEntities);
    this.allSourceCodeChanges = allSourceCodeChanges;
    this.oldSourceFilePath = oldSourceFilePath;
    this.newSourceFilePath = newSourceFilePath;
  }

  public void print() {
    System.out.println(changeType + "," + oldFullClassName + "-->" + newFullClassName);
  }

  public boolean checkUnchangedMethod(String targetOldFullMethodName) {
    for (MethodDiffEntity entry : listMethodDiffEntity) {
      if (entry.oldFullMethodName.equals(targetOldFullMethodName)) {
        return false; // indicates that target method is modified
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return changeType + "," + this.oldFullClassName + "-->" + this.newFullClassName;
  }
}
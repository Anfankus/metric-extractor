package cn.edu.seu.aggregation;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;


public class MethodDiffEntity {

  public String oldFullMethodName;
  public String newFullMethodName;

  //public String oldSourceFilePath;
  //public String newSourceFilePath;

  public List<SourceCodeChange> allSourceCodeChanges;
  public HighLevelDiffEntityChangeType changeType;
  public String oldSourceFilePath;
  public String newSourceFilePath;

  public MethodDiffEntity(String oldFullMethodName, String newFullMethodName,
      HighLevelDiffEntityChangeType changeType, List<SourceCodeChange> sccList,
      String oldSourceFilePath, String newSourceFilePath) {
    this.oldFullMethodName = oldFullMethodName;
    this.newFullMethodName = newFullMethodName;
    this.changeType = changeType;

    this.allSourceCodeChanges = new ArrayList<SourceCodeChange>();
    this.allSourceCodeChanges.addAll(sccList);

    this.oldSourceFilePath = oldSourceFilePath;
    this.newSourceFilePath = newSourceFilePath;
  }

  public void print() {
    System.out.println(changeType + "," + oldFullMethodName + "-->" + newFullMethodName);
		/*for (SourceCodeChange scc : changes) {
			//System.out.println("parent name:" + scc.getParentEntity().getUniqueName());
			//System.out.println("entity type:" + scc.getChangedEntity().getType().name());
			System.out.println("change type:" + scc.getChangeType());
			//System.out.println("changed's entity name:" + scc.getChangedEntity().getUniqueName() + "\n");
		}*/
  }
}
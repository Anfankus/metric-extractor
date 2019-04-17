package cn.edu.seu.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class ChangeAggregation {

  public static List<ClassDiffEntity> doClassAggregation(Map<String, List<SourceCodeChange>> map,
      String oldSourceFilePath,
      String newSourceFilePath) {
    List<ClassDiffEntity> classDiffEntities = new ArrayList<ClassDiffEntity>();
    for (Entry<String, List<SourceCodeChange>> classPairNameToSCCList : map.entrySet()) {
      String oldClassName = classPairNameToSCCList.getKey().split("-->")[0];
      String newClassName = classPairNameToSCCList.getKey().split("-->")[1];
      List<SourceCodeChange> changesBetweenTwoClasses = classPairNameToSCCList.getValue();

      List<MethodDiffEntity> methodDiffEntities = doMethodAggregation(oldClassName, newClassName,
          changesBetweenTwoClasses, oldSourceFilePath, newSourceFilePath);

      ClassDiffEntity newEntity = new ClassDiffEntity(oldClassName, newClassName,
          HighLevelDiffEntityChangeType.Change,
          methodDiffEntities, changesBetweenTwoClasses, oldSourceFilePath,
          newSourceFilePath); // not necessary to aggregate
      classDiffEntities.add(newEntity);
    }

    return classDiffEntities;
  }

  public static List<MethodDiffEntity> doMethodAggregation(String oldClassName, String newClassName,
      List<SourceCodeChange> changesBetweenTwoClasses, String oldSourceFilePath,
      String newSourceFilePath) {
    List<MethodDiffEntity> methodDiffEntities = new ArrayList<MethodDiffEntity>();

    Map<String, String> renamedMethodNameToOldMethodName = new HashMap<String, String>();
    for (SourceCodeChange scc : changesBetweenTwoClasses) {
      if (scc.getRootEntity().getType().isMethod()) {
        String oldName = scc.getParentEntity().getUniqueName();
        String newName = scc.getRootEntity().getUniqueName();
        if (scc.getChangeType() == ChangeType.METHOD_RENAMING) {
          // if already exists, override it
          renamedMethodNameToOldMethodName.put(newName, oldName);
        }
      }
    }

    for (SourceCodeChange scc : changesBetweenTwoClasses) {
      String newMethodName = scc.getRootEntity().getUniqueName();
      if (scc.getRootEntity().getType().isMethod()) {
        // NOTE: method renaming ==> is considered as "Change"
        if (renamedMethodNameToOldMethodName.containsKey(newMethodName)) {
          String oldMethodName = renamedMethodNameToOldMethodName.get(newMethodName);
          update(oldMethodName, newMethodName, HighLevelDiffEntityChangeType.Change, scc,
              methodDiffEntities,
              oldSourceFilePath, newSourceFilePath);

          //print(oldMethodName, newMethodName, scc);
        } else {
          String oldMethodName = newMethodName;
          if (oldClassName.equals(newClassName) == false) {
            oldMethodName = newMethodName.replace(newClassName, oldClassName);
          }

          update(oldMethodName, newMethodName, HighLevelDiffEntityChangeType.Change, scc,
              methodDiffEntities,
              oldSourceFilePath, newSourceFilePath);
          //print(oldMethodName, newMethodName, scc);
        }
      } else if (scc.getRootEntity().getType().isClass()) {
        if (scc.getChangeType() == ChangeType.ADDITIONAL_FUNCTIONALITY) {
          newMethodName = scc.getChangedEntity().getUniqueName();
          //addition,deletion,modification
          update("", newMethodName, HighLevelDiffEntityChangeType.Add, scc, methodDiffEntities,
              oldSourceFilePath,
              newSourceFilePath);
          //print(oldMethodName, newMethodName, scc);
        } else if (scc.getChangeType() == ChangeType.REMOVED_FUNCTIONALITY) {
          String oldMethodName = scc.getChangedEntity().getUniqueName();
          //addition,deletion,modification
          update(oldMethodName, "", HighLevelDiffEntityChangeType.Delete, scc, methodDiffEntities,
              oldSourceFilePath, newSourceFilePath);
          //print(oldMethodName, newMethodName, scc);
        }
      }
    }

    return methodDiffEntities;
  }

  private static void update(String oldMethodName, String newMethodName,
      HighLevelDiffEntityChangeType methodChangeType,
      SourceCodeChange scc, List<MethodDiffEntity> methodDiffEntities, String oldSourceFilePath,
      String newSourceFilePath) {
    boolean found = false;
    for (MethodDiffEntity entity : methodDiffEntities) {
      if (entity.oldFullMethodName.equals(oldMethodName) && entity.newFullMethodName
          .equals(newMethodName)
          && entity.changeType.equals(methodChangeType)) {
        entity.allSourceCodeChanges.add(scc);
        found = true;
        break;
      }
    }

    if (found == false) {
      List<SourceCodeChange> souceCodeChanges = new ArrayList<SourceCodeChange>();
      MethodDiffEntity newEntity = new MethodDiffEntity(oldMethodName, newMethodName,
          methodChangeType,
          souceCodeChanges, oldSourceFilePath, newSourceFilePath);
      methodDiffEntities.add(newEntity);
    }
  }
}

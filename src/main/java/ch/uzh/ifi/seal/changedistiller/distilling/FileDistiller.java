package ch.uzh.ifi.seal.changedistiller.distilling;

import java.io.BufferedReader;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.distilling.refactoring.RefactoringCandidateProcessor;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDifferencer;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;


/**
 * Distills {@link SourceCodeChange}s between two {@link File}.
 *
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 */
public class FileDistiller {

  private DistillerFactory fDistillerFactory;
  private ASTHelperFactory fASTHelperFactory;
  private RefactoringCandidateProcessor fRefactoringProcessor;

  private List<SourceCodeChange> fChanges;
  private ASTHelper<StructureNode> fLeftASTHelper;
  private ASTHelper<StructureNode> fRightASTHelper;
  private ClassHistory fClassHistory;
  private String fVersion;
  private Map<String, List<SourceCodeChange>> lFullTopClassToSourcCodeChanges; // added by Huihui Liu

  /*
   * bind(ASTNodeTypeConverter.class).to(JavaASTNodeTypeConverter.class);
   *
   * bind(SourceCodeChangeClassifier.class).to(JavaSourceCodeChangeClassifier.class);
   *
   * install(new FactoryModuleBuilder().build(DistillerFactory.class));
   *
   * install(new FactoryModuleBuilder().implement(ASTHelper.class, JavaASTHelper.class).build(ASTHelperFactory.class));
   */

  @Inject
  FileDistiller(DistillerFactory distillerFactory, ASTHelperFactory factory,
      RefactoringCandidateProcessor refactoringProcessor) {
    fDistillerFactory = distillerFactory;
    fASTHelperFactory = factory;
    fRefactoringProcessor = refactoringProcessor;

    lFullTopClassToSourcCodeChanges = new HashMap<String, List<SourceCodeChange>>();
  }

  /**
   * Extracts classified {@link SourceCodeChange}s between two {@link File}s.
   *
   * @param left file to extract changes
   * @param right file to extract changes
   */
  public void extractClassifiedSourceCodeChanges(File left, File right) {
    extractClassifiedSourceCodeChanges(left, "1.8", right, "1.8");
  }

  /**
   * Extracts classified {@link SourceCodeChange}s between two {@link File}s.
   *
   * @param left file to extract changes
   * @param leftVersion version of the language in the left file
   * @param right file to extract changes
   * @param leftVersion version of the language in the right file
   */
  @SuppressWarnings("unchecked")
  public void extractClassifiedSourceCodeChanges(File left, String leftVersion, File right,
      String rightVersion) {
    fLeftASTHelper = fASTHelperFactory.create(left, leftVersion);
    fRightASTHelper = fASTHelperFactory.create(right, rightVersion);

    extractDifferences();
  }

  private void extractDifferences() {
    StructureDifferencer structureDifferencer = new StructureDifferencer();
    structureDifferencer.extractDifferences(fLeftASTHelper.createStructureTree(),
        fRightASTHelper.createStructureTree());
    StructureDiffNode structureDiff = structureDifferencer.getDifferences();
    if (structureDiff != null) {
      fChanges = new LinkedList<SourceCodeChange>();
      // structureDiff is composed of leftCU and rightCU
      processRootChildren(structureDiff);
    } else {
      fChanges = Collections.emptyList();
    }
  }

  public void extractClassifiedSourceCodeChanges(File left, File right, String version) {
    fVersion = version;
    this.extractClassifiedSourceCodeChanges(left, right);
  }

  private void processRootChildren(StructureDiffNode diffNode) {
    // diffNode = rootDiffDode, fLeft = null, fRight =null
    // but diffNode.getChildern()= {leftDiffNode, rightDiffNode} for processing
    // multiple child (top-level calss/interface)

    for (StructureDiffNode child : diffNode.getChildren()) {
      if (child.isClassOrInterfaceDiffNode() && mayHaveChanges(child.getLeft(), child.getRight())) {
        if (fClassHistory == null) {
          if (fVersion != null) {
            fClassHistory = new ClassHistory(
                fRightASTHelper.createStructureEntityVersion(child.getRight(), fVersion));
          } else {
            fClassHistory = new ClassHistory(
                fRightASTHelper.createStructureEntityVersion(child.getRight()));
          }
        }
        processClassDiffNode(child);
      }
    }
  }

  private void processClassDiffNode(StructureDiffNode child) { // appear as a pair of DiffNode
    ClassDistiller classDistiller;
    if (fVersion != null) {
      classDistiller = new ClassDistiller(child, fClassHistory, fLeftASTHelper, fRightASTHelper,
          fRefactoringProcessor, fDistillerFactory, fVersion);
    } else {
      classDistiller = new ClassDistiller(child, fClassHistory, fLeftASTHelper, fRightASTHelper,
          fRefactoringProcessor, fDistillerFactory);
    }

    classDistiller.extractChanges();
    fChanges.addAll(classDistiller.getSourceCodeChanges());

    String leftFullClassName = child.getLeft().getFullyQualifiedName();
    String rightFullClassName = child.getRight().getFullyQualifiedName();

    this.lFullTopClassToSourcCodeChanges.put(leftFullClassName + "-->" + rightFullClassName,
        classDistiller.getSourceCodeChanges());
  }

  private boolean mayHaveChanges(StructureNode left, StructureNode right) {
    return (left != null) && (right != null);
  }

  public List<String> getNewVerClassName() {
    return this.fRightASTHelper.getClassName();
  }

  public List<SourceCodeChange> getSourceCodeChanges() {
    return fChanges;
  }

  public Map<String, List<SourceCodeChange>> getSourceCodeChanges2() {
    return this.lFullTopClassToSourcCodeChanges;
  }

  public ClassHistory getClassHistory() {
    return fClassHistory;
  }

  private String readText(String sourceFilePath) {
    StringBuffer sb = new StringBuffer();
    try {
      FileReader fr = new FileReader(sourceFilePath);
      BufferedReader br = new BufferedReader(fr);
      String line = "";
      while ((line = br.readLine()) != null) {
        sb.append(line + "\n");
      }
      fr.close();
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }
}

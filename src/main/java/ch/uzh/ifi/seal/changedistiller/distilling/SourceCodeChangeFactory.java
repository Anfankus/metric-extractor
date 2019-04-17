package ch.uzh.ifi.seal.changedistiller.distilling;

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

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.DeleteOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.InsertOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.MoveOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.UpdateOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeEditOperation;


/**
 * Factory for {@link SourceCodeChange} creation out of {@link TreeEditOperation}.
 *
 * @author Beat Fluri
 */
public class SourceCodeChangeFactory {

  /**
   * Creates an {@link Insert} change from the {@link InsertOperation}.
   *
   * @param structureEntity in which the source code change happened
   * @param insert operation to create the source code change
   * @return the insert source code changes from the insert operation
   */
  public Insert createInsertOperation(StructureEntityVersion structureEntity,
      InsertOperation insert) {
    if (isUsableForChangeExtraction(insert.getNodeToInsert())) {
      SourceCodeEntity parent = insert.getParentNode().getSourceCodeEntity();
      return new Insert(structureEntity, insert.getNodeToInsert().getSourceCodeEntity(), parent);
    }
    return null;
  }

  /**
   * Creates an {@link Delete} change from the {@link DeleteOperation}.
   *
   * @param structureEntity in which the source code change happened
   * @param delete operation to create the source code change
   * @return the delete source code changes from the delete operation
   */
  public Delete createDeleteOperation(StructureEntityVersion structureEntity,
      DeleteOperation delete) {
    if (isUsableForChangeExtraction(delete.getNodeToDelete())) {
      SourceCodeEntity parent = delete.getParentNode().getSourceCodeEntity();
      return new Delete(structureEntity, delete.getNodeToDelete().getSourceCodeEntity(), parent);
    }
    return null;
  }

  /**
   * Creates an {@link Move} change from the {@link MoveOperation}.
   *
   * @param structureEntity in which the source code change happened
   * @param move operation to create the source code
   * @return the move source code changes from the move operation
   */
  public Move createMoveOperation(StructureEntityVersion structureEntity, MoveOperation move) {
    if (isUsableForChangeExtraction(move.getNodeToMove())) {
      return new Move(structureEntity, move.getNodeToMove().getSourceCodeEntity(),
          move.getNewNode().getSourceCodeEntity(), move
          .getOldParent().getSourceCodeEntity(), move.getNewParent().getSourceCodeEntity());
    }
    return null;
  }

  /**
   * Creates an {@link Update} change from the {@link UpdateOperation}.
   *
   * @param structureEntity in which the source code change happened
   * @param update operation to create the source code
   * @return the insert source code changes from the update operation
   */
  public Update createUpdateOperation(StructureEntityVersion structureEntity,
      UpdateOperation update) {
    if (isUsableForChangeExtraction(update.getNodeToUpdate())) {
      String fUniqueName = update.getOldValue();
      EntityType fType = update.getNodeToUpdate().getSourceCodeEntity().getType();
      int fModifiers = update.getNodeToUpdate().getSourceCodeEntity().getModifiers();
      SourceRange fRange = update.getNodeToUpdate().getSourceCodeEntity().getSourceRange();
      SourceCodeEntity entity = new SourceCodeEntity(fUniqueName, fType, fModifiers, fRange);

      StructureEntityVersion rootEntity = structureEntity;
      SourceCodeEntity updatedEntity = entity;
      SourceCodeEntity newEntity = update.getNewNode().getSourceCodeEntity();
      SourceCodeEntity parentEntity = ((Node) update.getNodeToUpdate().getParent())
          .getSourceCodeEntity();
      return new Update(rootEntity, updatedEntity, newEntity, parentEntity);
    }
    return null;
  }

  private boolean isUsableForChangeExtraction(Node node) {
    return node.getLabel().isUsableForChangeExtraction();
  }

}

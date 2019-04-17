package ch.uzh.ifi.seal.changedistiller.distilling;

import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;

public interface DistillerFactory {

  Distiller create(StructureEntityVersion structureEntity);
}

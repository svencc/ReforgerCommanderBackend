package com.recom.commons.calculator.d8algorithm.structure;


import com.recom.commons.model.Aspect;
import com.recom.commons.model.D8Neighbour;
import com.recom.commons.model.maprendererpipeline.dataprovider.Cluster;
import com.recom.commons.model.maprendererpipeline.dataprovider.ClusterNode;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class D8AlgorithmForStructureCluster {


    @NonNull
    public Cluster<StructureItem> generateStructureClusters(@NonNull final Cluster<StructureItem> structureCluster) {
        final int nrCellsWidthX = structureCluster.getNrCellsWidth();
        final int nrCellsHeightY = structureCluster.getNrCellsHeight();

        for (int cellX = 0; cellX < nrCellsWidthX; cellX++) {
            for (int cellY = 0; cellY < nrCellsHeightY; cellY++) {
                processClusterFragmentCells(cellX, cellY, structureCluster);
            }
        }

        return structureCluster;
    }

    @NonNull
    private void processClusterFragmentCells(
            final int cellX,
            final int cellY,
            @NonNull final Cluster<StructureItem> cluster
    ) {
        // @TODO <<<<<<<<<<<<<<<<<<<<< das hier in den cluster-index umziehen; das soll der selber machen :D
        final int maxCellsWidthX = cluster.getNrCellsWidth();
        final int maxCellsHeightY = cluster.getNrCellsHeight();

        int surroundingStructureNeighbourSpaces = 0;
        for (final Aspect aspect : Aspect.iterateValues()) {
            final D8Neighbour neighbour = aspect.getNeighbour(cellX, cellY);
            if (neighbour.isOutCellRange(maxCellsWidthX, maxCellsHeightY)) {
                continue;
            } else {
                if (cluster.isItemThresholdExceeded(neighbour.getX(), neighbour.getY())) {
                    surroundingStructureNeighbourSpaces++;
                    final ClusterNode neighbourNode = cluster.getNode(neighbour.getX(), neighbour.getY());
                }
            }
        }
    }

}

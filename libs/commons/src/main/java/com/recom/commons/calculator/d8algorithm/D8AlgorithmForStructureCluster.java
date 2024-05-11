package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.model.Aspect;
import com.recom.commons.model.D8Neighbour;
import com.recom.commons.model.maprendererpipeline.dataprovider.ClusterIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.ClusterNode;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class D8AlgorithmForStructureCluster {


    @NonNull
    public ClusterIndex<StructureItem> generateStructureClusters(@NonNull final SpacialIndex<StructureItem> structureSpacialIndex) {
        final ClusterIndex<StructureItem> clusterIndex = ClusterIndex.from(structureSpacialIndex);

        final int nrCellsWidthX = clusterIndex.getNrCellsWidth();
        final int nrCellsHeightY = clusterIndex.getNrCellsHeight();

        for (int cellX = 0; cellX < nrCellsWidthX; cellX++) {
            for (int cellY = 0; cellY < nrCellsHeightY; cellY++) {
                processClusterFragmentCells(cellX, cellY, clusterIndex);
            }
        }

        return clusterIndex;
    }

    @NonNull
    private void processClusterFragmentCells(
            final int cellX,
            final int cellY,
            @NonNull final ClusterIndex<StructureItem> clusterIndex

    ) {
        // @TODO <<<<<<<<<<<<<<<<<< das hier in den cluster-index umziehen; das soll der selber machen :D
        final int maxCellsWidthX = clusterIndex.getNrCellsWidth();
        final int maxCellsHeightY = clusterIndex.getNrCellsHeight();

        int surroundingStructureNeighbourSpaces = 0;
        for (final Aspect aspect : Aspect.iterateValues()) {
            final D8Neighbour neighbour = aspect.getNeighbour(cellX, cellY);
            if (neighbour.isOutCellRange(maxCellsWidthX, maxCellsHeightY)) {
                continue;
            } else {
                if (clusterIndex.isItemThresholdExceeded(neighbour.getX(), neighbour.getY())) {
                    surroundingStructureNeighbourSpaces++;
                    final ClusterNode neighbourNode = clusterIndex.getNode(neighbour.getX(), neighbour.getY());
                }
            }
        }
    }

}

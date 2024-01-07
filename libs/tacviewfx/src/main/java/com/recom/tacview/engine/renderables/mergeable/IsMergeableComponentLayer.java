package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.entitycomponentsystem.ChildPropagateableSoilableState;
import com.recom.tacview.engine.entitycomponentsystem.ParentPropagateableSoilableState;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.Mergeable;

public interface IsMergeableComponentLayer extends Mergeable, HasPixelBuffer, ParentPropagateableSoilableState, ChildPropagateableSoilableState {

}

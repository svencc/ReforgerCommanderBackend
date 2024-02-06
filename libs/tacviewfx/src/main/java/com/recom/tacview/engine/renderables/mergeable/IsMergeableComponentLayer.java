package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.ecs.ChildPropagateableSoilableState;
import com.recom.tacview.engine.ecs.ParentPropagateableSoilableState;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.IsMergeable;

public interface IsMergeableComponentLayer extends IsMergeable, HasPixelBuffer, ParentPropagateableSoilableState, ChildPropagateableSoilableState {

}

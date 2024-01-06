package com.recom.commander.enginemodule;

import com.recom.commander.enginemodule.entity.component.RECOMMapEntity;
import com.recom.tacview.engine.entity.Environment;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TacViewerEngineModule extends EngineModule {

    @NonNull
    private final RenderProvider renderProvider;
    @NonNull
    private final RECOMMapEntity mapEntity;


    public TacViewerEngineModule(
            @NonNull final Environment environment,
            @NonNull final RECOMMapEntity mapEntity,
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        super(environment);
        this.mapEntity = mapEntity;
        this.renderProvider = renderProvider;
    }

    @Override
    public void init() {
        // @TODO:
        // in die entity oder die component mussüsen noch die render properties rein ... dann aber gleich mit dem neuen dynamic properties system ...
        // passiert dann aber in: RECOMMapEntityConfiguration
        // das ist vorraussetzung für die dynamische Anpassung der Auflösung / Screen / Fenstergröße
        getEnvironment().getEntities().add(mapEntity);
    }

    @Override
    public void startEngineModule() {

    }

    @Override
    public void handleInput() {
        super.handleInput();
    }

}

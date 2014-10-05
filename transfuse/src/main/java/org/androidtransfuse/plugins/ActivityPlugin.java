/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.plugins;

import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfusePlugin;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTPrimitiveType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.experiment.ScopesGeneration;
import org.androidtransfuse.experiment.generators.*;
import org.androidtransfuse.gen.GeneratorFactory;
import org.androidtransfuse.gen.componentBuilder.ListenerRegistrationGenerator;
import org.androidtransfuse.gen.componentBuilder.NonConfigurationInstanceGenerator;
import org.androidtransfuse.gen.variableBuilder.*;
import org.androidtransfuse.intentFactory.ActivityIntentFactoryStrategy;
import org.androidtransfuse.listeners.*;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
@Bootstrap
public class ActivityPlugin implements TransfusePlugin{

    @Inject
    InjectionBindingBuilder injectionBindingBuilder;
    @Inject
    DescriptorBuilderUtil builder;
    @Inject
    ASTClassFactory astClassFactory;
    @Inject
    ASTElementFactory astElementFactory;
    @Inject
    ViewInjectionNodeBuilder viewVariableBuilder;
    @Inject
    ExtraInjectionNodeBuilder extraInjectionNodeBuilder;
    @Inject
    SystemServiceBindingInjectionNodeBuilder systemServiceBindingInjectionNodeBuilder;
    @Inject
    ResourceInjectionNodeBuilder resourceInjectionNodeBuilder;
    @Inject
    PreferenceInjectionNodeBuilder preferenceInjectionNodeBuilder;
    @Inject
    ObservesExpressionGenerator.ObservesExpressionGeneratorFactory observesExpressionGeneratorFactory;
    @Inject
    Provider<ActivityManifestEntryGenerator> manifestGeneratorProvider;
    @Inject
    LayoutGenerator layoutGenerator;
    @Inject
    LayoutHandlerGenerator layoutHandlerGenerator;
    @Inject
    WindowFeatureGenerator windowFeatureGenerator;
    @Inject
    GeneratorFactory generatorFactory;
    @Inject
    ListenerRegistrationGenerator.ListerRegistrationGeneratorFactory listerRegistrationGeneratorFactory;
    @Inject
    NonConfigurationInstanceGenerator.NonconfigurationInstanceGeneratorFactory nonConfigurationInstanceGeneratorFactory;
    @Inject
    OnCreateInjectionGenerator.InjectionGeneratorFactory onCreateInjectionGeneratorFactory;
    @Inject
    ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory;

    @Override
    public void run(ConfigurationRepository repository) {

        repository.add(builder.component(Activity.class).method("onCreate", AndroidLiterals.BUNDLE).registration().event(OnCreate.class).superCall());
        repository.add(builder.component(Activity.class).method("onDestroy").event(OnDestroy.class).superCall());
        repository.add(builder.component(Activity.class).method("onPause").event(OnPause.class).superCall());
        repository.add(builder.component(Activity.class).method("onRestart").event(OnRestart.class).superCall());
        repository.add(builder.component(Activity.class).method("onResume").event(OnResume.class).superCall());
        repository.add(builder.component(Activity.class).method("onStart").event(OnStart.class).superCall());
        repository.add(builder.component(Activity.class).method("onStop").event(OnStop.class).superCall());
        repository.add(builder.component(Activity.class).method("onBackPressed").event(OnBackPressed.class).superCall());
        repository.add(builder.component(Activity.class).method("onPostCreate", AndroidLiterals.BUNDLE).event(OnPostCreate.class).superCall());
        repository.add(builder.component(Activity.class).method("onActivityResult", ASTPrimitiveType.INT, ASTPrimitiveType.INT, AndroidLiterals.INTENT).event(OnActivityResult.class));
        repository.add(builder.component(Activity.class).method("onNewIntent", AndroidLiterals.INTENT).event(OnNewIntent.class));
        repository.add(builder.component(Activity.class).method("onSaveInstanceState", AndroidLiterals.BUNDLE).event(OnSaveInstanceState.class).superCall());
        repository.add(builder.component(Activity.class).method("onRestoreInstanceState", AndroidLiterals.BUNDLE).event(OnRestoreInstanceState.class).superCall());
        repository.add(builder.component(Activity.class).extending(AndroidLiterals.LIST_ACTIVITY)
                .method("onListItemClick", AndroidLiterals.LIST_VIEW, AndroidLiterals.VIEW, ASTPrimitiveType.INT, ASTPrimitiveType.LONG).event(OnListItemClick.class));

        repository.add(builder.component(Activity.class).callThroughEvent(ActivityMenuComponent.class));
        repository.add(builder.component(Activity.class).callThroughEvent(ActivityOnKeyDownListener.class));
        repository.add(builder.component(Activity.class).callThroughEvent(ActivityOnKeyLongPressListener.class));
        repository.add(builder.component(Activity.class).callThroughEvent(ActivityOnKeyMultipleListener.class));
        repository.add(builder.component(Activity.class).callThroughEvent(ActivityOnKeyUpListener.class));
        repository.add(builder.component(Activity.class).callThroughEvent(ActivityOnTouchEventListener.class));
        repository.add(builder.component(Activity.class).callThroughEvent(ActivityOnTrackballEventListener.class));

        repository.add(builder.component(Activity.class).listener(AndroidLiterals.VIEW_ON_CLICK_LISTENER, AndroidLiterals.VIEW, "setOnClickListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.VIEW_ON_LONG_CLICK_LISTENER, AndroidLiterals.VIEW, "setOnLongClickListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.VIEW_ON_CREATE_CONTEXT_MENU_LISTENER, AndroidLiterals.VIEW, "setOnCreateContextMenuListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.VIEW_ON_KEY_LISTENER, AndroidLiterals.VIEW, "setOnKeyListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.VIEW_ON_TOUCH_LISTENER, AndroidLiterals.VIEW, "setOnTouchListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.VIEW_ON_FOCUS_CHANGE_LISTENER, AndroidLiterals.VIEW, "setOnFocusChangeListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.ADAPTER_VIEW_ON_ITEM_CLICK_LISTENER, AndroidLiterals.ADAPTER_VIEW, "setOnItemClickListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.ADAPTER_VIEW_ON_ITEM_LONG_CLICK_LISTENER, AndroidLiterals.ADAPTER_VIEW, "setOnItemLongClickListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.ADAPTER_VIEW_ON_ITEM_SELECTED_LISTENER, AndroidLiterals.ADAPTER_VIEW, "setOnItemSelectedListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.ABS_LIST_VIEW_ON_SCROLL_LISTENER, AndroidLiterals.ABS_LIST_VIEw, "setOnScrollListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.ABS_LIST_VIEW_MULTI_CHOICE_MODE_LISTENER, AndroidLiterals.ABS_LIST_VIEw, "setMultiChoiceModeListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.ABS_LIST_VIEW_RECYCLER_LISTENER, AndroidLiterals.ABS_LIST_VIEw, "setViewRecyclerListener"));
        repository.add(builder.component(Activity.class).listener(AndroidLiterals.TEXT_VIEW_ON_EDITOR_ACTION_LISTENER, AndroidLiterals.TEXT_VIEW, "setOnEditorActionListener"));

        repository.add(builder.component(Activity.class).add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                ASTMethod onCreateMethod = astElementFactory.findMethod(AndroidLiterals.ACTIVITY, "onCreate", AndroidLiterals.BUNDLE);
                ASTMethod onResumeMethod = astElementFactory.findMethod(AndroidLiterals.ACTIVITY, "onResume");
                ASTMethod onPauseMethod = astElementFactory.findMethod(AndroidLiterals.ACTIVITY, "onPause");
                
                descriptor.getAnalysisContext().getInjectionNodeBuilders().putAnnotation(Extra.class, extraInjectionNodeBuilder);
                descriptor.getAnalysisContext().getInjectionNodeBuilders().putAnnotation(Resource.class, resourceInjectionNodeBuilder);
                descriptor.getAnalysisContext().getInjectionNodeBuilders().putAnnotation(SystemService.class, systemServiceBindingInjectionNodeBuilder);
                descriptor.getAnalysisContext().getInjectionNodeBuilders().putAnnotation(Preference.class, preferenceInjectionNodeBuilder);
                descriptor.getAnalysisContext().getInjectionNodeBuilders().putAnnotation(View.class, viewVariableBuilder);

                descriptor.getGenerators().add(layoutGenerator);
                descriptor.getGenerators().add(layoutHandlerGenerator);
                descriptor.getGenerators().add(windowFeatureGenerator);
                descriptor.getGenerators().add(scopesGenerationFactory.build(onCreateMethod));
                descriptor.getGenerators().add(onCreateInjectionGeneratorFactory.build(onCreateMethod));
                //extra intent factory
                descriptor.getGenerators().add(generatorFactory.buildStrategyGenerator(ActivityIntentFactoryStrategy.class));
                //listener registration
                descriptor.getGenerators().add(listerRegistrationGeneratorFactory.build(onCreateMethod));
                //non configuration instance update
                descriptor.getGenerators().add(nonConfigurationInstanceGeneratorFactory.build(onCreateMethod));

                descriptor.getGenerators().add(observesExpressionGeneratorFactory.build(
                        onCreateMethod,
                        onResumeMethod,
                        onPauseMethod));

                descriptor.getAnalysisContext().getInjectionNodeBuilders()
                        .putType(AndroidLiterals.MENU_INFLATER, injectionBindingBuilder.dependency(AndroidLiterals.ACTIVITY).invoke(AndroidLiterals.MENU_INFLATER, "getMenuInflater").build());
            }
        }));
    }
}

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
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.experiment.ScopesGeneration;
import org.androidtransfuse.experiment.generators.FragmentLayoutGenerator;
import org.androidtransfuse.experiment.generators.ObservesExpressionGenerator;
import org.androidtransfuse.experiment.generators.OnCreateInjectionGenerator;
import org.androidtransfuse.gen.componentBuilder.ComponentBuilderFactory;
import org.androidtransfuse.gen.componentBuilder.ListenerRegistrationGenerator;
import org.androidtransfuse.gen.variableBuilder.*;
import org.androidtransfuse.listeners.FragmentMenuComponent;
import org.androidtransfuse.model.InjectionSignature;
import org.androidtransfuse.scope.ApplicationScope;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
@Bootstrap
public class FragmentPlugin implements TransfusePlugin{

    @Inject
    DescriptorBuilderUtil builder;
    @Inject
    ASTElementFactory astElementFactory;
    @Inject
    ASTClassFactory astClassFactory;
    @Inject
    ComponentBuilderFactory componentBuilderFactory;
    @Inject
    ListenerRegistrationGenerator.ListerRegistrationGeneratorFactory listenerRegistrationGeneratorFactory;
    @Inject
    ObservesExpressionGenerator.ObservesExpressionGeneratorFactory observesExpressionGeneratorFactory;
    @Inject
    OnCreateInjectionGenerator.InjectionGeneratorFactory injectionGeneratorFactory;
    @Inject
    FragmentLayoutGenerator fragmentLayoutGenerator;
    @Inject
    ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory;
    @Inject
    ProviderInjectionNodeBuilderFactory providerInjectionNodeBuilder;
    @Inject
    InjectionBindingBuilder injectionBindingBuilder;
    @Inject
    ExtraInjectionNodeBuilder extraInjectionNodeBuilder;
    @Inject
    SystemServiceBindingInjectionNodeBuilder systemServiceBindingInjectionNodeBuilder;
    @Inject
    ResourceInjectionNodeBuilder resourceInjectionNodeBuilder;
    @Inject
    PreferenceInjectionNodeBuilder preferenceInjectionNodeBuilder;
    @Inject
    FragmentViewInjectionNodeBuilder fragmentViewInjectionNodeBuilder;

    @Override
    public void run(ConfigurationRepository repository) {

        repository.add(builder.component(Fragment.class).method("onCreateView", AndroidLiterals.LAYOUT_INFLATER, AndroidLiterals.VIEW_GROUP, AndroidLiterals.BUNDLE).registration().event(OnCreate.class));
        repository.add(builder.component(Fragment.class).method("onActivityCreated", AndroidLiterals.BUNDLE).event(OnActivityCreated.class).superCall());
        repository.add(builder.component(Fragment.class).method("onStart").event(OnStart.class).superCall());
        repository.add(builder.component(Fragment.class).method("onResume").event(OnResume.class).superCall());
        repository.add(builder.component(Fragment.class).method("onPause").event(OnPause.class).superCall());
        repository.add(builder.component(Fragment.class).method("onStop").event(OnStop.class).superCall());
        repository.add(builder.component(Fragment.class).method("onDestroyView").event(OnDestroyView.class).superCall());
        repository.add(builder.component(Fragment.class).method("onDestroy").event(OnDestroy.class).superCall());
        repository.add(builder.component(Fragment.class).method("onDetach").event(OnDetach.class).superCall());
        repository.add(builder.component(Fragment.class).method("onLowMemory").event(OnLowMemory.class).superCall());
        repository.add(builder.component(Fragment.class).method("onActivityResult", ASTPrimitiveType.INT, ASTPrimitiveType.INT, AndroidLiterals.INTENT).event(OnActivityResult.class));
        repository.add(builder.component(Fragment.class).method("onConfigurationChanged", AndroidLiterals.CONTENT_CONFIGURATION).event(OnConfigurationChanged.class).superCall());

        repository.add(builder.component(Fragment.class)
                .extending(AndroidLiterals.LIST_FRAGMENT)
                .method("onListItemClick", AndroidLiterals.LIST_VIEW, AndroidLiterals.VIEW, ASTPrimitiveType.INT, ASTPrimitiveType.LONG).event(OnListItemClick.class));

        repository.add(builder.component(Fragment.class).callThroughEvent(FragmentMenuComponent.class));

        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.VIEW_ON_CLICK_LISTENER, AndroidLiterals.VIEW, "setOnClickListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.VIEW_ON_LONG_CLICK_LISTENER, AndroidLiterals.VIEW, "setOnLongClickListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.VIEW_ON_CREATE_CONTEXT_MENU_LISTENER, AndroidLiterals.VIEW, "setOnCreateContextMenuListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.VIEW_ON_KEY_LISTENER, AndroidLiterals.VIEW, "setOnKeyListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.VIEW_ON_TOUCH_LISTENER, AndroidLiterals.VIEW, "setOnTouchListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.VIEW_ON_FOCUS_CHANGE_LISTENER, AndroidLiterals.VIEW, "setOnFocusChangeListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.ADAPTER_VIEW_ON_ITEM_CLICK_LISTENER, AndroidLiterals.ADAPTER_VIEW, "setOnItemClickListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.ADAPTER_VIEW_ON_ITEM_LONG_CLICK_LISTENER, AndroidLiterals.ADAPTER_VIEW, "setOnItemLongClickListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.ADAPTER_VIEW_ON_ITEM_SELECTED_LISTENER, AndroidLiterals.ADAPTER_VIEW, "setOnItemSelectedListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.ABS_LIST_VIEW_ON_SCROLL_LISTENER, AndroidLiterals.ABS_LIST_VIEw, "setOnScrollListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.ABS_LIST_VIEW_MULTI_CHOICE_MODE_LISTENER, AndroidLiterals.ABS_LIST_VIEw, "setMultiChoiceModeListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.ABS_LIST_VIEW_RECYCLER_LISTENER, AndroidLiterals.ABS_LIST_VIEw, "setViewRecyclerListener"));
        repository.add(builder.component(Fragment.class).listener(AndroidLiterals.TEXT_VIEW_ON_EDITOR_ACTION_LISTENER, AndroidLiterals.TEXT_VIEW, "setOnEditorActionListener"));

        repository.add(builder.component(Fragment.class).add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                ASTMethod onCreateViewMethod = astElementFactory.findMethod(AndroidLiterals.FRAGMENT, "onCreateView", AndroidLiterals.LAYOUT_INFLATER, AndroidLiterals.VIEW_GROUP, AndroidLiterals.BUNDLE);
                ASTMethod onResumeMethod = astElementFactory.findMethod(AndroidLiterals.FRAGMENT, "onResume");
                ASTMethod onPauseMethod = astElementFactory.findMethod(AndroidLiterals.FRAGMENT, "onPause");
                ASTMethod onSaveInstanceStateMethod = astElementFactory.findMethod(AndroidLiterals.FRAGMENT, "onSaveInstanceState", AndroidLiterals.BUNDLE);

                descriptor.getGenerators().add(injectionGeneratorFactory.build(onCreateViewMethod));
                descriptor.getGenerators().add(scopesGenerationFactory.build(onCreateViewMethod));
                descriptor.getGenerators().add(fragmentLayoutGenerator);
                // onSaveInstanceState
                descriptor.getGenerators().add(componentBuilderFactory.buildFragmentOnSaveInstanceStateMethodCallbackGenerator(astClassFactory.getType(OnSaveInstanceState.class), onSaveInstanceStateMethod, onCreateViewMethod));

                descriptor.getGenerators().add(listenerRegistrationGeneratorFactory.build(onCreateViewMethod));

                descriptor.getGenerators().add(observesExpressionGeneratorFactory.build(
                        onCreateViewMethod,
                        onResumeMethod,
                        onPauseMethod
                ));

                InjectionNodeBuilderRepository injectionNodeBuilderRepository = descriptor.getAnalysisContext().getInjectionNodeBuilders();

                ASTType applicationScopeType = astElementFactory.getType(ApplicationScope.ApplicationScopeQualifier.class);
                ASTType applicationProvider = astElementFactory.getType(ApplicationScope.ApplicationProvider.class);
                injectionNodeBuilderRepository.putType(AndroidLiterals.APPLICATION, providerInjectionNodeBuilder.builderProviderBuilder(applicationProvider));
                injectionNodeBuilderRepository.putScoped(new InjectionSignature(AndroidLiterals.APPLICATION), applicationScopeType);

                injectionNodeBuilderRepository.putType(AndroidLiterals.FRAGMENT, injectionBindingBuilder.buildThis(AndroidLiterals.FRAGMENT));
                injectionNodeBuilderRepository.putType(AndroidLiterals.ACTIVITY, injectionBindingBuilder.dependency(AndroidLiterals.FRAGMENT).invoke(AndroidLiterals.ACTIVITY, "getActivity").build());
                injectionNodeBuilderRepository.putType(AndroidLiterals.CONTEXT, injectionBindingBuilder.dependency(AndroidLiterals.FRAGMENT).invoke(AndroidLiterals.CONTEXT, "getActivity").build());
                injectionNodeBuilderRepository.putType(AndroidLiterals.FRAGMENT_MANAGER, injectionBindingBuilder.dependency(AndroidLiterals.FRAGMENT).invoke(AndroidLiterals.FRAGMENT_MANAGER, "getFragmentManager").build());

                ASTType fragmentType = descriptor.getType();

                while(!fragmentType.equals(AndroidLiterals.FRAGMENT) && fragmentType.inheritsFrom(AndroidLiterals.FRAGMENT)){
                    injectionNodeBuilderRepository.putType(fragmentType, injectionBindingBuilder.buildThis(fragmentType));
                    fragmentType = fragmentType.getSuperClass();
                }

                injectionNodeBuilderRepository.putAnnotation(Extra.class, extraInjectionNodeBuilder);
                injectionNodeBuilderRepository.putAnnotation(Resource.class, resourceInjectionNodeBuilder);
                injectionNodeBuilderRepository.putAnnotation(SystemService.class, systemServiceBindingInjectionNodeBuilder);
                injectionNodeBuilderRepository.putAnnotation(Preference.class, preferenceInjectionNodeBuilder);
                injectionNodeBuilderRepository.putAnnotation(org.androidtransfuse.annotations.View.class, fragmentViewInjectionNodeBuilder);
            }
        }));
    }
}

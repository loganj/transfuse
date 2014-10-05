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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfusePlugin;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepositoryFactory;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.experiment.ScopesGeneration;
import org.androidtransfuse.experiment.generators.ObservesExpressionGenerator;
import org.androidtransfuse.experiment.generators.OnCreateInjectionGenerator;
import org.androidtransfuse.experiment.generators.ServiceManifestEntryGenerator;
import org.androidtransfuse.gen.GeneratorFactory;
import org.androidtransfuse.gen.componentBuilder.ListenerRegistrationGenerator;
import org.androidtransfuse.gen.variableBuilder.InjectionBindingBuilder;
import org.androidtransfuse.gen.variableBuilder.ProviderInjectionNodeBuilderFactory;
import org.androidtransfuse.intentFactory.ServiceIntentFactoryStrategy;
import org.androidtransfuse.listeners.ServiceOnStartCommand;
import org.androidtransfuse.listeners.ServiceOnUnbind;
import org.androidtransfuse.model.InjectionSignature;
import org.androidtransfuse.model.MethodDescriptor;
import org.androidtransfuse.scope.ApplicationScope;
import org.androidtransfuse.tomove.*;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
@Bootstrap
public class ServicePlugin implements TransfusePlugin{

    @Inject
    DescriptorBuilderUtil builder;
    @Inject
    ASTElementFactory astElementFactory;
    @Inject
    GeneratorFactory generatorFactory;
    @Inject
    ListenerRegistrationGenerator.ListerRegistrationGeneratorFactory listenerRegistrationGeneratorFactory;
    @Inject
    ObservesExpressionGenerator.ObservesExpressionGeneratorFactory observesExpressionDecoratorFactory;
    @Inject
    ServiceManifestEntryGenerator serviceManifestEntryGenerator;
    @Inject
    OnCreateInjectionGenerator.InjectionGeneratorFactory onCreateInjectionGeneratorFactory;
    @Inject
    ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory;
    @Inject
    InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory;
    @Inject
    ProviderInjectionNodeBuilderFactory providerInjectionNodeBuilder;
    @Inject
    InjectionBindingBuilder injectionBindingBuilder;

    @Override
    public void run(ConfigurationRepository repository) {
        repository.add(builder.component(Service.class).method("onCreate").registration().event(OnCreate.class).superCall());
        repository.add(builder.component(Service.class).method("onDestroy").event(OnDestroy.class).superCall());
        repository.add(builder.component(Service.class).method("onLowMemory").event(OnLowMemory.class).superCall());
        repository.add(builder.component(Service.class).method("onRebind", AndroidLiterals.INTENT).event(OnRebind.class).superCall());
        repository.add(builder.component(Service.class).method("onConfigurationChanged", AndroidLiterals.CONTENT_CONFIGURATION).event(OnConfigurationChanged.class).superCall());
        repository.add(builder.component(Service.class).extending(AndroidLiterals.INTENT_SERVICE)
                .method("onHandleIntent", AndroidLiterals.INTENT).event(OnHandleIntent.class).superCall());

        repository.add(builder.component(Service.class).callThroughEvent(ServiceOnStartCommand.class));
        repository.add(builder.component(Service.class).callThroughEvent(ServiceOnUnbind.class));

        repository.add(builder.component(Service.class).add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                ASTMethod onCreateMethod = astElementFactory.findMethod(AndroidLiterals.SERVICE, "onCreate");
                ASTMethod onDestroyMethod = astElementFactory.findMethod(AndroidLiterals.SERVICE, "onDestroy");

                descriptor.getGenerators().add(onCreateInjectionGeneratorFactory.build(onCreateMethod));
                descriptor.getGenerators().add(scopesGenerationFactory.build(onCreateMethod));
                descriptor.getGenerators().add(new OnBindGenerator());
                descriptor.getGenerators().add(listenerRegistrationGeneratorFactory.build(onCreateMethod));
                descriptor.getGenerators().add(observesExpressionDecoratorFactory.build(
                        onCreateMethod,
                        onCreateMethod,
                        onDestroyMethod
                ));
                descriptor.getGenerators().add(generatorFactory.buildStrategyGenerator(ServiceIntentFactoryStrategy.class));


                InjectionNodeBuilderRepository injectionNodeBuilderRepository = descriptor.getAnalysisContext().getInjectionNodeBuilders();
                ASTType applicationScopeType = astElementFactory.getType(ApplicationScope.ApplicationScopeQualifier.class);
                ASTType applicationProvider = astElementFactory.getType(ApplicationScope.ApplicationProvider.class);
                injectionNodeBuilderRepository.putType(AndroidLiterals.APPLICATION, providerInjectionNodeBuilder.builderProviderBuilder(applicationProvider));
                injectionNodeBuilderRepository.putType(AndroidLiterals.CONTEXT, injectionBindingBuilder.buildThis(AndroidLiterals.CONTEXT));
                injectionNodeBuilderRepository.putScoped(new InjectionSignature(AndroidLiterals.APPLICATION), applicationScopeType);

                injectionNodeBuilderRepository.putType(AndroidLiterals.SERVICE, injectionBindingBuilder.buildThis(AndroidLiterals.SERVICE));

                ASTType serviceType = descriptor.getType();

                while(!serviceType.equals(AndroidLiterals.SERVICE) && serviceType.inheritsFrom(AndroidLiterals.SERVICE)){
                    injectionNodeBuilderRepository.putType(serviceType, injectionBindingBuilder.buildThis(serviceType));
                    serviceType = serviceType.getSuperClass();
                }

                injectionNodeBuilderRepository.addRepository(injectionNodeBuilderRepositoryFactory.buildModuleConfiguration());
            }
        }));
    }

    private final class OnBindGenerator implements Generation {
        @Override
        public void schedule(ComponentBuilder builder, ComponentDescriptor descriptor) {
            builder.add(astElementFactory.findMethod(AndroidLiterals.SERVICE, "onBind", AndroidLiterals.INTENT), GenerationPhase.INIT, new ComponentMethodGenerator() {
                @Override
                public void generate(MethodDescriptor methodDescriptor, JBlock block) {
                    block._return(JExpr._null());
                }
            });
        }
    }
}

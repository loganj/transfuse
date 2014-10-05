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
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepositoryFactory;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.experiment.ScopesGeneration;
import org.androidtransfuse.experiment.generators.ApplicationManifestEntryGenerator;
import org.androidtransfuse.experiment.generators.ApplicationScopeSeedGenerator;
import org.androidtransfuse.experiment.generators.ObservesExpressionGenerator;
import org.androidtransfuse.experiment.generators.OnCreateInjectionGenerator;
import org.androidtransfuse.gen.variableBuilder.ProviderInjectionNodeBuilderFactory;
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
public class ApplicationPlugin implements TransfusePlugin{

    @Inject
    DescriptorBuilderUtil builder;
    @Inject
    ASTElementFactory astElementFactory;
    @Inject
    ObservesExpressionGenerator.ObservesExpressionGeneratorFactory observesExpressionGeneratorFactory;
    @Inject
    OnCreateInjectionGenerator.InjectionGeneratorFactory injectionGeneratorFactory;
    @Inject
    ApplicationManifestEntryGenerator applicationManifestEntryGenerator;
    @Inject
    ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory;
    @Inject
    ApplicationScopeSeedGenerator applicationScopeSeedGenerator;
    @Inject
    ProviderInjectionNodeBuilderFactory providerInjectionNodeBuilder;
    @Inject
    InjectionNodeBuilderRepositoryFactory variableBuilderRepositoryFactory;

    @Override
    public void run(ConfigurationRepository repository) {
        repository.add(builder.component(Application.class).method("onCreate").registration().event(OnCreate.class).superCall());
        repository.add(builder.component(Application.class).method("onLowMemory").event(OnLowMemory.class));
        repository.add(builder.component(Application.class).method("onTerminate").event(OnTerminate.class));
        repository.add(builder.component(Application.class).method("onConfigurationChanged", AndroidLiterals.CONTENT_CONFIGURATION).event(OnConfigurationChanged.class));

        repository.add(builder.component(Application.class).add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                ASTMethod onCreateMethod = astElementFactory.findMethod(AndroidLiterals.APPLICATION, "onCreate");
                ASTMethod onTerminateMethod = astElementFactory.findMethod(AndroidLiterals.APPLICATION, "onTerminate");

                descriptor.getGenerators().add(scopesGenerationFactory.build(onCreateMethod));
                descriptor.getGenerators().add(injectionGeneratorFactory.build(onCreateMethod));
                descriptor.getGenerators().add(observesExpressionGeneratorFactory.build(
                        onCreateMethod,
                        onCreateMethod,
                        onTerminateMethod
                ));
                descriptor.getGenerators().add(applicationScopeSeedGenerator);

                InjectionNodeBuilderRepository injectionNodeBuilderRepository = descriptor.getAnalysisContext().getInjectionNodeBuilders();


                ASTType applicationScopeType = astElementFactory.getType(ApplicationScope.ApplicationScopeQualifier.class);
                ASTType applicationProvider = astElementFactory.getType(ApplicationScope.ApplicationProvider.class);
                injectionNodeBuilderRepository.putType(AndroidLiterals.APPLICATION, providerInjectionNodeBuilder.builderProviderBuilder(applicationProvider));
                injectionNodeBuilderRepository.putType(AndroidLiterals.CONTEXT, providerInjectionNodeBuilder.builderProviderBuilder(applicationProvider));
                injectionNodeBuilderRepository.putScoped(new InjectionSignature(AndroidLiterals.APPLICATION), applicationScopeType);
                injectionNodeBuilderRepository.putScoped(new InjectionSignature(AndroidLiterals.CONTEXT), applicationScopeType);

                /*while(!applicationType.equals(AndroidLiterals.APPLICATION) && applicationType.inheritsFrom(AndroidLiterals.APPLICATION)){
                    injectionNodeBuilderRepository.putType(applicationType, injectionBindingBuilder.buildThis(applicationType));
                    applicationType = applicationType.getSuperClass();
                }*/

                injectionNodeBuilderRepository.addRepository(variableBuilderRepositoryFactory.buildModuleConfiguration());
            }
        }));
    }
}

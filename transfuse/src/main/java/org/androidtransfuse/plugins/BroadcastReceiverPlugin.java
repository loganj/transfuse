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
import org.androidtransfuse.annotations.BroadcastReceiver;
import org.androidtransfuse.annotations.OnReceive;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.experiment.ScopesGeneration;
import org.androidtransfuse.experiment.generators.OnCreateInjectionGenerator;
import org.androidtransfuse.gen.variableBuilder.InjectionBindingBuilder;
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
public class BroadcastReceiverPlugin implements TransfusePlugin{

    @Inject
    InjectionBindingBuilder injectionBindingBuilder;
    @Inject
    DescriptorBuilderUtil builder;
    @Inject
    OnCreateInjectionGenerator.InjectionGeneratorFactory onCreateInjectionGeneratorFactory;
    @Inject
    ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory;
    @Inject
    ASTElementFactory astElementFactory;
    @Inject
    ProviderInjectionNodeBuilderFactory providerInjectionNodeBuilder;
    @Inject
    InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory;

    @Override
    public void run(ConfigurationRepository repository) {
        repository.add(builder.component(BroadcastReceiver.class).method("onReceive", AndroidLiterals.CONTEXT, AndroidLiterals.INTENT).registration().event(OnReceive.class));

        repository.add(builder.component(BroadcastReceiver.class).add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                ASTMethod onReceiveMethod = astElementFactory.findMethod(AndroidLiterals.BROADCAST_RECEIVER, "onReceive", AndroidLiterals.CONTEXT, AndroidLiterals.INTENT);

                descriptor.getGenerators().add(scopesGenerationFactory.build(onReceiveMethod));
                descriptor.getGenerators().add(onCreateInjectionGeneratorFactory.build(onReceiveMethod));

                InjectionNodeBuilderRepository injectionNodeBuilderRepository = descriptor.getAnalysisContext().getInjectionNodeBuilders();

                ASTType applicationScopeType = astElementFactory.getType(ApplicationScope.ApplicationScopeQualifier.class);
                ASTType applicationProvider = astElementFactory.getType(ApplicationScope.ApplicationProvider.class);

                injectionNodeBuilderRepository.putType(AndroidLiterals.APPLICATION, providerInjectionNodeBuilder.builderProviderBuilder(applicationProvider));
                injectionNodeBuilderRepository.putScoped(new InjectionSignature(AndroidLiterals.APPLICATION), applicationScopeType);

                injectionNodeBuilderRepository.addRepository(injectionNodeBuilderRepositoryFactory.buildModuleConfiguration());

                ASTType broadcastReceiverType = descriptor.getType();

                while (!broadcastReceiverType.equals(AndroidLiterals.BROADCAST_RECEIVER) && broadcastReceiverType.inheritsFrom(AndroidLiterals.BROADCAST_RECEIVER)) {
                    injectionNodeBuilderRepository.putType(broadcastReceiverType, injectionBindingBuilder.buildThis(broadcastReceiverType));
                    broadcastReceiverType = broadcastReceiverType.getSuperClass();
                }
            }
        }));


    }
}

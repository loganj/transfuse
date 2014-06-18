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
package org.androidtransfuse.analysis;

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.adapter.ASTAnnotation;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.adapter.element.ASTTypeBuilderVisitor;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.annotations.BroadcastReceiver;
import org.androidtransfuse.annotations.OnReceive;
import org.androidtransfuse.gen.componentBuilder.ComponentBuilderFactory;
import org.androidtransfuse.gen.componentBuilder.ContextScopeComponentBuilder;
import org.androidtransfuse.gen.componentBuilder.ObservesRegistrationGenerator;
import org.androidtransfuse.gen.variableBuilder.InjectionBindingBuilder;
import org.androidtransfuse.model.ComponentDescriptor;
import org.androidtransfuse.model.manifest.Receiver;
import org.androidtransfuse.processor.ManifestManager;
import org.androidtransfuse.util.AndroidLiterals;
import org.androidtransfuse.util.TypeMirrorRunnable;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.type.TypeMirror;

import static org.androidtransfuse.util.AnnotationUtil.checkBlank;
import static org.androidtransfuse.util.AnnotationUtil.checkDefault;
import static org.androidtransfuse.util.TypeMirrorUtil.getTypeMirror;

/**
 * @author John Ericksen
 */
public class BroadcastReceiverAnalysis implements Analysis<ComponentDescriptor> {

    private final ASTClassFactory astClassFactory;
    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final AnalysisContextFactory analysisContextFactory;
    private final Provider<Receiver> receiverProvider;
    private final Provider<InjectionNodeBuilderRepository> injectionNodeBuilderRepositoryProvider;
    private final InjectionBindingBuilder injectionBindingBuilder;
    private final ManifestManager manifestManager;
    private final ComponentBuilderFactory componentBuilderFactory;
    private final IntentFilterFactory intentFilterBuilder;
    private final MetaDataBuilder metaDataBuilder;
    private final ContextScopeComponentBuilder contextScopeComponentBuilder;
    private final ObservesRegistrationGenerator observesExpressionDecorator;

    @Inject
    public BroadcastReceiverAnalysis(ASTClassFactory astClassFactory,
                                     ASTTypeBuilderVisitor astTypeBuilderVisitor,
                                     AnalysisContextFactory analysisContextFactory, Provider<Receiver> receiverProvider,
                                     Provider<InjectionNodeBuilderRepository> injectionNodeBuilderRepositoryProvider,
                                     InjectionBindingBuilder injectionBindingBuilder, ManifestManager manifestManager,
                                     ComponentBuilderFactory componentBuilderFactory,
                                     IntentFilterFactory intentFilterBuilder,
                                     MetaDataBuilder metaDataBuilder,
                                     ContextScopeComponentBuilder contextScopeComponentBuilder,
                                     ObservesRegistrationGenerator observesExpressionDecorator) {
        this.astClassFactory = astClassFactory;
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.analysisContextFactory = analysisContextFactory;
        this.receiverProvider = receiverProvider;
        this.injectionNodeBuilderRepositoryProvider = injectionNodeBuilderRepositoryProvider;
        this.injectionBindingBuilder = injectionBindingBuilder;
        this.manifestManager = manifestManager;
        this.componentBuilderFactory = componentBuilderFactory;
        this.intentFilterBuilder = intentFilterBuilder;
        this.metaDataBuilder = metaDataBuilder;
        this.contextScopeComponentBuilder = contextScopeComponentBuilder;
        this.observesExpressionDecorator = observesExpressionDecorator;
    }

    public ComponentDescriptor analyze(ASTType astType) {

        BroadcastReceiver broadcastReceiver = astType.getAnnotation(BroadcastReceiver.class);

        PackageClass receiverClassName;

        ComponentDescriptor receiverDescriptor = null;

        if (astType.extendsFrom(AndroidLiterals.BROADCAST_RECEIVER)) {
            //vanilla Android broadcast receiver
            PackageClass activityPackageClass = astType.getPackageClass();
            receiverClassName = buildPackageClass(astType, activityPackageClass.getClassName());
        } else {
            receiverClassName = buildPackageClass(astType, broadcastReceiver.name());

            TypeMirror type = getTypeMirror(new ReceiverTypeRunnable(broadcastReceiver));
            String receiverType = buildReceiverType(type);

            receiverDescriptor = new ComponentDescriptor(receiverType, receiverClassName);

            receiverDescriptor.setInjectionNodeFactory(componentBuilderFactory.buildBroadcastReceiverInjectionNodeFactory(astType));

            receiverDescriptor.setInitMethodBuilder(astClassFactory.getType(OnReceive.class), componentBuilderFactory.buildOnReceiveMethodBuilder());

            receiverDescriptor.addGenerators(contextScopeComponentBuilder);

            // make sure we have the mappings for the entire receiver inheritance chain so it can be injected
            InjectionNodeBuilderRepository injectionNodeBuilderRepository = injectionNodeBuilderRepositoryProvider.get();
            if(type != null) {
                ASTType applicationASTType = type.accept(astTypeBuilderVisitor, null);

                while(!applicationASTType.equals(AndroidLiterals.BROADCAST_RECEIVER) && applicationASTType.inheritsFrom(AndroidLiterals.BROADCAST_RECEIVER)){
                    injectionNodeBuilderRepository.putType(applicationASTType, injectionBindingBuilder.buildThis(applicationASTType));
                    applicationASTType = applicationASTType.getSuperClass();
                }
            }

            AnalysisContext analysisContext = analysisContextFactory.buildAnalysisContext(injectionNodeBuilderRepository);

            receiverDescriptor.setInjectionNodeFactory(componentBuilderFactory.buildInjectionNodeFactory(ImmutableSet.<ASTAnnotation>of(), astType, analysisContext));
            receiverDescriptor.addRegistration(observesExpressionDecorator);
        }

        setupManifest(receiverClassName.getFullyQualifiedName(), broadcastReceiver, astType);

        return receiverDescriptor;
    }

    private String buildReceiverType(TypeMirror type) {
        if (type != null && !type.toString().equals("java.lang.Object")) {
            return type.toString();
        } else {
            return AndroidLiterals.BROADCAST_RECEIVER.getName();
        }
    }

    private void setupManifest(String name, BroadcastReceiver annotation, ASTType astType) {

        Receiver manifestReceiver = buildReceiver(name, annotation);

        manifestReceiver.setIntentFilters(intentFilterBuilder.buildIntentFilters(astType));
        manifestReceiver.setMetaData(metaDataBuilder.buildMetaData(astType));

        manifestManager.addBroadcastReceiver(manifestReceiver);
    }
    
    protected Receiver buildReceiver(String name, BroadcastReceiver annotation){
        Receiver manifestReceiver = receiverProvider.get();

        manifestReceiver.setName(name);
        manifestReceiver.setLabel(checkBlank(annotation.label()));
        manifestReceiver.setProcess(checkBlank(annotation.process()));
        manifestReceiver.setPermission(checkBlank(annotation.permission()));
        manifestReceiver.setIcon(checkBlank(annotation.icon()));
        manifestReceiver.setEnabled(checkDefault(annotation.enabled(), true));
        manifestReceiver.setExported(annotation.exported().getValue());

        return manifestReceiver;
    }

    private PackageClass buildPackageClass(ASTType astType, String className) {
        PackageClass inputPackageClass = astType.getPackageClass();

        if (StringUtils.isBlank(className)) {
            return inputPackageClass.append("BroadcastReceiver");
        } else {
            return inputPackageClass.replaceName(className);
        }
    }

    private static final class ReceiverTypeRunnable extends TypeMirrorRunnable<BroadcastReceiver> {
        private ReceiverTypeRunnable(BroadcastReceiver receiverAnnotation) {
            super(receiverAnnotation);
        }

        @Override
        public void run(BroadcastReceiver annotation) {
            //accessing this throws an exception, caught in TypeMirrorUtil
            annotation.type();
        }
    }
}

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

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.adapter.element.ASTTypeBuilderVisitor;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.annotations.BroadcastReceiver;
import org.androidtransfuse.experiment.ComponentDescriptorImpl;
import org.androidtransfuse.experiment.generators.BroadcastReceiverManifestEntryGenerator;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import static org.androidtransfuse.util.TypeMirrorUtil.getTypeMirror;

/**
 * @author John Ericksen
 */
public class BroadcastReceiverAnalysis implements Analysis<ComponentDescriptor> {

    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final AnalysisContextFactory analysisContextFactory;
    private final BroadcastReceiverManifestEntryGenerator manifestEntryGenerator;
    private final ComponentAnalysis componentAnalysis;

    @Inject
    public BroadcastReceiverAnalysis(ASTTypeBuilderVisitor astTypeBuilderVisitor,
                                     AnalysisContextFactory analysisContextFactory,
                                     BroadcastReceiverManifestEntryGenerator manifestEntryGenerator,
                                     ComponentAnalysis componentAnalysis) {
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.analysisContextFactory = analysisContextFactory;
        this.manifestEntryGenerator = manifestEntryGenerator;
        this.componentAnalysis = componentAnalysis;
    }

    public ComponentDescriptor analyze(ASTType astType) {

        BroadcastReceiver broadcastReceiverAnnotation = astType.getAnnotation(BroadcastReceiver.class);

        ComponentDescriptor receiverDescriptor;

        if (astType.extendsFrom(AndroidLiterals.BROADCAST_RECEIVER)) {
            //vanilla Android broadcast receiver
            PackageClass activityPackageClass = astType.getPackageClass();
            PackageClass receiverClassName = componentAnalysis.buildComponentPackageClass(astType, activityPackageClass.getClassName(), "BroadcastReceiver");
            receiverDescriptor = new ComponentDescriptorImpl(astType, null, receiverClassName);
        } else {
            PackageClass receiverClassName = componentAnalysis.buildComponentPackageClass(astType, broadcastReceiverAnnotation.name(), "BroadcastReceiver");

            TypeMirror type = getTypeMirror(broadcastReceiverAnnotation, "type");
            ASTType receiverType = type == null || type.toString().equals(AndroidLiterals.OBJECT.getName()) ? AndroidLiterals.BROADCAST_RECEIVER : type.accept(astTypeBuilderVisitor, null);

            InjectionNodeBuilderRepository injectionNodeBuilderRepository = componentAnalysis.setupInjectionNodeBuilderRepository();

            AnalysisContext analysisContext = analysisContextFactory.buildAnalysisContext(injectionNodeBuilderRepository);
            receiverDescriptor = new ComponentDescriptorImpl(astType, receiverType, receiverClassName, analysisContext);

            componentAnalysis.buildDescriptor(receiverDescriptor, receiverType, BroadcastReceiver.class);
        }

        receiverDescriptor.getGenerators().add(manifestEntryGenerator);

        return receiverDescriptor;
    }
}

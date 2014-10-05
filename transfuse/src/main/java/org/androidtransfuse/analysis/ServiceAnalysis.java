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
import org.androidtransfuse.annotations.Service;
import org.androidtransfuse.experiment.ComponentDescriptorImpl;
import org.androidtransfuse.experiment.generators.ServiceManifestEntryGenerator;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import static org.androidtransfuse.util.TypeMirrorUtil.getTypeMirror;

/**
 * Service related Analysis
 *
 * @author John Ericksen
 */
public class ServiceAnalysis implements Analysis<ComponentDescriptor> {

    private final AnalysisContextFactory analysisContextFactory;
    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final ServiceManifestEntryGenerator serviceManifestEntryGenerator;
    private final ComponentAnalysis componentAnalysis;

    @Inject
    public ServiceAnalysis(AnalysisContextFactory analysisContextFactory,
                           ASTTypeBuilderVisitor astTypeBuilderVisitor,
                           ServiceManifestEntryGenerator serviceManifestEntryGenerator,
                           ComponentAnalysis componentAnalysis) {
        this.analysisContextFactory = analysisContextFactory;
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.serviceManifestEntryGenerator = serviceManifestEntryGenerator;
        this.componentAnalysis = componentAnalysis;
    }

    public ComponentDescriptor analyze(ASTType input) {

        Service serviceAnnotation = input.getAnnotation(Service.class);
        PackageClass serviceClassName;
        ComponentDescriptor serviceDescriptor;

        if (input.extendsFrom(AndroidLiterals.SERVICE)) {
            //vanilla Android Service
            PackageClass packageClass = input.getPackageClass();
            serviceClassName = componentAnalysis.buildComponentPackageClass(input, packageClass.getClassName(), "Service");

            serviceDescriptor = new ComponentDescriptorImpl(input, null, serviceClassName);
        } else {
            //generated Android Service
            serviceClassName = componentAnalysis.buildComponentPackageClass(input, serviceAnnotation.name(), "Service");

            TypeMirror type = getTypeMirror(serviceAnnotation, "type");

            ASTType serviceType = type == null || type.toString().equals("java.lang.Object") ? AndroidLiterals.SERVICE : type.accept(astTypeBuilderVisitor, null);

            AnalysisContext context = analysisContextFactory.buildAnalysisContext(componentAnalysis.setupInjectionNodeBuilderRepository());

            serviceDescriptor = new ComponentDescriptorImpl(input, serviceType, serviceClassName, context);

            componentAnalysis.buildDescriptor(serviceDescriptor, serviceType, Service.class);
        }

        serviceDescriptor.getGenerators().add(serviceManifestEntryGenerator);

        return serviceDescriptor;
    }
}
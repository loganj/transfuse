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
import org.androidtransfuse.annotations.Application;
import org.androidtransfuse.experiment.ComponentDescriptorImpl;
import org.androidtransfuse.experiment.generators.ApplicationManifestEntryGenerator;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import static org.androidtransfuse.util.TypeMirrorUtil.getTypeMirror;

/**
 * @author John Ericksen
 */
public class ApplicationAnalysis implements Analysis<ComponentDescriptor> {

    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final AnalysisContextFactory analysisContextFactory;
    private final ApplicationManifestEntryGenerator applicationManifestEntryGenerator;
    private final ComponentAnalysis componentAnalysis;

    @Inject
    public ApplicationAnalysis(ASTTypeBuilderVisitor astTypeBuilderVisitor,
                               AnalysisContextFactory analysisContextFactory,
                               ApplicationManifestEntryGenerator applicationManifestEntryGenerator,
                               ComponentAnalysis componentAnalysis) {
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.analysisContextFactory = analysisContextFactory;
        this.applicationManifestEntryGenerator = applicationManifestEntryGenerator;
        this.componentAnalysis = componentAnalysis;
    }

    public ComponentDescriptorImpl analyze(ASTType astType) {
        Application applicationAnnotation = astType.getAnnotation(Application.class);


        PackageClass applicationClassName;
        ComponentDescriptorImpl applicationDescriptor = null;

        if (astType.extendsFrom(AndroidLiterals.APPLICATION)) {
            //vanilla Android Application
            PackageClass activityPackageClass = astType.getPackageClass();
            applicationClassName = componentAnalysis.buildComponentPackageClass(astType, activityPackageClass.getClassName(), "Application");
            applicationDescriptor = new ComponentDescriptorImpl(astType, null, applicationClassName);
        } else {

            applicationClassName = componentAnalysis.buildComponentPackageClass(astType, applicationAnnotation.name(), "Application");

            TypeMirror type = getTypeMirror(applicationAnnotation, "type");
            ASTType applicationType = type == null || type.toString().equals("java.lang.Object") ? AndroidLiterals.APPLICATION : type.accept(astTypeBuilderVisitor, null);

            InjectionNodeBuilderRepository injectionNodeBuilderRepository = componentAnalysis.setupInjectionNodeBuilderRepository();

            //analyze delegate
            AnalysisContext analysisContext = analysisContextFactory.buildAnalysisContext(injectionNodeBuilderRepository);
            applicationDescriptor = new ComponentDescriptorImpl(astType, applicationType, applicationClassName, analysisContext);

            componentAnalysis.buildDescriptor(applicationDescriptor, applicationType, Application.class);
        }

        //add manifest elements
        applicationDescriptor.getGenerators().add(applicationManifestEntryGenerator);

        return applicationDescriptor;
    }
}

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
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepositoryFactory;
import org.androidtransfuse.annotations.Activity;
import org.androidtransfuse.experiment.ComponentDescriptorImpl;
import org.androidtransfuse.experiment.generators.ActivityManifestEntryGenerator;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.type.TypeMirror;

import static org.androidtransfuse.util.TypeMirrorUtil.getTypeMirror;

/**
 * Activity related Analysis
 *
 * @author John Ericksen
 */
public class ActivityAnalysis implements Analysis<ComponentDescriptor> {

    private final InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory;
    private final AnalysisContextFactory analysisContextFactory;
    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final Provider<ActivityManifestEntryGenerator> manifestGeneratorProvider;
    private final ComponentAnalysis componentAnalysis;

    @Inject
    public ActivityAnalysis(InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory,
                            AnalysisContextFactory analysisContextFactory,
                            ASTTypeBuilderVisitor astTypeBuilderVisitor,
                            Provider<ActivityManifestEntryGenerator> manifestGeneratorProvider,
                            ComponentAnalysis componentAnalysis) {
        this.injectionNodeBuilderRepositoryFactory = injectionNodeBuilderRepositoryFactory;
        this.analysisContextFactory = analysisContextFactory;
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.manifestGeneratorProvider = manifestGeneratorProvider;
        this.componentAnalysis = componentAnalysis;
    }

    public ComponentDescriptor analyze(ASTType input) {

        Activity activityAnnotation = input.getAnnotation(Activity.class);
        PackageClass activityClassName;
        ComponentDescriptor activityDescriptor;

        if (input.extendsFrom(AndroidLiterals.ACTIVITY)) {
            //vanilla Android activity
            PackageClass activityPackageClass = input.getPackageClass();
            activityClassName = componentAnalysis.buildComponentPackageClass(input, activityPackageClass.getClassName(), "Activity");
            activityDescriptor = new ComponentDescriptorImpl(input, null, activityClassName);
        } else {
            //generated Android activity
            activityClassName = componentAnalysis.buildComponentPackageClass(input, activityAnnotation.name(), "Activity");

            TypeMirror type = getTypeMirror(activityAnnotation, "type");

            ASTType activityType = type == null || type.toString().equals(AndroidLiterals.OBJECT.getName()) ? AndroidLiterals.ACTIVITY : type.accept(astTypeBuilderVisitor, null);

            AnalysisContext context = analysisContextFactory.buildAnalysisContext(buildVariableBuilderMap());

            activityDescriptor = new ComponentDescriptorImpl(input, activityType, activityClassName, context);

            componentAnalysis.buildDescriptor(activityDescriptor, activityType, Activity.class);
        }

        //add manifest elements
        activityDescriptor.getGenerators().add(manifestGeneratorProvider.get());

        return activityDescriptor;
    }

    private InjectionNodeBuilderRepository buildVariableBuilderMap() {

        InjectionNodeBuilderRepository injectionNodeBuilderRepository = componentAnalysis.setupInjectionNodeBuilderRepository();

        injectionNodeBuilderRepository.addRepository(
                injectionNodeBuilderRepositoryFactory.buildModuleConfiguration());

        return injectionNodeBuilderRepository;
    }
}
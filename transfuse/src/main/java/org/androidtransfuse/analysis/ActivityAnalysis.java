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

import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.adapter.element.ASTTypeBuilderVisitor;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepositoryFactory;
import org.androidtransfuse.annotations.Activity;
import org.androidtransfuse.experiment.ComponentDescriptorImpl;
import org.androidtransfuse.experiment.generators.ActivityManifestEntryGenerator;
import org.androidtransfuse.gen.variableBuilder.InjectionBindingBuilder;
import org.androidtransfuse.gen.variableBuilder.ProviderInjectionNodeBuilderFactory;
import org.androidtransfuse.model.InjectionSignature;
import org.androidtransfuse.scope.ApplicationScope;
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
    private final ASTElementFactory astElementFactory;
    private final ProviderInjectionNodeBuilderFactory providerInjectionNodeBuilder;
    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final InjectionBindingBuilder injectionBindingBuilder;
    private final Provider<ActivityManifestEntryGenerator> manifestGeneratorProvider;
    private final ComponentAnalysis componentAnalysis;

    @Inject
    public ActivityAnalysis(InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory,
                            AnalysisContextFactory analysisContextFactory,
                            ASTElementFactory astElementFactory,
                            ProviderInjectionNodeBuilderFactory providerInjectionNodeBuilder,
                            ASTTypeBuilderVisitor astTypeBuilderVisitor,
                            InjectionBindingBuilder injectionBindingBuilder,
                            Provider<ActivityManifestEntryGenerator> manifestGeneratorProvider,
                            ComponentAnalysis componentAnalysis) {
        this.injectionNodeBuilderRepositoryFactory = injectionNodeBuilderRepositoryFactory;
        this.analysisContextFactory = analysisContextFactory;
        this.astElementFactory = astElementFactory;
        this.providerInjectionNodeBuilder = providerInjectionNodeBuilder;
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.injectionBindingBuilder = injectionBindingBuilder;
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

            ASTType activityType = type == null || type.toString().equals("java.lang.Object") ? AndroidLiterals.ACTIVITY : type.accept(astTypeBuilderVisitor, null);

            AnalysisContext context = analysisContextFactory.buildAnalysisContext(buildVariableBuilderMap(activityType));

            activityDescriptor = new ComponentDescriptorImpl(input, activityType, activityClassName, context);

            componentAnalysis.buildDescriptor(activityDescriptor, activityType, Activity.class);
        }

        //add manifest elements
        activityDescriptor.getGenerators().add(manifestGeneratorProvider.get());

        return activityDescriptor;
    }

    private InjectionNodeBuilderRepository buildVariableBuilderMap(ASTType activityType) {

        InjectionNodeBuilderRepository injectionNodeBuilderRepository = componentAnalysis.setupInjectionNodeBuilderRepository();

        ASTType applicationScopeType = astElementFactory.getType(ApplicationScope.ApplicationScopeQualifier.class);
        ASTType applicationProvider = astElementFactory.getType(ApplicationScope.ApplicationProvider.class);
        injectionNodeBuilderRepository.putScoped(new InjectionSignature(AndroidLiterals.APPLICATION), applicationScopeType);
        injectionNodeBuilderRepository.putType(AndroidLiterals.APPLICATION, providerInjectionNodeBuilder.builderProviderBuilder(applicationProvider));
        injectionNodeBuilderRepository.putType(AndroidLiterals.CONTEXT, injectionBindingBuilder.buildThis(AndroidLiterals.CONTEXT));

        injectionNodeBuilderRepository.putType(AndroidLiterals.ACTIVITY, injectionBindingBuilder.buildThis(AndroidLiterals.ACTIVITY));

        while(!activityType.equals(AndroidLiterals.ACTIVITY) && activityType.inheritsFrom(AndroidLiterals.ACTIVITY)){
            injectionNodeBuilderRepository.putType(activityType, injectionBindingBuilder.buildThis(activityType));
            activityType = activityType.getSuperClass();
        }

        injectionNodeBuilderRepository.addRepository(
                injectionNodeBuilderRepositoryFactory.buildModuleConfiguration());

        return injectionNodeBuilderRepository;

    }

    private ASTMethod getASTMethod(String methodName, ASTType... args) {
        return getASTMethod(AndroidLiterals.ACTIVITY, methodName, args);
    }

    private ASTMethod getASTMethod(ASTType type, String methodName, ASTType... args) {
        return astElementFactory.findMethod(type, methodName, args);
    }
}
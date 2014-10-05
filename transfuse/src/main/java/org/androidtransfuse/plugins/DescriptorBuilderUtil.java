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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.MethodSignature;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.analysis.AnnotatedTypeMatcher;
import org.androidtransfuse.analysis.ListenableMethod;
import org.androidtransfuse.analysis.ManualSuperGenerator;
import org.androidtransfuse.analysis.astAnalyzer.RegistrationAnalyzer;
import org.androidtransfuse.analysis.astAnalyzer.registration.RegistrationGenerators;
import org.androidtransfuse.analysis.repository.RegistrationGeneratorFactory;
import org.androidtransfuse.experiment.generators.MethodCallbackGenerator;
import org.androidtransfuse.experiment.generators.SuperGenerator;
import org.androidtransfuse.gen.componentBuilder.ComponentBuilderFactory;
import org.androidtransfuse.tomove.ComponentDescriptor;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author John Ericksen
 */
public class DescriptorBuilderUtil {

    private final ASTElementFactory astElementFactory;
    private final ASTClassFactory astClassFactory;
    private final ComponentBuilderFactory componentBuilderFactory;
    private final SuperGenerator.SuperGeneratorFactory superGeneratorFactory;
    private final ManualSuperGenerator.Factory manualSuperGeneratorFactory;
    private final RegistrationGenerators registrationGenerators;

    @Inject
    public DescriptorBuilderUtil(ASTElementFactory astElementFactory,
                                 ASTClassFactory astClassFactory,
                                 ComponentBuilderFactory componentBuilderFactory,
                                 SuperGenerator.SuperGeneratorFactory superGeneratorFactory,
                                 ManualSuperGenerator.Factory manualSuperGeneratorFactory, RegistrationGenerators registrationGenerators) {
        this.astElementFactory = astElementFactory;
        this.astClassFactory = astClassFactory;
        this.componentBuilderFactory = componentBuilderFactory;
        this.superGeneratorFactory = superGeneratorFactory;
        this.manualSuperGeneratorFactory = manualSuperGeneratorFactory;
        this.registrationGenerators = registrationGenerators;
    }

    public ComponentDescriptorBuilder component(Class<? extends Annotation> componentAnnotation) {
        return new ComponentDescriptorBuilder(componentAnnotation);
    }

    public final class ComponentDescriptorBuilder {
        private final Class<? extends Annotation> componentAnnotation;
        private ASTType componentType;

        public ComponentDescriptorBuilder(Class<? extends Annotation> componentAnnotation) {
            this.componentAnnotation = componentAnnotation;
        }

        public ComponentDescriptorBuilder extending(String className) {
            if(componentType != null){
                //todo: throw Plugin Exception
            }
            componentType = new ASTStringType(className);
            return this;
        }

        public ComponentDescriptorBuilder extending(ASTType componentType){
            if(componentType != null){
                //todo: throw Plugin Exception
            }
            this.componentType = componentType;
            return this;
        }

        public DescriptorBuilder callThroughEvent(final Class<?> callThroughEventClass) {
            return new ComponentTypeDescriptorBuilderDecorator(astClassFactory, new AnnotatedTypeMatcher(componentType, astClassFactory.getType(componentAnnotation)),
                new DescriptorBuilder() {
                    @Override
                    public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                        ASTType listenerType = astElementFactory.getType(callThroughEventClass);
                        RegistrationGeneratorFactory registrationGenerator = registrationGenerators.buildCallThroughMethodGenerator(listenerType);

                        descriptor.getAnalysisContext().getInjectionNodeBuilders().getAnalysisRepository().add(new RegistrationAnalyzer(ImmutableMap.of(listenerType, registrationGenerator)));
                    }
                });
        }

        public DescriptorBuilder listener(final ASTType listenerType, final ASTType listenableType, final String listenerMethod) {
            return new ComponentTypeDescriptorBuilderDecorator(astClassFactory, new AnnotatedTypeMatcher(componentType, astClassFactory.getType(componentAnnotation)),
                new DescriptorBuilder() {
                    @Override
                    public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                        RegistrationGeneratorFactory registrationGenerator = registrationGenerators.buildViewRegistrationGenerator(new ListenableMethod(listenableType, listenerMethod));

                        descriptor.getAnalysisContext().getInjectionNodeBuilders().getAnalysisRepository().add(new RegistrationAnalyzer(ImmutableMap.of(listenerType, registrationGenerator)));
                    }
                });
        }

        public MethodBuilderImpl method(String methodName){
            return new MethodBuilderImpl(methodName, Collections.EMPTY_LIST);
        }

        public MethodBuilderImpl method(String methodName, String firstParameter, String... parameters){
            List<ASTType> parameterList = new ArrayList<ASTType>();
            parameterList.add(new ASTStringType(firstParameter));
            if(parameters != null){
                for (String parameter : parameters) {
                    parameterList.add(new ASTStringType(parameter));
                }
            }
            return new MethodBuilderImpl(methodName, parameterList);
        }

        public MethodBuilderImpl method(String methodName, ASTType firstParameter, ASTType... parameters){
            List<ASTType> parameterList = new ArrayList<ASTType>();
            parameterList.add(firstParameter);
            if(parameters != null){
                parameterList.addAll(Arrays.asList(parameters));
            }
            return new MethodBuilderImpl(methodName, parameterList);
        }

        public DescriptorBuilder add(DescriptorBuilder descriptorBuilder) {
            return matchComponent(descriptorBuilder);
        }

        private DescriptorBuilder matchComponent(DescriptorBuilder descriptorBuilder){
            return new ComponentTypeDescriptorBuilderDecorator(astClassFactory, new AnnotatedTypeMatcher(componentType, astClassFactory.getType(componentAnnotation)), descriptorBuilder);
        }

        public class MethodBuilderImpl implements MethodBuilder {

            private final String methodName;
            private final List<ASTType> parameters;
            private final List<DescriptorBuilder> builders = new ArrayList<DescriptorBuilder>();

            private MethodBuilderImpl(String methodName, List<ASTType> parameters) {
                this.methodName = methodName;
                this.parameters = parameters;
            }

            public MethodBuilder event(final Class<? extends Annotation> eventAnnotation) {
                builders.add(matchComponent(
                    new DescriptorBuilder() {
                        @Override
                        public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                            ASTMethod method = astElementFactory.findMethod(type, methodName, parameters);

                            if(method == null){
                                throw new TransfuseAnalysisException("Unable to find method with signature: " +
                                        type + "." +
                                        methodName + "(" + Joiner.on(", ").join(parameters) + ")");
                            }

                            ASTType eventAnnotationType = astClassFactory.getType(eventAnnotation);

                            MethodCallbackGenerator methodCallbackGenerator = componentBuilderFactory.buildMethodCallbackGenerator(eventAnnotationType, method, descriptor.getRegistrationMethod());

                            descriptor.getGenerators().add(methodCallbackGenerator);
                        }
                    }));
                return this;
            }

            public MethodBuilder superCall(){
                builders.add(matchComponent(
                    new DescriptorBuilder() {
                        @Override
                        public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                            ASTMethod method = astElementFactory.findMethod(type, methodName, parameters);

                            descriptor.getGenerators().add(superGeneratorFactory.build(method));
                        }
                    }));
                return this;
            }

            public MethodBuilder registration() {
                builders.add(matchComponent(
                        new DescriptorBuilder() {
                            @Override
                            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                                ASTMethod registrationMethod = astElementFactory.findMethod(type, methodName, parameters);
                                descriptor.getGenerateFirst().add(new MethodSignature(registrationMethod));

                                descriptor.getGenerators().add(manualSuperGeneratorFactory.build(registrationMethod));
                                descriptor.setRegistrationMethod(registrationMethod);
                            }
                        }));
                return this;
            }

            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                for (DescriptorBuilder builder : builders) {
                    builder.buildDescriptor(descriptor, type, componentAnnotation);
                }
            }
        }
    }

    public interface MethodBuilder extends DescriptorBuilder{

        MethodBuilder event(Class<? extends Annotation> eventAnnotation);

        MethodBuilder superCall();

        MethodBuilder registration();
    }
}

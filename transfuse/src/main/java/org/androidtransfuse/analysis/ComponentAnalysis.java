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

import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
public class ComponentAnalysis {

    private final ConfigurationRepositoryImpl repository;
    private final Provider<InjectionNodeBuilderRepository> injectionNodeBuilderRepositoryProvider;

    @Inject
    public ComponentAnalysis(ConfigurationRepositoryImpl repository,
                             Provider<InjectionNodeBuilderRepository> injectionNodeBuilderRepositoryProvider){
        this.repository = repository;
        this.injectionNodeBuilderRepositoryProvider = injectionNodeBuilderRepositoryProvider;
    }

    public InjectionNodeBuilderRepository setupInjectionNodeBuilderRepository() {
        return injectionNodeBuilderRepositoryProvider.get();
    }

    public PackageClass buildComponentPackageClass(ASTType astType, String className, String componentName) {
        PackageClass inputPackageClass = astType.getPackageClass();

        if (StringUtils.isBlank(className)) {
            return inputPackageClass.append(componentName);
        } else {
            return inputPackageClass.replaceName(className);
        }
    }

    public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
        for (DescriptorBuilder descriptorBuilder : repository.getDescriptorBuilders()) {
            descriptorBuilder.buildDescriptor(descriptor, type, componentAnnotation);
        }
    }
}

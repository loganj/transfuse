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
package org.androidtransfuse.analysis.repository;

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.analysis.module.ModuleRepository;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author John Ericksen
 */
public class InjectionNodeBuilderRepositoryFactory implements ModuleRepository {

    @Singleton
    public static final class InjectionNodeRepository{

        private final Set<ASTType> installedComponents = new HashSet<ASTType>();
        private final InjectionNodeBuilderRepository moduleRepository;

        @Inject
        public InjectionNodeRepository(InjectionNodeBuilderRepository moduleRepository){
            this.moduleRepository = moduleRepository;
        }
    }

    private final InjectionNodeRepository repository;
    private final Provider<InjectionNodeBuilderRepository> injectionNodeBuilderRepositoryProvider;
    private final ScopeAspectFactoryRepositoryProvider scopeAspectFactoryRepositoryProvider;

    @Inject
    public InjectionNodeBuilderRepositoryFactory(Provider<InjectionNodeBuilderRepository> injectionNodeBuilderRepositoryProvider,
                                                 ScopeAspectFactoryRepositoryProvider scopeAspectFactoryRepositoryProvider,
                                                 InjectionNodeRepository repository) {
        this.injectionNodeBuilderRepositoryProvider = injectionNodeBuilderRepositoryProvider;
        this.scopeAspectFactoryRepositoryProvider = scopeAspectFactoryRepositoryProvider;
        this.repository = repository;
    }

    public InjectionNodeBuilderRepository buildModuleConfiguration() {
        InjectionNodeBuilderRepository builderRepository = injectionNodeBuilderRepositoryProvider.get();
        builderRepository.addRepository(this.repository.moduleRepository);
        builderRepository.addRepository(scopeAspectFactoryRepositoryProvider.get());
        builderRepository.addRepository(injectionNodeBuilderRepositoryProvider.get());

        return builderRepository;
    }

    @Override
    public Collection<ASTType> getInstalledAnnotatedWith(Class<? extends Annotation> annotation) {
        ImmutableSet.Builder<ASTType> installedBuilder = ImmutableSet.builder();

        for (ASTType installedComponent : repository.installedComponents) {
            if(installedComponent.isAnnotated(annotation)){
                installedBuilder.add(installedComponent);
            }
        }

        return installedBuilder.build();
    }

    @Override
    public void addInstalledComponents(ASTType[] astType) {
        repository.installedComponents.addAll(Arrays.asList(astType));
    }

    @Override
    public void addModuleRepository(InjectionNodeBuilderRepository repository) {
        this.repository.moduleRepository.addRepository(repository);
    }
}

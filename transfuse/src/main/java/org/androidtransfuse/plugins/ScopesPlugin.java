package org.androidtransfuse.plugins;

import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfusePlugin;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.annotations.TransfuseModule;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.gen.scopeBuilder.CustomScopeAspectFactoryFactory;
import org.androidtransfuse.gen.scopeBuilder.SingletonScopeAspectFactory;
import org.androidtransfuse.scope.ApplicationScope;
import org.androidtransfuse.scope.ConcurrentDoubleLockingScope;
import org.androidtransfuse.tomove.ComponentDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
@Bootstrap
public class ScopesPlugin implements TransfusePlugin {

    @Inject
    ASTClassFactory astClassFactory;
    @Inject
    SingletonScopeAspectFactory singletonScopeAspectFactory;
    @Inject
    CustomScopeAspectFactoryFactory customScopeAspectFactoryFactory;

    @Override
    public void run(ConfigurationRepository repository) {
        repository.add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                InjectionNodeBuilderRepository injectionNodeBuilders = descriptor.getAnalysisContext().getInjectionNodeBuilders();
                ASTType concurrentScopeType = astClassFactory.getType(ConcurrentDoubleLockingScope.class);

                injectionNodeBuilders.putScopeAspectFactory(astClassFactory.getType(TransfuseModule.class), concurrentScopeType, singletonScopeAspectFactory);
                injectionNodeBuilders.putScopeAspectFactory(astClassFactory.getType(Singleton.class), concurrentScopeType, singletonScopeAspectFactory);
                injectionNodeBuilders.putScopeAspectFactory(
                        astClassFactory.getType(ApplicationScope.ApplicationScopeQualifier.class),
                        astClassFactory.getType(ApplicationScope.class), customScopeAspectFactoryFactory.buildScopeBuilder(astClassFactory.getType(ApplicationScope.ApplicationScopeQualifier.class)));
            }
        });
    }
}

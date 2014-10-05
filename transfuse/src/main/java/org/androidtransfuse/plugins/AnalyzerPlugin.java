package org.androidtransfuse.plugins;

import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfusePlugin;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.analysis.astAnalyzer.*;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.tomove.ComponentDescriptor;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author John Ericksen
 */
@Bootstrap
public class AnalyzerPlugin implements TransfusePlugin {
    
    @Inject
    AOPProxyAnalyzer aopProxyAnalyzer;
    @Inject
    InjectionAnalyzer injectionAnalyzer;
    @Inject
    ListenerAnalysis methodCallbackAnalysis;
    @Inject
    ScopeAnalysis scopeAnalysis;
    @Inject
    DeclareFieldAnalysis declareFieldAnalysis;
    @Inject
    ObservesAnalysis observesAnalysis;
    @Inject
    NonConfigurationAnalysis nonConfigurationAnalysis;
    @Inject
    AnnotationValidationAnalysis annotationValidationAnalysis;
    @Inject
    ManualSuperAnalysis manualSuperAnalysis;

    @Override
    public void run(ConfigurationRepository repository) {
        repository.add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                Set<ASTAnalysis> analysisRepository = descriptor.getAnalysisContext().getInjectionNodeBuilders().getAnalysisRepository();

                analysisRepository.add(aopProxyAnalyzer);
                analysisRepository.add(injectionAnalyzer);
                analysisRepository.add(methodCallbackAnalysis);
                analysisRepository.add(scopeAnalysis);
                analysisRepository.add(declareFieldAnalysis);
                analysisRepository.add(observesAnalysis);
                analysisRepository.add(nonConfigurationAnalysis);
                analysisRepository.add(annotationValidationAnalysis);
                analysisRepository.add(manualSuperAnalysis);
            }
        });
    }
}

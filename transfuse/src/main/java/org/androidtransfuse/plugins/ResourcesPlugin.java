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

import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfusePlugin;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.gen.variableBuilder.InjectionBindingBuilder;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
@Bootstrap
public class ResourcesPlugin implements TransfusePlugin {

    @Inject
    InjectionBindingBuilder injectionBindingBuilder;

    @Override
    public void run(ConfigurationRepository repository) {
        repository.add(new DescriptorBuilder() {
            @Override
            public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                descriptor.getAnalysisContext().getInjectionNodeBuilders()
                        .putType(AndroidLiterals.RESOURCES,
                                injectionBindingBuilder.dependency(AndroidLiterals.APPLICATION)
                                        .invoke(AndroidLiterals.RESOURCES, "getResources").build());
            }
        });
    }
}

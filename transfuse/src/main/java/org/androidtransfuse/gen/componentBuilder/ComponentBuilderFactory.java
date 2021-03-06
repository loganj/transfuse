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
package org.androidtransfuse.gen.componentBuilder;

import com.google.common.collect.ImmutableList;
import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.annotations.Factory;
import org.androidtransfuse.experiment.generators.FragmentOnSaveInstanceStateMethodCallbackGenerator;
import org.androidtransfuse.experiment.generators.MethodCallbackGenerator;
import org.androidtransfuse.model.InjectionNode;

import javax.inject.Named;

/**
 * @author John Ericksen
 */
@Factory
public interface ComponentBuilderFactory {

    MethodCallbackGenerator buildMethodCallbackGenerator(ASTType eventAnnotation, @Named("eventMethod") ASTMethod eventMethod, @Named("creationMethod")ASTMethod creationMethod);

    FragmentOnSaveInstanceStateMethodCallbackGenerator buildFragmentOnSaveInstanceStateMethodCallbackGenerator(ASTType eventAnnotation, @Named("eventMethod") ASTMethod eventMethod, @Named("creationMethod")ASTMethod creationMethod);

    ViewRegistrationGenerator buildViewRegistrationGenerator(/*@Assisted("viewInjectionNode")*/ @Named("viewInjectionNode") InjectionNode viewInjectionNode, String listenerMethod, /*@Assisted("targetInjectionNode")*/ @Named("targetInjectionNode") InjectionNode injectionNode, ViewRegistrationInvocationBuilder invocationBuilder);

    ViewMethodRegistrationInvocationBuilderImpl buildViewMethodRegistrationInvocationBuilder(ASTMethod getterMethod);

    ViewFieldRegistrationInvocationBuilderImpl buildViewFieldRegistrationInvocationBuilder(ASTField field);

    ActivityDelegateRegistrationGenerator buildActivityRegistrationGenerator(ActivityDelegateASTReference activityDelegateASTReference, ImmutableList<ASTMethod> methods);

    ActivityTypeDelegateASTReference buildActivityTypeDelegateASTReference();

    ActivityMethodDelegateASTReference buildActivityMethodDelegateASTReference(ASTMethod astMethod);

    ActivityFieldDelegateASTReference buildActivityFieldDelegateASTReference(ASTField astField);
}

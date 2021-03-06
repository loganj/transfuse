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
package org.androidtransfuse.gen.variableBuilder;

import com.google.common.collect.ImmutableList;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.annotations.Factory;
import org.androidtransfuse.gen.variableBuilder.resource.ResourceExpressionBuilder;
import org.androidtransfuse.model.InjectionNode;
import org.androidtransfuse.model.TypedExpression;

import javax.inject.Named;

/**
 * @author John Ericksen
 */
@Factory
public interface VariableInjectionBuilderFactory {

    ProviderInjectionNodeBuilder buildProviderInjectionNodeBuilder(ASTType astType);

    ProviderVariableBuilder buildProviderVariableBuilder(InjectionNode providerInjectionNode);

    VariableASTImplementationInjectionNodeBuilder buildVariableInjectionNodeBuilder(ASTType astType);

    SystemServiceVariableBuilder buildSystemServiceVariableBuilder(String systemService, InjectionNode contextInjectionNode);

    ResourceVariableBuilder buildResourceVariableBuilder(int resourceId, ResourceExpressionBuilder resourceExpressionBuilder);

    ExtraValuableBuilder buildExtraVariableBuilder(String extraId, InjectionNode activityInjectionNode, /*@Assisted("nullable")*/ @Named("nullable") boolean nullable, /*@Assisted("wrapped")*/ @Named("wrapped") boolean wrapped);

    ViewVariableBuilder buildViewVariableBuilder(Integer viewId, String viewTag, InjectionNode activityInjectionNode, JType jType);

    PreferenceVariableBuilder buildPreferenceVariableBuilder(ASTType preferenceType, String preferenceName, InjectionNode preferenceManagerInjectionNode);

    StaticInvocationVariableBuilder buildStaticInvocationVariableBuilder(ASTType invocationTarget, String staticInvocation);

    MethodCallVariableBuilder buildMethodCallVariableBuilder(String methodName, ImmutableList<JExpression> arguments);

    DependentInjectionNodeBuilder buildDependentInjectionNodeBuilder(/*@Assisted("dependency")*/ @Named("dependency") ASTType dependency, /*@Assisted("returnType")*/ @Named("returnType") ASTType returnType, DependentVariableBuilder variableBuilder);

    DependentVariableBuilderWrapper buildDependentVariableBuilderWrapper(InjectionNode dependency, DependentVariableBuilder dependentVariableBuilder, ASTType type);

    IndependentInjectionNodeBuilder buildInjectionNodeBuilder(VariableBuilder variableBuilder);

    IndependentVariableBuilderWrapper buildIndependentVariableBuilderWrapper(ASTType astType, JExpression expression);

    ExpressionVariableBuilderWrapper buildExpressionWrapper(TypedExpression typedExpression);

    FragmentViewVariableBuilder buildFragmentViewVariableBuilder(Integer viewId, String viewTag, InjectionNode fragmentInjectionNode, JType jType);

    FactoryNodeBuilder buildFactoryNodeBuilder(ASTType factoryType);
}

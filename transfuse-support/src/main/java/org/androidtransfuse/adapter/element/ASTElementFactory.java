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
package org.androidtransfuse.adapter.element;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.adapter.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class to build a specific AST tree element from the provided Element base type
 *
 * @author John Ericksen
 */
@Singleton
public class ASTElementFactory {

    private final Map<TypeElement, ASTType> typeCache = new HashMap<TypeElement, ASTType>();

    private final ASTElementConverterFactory astElementConverterFactory;
    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final ASTFactory astFactory;
    private final Elements elements;

    @Inject
    public ASTElementFactory(Elements elements,
                             ASTFactory astFactory,
                             ASTTypeBuilderVisitor astTypeBuilderVisitor,
                             ASTElementConverterFactory astElementConverterFactory) {
        this.elements = elements;
        this.astFactory = astFactory;
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.astElementConverterFactory = astElementConverterFactory;
    }

    public ASTType buildASTElementType(DeclaredType declaredType) {

        ASTType astType = getType((TypeElement) declaredType.asElement());

        if (!declaredType.getTypeArguments().isEmpty()) {
            return astFactory.buildGenericTypeWrapper(astType, astFactory.buildParameterBuilder(declaredType));
        }
        return astType;
    }

    /**
     * Build a ASTType from the provided TypeElement.
     *
     * @param typeElement required input Element
     * @return ASTType constructed using teh input Element
     */
    public synchronized ASTType getType(TypeElement typeElement) {
        if (!typeCache.containsKey(typeElement)) {
            typeCache.put(typeElement, buildType(typeElement));
        }

        return typeCache.get(typeElement);
    }

    public ASTType getType(Class clazz){
        return getType(elements.getTypeElement(clazz.getCanonicalName()));
    }

    private ASTType buildType(TypeElement typeElement) {
        //build placeholder for ASTElementType and contained data structures to allow for children population
        //while avoiding back link loops
        PackageClass packageClass = buildPackageClass(typeElement);

        ASTTypeVirtualProxy astTypeProxy = new ASTTypeVirtualProxy(packageClass);
        typeCache.put(typeElement, astTypeProxy);

        ImmutableSet.Builder<ASTConstructor> constructors = ImmutableSet.builder();
        ImmutableSet.Builder<ASTField> fields = ImmutableSet.builder();
        ImmutableSet.Builder<ASTMethod> methods = ImmutableSet.builder();

        ASTType superClass = null;
        if (typeElement.getSuperclass() != null) {
            superClass = typeElement.getSuperclass().accept(astTypeBuilderVisitor, null);
        }

        ImmutableSet<ASTType> interfaces = FluentIterable.from(typeElement.getInterfaces())
                .transform(astTypeBuilderVisitor)
                .toSet();

        ImmutableSet.Builder<ASTAnnotation> annotations = ImmutableSet.builder();

        //iterate and build the contained elements within this TypeElement
        constructors.addAll(transformAST(typeElement.getEnclosedElements(), ASTConstructor.class));
        fields.addAll(transformAST(typeElement.getEnclosedElements(), ASTField.class));
        methods.addAll(transformAST(typeElement.getEnclosedElements(), ASTMethod.class));

        annotations.addAll(getAnnotations(typeElement));

        ASTType astType = new ASTElementType(packageClass,
                typeElement,
                constructors.build(),
                methods.build(),
                fields.build(),
                superClass,
                interfaces,
                annotations.build());

        astTypeProxy.load(astType);

        return astType;
    }

    private PackageClass buildPackageClass(TypeElement typeElement) {

        PackageElement packageElement = elements.getPackageOf(typeElement);

        String pkg = packageElement.getQualifiedName().toString();
        String qualifiedName = typeElement.getQualifiedName().toString();
        String name;
        if(pkg.length() < qualifiedName.length()){
            name = qualifiedName.substring(pkg.length() + 1);
        }
        else{
            name = qualifiedName;
        }

        return new PackageClass(pkg, name);
    }

    private <T extends ASTBase> List<T> transformAST(List<? extends Element> enclosedElements, Class<T> astType) {
        return FluentIterable
                .from(enclosedElements)
                .transform(astElementConverterFactory.buildASTElementConverter(astType))
                .filter(Predicates.notNull())
                .toList();
    }

    /**
     * Build a ASTElementField from the given VariableElement
     *
     * @param variableElement required input Element
     * @return ASTElementField
     */
    public ASTField getField(VariableElement variableElement) {
        ASTAccessModifier modifier = buildAccessModifier(variableElement);

        return new ASTElementField(variableElement, astTypeBuilderVisitor, modifier, getAnnotations(variableElement));
    }

    private ASTAccessModifier buildAccessModifier(Element element) {
        for (Modifier elementModifier : element.getModifiers()) {
            switch (elementModifier) {
                case PUBLIC:
                    return ASTAccessModifier.PUBLIC;
                case PROTECTED:
                    return ASTAccessModifier.PROTECTED;
                case PRIVATE:
                    return ASTAccessModifier.PRIVATE;
            }
        }

        return ASTAccessModifier.PACKAGE_PRIVATE;
    }

    /**
     * Build an ASTMethod from the provided ExecutableElement
     *
     * @param executableElement required input element
     * @return ASTMethod
     */
    public ASTMethod getMethod(ExecutableElement executableElement) {

        ImmutableList<ASTParameter> parameters = getParameters(executableElement.getParameters());
        ASTAccessModifier modifier = buildAccessModifier(executableElement);
        ImmutableSet<ASTType> throwsTypes = buildASTElementTypes(executableElement.getThrownTypes());

        return new ASTElementMethod(executableElement, astTypeBuilderVisitor, parameters, modifier, getAnnotations(executableElement), throwsTypes);
    }

    private ImmutableSet<ASTType> buildASTElementTypes(List<? extends TypeMirror> mirrorTypes) {
        return FluentIterable.from(mirrorTypes)
                .transform(astTypeBuilderVisitor)
                .toSet();
    }

    /**
     * Build a list of ASTParameters corresponding tot he input VariableElement list elements
     *
     * @param variableElements required input element
     * @return list of ASTParameters
     */
    private ImmutableList<ASTParameter> getParameters(List<? extends VariableElement> variableElements) {
        ImmutableList.Builder<ASTParameter> astParameterBuilder = ImmutableList.builder();

        for (VariableElement variables : variableElements) {
            astParameterBuilder.add(getParameter(variables));
        }

        return astParameterBuilder.build();
    }

    /**
     * Build an ASTParameter from the input VariableElement
     *
     * @param variableElement required input element
     * @return ASTParameter
     */
    public ASTParameter getParameter(Element variableElement) {
        return new ASTElementParameter(variableElement, astTypeBuilderVisitor, getAnnotations(variableElement));
    }

    /**
     * Build an ASTConstructor from the input ExecutableElement
     *
     * @param executableElement require input element
     * @return ASTConstructor
     */
    public ASTConstructor getConstructor(ExecutableElement executableElement) {
        ImmutableList<ASTParameter> parameters = getParameters(executableElement.getParameters());
        ASTAccessModifier modifier = buildAccessModifier(executableElement);
        ImmutableSet<ASTType> throwsTypes = buildASTElementTypes(executableElement.getThrownTypes());

        return new ASTElementConstructor(executableElement, parameters, modifier, getAnnotations(executableElement), throwsTypes);
    }

    private ImmutableSet<ASTAnnotation> getAnnotations(Element element) {
        ImmutableSet.Builder<ASTAnnotation> annotationBuilder = ImmutableSet.builder();

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            ASTType type = getType((TypeElement) annotationMirror.getAnnotationType().asElement());

            annotationBuilder.add(astFactory.buildASTElementAnnotation(annotationMirror, type));
        }

        return annotationBuilder.build();
    }

    public ASTMethod findMethod(ASTType type, String methodName, ASTType... args) {
        TypeElement typeElement = elements.getTypeElement(type.getName());

        ASTType updatedType = getType(typeElement);

        for(ASTType typeLoop = updatedType; typeLoop != null ; typeLoop = typeLoop.getSuperClass()){
            for (ASTMethod astMethod : typeLoop.getMethods()) {
                if(astMethod.getName().equals(methodName) && parametersMatch(astMethod.getParameters(), args)){
                    return astMethod;
                }
            }
        }

        return null;
    }

    private boolean parametersMatch(List<ASTParameter> parameters, ASTType[] args) {

        if(args == null){
            return parameters.isEmpty();
        }

        if(parameters.size() != args.length){
            return false;
        }

        for(int i = 0; i < parameters.size(); i++){
            if(!parameters.get(i).getASTType().getName().equals(args[i].getName())){
                return false;
            }
        }

        return true;
    }
}

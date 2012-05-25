package org.androidtransfuse.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.androidtransfuse.analysis.AnalysisContext;
import org.androidtransfuse.analysis.InjectionPointFactory;
import org.androidtransfuse.analysis.SimpleAnalysisContextFactory;
import org.androidtransfuse.analysis.adapter.ASTClassFactory;
import org.androidtransfuse.analysis.adapter.ASTMethod;
import org.androidtransfuse.analysis.adapter.ASTParameter;
import org.androidtransfuse.analysis.targets.MockAnalysisClass;
import org.androidtransfuse.config.TransfuseGenerationGuiceModule;
import org.androidtransfuse.model.ConstructorInjectionPoint;
import org.androidtransfuse.model.FieldInjectionPoint;
import org.androidtransfuse.model.InjectionNode;
import org.androidtransfuse.model.MethodInjectionPoint;
import org.androidtransfuse.util.JavaUtilLogger;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author John Ericksen
 */
public class InjectionPointFactoryTest {

    private AnalysisContext emptyContext;
    @Inject
    private ASTClassFactory astClassFactory;
    @Inject
    private SimpleAnalysisContextFactory contextFactory;
    @Inject
    private InjectionPointFactory injectionPointFactory;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new TransfuseGenerationGuiceModule(new JavaUtilLogger(this)));
        injector.injectMembers(this);
        emptyContext = contextFactory.buildContext();
    }

    @Test
    public void testConstructorInjectionPointBuild() {
        Constructor<?>[] constructors = MockAnalysisClass.class.getConstructors();
        Constructor constructor = constructors[0];

        ConstructorInjectionPoint constructorInjectionPoint = injectionPointFactory.buildInjectionPoint(astClassFactory.buildASTClassConstructor(constructor), emptyContext);

        TypeVariable[] typeParameters = constructor.getTypeParameters();
        List<InjectionNode> injectionNodes = constructorInjectionPoint.getInjectionNodes();
        for (int i = 0; i < typeParameters.length; i++) {

            InjectionNode injectionNode = injectionNodes.get(i);
            TypeVariable typeParameter = typeParameters[i];
            assertEquals(typeParameter.getName(), injectionNode.getClassName());
        }
    }

    @Test
    public void testMethodInjectionPointBuild() {
        Method[] methods = MockAnalysisClass.class.getDeclaredMethods();
        Method method = methods[0];

        List<ASTParameter> astParameters = astClassFactory.buildASTTypeParameters(method);
        ASTMethod astMethod = astClassFactory.buildASTClassMethod(method);

        MethodInjectionPoint methodInjectionPoint = injectionPointFactory.buildInjectionPoint(astMethod, emptyContext);


        List<InjectionNode> injectionNodes = methodInjectionPoint.getInjectionNodes();
        for (int i = 0; i < astParameters.size(); i++) {

            InjectionNode injectionNode = injectionNodes.get(i);
            ASTParameter typeParameter = astParameters.get(i);
            assertEquals(typeParameter.getName(), injectionNode.getClassName());
        }
    }

    @Test
    public void testParameterInjectionPointBuild() {
        Field[] fields = MockAnalysisClass.class.getDeclaredFields();
        Field field = fields[0];

        FieldInjectionPoint fieldInjectionPoint = injectionPointFactory.buildInjectionPoint(astClassFactory.buildASTClassField(field), emptyContext);

        InjectionNode injectionNode = fieldInjectionPoint.getInjectionNode();

        assertEquals(field.getType().getName(), injectionNode.getClassName());
    }
}

package test.concordion.internal;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.concordion.internal.extension.ExtensionInitialisationException;
import org.concordion.internal.extension.FixtureExtensionLoader;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import test.concordion.JavaSourceCompiler;
import test.concordion.extension.fake.FakeExtension1;
import test.concordion.extension.fake.FakeExtension2;

@SuppressWarnings({"rawtypes","unchecked"})
public class FixtureExtensionLoaderWithClassAnnotationTest {
    private final JavaSourceCompiler compiler = new JavaSourceCompiler();
    private final FixtureExtensionLoader loader = new FixtureExtensionLoader();

    @Test
    public void loadsExtensionListedInClassAnnotation() throws Exception {
        String annotation = "@Extensions({FakeExtension1.class})";
        
        List extensions = loader.getExtensionsForFixture(withClassAnnotation(annotation));
        
        assertThat((List<Object>)extensions, CoreMatchers.hasItem(CoreMatchers.instanceOf(FakeExtension1.class)));
    }
    
    @Test
    public void errorsIfExtensionConstructorIsNotPublic() throws Exception {
        String annotation = "@Extensions({FakeExtensionWithPrivateConstructor.class})"; 
        
        try {
            loader.getExtensionsForFixture(withClassAnnotation(annotation));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), CoreMatchers.containsString("Unable to access no-args constructor"));
        }
    }

    @Test
    public void errorsIfExtensionDoesNotHaveNoArgsConstructor() throws Exception {
        String annotation = "@Extensions({FakeExtensionWithoutNoArgsConstructor.class})"; 
        
        try {
            loader.getExtensionsForFixture(withClassAnnotation(annotation));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), CoreMatchers.containsString("Unable to instantiate extension"));
        }
    }

    @Test
    public void loadsExtensionFromFactoriesListedInClassAnnotation() throws Exception {
        String annotation = "@Extensions({FakeExtension2Factory.class})";
        
        List extensions = loader.getExtensionsForFixture(withClassAnnotation(annotation));
        
        assertThat((List<Object>)extensions, CoreMatchers.hasItem(CoreMatchers.instanceOf(FakeExtension2.class)));
    }
    
    @Test
    public void errorsIfClassIsNotExtensionOrExtensionFactory() throws Exception {
        String annotation = "@Extensions({java.util.Date.class})";
        
        try {
            loader.getExtensionsForFixture(withClassAnnotation(annotation));
            fail("Expected ExtensionInitialisationException");
        } catch (ExtensionInitialisationException e) {
            assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("must implement"), 
            		CoreMatchers.containsString("ConcordionExtension or"), CoreMatchers.containsString("ConcordionExtensionFactory")));
        }
    }
    
    private Object withClassAnnotation(String annotation) throws Exception, InstantiationException,
            IllegalAccessException {
        return withClassAnnotation(annotation, null);
    }

    private Object withClassAnnotation(String annotation, String superClassName) throws Exception, InstantiationException,
            IllegalAccessException {
        String className = "ExampleFixture";
        Class<?> clazz = classWithAnnotation(annotation, className, superClassName);
        Object fixture = clazz.newInstance();
        return fixture;
    }
    
    private Class<?> classWithAnnotation(String annotation, String className, String superClassName) throws Exception {
        String code = 
            "import org.concordion.api.extension.Extensions;" +
            "import org.concordion.api.extension.ConcordionExtension;" +
            "import org.concordion.api.extension.ConcordionExtender;" +
            "import test.concordion.extension.fake.*;" +
            annotation +
            "public class " + className + (superClassName != null ? " extends " + superClassName : "") + " {" +
            "}";    
        Class<?> clazz = compiler.compile(className, code);
        return clazz;
    }
}

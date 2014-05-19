package test.concordion.internal.runner;

import java.io.IOException;

import org.concordion.api.ExpectedToFail;
import org.concordion.api.Result;
import org.concordion.api.RunnerResult;
import org.concordion.api.Unimplemented;
import org.concordion.internal.runner.DefaultConcordionRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import test.concordion.ConsoleLogGobbler;
import test.concordion.StubLogger;

public class DefaultConcordionRunnerTest {
    
    @Rule 
    public ConsoleLogGobbler logGobbler = new ConsoleLogGobbler();  // Ensure error log messages don't appear on console
    private final StubLogger stubLogger = new StubLogger();
    
    private final TestDefaultConcordionRunner runner = new TestDefaultConcordionRunner();

    @Test
    public void returnsFailureOnJUnitFailure() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, StubResult.FAILURE);
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.FAILURE));
    }
    
    @Test
    public void returnsSuccessOnJUnitSuccess() throws Exception {
    	StubResult.SUCCESS.reset();
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, StubResult.SUCCESS);
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.SUCCESS));
    }

    // JUnit success is reported when an ExpectedToFail test does fail
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenExpectedToFail() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(ExpectedToFailClass.class, StubResult.SUCCESS);
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.IGNORED));
    }
    
    // JUnit success is reported when an Unimplemented test is unimplemented
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenUnimplemented() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnimplementedClass.class, StubResult.SUCCESS);
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.IGNORED));
    }
    
    // JUnit failure is reported when an ExpectedToFail test does not fail
    @Test
    public void returnsFailureOnJUnitFailureWhenExpectedToFail() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(ExpectedToFailClass.class, StubResult.FAILURE);
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.FAILURE));
    }
    
    // JUnit failure is reported when an Unimplemented test is implemented
    @Test
    public void returnsFailureOnJUnitFailureWhenUnimplemented() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnimplementedClass.class, StubResult.FAILURE);
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.FAILURE));
    }
    
    @Test
    public void returnsIgnoredOnJUnitSuccessWhenIgnoredCountGreaterThanZero() throws Exception {
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, StubResult.SUCCESS.withIgnoreCount(1));
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.IGNORED));
    }

    @Test
    public void doesNotThrowExceptionOnAssertionError() throws Exception {
        Throwable error = new AssertionError();
        RunnerResult myresult = runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
        Assert.assertThat(myresult.getResult(), CoreMatchers.is(Result.FAILURE));
    }

    @Test
    public void throwsExceptionOnCheckedException() throws Exception {
        Throwable error = new IOException("dummy exception");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
            Assert.fail("expected IOException");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("dummy exception"));
        }
    }

    @Test
    public void throwsWrappedExceptionOnThrowable() throws Exception {
        Throwable error = new InternalError("dummy throwable");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
            Assert.fail("expected InternalError");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.is("dummy throwable"));
        }
    }

    @Test
    public void throwsRuntimeExceptionOnRuntimeException() throws Exception {
        Throwable error = new IllegalArgumentException("dummy runtime exception");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("dummy runtime exception"));
        }
    }

    @Test
    public void logsExceptions() throws Exception {
        Throwable error = new IOException("dummy IO exception");
        try {
            runner.decodeJUnitResult(UnannotatedClass.class, new StubResult().withFailure(error));
        } catch (IOException e) {
        }
        Assert.assertThat(stubLogger.getNewLogMessages(), CoreMatchers.containsString("java.io.IOException: dummy IO exception"));
    } 
    
    private static final class UnannotatedClass {
    }

    @ExpectedToFail
    private static final class ExpectedToFailClass {
    }

    @Unimplemented
    private static final class UnimplementedClass {
    }

    private static final class TestDefaultConcordionRunner extends DefaultConcordionRunner {
        protected RunnerResult decodeJUnitResult(Class<?> concordionClass, org.junit.runner.Result jUnitResult) throws Exception {
            return super.decodeJUnitResult(concordionClass, jUnitResult);
        }
    }
}
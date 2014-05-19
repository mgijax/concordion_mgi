package test.concordion.internal.listener;

import org.concordion.api.Resource;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.internal.XMLParser;
import org.concordion.internal.listener.BreadcrumbRenderer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import test.concordion.ConsoleLogGobbler;
import test.concordion.StubLogger;
import test.concordion.StubSource;

public class BreadcrumbRendererTest {

    private static final String EXPECTED_SOURCE_NAME = "stub";
    private static final String SPEC_RESOURCE_NAME = "/parent/Child.html";
    private static final String PACKAGE_RESOURCE_NAME = "/parent/Parent.html";
    private static final String ERRONEOUS_HTML = "<html><head></html>";
    
    private final StubSource stubSource = new StubSource();
    private final StubLogger stubLogger = new StubLogger();
    private final BreadcrumbRenderer renderer = new BreadcrumbRenderer(stubSource, new XMLParser());
    
    @Rule 
    public ConsoleLogGobbler logGobbler = new ConsoleLogGobbler();  // Ensure error log messages don't appear on console

    @Test
    public void logsNameOfErroneousHtmlFileOnParseError() {
        stubSource.addResource(PACKAGE_RESOURCE_NAME, ERRONEOUS_HTML);
        SpecificationProcessingEvent event = new SpecificationProcessingEvent(new Resource(SPEC_RESOURCE_NAME), null);
        renderer.afterProcessingSpecification(event);

        String logMessage = stubLogger.getNewLogMessages();
        Assert.assertThat(logMessage, CoreMatchers.containsString("Failed to parse XML document"));
        Assert.assertThat(logMessage, CoreMatchers.containsString(String.format("[%s: %s]", EXPECTED_SOURCE_NAME, PACKAGE_RESOURCE_NAME)));
    }
}

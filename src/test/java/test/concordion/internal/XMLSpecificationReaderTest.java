package test.concordion.internal;


import org.concordion.api.Resource;
import org.concordion.internal.ParsingException;
import org.concordion.internal.XMLParser;
import org.concordion.internal.XMLSpecificationReader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import test.concordion.StubSource;

public class XMLSpecificationReaderTest {

    private static final String EXPECTED_SOURCE_NAME = "stub";
    private static final String SPEC_RESOURCE_NAME = "/Spec.html";
    private static final String ERRONEOUS_HTML = "<html><head></html>";
    
    private final StubSource stubSource = new StubSource();
    private final XMLSpecificationReader reader = new XMLSpecificationReader(stubSource, new XMLParser(), null);
    
    @Test
    public void includesSourceAndResourceNameOnFailure() throws Exception {
        try {
            stubSource.addResource(SPEC_RESOURCE_NAME, ERRONEOUS_HTML);
            reader.readSpecification(new Resource(SPEC_RESOURCE_NAME));
            Assert.fail("Expected ParsingException");
        } catch (ParsingException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Failed to parse XML document"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(String.format("[%s: %s]", EXPECTED_SOURCE_NAME, SPEC_RESOURCE_NAME)));
        }
    }
}

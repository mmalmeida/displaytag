package org.displaytag.tags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.test.DisplaytagCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;


/**
 * Tests for basic displaytag functionalities.
 * @author fgiust
 * @version $Revision$ ($Author$)
 */
public class BasicTableTagTest extends DisplaytagCase
{

    /**
     * logger.
     */
    private static Log log = LogFactory.getLog(BasicTableTagTest.class);


    /**
     * Instantiates a new test case.
     * @param name test name
     */
    public BasicTableTagTest(String name)
    {
        super(name);
    }

    /**
     * Verifies that the generated page contains a table with the expected number of columns.
     * @throws Exception any axception thrown during test.
     */
    public void testAutoGeneratedColumns() throws Exception
    {

        WebRequest request = new GetMethodWebRequest("http://localhost/autocolumns.jsp");

        WebResponse response = runner.getResponse(request);

        log.debug("RESPONSE: " + response.getText());

        WebTable[] tables = response.getTables();

        assertEquals("Expected one table in result.", 1, tables.length);

        assertEquals("Bad number of generated columns.", 4, tables[0].getColumnCount());
    }
}

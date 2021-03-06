package org.displaytag.export;

import org.displaytag.model.TableModel;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FOPException; import org.apache.fop.apps.FopFactory;
import org.apache.fop.fo.ValidationException;
import org.apache.xmlgraphics.util.MimeConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.servlet.jsp.JspException;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Exports the data to a totaled xml format, and then transforms that data using XSL-FO to a pdf.
 * The stylesheet can be fed in as a string from the property export.pdf.fo.stylesheetbody, or you can use a default
 *  stylesheet named by the property export.pdf.fo.stylesheet.
 *     When you are developing a stylesheet, this class will
 * output the raw FO if you set your log level to debug, which is very handy if you are getting errors or unexpected
 * pdf output. See asFo_us.xsl for a sample XSL-FO stylesheet.
 *
 * The basic structure of the intermediate XML is
 * <table>
 *   <header>
 *      <header-cell>AntColumn</hearder-cell>
 *   </header>
 *   <data>
 *    <subgroup grouped-by="0">
 *      <row>
 *          <cell grouped="true">Ant</cell>
 *      </row>
 *      <subtotal> <subtotal-cell></subtotal-cell>
 *    </subgroup>
 *   </data>
 *  </table>
 *
 * @author rapruitt
 * Date: Aug 26, 2009
 * Time: 1:55:29 PM
 * @see  FopExportView#SPECIFIC_STYLESHEET the property that contains the text of a stylesheet
 * @see  FopExportView#DEFAULT_STYLESHEET the defualt stylesheet location
 * @see XslTransformerTest#XML for a sample of the XML output
 * @see XmlTotalsWriter
 *
 */
public class FopExportView implements BinaryExportView {
    private static Log log = LogFactory.getLog(FopExportView.class);
    /**
     * Default stylesheet.
     */
    public static final String DEFAULT_STYLESHEET = "export.pdf.fo.stylesheet";  //$NON-NLS-1$
    /**
     * A stylesheet as a string on a property.
     */
    public static final String SPECIFIC_STYLESHEET = "export.pdf.fo.stylesheetbody"; //$NON-NLS-1$
    /**
     * TableModel to render.
     */
    protected TableModel model;

    /**
     * @see org.displaytag.export.ExportView#setParameters(TableModel, boolean, boolean, boolean)
     */
    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader,
        boolean decorateValues)
    {
        this.model = tableModel;
    }

    /**
     * @see org.displaytag.export.BaseExportView#getMimeType()
     * @return "application/pdf"
     */
    public String getMimeType()
    {
        return "application/pdf"; //$NON-NLS-1$
    }


    /**
     * Load the stylesheet.
     * @return the stylesheet
     * @throws IOException if we cannot locate it
     */
    public InputStream getStyleSheet() throws IOException
    {

        InputStream styleSheetStream;
        String styleSheetString = model.getProperties().getProperty(SPECIFIC_STYLESHEET);
        if (StringUtils.isNotEmpty(styleSheetString))
        {
            styleSheetStream = new ByteArrayInputStream(styleSheetString.getBytes());
        }
        else
        {
            String styleSheetPath = model.getProperties().getProperty(DEFAULT_STYLESHEET);
            styleSheetStream = this.getClass().getResourceAsStream(styleSheetPath);
            if (styleSheetStream == null)
            {
                throw new IOException("Cannot locate stylesheet " + styleSheetPath);               //$NON-NLS-1$
            }
        }
        return styleSheetStream;
    }


    /**
     * Don't forget to enable debug if you want to see the raw FO.
     * @param out output writer
     * @throws IOException
     * @throws JspException
     */
    public void doExport(OutputStream out) throws IOException, JspException
    {
        String xmlResults = getXml();

        FopFactory fopFactory = FopFactory.newInstance();
        Source xslt = new StreamSource(getStyleSheet());
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer;
        try
        {
            transformer = factory.newTransformer(xslt);
        }
        catch (TransformerConfigurationException e)
        {
            throw new JspException("Cannot configure pdf export "+e.getMessage(),e);             //$NON-NLS-1$
        }

        boolean outputForDebug = log.isDebugEnabled();
        if (outputForDebug)
        {
            logXsl(xmlResults, transformer, null);
        }

        Fop fop;
        try
        {
            fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
        }
        catch (FOPException e)
        {
            throw new JspException("Cannot configure pdf export "+e.getMessage(),e);           //$NON-NLS-1$
        }

        StreamSource src = new StreamSource( new StringReader(xmlResults));
        Result res;
        try
        {
            res = new SAXResult(fop.getDefaultHandler());
        }
        catch (FOPException e)
        {
            throw new JspException("error setting up transform ",e);                         //$NON-NLS-1$
        }
        try
        {
            transformer.transform(src, res);
        }
        catch (TransformerException e)
        {
            if (e.getCause() instanceof ValidationException)
            {
                // recreate the errant fo
                ValidationException ve = (ValidationException) e.getCause();
                logXsl(xmlResults, transformer, ve);
            }
            else
            {
                throw new JspException("error creating pdf output",e);                         //$NON-NLS-1$
            }

        }
    }

    protected String getXml() throws JspException {
        XmlTotalsWriter totals = new XmlTotalsWriter(model);
        totals.writeTable(model, "-1");
        return totals.getXml();
    }

    /**
     * log it.
     * @param xmlResults raw
     * @param transformer   the transformer
     * @param e the optional exception
     * @throws JspException wrapping an existing error
     */
    protected void logXsl(String xmlResults,Transformer transformer, Exception e) throws JspException
    {
        StreamResult debugRes = new StreamResult(new StringWriter());
        StreamSource src =  new StreamSource( new StringReader(xmlResults));
        try
        {
            transformer.transform(src, debugRes);
            if (e != null)
            {
                log.error("xslt-fo error " + e.getMessage(), e);                                    //$NON-NLS-1$
                log.error("xslt-fo result of " + debugRes.getWriter());                                    //$NON-NLS-1$
                throw new JspException("Stylesheet produced invalid xsl-fo result", e);              //$NON-NLS-1$
            }
            else
            {
                log.info("xslt-fo result of " +  debugRes.getWriter());                                    //$NON-NLS-1$
            }
        }
        catch (TransformerException ee)
        {
            throw new JspException("error creating pdf output " +ee.getMessage(), ee);                         //$NON-NLS-1$
        }
    }

    /**
     * If you are authoring a stylesheet locally, this is highly recommended as a way to test your stylesheet agaisnt
     * dummy data.
     * @see XslTransformerTest#XML as a sample
     * @param xmlSrc xml as string
     * @param styleSheetPath the path to the stylesheet
     * @throws Exception if trouble
     */
    public static void transform(String xmlSrc, String styleSheetPath, File f) throws Exception
    {                    //"org.displaytag.export."
//        String xmlFile = " C:\\dev\\displaytag\\trunk\\displaytag\\src\\main\\resources\\org\\displaytag\\export\\xmlExportValue.xml";

        
        FopFactory fopFactory = FopFactory.newInstance();
        InputStream styleSheetStream = FopExportView.class.getResourceAsStream(styleSheetPath);

        Source xslt = new StreamSource(styleSheetStream );
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer;
        try
        {
            transformer = factory.newTransformer(xslt);
        }
        catch (TransformerConfigurationException e)
        {
            throw new JspException("Cannot configure pdf export "+e.getMessage(),e);             //$NON-NLS-1$
        }
        Fop fop;
        try
        {
            FileOutputStream fw = new FileOutputStream(f);
            fop = fopFactory.newFop(MimeConstants.MIME_PDF, fw);
        }
        catch (FOPException e)
        {
            throw new JspException("Cannot configure pdf export "+e.getMessage(),e);           //$NON-NLS-1$
        }

        Source src = new StreamSource( new StringReader(xmlSrc));
        Result res;
        try
        {
            res = new SAXResult(fop.getDefaultHandler());
        }
        catch (FOPException e)
        {
            throw new JspException("error setting up transform ",e);                         //$NON-NLS-1$
        }
        try
        {
            transformer.transform(src, res);
        }
        catch (TransformerException e)
        {
            throw new JspException("error creating pdf output " + e.getMessage(),e);                         //$NON-NLS-1$
        }
    }
}



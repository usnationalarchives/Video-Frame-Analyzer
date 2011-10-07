package gov.nara.nwts.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;
import org.w3c.dom.ls.LSSerializer;

/**
 * DOM-related tools used by the Video Frame Analyzer.
 * An earlier incarnation of this application built an html file as output (thus the name of the class)
 * @author TBrady
 *
 */
public class HtmlDocument {
	static DocumentBuilderFactory dbf;
	public static DocumentBuilder db;
	static {
		  dbf = DocumentBuilderFactory.newInstance();
		  try {
			db = dbf.newDocumentBuilder();
		  } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	}
 

	/**
	 * A DOM3 implementation is used to refine the parsing mechanism for the application in order to minimize the amount of  data that will be brought into memory at one time.
	 */
	static DOMImplementationLS impl;
	static {
        //System.setProperty(DOMImplementationRegistry.PROPERTY,"org.apache.xerces.dom.DOMXSImplementationSourceImpl");
        DOMImplementationRegistry registry;
		try {
			registry = DOMImplementationRegistry.newInstance();
	        impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * Create a parser with a custom filter.
	 */
	public static LSParser getLSParser(DOMErrorHandler err, LSParserFilter pfilter) throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        // create DOMBuilder
        LSParser builder = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
        //DOMConfiguration config = builder.getDomConfig();

        // create Error Handler
        //DOMErrorHandler errorHandler = err;
        
        // create filter
        LSParserFilter filter = pfilter;
        
        builder.setFilter(filter);
        return builder;
	}
	
	/**
	 * Parse a file using a cusom Parser filter.
	 */
	public static Document parse(File f, DOMErrorHandler err, LSParserFilter pfilter) throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		LSParser builder = getLSParser(err, pfilter);
		
		return builder.parseURI(f.toURI().toString());
	}
	
	public static void serialize(Document d) throws FileNotFoundException{
		serialize(d, null);
	}
	
	/**
	 * The preferred mechanism to save an XML file is to use a Transformer to build the output.
	 * Since a DOMImplementation object already exists, this code works, so it has been left in tact.
	 */
	public static void serialize(Document d, File f) throws FileNotFoundException{
        LSSerializer domWriter = impl.createLSSerializer();
        
        //config = domWriter.getDomConfig();
        //config.setParameter("xml-declaration", Boolean.FALSE);
        //config.setParameter("validate",errorHandler);

        // serialize document to standard output
        //domWriter.writeNode(System.out, doc);
        LSOutput dOut = impl.createLSOutput();
        if (f == null) {
          dOut.setByteStream(System.out);
          domWriter.write(d,dOut);	
        } else {
        	FileOutputStream fout = new FileOutputStream(f);
        	dOut.setByteStream(fout);
        	domWriter.write(d,dOut);
        	try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
	}

	static XPathFactory xfactory;
	static {
		xfactory = XPathFactory.newInstance();
	}
	
	/**
	 * Extract an item from an XML document via an XPath.
	 */
	public static String runXpath(Document d, String expression) throws XPathExpressionException {
		XPath xpath = xfactory.newXPath();
		XPathExpression xpe = xpath.compile(expression);
		return xpe.evaluate(d);
	}

}

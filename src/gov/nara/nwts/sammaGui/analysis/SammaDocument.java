package gov.nara.nwts.sammaGui.analysis;
import gov.nara.nwts.sammaGui.AnalyzeSamma;
import gov.nara.nwts.util.DOM3Handler;
import gov.nara.nwts.util.HtmlDocument;

import java.io.File;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

/** Corresponds to a SAMMA XML file, invokes the custom parser functionality which will trigger the line-by-line analysis of frame data where needed.*/
public class SammaDocument {
	Document d;
	File f;
	int sampleCount = 0;
	 
	public SammaDocument(AnalyzeSamma analyzeSamma, File f) throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, XPathExpressionException{
		this.f = f;
		DOM3Handler dom3 = new DOM3Handler(analyzeSamma);
		d = HtmlDocument.parse(f, dom3, dom3);
		String sc = runXpath("/SammaSolo/Metadata/Log/SampleCount");
		sampleCount = Integer.parseInt(sc);
	}
	
	/** Execute an Xpath expression that will return a String*/
	public String runXpath(String xpath) throws XPathExpressionException {
		if (xpath == null) return null;
		return HtmlDocument.runXpath(d, xpath);
	}

	/** Execute an Xpath expression that will return a double*/
	public double runDoubleXpath(String xpath) throws XPathExpressionException, NumberFormatException {
		String s = runXpath(xpath);			
		return Double.parseDouble(s);
	}

} 

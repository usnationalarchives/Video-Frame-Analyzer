package gov.nara.nwts.sammaGui.analysis;

import gov.nara.nwts.sammaGui.AnalyzeSamma;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Analysis object which examines a file-level property rather than a frame-specific property; XPATH expressions are used to evaluate the file-level property.
 * Note: An additional attribute in the XML will indicate if the extracted value should be interpreted as a time code.
 * @author TBrady
 *
 */
public class PassFailXmlAnalysis extends XmlAnalysis {
	public String xpath;
	public Element checkFile;
	public Element fileValue; 

	/** Read a new PassFailXmlAnalysis object from XML*/
	PassFailXmlAnalysis(Element e) {
		super(e);
		fileValue = getElement(e, SammaXml.ELEMENT.fileValue);
		if (fileValue!=null) {
			xpath = fileValue.getAttribute(SammaXml.ATTR.xpath.toString());
		}
		checkFile = getElement(e,SammaXml.ELEMENT.checkFile);
	}
	/** Construct the XML for a new PassFailXmlAnalysis object*/
	public PassFailXmlAnalysis(Document d) {
		super(d);
		fileValue = d.createElement(SammaXml.ELEMENT.fileValue.tag);
		checkFile = d.createElement(SammaXml.ELEMENT.checkFile.tag);
		root.appendChild(checkFile);
		root.appendChild(fileValue);
	}

	public void createFrameElements(Element e) {
	}
	
	public void createFrameElements(Document d) {
	}
	
	/** Extract a file level value using the XPath expression for the object*/
	public double getFileValue(SammaDocument doc) {
		try {
			return doc.runDoubleXpath(xpath);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}
		
	}

	public Element getElement(SammaXml.ELEMENT elem) {
		if (elem == SammaXml.ELEMENT.checkFile) { 
			return checkFile;
		} else if (elem == SammaXml.ELEMENT.fileValue) {
			return fileValue;
		}
		return null;
	}
	protected boolean checkFatal(int frame, double val) {
		return false;
	}

	protected boolean checkValid(int frame, double val) {
		return true;
	}

	protected boolean evaluateRangeIsError(Range range) {
		return false;
	}

	public String getPassingCriteria() {
		return interpret(checkFile);
	}
	

	public String getFileValueString(SammaDocument doc) {
		String s = fileValue.getAttribute(SammaXml.ATTR.format.attr);
		if (s==null) s = "";
		if (s.equals(SammaXml.VAL_FORMAT.time.name())) {
			return Range.getTime((new Double(getFileValue(doc))).intValue());
		} 
		return AnalyzeSamma.nf.format(getFileValue(doc));
	}
	
	/** If the extracted value does not satisfy test criteria*/
	public boolean evaluateFileValid(SammaDocument doc) {
		double dbl = getFileValue(doc);
		boolean b = test(checkFile, dbl);
		if (!b) {
			Range r = new Range(1, dbl);
			r.mark(doc.sampleCount, dbl);
			r.error = true;
			errbuf.add(r);
		}
		return b;
	}

}

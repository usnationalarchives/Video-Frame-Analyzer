package gov.nara.nwts.sammaGui.analysis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Represents an analysis task that will be performed against a single frame level value identified by a 2 letter "code" within the frame-level data; the criteria for the analysis are captured in XML using attributes that correspond to comparison operators. 
 * @see SammaXml for the definitions of the elements and attributes in use
 * @author TBrady
 *
 */
public class XmlAnalysis extends Analysis {
	/** 
	 * Helper method to find a specific element within a root element or to create that element if it does not exist. 
	 */
	public Element getElement(Element parent, SammaXml.ELEMENT tag){
		NodeList nl = parent.getElementsByTagName(tag.toString());
		if (nl.getLength() == 1)
			return (Element)nl.item(0);
		Element newel = parent.getOwnerDocument().createElement(tag.toString());
		parent.appendChild(newel);
		return newel;
	}
	
	public Element checkValid;
	public Element checkFatal;
	public Element checkRangeError;
	
	/** Initialize the object from an XML element*/
	public XmlAnalysis(Element e) {
		super(e);
		createFrameElements(e);
	}

	
	/** Create a new XML element to correspond to the object*/
	public XmlAnalysis(Document d) {
		super(d.createElement(SammaXml.ELEMENT.analysis.tag));
		createFrameElements(d);
	}
	
	/** Initialize object members from an XML element*/
	public void createFrameElements(Element e) {
		checkValid = getElement(e, SammaXml.ELEMENT.checkValid);
		checkFatal = getElement(e, SammaXml.ELEMENT.checkFatal);
		checkRangeError = getElement(e, SammaXml.ELEMENT.checkRangeError);		
	}
	
	/** Create a new XML elements for that will correspond to the objects members*/
	public void createFrameElements(Document d) {
		checkValid = d.createElement(SammaXml.ELEMENT.checkValid.tag);
		checkFatal = d.createElement(SammaXml.ELEMENT.checkFatal.tag);
		checkRangeError = d.createElement(SammaXml.ELEMENT.checkRangeError.tag);
		root.appendChild(checkValid);
		root.appendChild(checkRangeError);
		root.appendChild(checkFatal);		
	}

	
	public String getInitText() {
		return "";
	}
	
	/** Return the element corresponding to a specific tag enum value*/
	public Element getElement(SammaXml.ELEMENT name) {
		if (name == SammaXml.ELEMENT.checkValid) { 
			return checkValid;
		} else if (name == SammaXml.ELEMENT.checkFatal) {
			return checkFatal;
		} else if (name == SammaXml.ELEMENT.checkRangeError) {
			return checkRangeError;
		}
		return null;
	}
	
	/** Translate an elements comparison attributes and other attributes into human-readable text*/
	public String interpret(Element e){
		if (e==null) return "";
		StringBuffer buf = new StringBuffer();
		if (e.hasAttribute(SammaXml.ATTR.EQ.attr)) { 
			buf.append(code);
			buf.append(" = " + e.getAttribute(SammaXml.ATTR.EQ.attr) +". ");
		}
		if (e.hasAttribute(SammaXml.ATTR.NEQ.attr)) { 
			buf.append(code);
			buf.append(" != " + e.getAttribute(SammaXml.ATTR.NEQ.attr) +". "); 
		}
		if (e.hasAttribute(SammaXml.ATTR.GT.attr)) { 
			buf.append(code);
			buf.append(" > " + e.getAttribute(SammaXml.ATTR.GT.attr) +". "); 
		}
		if (e.hasAttribute(SammaXml.ATTR.GTE.attr)) {
			buf.append(code);
			buf.append(" >= " + e.getAttribute(SammaXml.ATTR.GTE.attr) +". ");
		}
		if (e.hasAttribute(SammaXml.ATTR.LT.attr)) {
			buf.append(code);
			buf.append(" < " + e.getAttribute(SammaXml.ATTR.LT.attr) +". ");
		}
		if (e.hasAttribute(SammaXml.ATTR.LTE.attr)) { 
			buf.append(code);
			buf.append(" <= " + e.getAttribute(SammaXml.ATTR.LTE.attr) +". ");
		}
		if (e.hasAttribute(SammaXml.ATTR.minLenSec.attr)) 
			buf.append("For at least " + e.getAttribute(SammaXml.ATTR.minLenSec.attr) +" secs. "); 
		if (e.hasAttribute(SammaXml.ATTR.skipFirstSec.attr)) 
			buf.append("Ignoring the first " + e.getAttribute(SammaXml.ATTR.skipFirstSec.attr) +" secs. "); 
		if (e.hasAttribute(SammaXml.ATTR.skipLast.attr)) 
			if (e.getAttribute(SammaXml.ATTR.skipLast.attr).equals("true")) 
				buf.append("Ignoring the last range ");
		
		return buf.length() > 0 ? getInitText() + buf.toString() : "";
	}
	
	/** Get the double value from a specific attribute; note within the application xml all numeric comparisons are performed on double values.*/
	public Double getDouble(Element el, SammaXml.ATTR attr){
		Double d = null;
		if (el.hasAttribute(attr.toString())) {
			try {
				d = Double.parseDouble(el.getAttribute(attr.toString()));
			} catch(NumberFormatException e){
			}
		}
		return d;
	}
	
	/**
	 * Using the comparison attributes within an element, evaluate a double value against the comparison criteria
	 * @return true if the value satisfies all criteria
	 */
	protected boolean test(Element e, double val) {
		boolean b = false;
		Double d;
		d = getDouble(e, SammaXml.ATTR.EQ);
		if (d != null) {
			b = b || (val==d.doubleValue());
		}
		d = getDouble(e, SammaXml.ATTR.NEQ);
		if (d != null) {
			b = b || (val != d.doubleValue());
		}
		d = getDouble(e, SammaXml.ATTR.LT);
		if (d != null) {
			b = b || (val < d.doubleValue());
		}
		d = getDouble(e, SammaXml.ATTR.LTE);
		if (d != null) {
			b = b || (val <= d.doubleValue());
		}
		d = getDouble(e, SammaXml.ATTR.GT);
		if (d != null) {
			b = b || (val > d.doubleValue());
		}
		d = getDouble(e, SammaXml.ATTR.GTE);
		if (d != null) {
			b = b || (val >= d.doubleValue());
		}
		return b;
	}

	/** 
	 * Test a value against the fatal error criteria for the Analysis; regular errors are subject to range checking before determining if they are an error or a warning while fatal errors take precedence over any error range checks.
	 */
	protected boolean checkFatal(int frame, double val) {
		return test(checkFatal, val);
	}
	/** 
	 * Test a value against the validity criteria for the Analysis
	 */
	protected boolean checkValid(int frame, double val) {
		return test(checkValid, val);
	}

	/** 
	 * At the end of processing of a file, determine if any normal (non-fatal) errors should be classified as warnings based upon the position or duration of the range in which they occur.
	 */
	protected boolean evaluateRangeIsError(Range range) {
		boolean b = true;
		Double d;
		d = getDouble(checkRangeError, SammaXml.ATTR.minLenSec);
		if (d!=null){
			b = b && (range.getFrameCount() > d * Range.FPS);
		}
		d = getDouble(checkRangeError, SammaXml.ATTR.skipFirstSec);
		if (d!=null){
			b = b && (range.end > d * Range.FPS);
		}
		String s = checkRangeError.getAttribute(SammaXml.ATTR.skipLast.toString());
		if (s.equals("true")){
			b = b && (range.last == false);
		}
		return b;
	}

	/** human readable summary of fatal criteria*/
	public String getFatalCriteria() {
		return interpret(checkFatal);
	}

	/** human readable summary of validity criteria*/
	public String getPassingCriteria() {
		return interpret(checkValid);
	}

	/** human readable summary of range criteria that will determine whether a range is an error range or a warning range*/
	public String getRangeCriteria() {
		return interpret(checkRangeError);
	}

}

package gov.nara.nwts.sammaGui.analysis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A user may create a new analysis object in the analysis editor; this class is used to all that new object to become any one of the supported types of analysis objects.
 * Once the new object has been saved, it will be saved as a specific type of Analysis object.
 * @author TBrady
 *
 */
public class NewAnalysis extends Analysis {
	public XmlAnalysis frameAnalysis;
	public PassFailXmlAnalysis fileAnalysis;	
	public AverageXmlAnalysis averageAnalysis;
	
	public NewAnalysis(Document d) {
		super(d.createElement(SammaXml.ELEMENT.newAnalysis.tag));
		frameAnalysis = new XmlAnalysis(d);
		averageAnalysis = new AverageXmlAnalysis(d);
		fileAnalysis = new PassFailXmlAnalysis(d);
	}

	public Element getElement(SammaXml.ELEMENT name) {
		return null;
	}

	protected boolean checkFatal(int frame, double val) {
		return false;
	}

	protected boolean checkValid(int frame, double val) {
		return false;
	}

	protected boolean evaluateRangeIsError(Range range) {
		return false;
	}

	public String getFatalCriteria() {
		return null;
	}

	public String getPassingCriteria() {
		return null;
	}

	public String getRangeCriteria() {
		return null;
	}

}

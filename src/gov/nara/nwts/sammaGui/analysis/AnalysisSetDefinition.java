package gov.nara.nwts.sammaGui.analysis;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Contains the metadata defining the name and purpose for an AnalysisSet.
 * @author TBrady
 *
 */
public class AnalysisSetDefinition {
	public Element root;

	public Element getElement(Element parent, String tag){
		NodeList nl = parent.getElementsByTagName(tag);
		if (nl.getLength() == 1)
			return (Element)nl.item(0);
		return null;
	}
	AnalysisSetDefinition(Element e) {
		root = e;
	}

	public AnalysisSetDefinition(Document d, String name) {
		root = d.createElement(SammaXml.ELEMENT.ANALYSIS_SET.tag);
		d.getDocumentElement().appendChild(root);
		root.setAttribute(SammaXml.ATTR.NAME.attr, name);
		root.setAttribute(SammaXml.ATTR.purpose.attr, "");
	}

	public AnalysisSetDefinition(Document d, Element e) {
		root = (Element)d.importNode(e.cloneNode(true), true);
		d.getDocumentElement().appendChild(root);
	}

	public String name() {
		return root.getAttribute(SammaXml.ATTR.NAME.attr);
	}

	String name(int len) {
		StringBuffer buf = new StringBuffer(root.getAttribute(SammaXml.ATTR.NAME.attr));
		for(int i=buf.length(); i<len;i++){
			buf.append(" ");
		}
		return buf.toString();
	}

	
	public String purpose() {
		return root.getAttribute(SammaXml.ATTR.purpose.attr);
	}
	
	public AnalysisSet getAnalysisSet() throws SAXException, IOException {
		return new AnalysisSet(this);
	}
}

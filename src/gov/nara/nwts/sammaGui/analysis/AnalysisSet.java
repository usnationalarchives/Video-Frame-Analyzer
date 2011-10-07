package gov.nara.nwts.sammaGui.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * An Analysis Set is a collection of Analysis tasks; in XML form an AnalysisSet is an element containing a number of Analysis elements.
 * @author TBrady
 *
 */
public class AnalysisSet extends ArrayList<Analysis> {
	private static final long serialVersionUID = 1L;
	public AnalysisSetDefinition def;
	public AnalysisSet(AnalysisSetDefinition asd) throws SAXException, IOException {
		this.def = asd;
		initXml(asd.root);
	}

	/** Summarize all of the steps of an Analysis Set in readable form*/
	public String getSummary() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nNAME: "+def.name());
		buf.append("\nPURPOSE: "+def.purpose());
		buf.append("\n===============================================================\n");
		for(Iterator<Analysis>i=iterator(); i.hasNext();){
			Analysis a = i.next();
			if (a.code.length()> 0){
				buf.append(a.code +": ");				
			}
			buf.append(a.name+"\n");
			if (a.getFatalCriteria().length()> 0){
				buf.append("\tFAILS IF: "+a.getFatalCriteria()+"\n");
			}
			buf.append("\tPASSES IF: "+a.getPassingCriteria()+"\n");
			if (a.getRangeCriteria().length()> 0){
				buf.append("\tWHEN: "+a.getRangeCriteria()+"\n");				
			}
		}
		return buf.toString();
	}
	
	/**
	 * Factory-style method that will initialize Analysis elements based on a specific attribute value that corresponds to a subclass of {@link Analysis}
	 */
	public void initXml(Element root) throws SAXException, IOException {
		NodeList nl = root.getElementsByTagName(SammaXml.ELEMENT.analysis.name());
		for(int i=0; i<nl.getLength(); i++){
			Element ea = (Element)nl.item(i);
			String type = ea.getAttribute(SammaXml.ATTR.type.name());
			Analysis analysis = null;
			if (type.equals(SammaXml.VAL_ANALYSIS.AverageAnalysis.name())) {
				analysis = new AverageXmlAnalysis(ea);
			} else if (type.equals(SammaXml.VAL_ANALYSIS.PassFailAnalysis.name())) {
				analysis = new PassFailXmlAnalysis(ea);
			} else if (type.equals(SammaXml.VAL_ANALYSIS.XmlAnalysis.name())) {
				analysis = new XmlAnalysis(ea);
			} else {
				analysis = new XmlAnalysis(ea);
			} 
			if (analysis!=null){
				add(analysis);
			}
		}
	}
	
}

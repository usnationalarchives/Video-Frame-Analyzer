package gov.nara.nwts.sammaGui.analysis;

import gov.nara.nwts.util.HtmlDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Represents a file containing one or more AnalysisSetDefitions
 * @author TBrady
 *
 */
public class AnalysisSetDefinitionFile {
	public int maxFiles = 0;
	public ArrayList<AnalysisSetDefinition> list;
	HashMap<String, AnalysisSetDefinition> map;
	public Document d;
	public File f;

	public AnalysisSetDefinitionFile(File f) throws SAXException, IOException {
		this.f = f;
		list = new ArrayList<AnalysisSetDefinition>();
		map = new HashMap<String, AnalysisSetDefinition>();
		if (f.exists()) {
			d = HtmlDocument.db.parse(f);
			NodeList nl = d.getElementsByTagName(SammaXml.ELEMENT.ANALYSIS_SET.tag);
			for (int i = 0; i < nl.getLength(); i++) {
				AnalysisSetDefinition asd = new AnalysisSetDefinition(
						(Element) nl.item(i));
				list.add(asd);
				map.put(asd.name(), asd);
			}
		} else {
			d = HtmlDocument.db.newDocument();
			Element e = d.createElement(SammaXml.ELEMENT.doc.tag);
			d.appendChild(e);
			e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); 
			e.setAttribute("xsi:noNamespaceSchemaLocation", "samma.xsd"); 
			
		}
	}

	public static File getDefaultFile() throws Exception {
		String s = "SammaAnalysis";
		File f = new File(s+".xml");
		int i=0;
		while (f.exists()) {
			i++;
			if (i > 10) throw new Exception("Cannot create a default Analysis Set Definition File.  Please open a valid file to proceed.");
			f = new File(s+i+".xml");
		}
		return f;
	}
	
	public AnalysisSetDefinitionFile() throws Exception {
		this(AnalysisSetDefinitionFile.getDefaultFile());
	}

	public AnalysisSetDefinition chooseAnalysisSetDefinition()
			throws IOException {
		AnalysisSetDefinition retasd = null;
		while (retasd == null) {
			System.out
					.println("Please enter the name of the Analysis Set that you would like to use.");
			String defval = (list.size() > 0) ? list.get(0).name() : "";
			for (Iterator<AnalysisSetDefinition> i = list.iterator(); i
					.hasNext();) {
				AnalysisSetDefinition asd = i.next();
				System.out.println(" * " + asd.name(20) + "\t" + asd.purpose());
			}
			System.out.print("[" + defval + "]: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String s = br.readLine().trim();
			if (s.equals(""))
				s = defval;
			retasd = map.get(s);
		}
		return retasd;
	}

}

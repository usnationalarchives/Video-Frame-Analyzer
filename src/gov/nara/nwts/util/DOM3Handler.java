package gov.nara.nwts.util;
import gov.nara.nwts.sammaGui.AnalyzeSamma;
import gov.nara.nwts.sammaGui.analysis.Analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSParserFilter;
import org.w3c.dom.traversal.NodeFilter;
/**
 * Custom parser handler that will divert the contents of certain tags to a function rather than instantiating those contents in a DOM object.
 * The SAMMA XML files contain a custom element for every frame of video.  The files are too large to process as a DOM tree.
 * As frame elements are discovered, a custom function is invoked to interpret the data from each frame.
 * NOTE: this class is in the util package, but it contains application-specific code.  Consider refactoring and moving this logic to the sammaGui package
 * @author TBrady
 *
 */
public class DOM3Handler implements DOMErrorHandler, LSParserFilter {
	/** The element name of each frame of video starts with the name "Sample" */
	Pattern patt;
	Pattern pattCode;
	boolean flag = false;
	AnalyzeSamma analyzeSamma;

	public DOM3Handler(AnalyzeSamma analyzeSamma) {
		this.analyzeSamma = analyzeSamma;
		patt = Pattern.compile("^Sample\\d+$");
		StringBuffer buf = new StringBuffer("IX");
		for (Iterator<Analysis> i = analyzeSamma.analyses.iterator(); i.hasNext();) {
			Analysis a = i.next();
			buf.append("|");
			buf.append(a.code);
		}
		pattCode = Pattern.compile("^(" + buf.toString() + ")=(.*)$");
	}

	public boolean handleError(DOMError arg0) {
		return false;
	}

	/** 
	 * For all tags SampleXXX, call invoke {@link #analyzeLine(String)}, do not add the item to the main DOM tree.
	 * The content of the Sample tags look like attribute values but the content is really name value pairs delimited in a text node
	 **/
	public short acceptNode(Node n) {
		if (n instanceof Element) {
			Element e = (Element) n;
			String s = e.getTagName();
			if (patt.matcher(s).matches()) {
				String line = e.getTextContent();
				analyzeLine(line);
				return LSParserFilter.FILTER_REJECT;
			}

		}
		return 0;
	}

	/** Determines the type events that will trigger an acceptance check */
	public int getWhatToShow() {
		return NodeFilter.SHOW_ELEMENT;
	}

	public short startElement(Element e) {
		return 0;
	}

	/** Tokenize the line to extract name/value pairs and pass them to the Analysis modules as a HashMap of name/value pairs*/
	public void analyzeLine(String line) {
		try {
			StringTokenizer st = new StringTokenizer(line);
			HashMap<String, String> map = new HashMap<String, String>();
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				Matcher mt = pattCode.matcher(tok);
				if (mt.matches()) {
					map.put(mt.group(1), mt.group(2));
				}
			}

			for (Iterator<Analysis> i = analyzeSamma.analyses.iterator(); i.hasNext();) {
				Analysis a = i.next();
				a.analyze(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}

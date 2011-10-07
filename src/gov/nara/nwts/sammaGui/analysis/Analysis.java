package gov.nara.nwts.sammaGui.analysis;

import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * Abstract representation of an analysis task that might be performed against a Samma XML file; the definition of the Analysis to be performed is contained within an XML tag.
 * Each Analysis object (and its subclasses) are instantiated from an XML file.
 * @see SammaXml for the definitions of the elements and attributes in use
 * @author TBrady
 *
 */
public abstract class Analysis {
	
	public Element root;
	
	int totalFail = 0;
	int totalWarn = 0;
	public String code;
	public String name;
	Range errRange;
	public AnalysisResult errbuf;

	int getFileFrameFail() {
		int tot = 0;
		for (Iterator<Range> i = errbuf.iterator(); i.hasNext();) {
			tot += i.next().getFrameCount();
		}
		return tot;
	}

	protected abstract boolean checkValid(int frame, double val);
	protected abstract boolean checkFatal(int frame, double val);
	protected abstract boolean evaluateRangeIsError(Range range);

	public abstract String getPassingCriteria();
	public abstract String getRangeCriteria();
	public abstract String getFatalCriteria();
	public abstract Element getElement(SammaXml.ELEMENT e);

	/** 
	 * Perform a check on a specific value for a frame of video
	 * @param map Map of name value pairs associated with a frame of video (may be used in subclasses)
	 * @param val A specific value against which a check will be peformed
	 * @return Indicator of whether or not a specific check passed
	 */
	boolean doCheck(HashMap<String, String> map, double val) {
		int frame = getFrame(map);
		boolean bValid = checkValid(frame, val);
		boolean bFatal = checkFatal(frame,val);
		//when analyzing averages, it is possible to pass validity but have a fatal reading
		if (bFatal) {bValid = false;}
		if (bValid) {
			if (errRange != null) {
				errbuf.add(errRange);
				errRange = null;
			}
		} else {
			if (errRange == null) {
				errRange = new Range(frame, val);
			} else {
				errRange.mark(frame, val);
			}
			if (bFatal) errRange.error = true;
		}
		return bValid;
	}

	/** Get the number of error frames for a file*/
	int getFileCount() {
		return getFileFrameFail();
	}

	/** At the end of processing for a given file, compute a summary of all of the error ranges for the file.  
	 */
	public void eof(SammaDocument doc) {
		if (errRange != null) {
			errRange.markLast();
			errbuf.add(errRange);
			errRange = null;
		}
		errbuf.evaluateFileValid(doc);
		totalWarn += errbuf.getRangeCount(false);
		totalFail += errbuf.getRangeCount(true);
	}

	/** Create a new AnalysisResult object to begin tracking error and warning frames*/
	public void clearBuf() {
		errbuf = new AnalysisResult(this);
	}

	/** get the frame number extracted from the name/value pairs for a frame of video*/
	int getFrame(HashMap<String, String> map) {
		return Integer.parseInt(map.get(SAMMACODES.IX.code));
	}

	/** Perform analysis on the name/value pairs associated with a single frame of video*/
	public boolean analyze(HashMap<String, String> map) {
		String s = map.get(code);
		if (s == null)
			return true;
		double d = Double.parseDouble(s);
		return doCheck(map, d);
	}

	/** Base constructor initializing the attributes common to all Analysis types*/
	Analysis(Element e) {
		this.root = e;
		this.code = e.getAttribute(SammaXml.ATTR.code.attr);
		this.name = e.getAttribute(SammaXml.ATTR.NAME.attr);
		errbuf = new AnalysisResult(this);
	}
	
}

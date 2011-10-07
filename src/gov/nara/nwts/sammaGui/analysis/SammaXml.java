package gov.nara.nwts.sammaGui.analysis;

/**
 * Class containing several enumeration representations of the tags and attributes used within the Analysis Set Defintiion File
 * @author TBrady
 *
 */
public class SammaXml {
	
/** 
 * Common attributes used by elements within the Analysis Set Definition File
 * @author TBrady
 *
 */
public enum ATTR {
	EQ, NEQ, LT, LTE, GT, GTE,
	NAME("name"), purpose, code,
	secs,
	skipLast,
	skipFirstSec,
	minLenSec,
	RETURN("return"),
	xpath,
	format,
	type;
	
	public String attr;
	ATTR() {
		this("");
		attr = super.name();
	}
	ATTR(String s) {
		attr = s;
	}
}

/** 
 * Elements comprising the Analysis Set Definition file
 * @author TBrady
 *
 */
public enum ELEMENT {
	doc,
	ANALYSIS_SET("analysis-set"),
	newAnalysis,
	analysis,
	average,
	checkValid,
	checkFatal,
	checkRangeError,
	checkFile,
	fileValue;
	
	String tag;
	ELEMENT() {
		this("");
		tag = super.name();
	}
	ELEMENT(String s) {
		tag = s;
	}
}

/**
 * Allowable values for the format attribute of the PassFailAnalysis
 * @author TBrady
 *
 */
public enum VAL_FORMAT {
	time;
}

/**
 * Enumeration of supported Analysis types
 * @author TBrady
 *
 */
public enum VAL_ANALYSIS {
	XmlAnalysis, AverageAnalysis, PassFailAnalysis;
}


}

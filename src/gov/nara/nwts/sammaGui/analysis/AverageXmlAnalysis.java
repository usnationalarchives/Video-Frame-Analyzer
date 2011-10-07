package gov.nara.nwts.sammaGui.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
 * Analysis rule that examines the average value of a name/value pair over a specified duration.
 * @author TBrady
 *
 */
public class AverageXmlAnalysis extends XmlAnalysis {
	/** List into which individual values will be stored so that a duration-based average can be computed. */
	ArrayList<Double> valset;
	int duration = 1;
	Element avg;

	/**
	 * The XML for an average analysis is very similar to an {@link XmlAnalysis} with the addition of a duration attribute.
	 * @param e
	 */
	public AverageXmlAnalysis(Element e) {
		super(e);
		avg = this.getElement(e, SammaXml.ELEMENT.average);
		if (avg != null) {
			Double d = getDouble(avg, SammaXml.ATTR.secs);
			if (d!=null){
				this.duration = d.intValue();
			}
		}
		valset = new ArrayList<Double>();
	}

	/** 
	 * Construct a new AverageAnalysis element
	 * @param d
	 */
	public AverageXmlAnalysis(Document d) {
		super(d);
		avg = d.createElement(SammaXml.ELEMENT.average.tag);
		root.insertBefore(avg, checkValid);
		valset = new ArrayList<Double>();
	}
	public String getInitText() {
		return duration + " second analysis ";
	}
	
	public Element getElement(SammaXml.ELEMENT elem) {
		if (elem == SammaXml.ELEMENT.average) { 
			return avg;
		} 
		return super.getElement(elem);
	}

	/** 
	 * Add a value to the value set and extract the current average value (for the specified duration), pass the average value to {@link XmlAnalysis#doCheck(HashMap, double)} 
	 */
	boolean doCheck(HashMap<String, String> map, double val) {
		valset.add(new Double(val));
		return super.doCheck(map, getAverageBySec(duration));
	}

	/** 
	 * Prepare the analysis object for the next file to be processed
	 */
	public void clearBuf() {
		valset.clear();
		super.clearBuf();
	}

	/**
	 * Compute an average value from the value set
	 * @param frames number of frames to use in computing the average
	 * @return the average value over the specified number of frames (or since the start of the file)
	 */
	double getAverage(int frames) {
		double sum = 0;
		int count = 0;
		for (int i = Math.max(0, valset.size() - frames); i < valset.size(); i++) {
			sum += valset.get(i).doubleValue();
			count++;
		}
		return (count == 0) ? 0 : sum / count;
	}

	/**
	 * Compute an average value from the value set
	 * @param sec number of seconds to use in computing the average
	 * @return the average value over the specified number of frames (or since the start of the file)
	 */
	double getAverageBySec(int sec) {
		return getAverage((int) Math.round(Range.FPS * sec));
	}

}

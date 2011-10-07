package gov.nara.nwts.sammaGui.analysis;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tracks information about the Ranges of error and warning frames for an Analsis, computes summary information about how a specific file performed in relation to a specific Analysis step.
 * @author TBrady
 *
 */
public 	class AnalysisResult extends ArrayList<Range>{
	private static final long serialVersionUID = 1L;
	
	public Analysis a;
	public AnalysisResult(Analysis a) {
		this.a = a;
	}
	/**
	 * Evaluate each  frame range identified during analysis to determine whether the range is a warning range or an error range.
	 * In some instances the determination of whether or not a range is an error range cannot be known until file processing is complete.  
	 * For instance, if errors are ignored in the last X seconds of a video or in the last range within a video, that determination cannot be known until the whole file has been analyzed.
	 * @return true if no error ranges exist (warning ranges are OK)
	 */
	public boolean evaluateRanges() {
		boolean success = true;
		for(Iterator<Range> i=iterator(); i.hasNext(); ){
			Range range = i.next();
			if (a.evaluateRangeIsError(range)){
				range.error = true;
				success = false;
			}
		}
		return success;		
	}
	/** 
	 * Indicates whether or not a file contains an error range.
	 * At this time, this is just a shallow alias for the {@link #evaluateRanges()} function
	 * Note: this could probably be optimized to not re-perform the check if it has already been done.
	 * */
	public boolean evaluateFileValid() {
		return evaluateRanges();
	}
	/** 
	 * Indicates whether or not a file contains an error range.
	 * Note: this could probably be optimized to not re-perform the check if it has already been done
	 * @param doc appears not to be used
	 * */
	public boolean evaluateFileValid(SammaDocument doc) {
		return evaluateRanges();
	}
	/**
	 * Returns a count of the number of warning or error ranges
	 * @param error if true, report only the count of error ranges; if false report the count of error and warning ranges
	 * @return the number of ranges that qualify as errors / warnings & errors
	 */
	public int getRangeCount(boolean error) {
		if (!error) return size();
		int err = 0;
		for(Iterator<Range> i=iterator(); i.hasNext(); ){
			if(i.next().error) {
				err++;
			}
		}
		return err;
	}
	
	/** 
	 * Generate a human readable summary of warning and error range counts.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getRangeCount(false));
		buf.append(" Warn / ");
		buf.append(getRangeCount(true));
		buf.append(" Err");
		return buf.toString();
	}
}
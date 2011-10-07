package gov.nara.nwts.sammaGui.analysis;
import java.text.NumberFormat;

/**
 * Class used to track the starting frame and ending frame in which an error or warning condition persists
 * @author TBrady
 *
 */
public class Range {
	public static final double FPS = 29.97;
	public static final int SPM = 60;
	public static final double FPM = 29.97 * 60;

	public static NumberFormat t7;
	public static NumberFormat t3;
	public static NumberFormat t2;
	static {
		t7 = NumberFormat.getIntegerInstance();
		t7.setGroupingUsed(false);
		t7.setMinimumIntegerDigits(7);
		t3 = NumberFormat.getIntegerInstance();
		t3.setMinimumIntegerDigits(3);
		t2 = NumberFormat.getIntegerInstance();
		t2.setMinimumIntegerDigits(2);	
	}

	public double max = -1;
	public double min = -1;
	public double sum = 0;
	public int start;
	public int end = -1;
	boolean last = false;
	/** Errors will initially be treated as warnings. Based on the range, an item
	may be classified an error. */
	public boolean error = false;

	/**
	 * Construct a range object based on the start of an error condition
	 * @param frame starting frame in which an error condition occurred (the ending frame will also be initialized with this value)
	 * @param val the invalid value that triggered the creation of this error range
	 */
	public Range(int frame, double val) {
		this.start = frame;
		this.end = frame;
		this.sum = val;
		this.max = val;
		this.min = val;
	}

	/** 
	 * Update a Range object to indicate the continuation of an error condition. 
	 * The end frame number will be updated.  Maximum and minimum values for the range will also be updated.
	 * A sum will also be computed in order to allow an average value to be reported for the Range.
	 * @param frame most recent frame in which an error condition occurred
	 * @param val the most recent error value discovered
	 */
	public void mark(int frame, double val) {
		sum += val;
		max = Math.max(max, val);
		min = Math.min(min, val);
		this.end = frame;
	}

	/** 
	 * Compute an average of the values occurring within a specific range
	 * @return the average error value reported for the duration of the range
	 */
	public double getAverage() {
		int fc = getFrameCount();
		return (fc == 0) ? 0 : sum/fc;
	}
	
	/**
	 * Note that the Range object includes the final video frame for the file being tested; many Analysis rules disregard error conditions that occur only in the final Range of frames.
	 */
	public void markLast() {
		last = true;
	}

	/**
	 * When displaying the details of a specific range, make note of whether the range includes the first frame, the last frame or every frame within the file
	 */
	public String getNote() {
		if (last && (start == 1)) {
			return "ALL";
		} else if (last) {
			return "END";
		} else if (start == 1) {
			return "START";
		}
		return "";
	}

	/**
	 * Generate a human readable text report of the Range
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(t7.format(getFrameCount()));
		buf.append(" frames: ");
		buf.append(getTime(start));
		if (start != end) {
			buf.append(" - ");
			buf.append(getTime(end));
		}
		buf.append(error ? "ERR " : "");
		buf.append(getNote());
		return buf.toString();
	}

	/**
	 * Compute the number of frames within a range
	 */
	public int getFrameCount() {
		return end - start + 1;
	}

	public String getStartTimeCode() {
		return getTimeCode(start);
	}
	public String getEndTimeCode() {
		return getTimeCode(end);
	}
	
	/**
	 * Generate a readable report of a specific time code within a range.
	 * @param frame the number of the frame to report
	 * @return a textual representation of the time code for the frame including the time in minutes:seconds.frame and the actual frame number normalized to a fixed width.
	 */
	public static String getTimeCode(int frame) {
		StringBuffer buf = new StringBuffer();
		double minx = frame/FPM;
		int min = (int) minx;
		double secx = (minx - min) * SPM;
		int sec = (int)secx;
		int fract = (int)((secx - sec) * FPS);
		buf.append(t3.format(min));
		buf.append(":");
		buf.append(t2.format(sec));
		buf.append(".");
		buf.append(t2.format(fract));
		return buf.toString();
	}
	public static String getTime(int frame) {
		StringBuffer buf = new StringBuffer();
		buf.append(getTimeCode(frame));
		buf.append(" (" + t7.format(frame) + ") ");
		return buf.toString();
	}

}

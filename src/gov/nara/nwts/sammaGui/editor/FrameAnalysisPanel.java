package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.SammaXml;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Element;

/**Editor for an XmlAnalysis object or AverageXmlAnalysis object*/
class FrameAnalysisPanel extends AnalysisPanel {
	private static final long serialVersionUID = 1L;
	EqualsPanel checkValid;
	EqualsPanel checkFatal;
	AveragePanel ap;
	RangePanel rp;
	
	FrameAnalysisPanel(AnalysisSetEditor ase, Analysis a, boolean avg) {
		super(ase, a, true);
		if (avg) {
			ap = new AveragePanel(a,a.getElement(SammaXml.ELEMENT.average));
			box.add(ap);
		}
		checkValid = new EqualsPanel(ase, a.getElement(SammaXml.ELEMENT.checkValid));
		checkValid.setBorder(BorderFactory.createTitledBorder("Condition that must be true in order to Pass (excluding special ranges"));
		box.add(checkValid);
		rp = new RangePanel(ase,a,a.getElement(SammaXml.ELEMENT.checkRangeError));
		box.add(rp);
		checkFatal = new EqualsPanel(ase, a.getElement(SammaXml.ELEMENT.checkFatal));
		checkFatal.setBorder(BorderFactory.createTitledBorder("Failure Condition regardless of ranges"));
		box.add(checkFatal);
	}

	public void savePanel(AnalysisSetDefinition asd) {
		if (ap != null) ap.savePanel(asd);
		checkValid.savePanel(asd);
		rp.savePanel(asd);
		checkFatal.savePanel(asd);
		setAttribute(root, SammaXml.ATTR.type, (ap==null) ? SammaXml.VAL_ANALYSIS.XmlAnalysis.toString() : SammaXml.VAL_ANALYSIS.AverageAnalysis.toString());
		super.savePanel(asd);
	}	

	public class AveragePanel extends DefaultXmlablePanel {
		private static final long serialVersionUID = 1L;
		JTextField average;
		
		AveragePanel(Analysis a, Element e) {
			super(e);
			average  = addField(
					ase,
					this, 
					"Duration (seconds)", 
					"",
					getAttribute(a.getElement(SammaXml.ELEMENT.average),SammaXml.ATTR.secs));
			add(average);
			setBorder(BorderFactory.createTitledBorder("Duration in which averages will be examined (seconds)"));
		}
		public void savePanel(AnalysisSetDefinition asd) {
			setAttribute(root, SammaXml.ATTR.secs, average.getText() );			
		}	
	}

	public class RangePanel extends DefaultXmlablePanel {
		private static final long serialVersionUID = 1L;
		JCheckBox skipLast;
		JTextField skipFirstSec;
		JTextField minLenSec;
		
		RangePanel(AnalysisSetEditor ase, Analysis a, Element e) {
			super(e);
			setLayout(new GridLayout(3,2));
			setBorder(BorderFactory.createTitledBorder("Exclude the following ranges when determining PASS vs. FAIL."));
			
			Element checkRangeError = a.getElement(SammaXml.ELEMENT.checkRangeError);
			skipLast = new JCheckBox("Skip Last Range", getAttribute(checkRangeError,SammaXml.ATTR.skipLast).equals("true"));
			skipLast.setToolTipText("When reporting errors, ignore the final range of frames");
			add(new JLabel());
			add(skipLast);
			skipFirstSec  = addField(
				ase,
				this, 
				"Skip first sec(s)", 
				"If set, no ranges in the first X seconds will be flagged as error ranges",
				getAttribute(checkRangeError,SammaXml.ATTR.skipFirstSec));
			minLenSec  = addField(
				ase,
				this, 
				"Min range sec(s)", 
				"If set, ranges of shorter duration than this value will not be flagged as error ranges",
				getAttribute(checkRangeError,SammaXml.ATTR.minLenSec));
		}
		public void savePanel(AnalysisSetDefinition asd) {
			setAttribute(root, SammaXml.ATTR.skipLast, skipLast.isSelected() ? "true" : "false");
			setAttribute(root, SammaXml.ATTR.skipFirstSec, skipFirstSec.getText());
			setAttribute(root, SammaXml.ATTR.minLenSec, minLenSec.getText());
		}	
	}
}


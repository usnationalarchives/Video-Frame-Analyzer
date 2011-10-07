package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.PassFailXmlAnalysis;
import gov.nara.nwts.sammaGui.analysis.SammaXml;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;


/** Editor for a PassFailXmlEditor Panel*/
class FileAnalysisPanel extends AnalysisPanel {
	private static final long serialVersionUID = 1L;
	JTextField xpath;
	JCheckBox cb;
	EqualsPanel cond;
	
	FileAnalysisPanel(AnalysisSetEditor ase, Analysis a) {
		super(ase, a, false);
		JPanel p = new JPanel();
		box.add(p);
		if (a instanceof PassFailXmlAnalysis) {
			PassFailXmlAnalysis pa = (PassFailXmlAnalysis)a;
			xpath = new JTextField(pa.xpath,35);
			xpath.setBorder(BorderFactory.createTitledBorder("Document attribute (XPATH)"));
			xpath.setToolTipText("Example: /SammaSolo/Metadata/Log/SampleCount.\nConsult w3c.org for details on XPATH Expressions");
			xpath.getDocument().addDocumentListener(ase);
			p.add(xpath);
			cb = new JCheckBox("Report as Timecode",getAttribute(pa.fileValue,SammaXml.ATTR.format).equals("time"));
			cb.addActionListener(ase);
			p.add(cb);
			cond = new EqualsPanel(ase, pa.checkFile);
			box.add(cond);
		}
	}
	
	public void savePanel(AnalysisSetDefinition asd) {
		cond.savePanel(asd);
		if (a instanceof PassFailXmlAnalysis) {
			PassFailXmlAnalysis pa = (PassFailXmlAnalysis)a;
			setAttribute(pa.fileValue, SammaXml.ATTR.xpath, xpath.getText());
			setAttribute(pa.fileValue, SammaXml.ATTR.format, cb.isSelected() ? SammaXml.VAL_FORMAT.time.toString() : "");
		}
		setAttribute(root, SammaXml.ATTR.type, SammaXml.VAL_ANALYSIS.PassFailAnalysis.toString());
		super.savePanel(asd);
	}	

}

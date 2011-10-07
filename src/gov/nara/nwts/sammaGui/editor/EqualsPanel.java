package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.SammaXml;

import java.awt.GridLayout;

import javax.swing.JTextField;

import org.w3c.dom.Element;

/** Re-usable, Panel for setting comparison attributes within Analysis objects*/
public class EqualsPanel extends DefaultXmlablePanel {
	private static final long serialVersionUID = 1L;
	JTextField eq;
	JTextField neq;
	JTextField gt;
	JTextField gte;
	JTextField lt;
	JTextField lte;
	
	EqualsPanel(AnalysisSetEditor ase, Element e) {
		super(e);
		setLayout(new GridLayout(2,6));
		eq  = addField(
			ase,
			this, 
			"Equals", 
			"Test value is equal to",
			getAttribute(e,SammaXml.ATTR.EQ));
		lt  = addField(
			ase,
			this, 
			"Less Than", 
			"Test value is less than",
			getAttribute(e,SammaXml.ATTR.LT));
		gt  = addField(
			ase,
			this, 
			"Greater Than", 
			"Test value is greater than",
			getAttribute(e,SammaXml.ATTR.GT));
		neq = addField(
			ase,
			this, 
			"Not Equal", 
			"Test value is not equal to",
			getAttribute(e,SammaXml.ATTR.NEQ));
		lte = addField(
			ase,
			this, 
			"Less Than/Equal", 
			"Test value is less than or equal to",
			getAttribute(e,SammaXml.ATTR.LTE));
		gte = addField(
			ase,
			this, 
			"Grtr Than/Equal", 
			"Test value is greater than or equal to",
			getAttribute(e,SammaXml.ATTR.GTE));
	}

	public void savePanel(AnalysisSetDefinition asd) {
		setAttribute(root, SammaXml.ATTR.EQ, eq.getText());
		setAttribute(root, SammaXml.ATTR.NEQ, neq.getText());
		setAttribute(root, SammaXml.ATTR.LT, lt.getText());
		setAttribute(root, SammaXml.ATTR.LTE, lte.getText());
		setAttribute(root, SammaXml.ATTR.GT, gt.getText());
		setAttribute(root, SammaXml.ATTR.GTE, gte.getText());
	}
}

package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.SAMMACODES;
import gov.nara.nwts.sammaGui.analysis.SammaXml;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Common functions for a Panel representing an Analysis object*/
public class AnalysisPanel extends DefaultXmlablePanel {
	private static final long serialVersionUID = 1L;

	Analysis a;
	Box box;
	JTextField name;
	JComboBox code;
	JCheckBox cb;
	AnalysisSetEditor ase;
	
	public AnalysisPanel(AnalysisSetEditor ase, Analysis a, boolean showCode) {
		super(a.root);
		this.ase = ase;
		cb = new JCheckBox("Remove");
		cb.addActionListener(ase);
		cb.setToolTipText("Select this box to remove this Analysis Step from the Analysis Set.");
		box = Box.createVerticalBox();
		add(box);
		JPanel p = new JPanel();
		box.add(p);
		if (showCode) {
			code = new JComboBox(SAMMACODES.values());
			code.addActionListener(ase);
			p.add(code);
			code.setBorder(BorderFactory.createTitledBorder("Samma Code"));
			if (a!=null) {
				for(int i=0; i<code.getItemCount(); i++) {
					SAMMACODES scode = (SAMMACODES)code.getItemAt(i);
					if (a.code.equals(scode.code)) {
						code.setSelectedIndex(i);
						break;
					}
				}
			}
		}
		name = new JTextField(20);
		name.getDocument().addDocumentListener(ase);
		if (a!=null) name.setText(a.name);
		name.setBorder(BorderFactory.createTitledBorder("Test Name"));
		p.add(name);
		p.add(cb);
		this.a = a;
	}
	
	public void savePanel(AnalysisSetDefinition asd) {
		asd.root.appendChild(root);
		if (code !=null) {
			SAMMACODES sc = (SAMMACODES)code.getSelectedItem();
			setAttribute(root, SammaXml.ATTR.code, sc.code);
			a.code = sc.code;
		}
		setAttribute(root, SammaXml.ATTR.NAME, name.getText());
		a.name = name.getText();
	}

	public boolean isDeleted() {
		return cb.isSelected();
	}
	

}

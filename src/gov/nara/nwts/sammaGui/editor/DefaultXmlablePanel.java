package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.SammaXml;

import java.awt.FlowLayout;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.w3c.dom.Element;

/** Common functions for each of the Panels that are built and saved as XML*/
public abstract class DefaultXmlablePanel extends JPanel implements XmlablePanel {
	private static final long serialVersionUID = 1L;
	Element root;
	
	public DefaultXmlablePanel(Element e) {
		if (e==null)throw new Error("null");
		root = e;
	}
	
	public JPanel createPanel() {
		return this;
	}

	public Element getPanelElement() {
		return root;
	}

	public abstract void savePanel(AnalysisSetDefinition asd);
	static String getAttribute(Element e, SammaXml.ATTR attr) {
		if (e==null) return "";
		return e.getAttribute(attr.attr);
	}
	
	
	static JTextField addField(AnalysisSetEditor ase, JPanel par, String label, String tooltip, String val) {
		par.add(new JLabel(label+": ", JLabel.RIGHT));
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		par.add(p);
		JFormattedTextField tf = new JFormattedTextField(NumberFormat.getNumberInstance());
		tf.setInputVerifier(new MyInputVerifier(ase));
		tf.setColumns(8);
		tf.setText(val);
		p.add(tf);
		tf.setToolTipText(tooltip);
		tf.getDocument().addDocumentListener(ase);
		return tf;
	}

	public void setAttribute(Element e, SammaXml.ATTR attr, String val) {
		if (val == null) {
			e.removeAttribute(attr.attr);
		} else if (val.trim().equals("")) {
			e.removeAttribute(attr.attr);			
		} else {
			e.setAttribute(attr.attr, val.trim());
		}
	}
}

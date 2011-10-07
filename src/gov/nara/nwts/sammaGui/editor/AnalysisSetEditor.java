package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.AnalyzeSammaGui;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinitionFile;
import gov.nara.nwts.sammaGui.analysis.NewAnalysis;
import gov.nara.nwts.sammaGui.analysis.SammaXml;
import gov.nara.nwts.util.HtmlDocument;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**Screen used to edit an AnalysisSet*/
public class AnalysisSetEditor extends JPanel implements ActionListener, DocumentListener {
	private static final long serialVersionUID = 1L;
	AnalyzeSammaGui asg;
	AnalysisSetDefinition asd;
	AnalysisSetDefinitionFile asdf;
	JButton delete;
	JButton save;
	JButton add;
	JButton next;
	JButton prev;
	JButton cancel;
	Document d;
	Box box;
	JTextField name;
	JTextArea purpose;
	JTabbedPane tabs;
	
	public AnalysisSetEditor(AnalyzeSammaGui asg, AnalysisSetDefinition asd, AnalysisSetDefinitionFile asdf) throws SAXException, IOException {
		super(new BorderLayout());
		this.asg = asg;
		this.asd = asd;
		this.asdf = asdf;
		this.d = asd.root.getOwnerDocument();
		JPanel p = new JPanel();
		add(p, BorderLayout.SOUTH);
		add = new JButton("Add Step");
		add.addActionListener(this);
		prev = new JButton("Move Before");
		prev.addActionListener(this);
		next = new JButton("Move After");
		next.addActionListener(this);
		save = new JButton("Save");
		save.addActionListener(this);
		delete = new JButton("Delete Analysis Set");
		delete.addActionListener(this);
		cancel = new JButton("Close");
		cancel.addActionListener(this);
		p.add(prev);
		p.add(next);
		p.add(add);
		p.add(save);
		p.add(delete);
		p.add(cancel);

		box = Box.createVerticalBox();
		add(new JScrollPane(box), BorderLayout.CENTER);
		p = new JPanel();
		p.add(new JLabel("File: "+asdf.f.getAbsolutePath()));
		box.add(p);
		p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Analysis Set Name"));
		box.add(p);
		name = new JTextField(asd.name(), 40);
		p.add(name);
		p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Analysis Set Purpose"));
		box.add(p);
		purpose = new JTextArea(asd.purpose(), 4, 50);
		purpose.setLineWrap(true);
		p.add(purpose);
		tabs = new JTabbedPane();
		tabs.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				setNav();
			}
		});
		box.add(tabs);
		for(Analysis a: asd.getAnalysisSet()) {
			tabs.add(new TabbedAnalysisPanel(this, a),a.name);
		}
		save.setEnabled(false);
		setNav();
	}
	
	public void setNav() {
		prev.setEnabled(tabs.getSelectedIndex()!=0);
		next.setEnabled(tabs.getSelectedIndex()!=tabs.getTabCount()-1);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == cancel) {
			if (save.isEnabled()){
				if (JOptionPane.showConfirmDialog(asg, "Any unsaved changes will be lost.  Click YES to proceed") == JOptionPane.CANCEL_OPTION){
					return;
				}
			}
		} else if (arg0.getSource() == prev) {
			Component c = tabs.getSelectedComponent();
			String title = tabs.getTitleAt(tabs.getSelectedIndex());
			int i = tabs.getSelectedIndex() - 1;
			tabs.remove(c);
			tabs.add(c, title, i);
			tabs.setSelectedIndex(i);
			changeMade();
			return;
		} else if (arg0.getSource() == next) {
			Component c = tabs.getSelectedComponent();
			String title = tabs.getTitleAt(tabs.getSelectedIndex());
			int i = tabs.getSelectedIndex() + 1;
			tabs.remove(c);
			tabs.add(c, title, i);
			tabs.setSelectedIndex(i);
			changeMade();
			return;
		} else if (arg0.getSource() == add) {
			tabs.add(new TabbedAnalysisPanel(this, new NewAnalysis(asd.root.getOwnerDocument())), "New");
			tabs.setSelectedIndex(tabs.getTabCount()-1);
			changeMade();
			return;
		} else if (arg0.getSource() == delete) {
			try {
				if (JOptionPane.showConfirmDialog(asg, "Are you certain that you wish to remove this Analysis Set?  Click YES to proceed") == JOptionPane.CANCEL_OPTION){
					return;
				}
				asd.root.getParentNode().removeChild(asd.root);
				HtmlDocument.serialize(d, asdf.f);
				asg.setAnalysisSetDefs(true);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(asg, "Error saving the Analysis Set Definition File: " +e.getMessage());
				e.printStackTrace();
			}
		} else if (arg0.getSource() == save) {
			try {
				asd.root.setAttribute("name", name.getText());
				asd.root.setAttribute("purpose", purpose.getText());
				NodeList nl = asd.root.getElementsByTagName(SammaXml.ELEMENT.analysis.toString());
				for(int i=0; i<nl.getLength(); i++){
					asd.root.removeChild(nl.item(i));
				}
				int i=0;
				for(int ii=0; ii<tabs.getComponentCount(); ii++) {
					Component c = tabs.getComponentAt(ii);
					if (c instanceof AnalysisPanel) {
						AnalysisPanel ap = (AnalysisPanel)c;
						if (ap.isDeleted()) {
							tabs.remove(ap);
						} else { 
							ap.savePanel(asd);
							tabs.setTitleAt(i, ap.a.name);
							i++;
						}
					}
				}
				HtmlDocument.serialize(d, asdf.f);
				asg.setAnalysisSetDefs(true);
				save.setEnabled(false);
				return;
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(asg, "Error saving the Analysis Set Definition File: " +e.getMessage());
				e.printStackTrace();
			}
		} else {
			changeMade();
			return;
		}
		asg.tabs.remove(this);
		asg.tabs.setEnabledAt(0, true);
	}

	public void changeMade() {
		save.setEnabled(true);
		setNav();
	}

	public void changedUpdate(DocumentEvent de) {
		changeMade();
	}

	public void insertUpdate(DocumentEvent de) {
		changeMade();
	}

	public void removeUpdate(DocumentEvent de) {
		changeMade();
	}
}

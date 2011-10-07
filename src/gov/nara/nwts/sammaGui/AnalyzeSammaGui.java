package gov.nara.nwts.sammaGui;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinitionFile;
import gov.nara.nwts.sammaGui.analysis.SammaXml;
import gov.nara.nwts.sammaGui.editor.AnalysisSetEditor;
import gov.nara.nwts.util.FileSelect;
import gov.nara.nwts.util.HtmlDocument;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Main GUI driver for the Video Frame Analysis Tool ("SAMMA Analyzer").
 * This application was originally created by Terry Brady in NARA's Digitization Services Branch.
 * @author TBrady
 *
 */
public class AnalyzeSammaGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	AnalyzeSamma analyzeSamma;
	JTextField asdTextField;
	public JTabbedPane tabs;
	
	DefaultTableModel tm;
	JTable jt;
	JTextArea summary;
	JFormattedTextField num;
	JButton run;
	String asdfName = "";
	public Preferences preferences;
	JTextField input;
	JButton edit;
	JButton add;
	JButton dup;
	JButton up;
	JButton down;
	
	/** Get the selected Analysis Set Definition File (as a file)*/
	public File getAsdFile() {
		return new File(asdTextField.getText());
	}
	/** Get the directory of the Analysis Set Definition File (as a file)*/
	public File getAsdDir() {
		File tf = getAsdFile();
		return (tf.exists()) ? (new File(tf.getAbsolutePath())).getParentFile() : new File(System.getProperty("user.home"));
	}
	
	/** After a selection change has been made on the home screen, determine whether or not to enable the "run" button*/
	public void check() {
		run.setEnabled(false);
		if (jt.getSelectedRow() == -1) return;
		String s = input.getText().trim();
		if (s.equals("")) return;
		File f = new File(s);
		run.setEnabled(f.exists());
	}
	
	
	
	public AnalyzeSammaGui() throws IOException, SAXException  {
		super("NARA Video Frame Analyzer");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	preferences = Preferences.userNodeForPackage(getClass());
    	input = new JTextField(preferences.get("root", ""), 50);
    	input.setEditable(false);
    	input.setBorder(BorderFactory
				.createTitledBorder("XML Input Directory"));
    	input.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent arg0) {
				preferences.put("root", input.getText().trim());
				AnalyzeSammaGui.this.check();
			}
		});
    	String root = preferences.get("rootdef", AnalyzeSamma.defAnalysis);
		JMenuBar jmb = new JMenuBar();
		JMenu menu = new JMenu("Video Frame Analyzer");
		jmb.add(menu);
		JMenuItem jmi = new JMenuItem("About NARA Video Frame Analyzer");
		menu.add(jmi);
		jmi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(AnalyzeSammaGui.this, 
				"This application was created by the Digitization Services Branch of \n" +
				"the National Archives and Records Administration (NARA).\n" +
				"Please see the accompanying README file for more information." +
				"\n\nContact: OpenGov@nara.gov", 
				"About File Analyzer", JOptionPane.INFORMATION_MESSAGE);
			}});
		setLayout(new BorderLayout());
		add(jmb, BorderLayout.NORTH);
		JPanel tp = new JPanel(new BorderLayout());
		JPanel p = new JPanel();
		tp.add(p, BorderLayout.NORTH);
		p.add(input);
		JButton jb = new JButton("...");
		p.add(jb);
		jb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new FileSelect(AnalyzeSammaGui.this, input, "Please select a directory SAMMA XML files.");			
			}
		});
		analyzeSamma = new AnalyzeSamma(root);
		tabs = new JTabbedPane();
		add(tabs, BorderLayout.CENTER);
		JPanel mp = new JPanel(new BorderLayout());
		tabs.add(mp, "Choose Analysis");
		p = new JPanel();
		tp.add(p, BorderLayout.SOUTH);
		p.setBorder(BorderFactory.createTitledBorder("Analysis Set Definition File"));
		mp.add(tp, BorderLayout.NORTH);
		asdTextField = new JTextField(root, 50);
		p.add(asdTextField);
		asdTextField.setEditable(false);
		JButton b = new JButton("...");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new ASImporter(AnalyzeSammaGui.this);
			}});
		p.add(b);
			
		String[] cols = {"profile","description"};
		tm = new DefaultTableModel(cols, 6);
		jt = new JTable(tm);
		jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jt.setPreferredScrollableViewportSize(new Dimension(400,200));
		
		mp.add(new JScrollPane(jt), BorderLayout.CENTER);
		
		parseAnalysisSetDefs(new File(root));
		
		JPanel bp = new JPanel(new BorderLayout());
		mp.add(bp, BorderLayout.SOUTH);
		
		JPanel ep = new JPanel();
		bp.add(ep, BorderLayout.NORTH);
		up = new JButton("Move Up");
		up.addActionListener(this);
		ep.add(up);
		down = new JButton("Move Down");
		down.addActionListener(this);
		ep.add(down);
		edit = new JButton("Edit Analysis Set");
		edit.addActionListener(this);
		ep.add(edit);
		dup = new JButton("Duplicate Analysis Set");
		dup.addActionListener(this);
		ep.add(dup);
		add = new JButton("Add Analysis Set");
		add.addActionListener(this);
		ep.add(add);
		summary = new JTextArea(20,60);
		bp.add(new JScrollPane(summary), BorderLayout.CENTER);

		jt.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				AnalyzeSammaGui.this.setSummary();
			}
		});

		JPanel bbp = new JPanel();
		bp.add(bbp,BorderLayout.SOUTH);
		bbp.add(new JLabel("Num files to prcoess (blank for all): "));
		NumberFormat nf = NumberFormat.getIntegerInstance();
		num = new JFormattedTextField(nf);
		num.setColumns(5);
		bbp.add(num);
		run = new JButton("Run Analysis");
		run.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				analyzeSamma.sw = new ASSwingWorker(AnalyzeSammaGui.this);
				analyzeSamma.sw.execute();
			}	
			
		});
		bbp.add(run);
		setSummary();
		check();
		pack();
		setVisible(true);
	}
	
	/** Force the GUI to re-initialize the Analysis Set Definition displays*/
	public void setAnalysisSetDefs() {
		setAnalysisSetDefs(true);
	}
	/** Refresh the Analysis Set Definition displays
	 * @param force force the definition file to be re-read
	 * */
	public void setAnalysisSetDefs(boolean force) {
		if (force) {
			asdfName = "";
			analyzeSamma.asdfName = "";
		}
		File f = getAsdFile();
		if (!f.exists()) {
			//JOptionPane.showMessageDialog(this,"File does not exist");	
		} else if (!asdfName.equals(f.getAbsolutePath())) {
			parseAnalysisSetDefs(f);
			setSummary();
			asdfName = f.getAbsolutePath();
		}
	}
	
	/** Parse the Analysis Set Definition File and build each Analysis definition, update the table of Analysis Set Defintions*/
	public void parseAnalysisSetDefs(File f) {
		try {
			analyzeSamma.setAnalyzisDefinitionFile(f);
			tm.setRowCount(0);
			if (summary!=null) summary.setText("");
			for(Iterator<AnalysisSetDefinition>i=analyzeSamma.asdf.list.iterator(); i.hasNext(); ){
				AnalysisSetDefinition asd = i.next();
				Vector<String> v= new Vector<String>();
				v.add(asd.name());
				v.add(asd.purpose());
				tm.addRow(v);
			}
			if (tm.getRowCount() > 0) {
				jt.setRowSelectionInterval(0, 0);
			}
		} catch (SAXException e) {
			JOptionPane.showMessageDialog(this,e.getMessage());	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,e.getMessage());	
		} 
	}
	
	/** Update the summary field to reflect the chosen Analysis Set Definition, update the editing controls for the Analysis Set Definition editor*/
	public void setSummary()  {
		int sel = jt.getSelectedRow();
		try {
			if (sel == -1) {
				summary.setText("");
				analyzeSamma.setAnalysisSetDefinition(null);
				edit.setEnabled(false);
				dup.setEnabled(false);
				up.setEnabled(false);
				down.setEnabled(false);
			} else {
				analyzeSamma.setAnalysisSetDefinition(analyzeSamma.asdf.list.get(sel));
				if (analyzeSamma.analyses != null) {
					summary.setText(analyzeSamma.analyses.getSummary());
				}
				edit.setEnabled(true);
				dup.setEnabled(true);
				up.setEnabled(sel!=0);
				down.setEnabled(sel!=tm.getRowCount()-1);
			}
			check();
		} catch (SAXException e) {
			JOptionPane.showMessageDialog(this,e.getMessage());	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,e.getMessage());	
		}
	}
	
	public static void main(String[] args) {
		try {
			new AnalyzeSammaGui();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	/** Controls the actions of each of the Analysis Set Definition Editing buttons*/
	public void actionPerformed(ActionEvent arg0) {
		int currow = jt.getSelectedRow();
		String title = "";
		AnalysisSetDefinition asd = null;
		AnalysisSetDefinitionFile asdf;
		try {
			asdf = (analyzeSamma.asdf != null) ? analyzeSamma.asdf : new AnalysisSetDefinitionFile();
			Element asdfroot = asdf.d.getDocumentElement();
			NodeList nl = asdfroot.getElementsByTagName(SammaXml.ELEMENT.ANALYSIS_SET.toString());
			
			if (arg0.getSource() == add) {
				title = "Add [New]";
				asd = new AnalysisSetDefinition(asdf.d, "new");
			} else if (arg0.getSource() == dup) {
				title = "Duplicate ["+analyzeSamma.asd.name()+"]";
				asd = new AnalysisSetDefinition(asdf.d, analyzeSamma.asd.root);
			} else if (arg0.getSource() == edit) {
				title = "Edit ["+analyzeSamma.asd.name()+"]";
				asd = analyzeSamma.asd;
			} else if (arg0.getSource() == up) {
				Element cur = (Element)asdfroot.removeChild(nl.item(currow));
				asdfroot.insertBefore(cur, nl.item(currow-1));
				refreshTable();
				jt.setRowSelectionInterval(currow-1, currow-1);
				save();
				return;
			} else if (arg0.getSource() == down) {
				asd = analyzeSamma.asd;
				if (currow+2 > jt.getRowCount()) {
					asdfroot.appendChild(nl.item(currow));
				} else {
					asdfroot.insertBefore(nl.item(currow), nl.item(currow+2));					
				}
				refreshTable();
				jt.setRowSelectionInterval(currow+1, currow+1);
				save();
				return;
			} else {
				return;
			}
			AnalysisSetEditor p = new AnalysisSetEditor(this,asd, asdf);
			tabs.add(p, title);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
			return;
		} 
		tabs.setEnabledAt(0,false);
		tabs.setSelectedIndex(tabs.getTabCount()-1);
	}
	
	/** Refresh the table of Analysis Set Defintions*/
	public void refreshTable() {
		tm.setRowCount(0);
		NodeList nl = analyzeSamma.asdf.d.getElementsByTagName(SammaXml.ELEMENT.ANALYSIS_SET.toString());
		for(int i = 0; i< nl.getLength(); i++) {
			Element e = (Element)nl.item(i);
			Vector<String> v = new Vector<String>();
			v.add(e.getAttribute("name"));
			v.add(e.getAttribute("purpose"));
			tm.addRow(v);
		}
		jt.repaint();
	}
	
	/** Save the Analysis Set Definition File after making changes*/
	public void save() {
		try {
			HtmlDocument.serialize(analyzeSamma.asdf.d, analyzeSamma.asdf.f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
}

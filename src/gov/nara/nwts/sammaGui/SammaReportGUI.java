package gov.nara.nwts.sammaGui;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisResult;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.Range;
import gov.nara.nwts.util.BrowserSwingWorker;
import gov.nara.nwts.util.TableSaver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
/**
 * GUI panel displaying the results of an analysis; this table details each of the files that were processed on an analysis by analysis basis.
 * Once a file is selected, the detailed error ranges will appear in the error range table.
 * @author TBrady
 *
 */
public class SammaReportGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	AnalyzeSamma analyzeSamma;
	DefaultTableModel tm;
	JTable jt;
	HashMap<File,Integer> rowMatrix;
	JTable rangeJt;
	DefaultTableModel rangeTm;
	JComboBox acombo;
	JComboBox errcombo;
	ASSwingWorker asw;
	TableRowSorter<TableModel> sorter;
	JTextField tfile;
	JTextField tpdf;
	AnalysisSetDefinition asd;
	JButton save;
	JButton cancel;
	JButton bfile;
	JButton bpdf;

	public static final String ALL = "ALL Tests";
	public static final String RERROR = "Error";
	public static final String RWARN = "";
	
	String[] rcols = {"Analysis Step","Frame Count","Start Frame","Start Time","End Frame", "End Time","Average","Min","Max","Error","Note"};
	public enum ERR {
		ALL_RANGES,
		ERROR_RANGES,
		WARNING_RANGES;
	}
	
    public SammaReportGUI(AnalyzeSamma analyzeSamma, ASSwingWorker asw) {
    	super(new BorderLayout());
    	Box box = Box.createVerticalBox();
    	add(box, BorderLayout.CENTER);
    	errcombo = new JComboBox();
    	for(ERR err: ERR.values()) {
    		errcombo.addItem(err);
    	}
    	errcombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SammaReportGUI.this.setFilter();
			}
		});
    	acombo = new JComboBox();
    	acombo.addItem(ALL);
    	acombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SammaReportGUI.this.setFilter();
			}
		});
    	rowMatrix = new HashMap<File,Integer>();
    	this.asw = asw;
		this.analyzeSamma = analyzeSamma;
		this.asd = analyzeSamma.asd;
		ArrayList<File> files = asw.listFiles();
		if (analyzeSamma.getMaxFiles() > 0) {
			while(files.size() > analyzeSamma.getMaxFiles()) {
				files.remove(files.size()-1);
			}
		}
		Vector<String> cols = new Vector<String>();
		cols.add("File");
		cols.add("Summary");
		for(Analysis a: analyzeSamma.analyses) {
			cols.add(a.name);
			acombo.addItem(a.name);
		}
		cols.add("Notes");
		tm = new DefaultTableModel(cols, 0);
		rangeTm = new DefaultTableModel(rcols,0);
		rangeJt = new JTable(rangeTm) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		
		sorter = new TableRowSorter<TableModel>(rangeTm);
		rangeJt.setRowSorter(sorter);

		Vector<Object> vals1 = new Vector<Object>();
		Vector<Object> vals2 = new Vector<Object>();
		Vector<Object> vals3 = new Vector<Object>();
		vals1.add("Frames Passing Criteria");
		vals2.add("Range Criteria");
		vals3.add("Frame Fail Criteria");
		vals1.add("");
		vals2.add("");
		vals3.add("");
		for(Analysis a: analyzeSamma.analyses) {
			vals1.add(a.getPassingCriteria());
			vals2.add(a.getRangeCriteria());
			vals3.add(a.getFatalCriteria());
		}
		tm.addRow(vals1);
		tm.addRow(vals2);
		tm.addRow(vals3);
		
		for(File f: files) {
			rowMatrix.put(f,tm.getRowCount());
			Vector<Object> vals = new Vector<Object>();
			vals.add(f);
			vals.add("--");
			for(Analysis a: analyzeSamma.analyses) {
				a.toString();
				vals.add("--");
			}
			vals.add("");
			tm.addRow(vals);
		}
		jt = new JTable(tm) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int col) {
				return col == tm.getColumnCount()-1;
			}
			
		
			/**
			 * Update the error range report table to correspond with the selected file
			 */
			public void valueChanged(ListSelectionEvent e) {
				try {
					int row = this.getSelectedRow();
					if (row < 3) {
						return;
					}
					if (row < 0) return;
					File f = (File)tm.getValueAt(row, 0);
					tfile.setText(f == null ? "" : f.getAbsolutePath());
					bfile.setEnabled(f!=null);
					File pf = getPdf(f);
					tpdf.setText(pf == null ? "" : pf.getAbsolutePath());
					bpdf.setEnabled(pf!=null);
					Vector<AnalysisResult> res = SammaReportGUI.this.asw.results.get(f);
					if (res != null) {
					rangeTm.setRowCount(0);
					for(AnalysisResult ar: res) {
						if (ar == null) continue;
						for(Iterator<Range> i=ar.iterator(); i.hasNext();){
							Vector<Object> val = new Vector<Object>();
							Range r = i.next();
							val.add(ar.a.name);
							val.add(r.getFrameCount());
							val.add(r.start);
							val.add(r.getStartTimeCode());
							val.add(r.end);
							val.add(r.getEndTimeCode());
							val.add(r.getAverage());
							val.add(r.min);
							val.add(r.max);
							val.add(r.error ? RERROR : RWARN);
							val.add(r.getNote());
							rangeTm.addRow(val);
						}
					}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				jt.repaint();
			}
			
		};
		jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		setColumns();
		//jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//jt.setEnabled(false);
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("File Analysis Summary"));
		p.add(new JScrollPane(jt), BorderLayout.CENTER);
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p.add(p1, BorderLayout.SOUTH);
		save = new JButton("Save");
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new TableSaver(SammaReportGUI.this.asw.asg, tm, jt, asd.name());
			}			
		});
		p1.add(save);
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SammaReportGUI.this.analyzeSamma.doCancel = true;
				cancel.setEnabled(false);
				JOptionPane.showMessageDialog(null, "Cancelling after current file is processed...");
			}			
		});
		p1.add(cancel);
		box.add(p);
		p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Range Summary"));
		p.add(new JScrollPane(rangeJt), BorderLayout.CENTER);
		p1 = new JPanel(new BorderLayout());
		p.add(p1, BorderLayout.NORTH);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p1.add(p2, BorderLayout.NORTH);
		tfile = new JTextField(50);
		tfile.setEditable(false);
		tfile.setBackground(this.getBackground());
		p2.add(tfile);
		bfile = new JButton("Open");
		bfile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				(new BrowserSwingWorker(new File(tfile.getText()))).run();
			}
		});
		bfile.setEnabled(false);
		p2.add(bfile);
		p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p1.add(p2, BorderLayout.CENTER);
		tpdf = new JTextField(50);
		tpdf.setEditable(false);
		tpdf.setBackground(this.getBackground());
		p2.add(tpdf);
		bpdf = new JButton("Open");
		bpdf.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				(new BrowserSwingWorker(new File(tpdf.getText()))).run();
			}
		});
		bpdf.setEnabled(false);
		p2.add(bpdf);
		p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p1.add(p2, BorderLayout.SOUTH);
		p2.add(acombo);
		p2.add(errcombo);
		box.add(p);
    }
    
	/**
	 * Update the file-specific report table with a set of results
	 */
    public void update(File f, Vector<AnalysisResult> results) {
    	Integer row = rowMatrix.get(f);
    	if (row == null) return;
    	int col = 2;
    	boolean pass = true;
    	for(AnalysisResult res: results) {
    		tm.setValueAt(res.toString(), row, col);
    		col++;
    		pass = pass && res.evaluateFileValid();
    	}
		tm.setValueAt(pass ? "PASS" : "FAIL", row, 1);
    }
    
	class FnameFilter implements FilenameFilter {
		String s;
		FnameFilter(String s){this.s = s;}
		public boolean accept(File p, String name) {return name.startsWith(s) && name.toLowerCase().endsWith(".pdf");}
	}

	/**
	 * Locate the pdf file (if present) for an XML report file
	 */
	public File getPdf(File f){
		File pf = new File(f.getAbsolutePath().replace(".xml",".pdf"));
		if (pf.exists()) return pf;
		String s = f.getName().replace(".xml", "");
		File[] flist = f.getParentFile().listFiles(new FnameFilter(s));
		if (flist.length > 0) return flist[0];
		return null;
	}
	
	/**
	 * Configure the columns for the file results table
	 */
	void setColumns() {
		TableColumnModel tcm = jt.getColumnModel();
		TableColumn tc;
		for (int i = 0; i < jt.getColumnCount(); i++) {
			tc = tcm.getColumn(i);
			if (i < 1) {
				tc.setPreferredWidth(170);
			} else if (i == 1){
				tc.setPreferredWidth(80);
			} else {
				tc.setPreferredWidth(120);
			}	
			tc.setCellRenderer(new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus, int r, int c){
					Component comp = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, r, c);
					if (r < 3) {
						comp.setForeground(Color.black);
						comp.setBackground(SammaReportGUI.this.getBackground());
						if (val!=null) {
							((JComponent)comp).setToolTipText(val.toString());
						}
						return comp;
					} else {
						comp.setBackground(isSelected ? new Color(184,207, 229) : Color.white);
					}
					if (c == 0) {
					} else if (c == 1) {
						if (val == null) {
						} else if (val.equals("PASS")) {
							comp.setForeground(new Color(0x06,0x5F,0x12));
						} else if (val.equals("FAIL")) {
							comp.setForeground(Color.red);
						} else {
							comp.setForeground(Color.orange);
						}
					} else if (c == tm.getColumnCount()-1) {
					} else {
						if (val == null) {
						} else if (val.toString().endsWith(" / 0 Err")) {
							comp.setForeground(Color.black);
						} else if (val.toString().endsWith("Err")){
							comp.setForeground(Color.red);
						} else {
							comp.setForeground(Color.orange);
						}
					}
					return comp;
				}
				
				public void setValue(Object value) {
					if (value == null)
						return;
					if (value instanceof File) {
						File f = ((File)value);
						setText(f.getName());
					} else {
						setText(value.toString());
					}
				}

			});

		} 
	}
	
	/**
	 * Filter the error range report table based on the selected filter criteria
	 */
	void setFilter() {
		sorter.setRowFilter(
			new RowFilter<TableModel,Integer>(){
			public boolean include(
					javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> row) {
				try {
					String s = acombo.getSelectedItem().toString();
					ERR serr = (ERR)errcombo.getSelectedItem();
					if (row == null) return true;
					boolean b = true;
					String aname = (row.getValueCount()>0) ? row.getStringValue(0) : "";
					String err = (row.getValueCount()>9) ? row.getStringValue(9) : "";
					if (!s.equals(ALL)) {
						b = b && s.equals(aname);
					}
					if (serr == ERR.ERROR_RANGES) {
						b = b && err.equals(RERROR);
					} else if (serr == ERR.WARNING_RANGES) {
						b = b && err.equals(RWARN);
					}
					return b;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}
	);
	}
	
	
}  


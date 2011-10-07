package gov.nara.nwts.sammaGui;
import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 * Pop-up window that allows the user to specify an Analysis Set Definition File.
 * @author TBrady
 *
 */
public class ASImporter extends JFrame {
	
	private static final long serialVersionUID = 1L;
	AnalyzeSammaGui asg;
	ASImporter(AnalyzeSammaGui asg) {
		super("Analysis Set Definition File Selection");
		this.asg = asg;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel(new BorderLayout());
		add(p);
		JFileChooser jfc = new JFileChooser() {
			private static final long serialVersionUID = 1L;
			public void cancelSelection() {
				ASImporter.this.dispose();
			}
			public void approveSelection() {
				ASImporter.this.asg.asdTextField.setText(this.getSelectedFile().getAbsolutePath());
				ASImporter.this.setVisible(false);
				ASImporter.this.dispose();
				ASImporter.this.asg.setAnalysisSetDefs();
				ASImporter.this.asg.preferences.put("rootdef", this.getSelectedFile().getAbsolutePath());
			}
		};
		jfc.setCurrentDirectory(asg.getAsdDir());
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new FileFilter(){
			public boolean accept(File arg0) {
				if (arg0.isDirectory()) return true;
				return arg0.getName().toLowerCase().endsWith(".xml");
			}
			public String getDescription() {
				return "*.xml";
			}});
		p.add(jfc, BorderLayout.CENTER);
		p.add(new JLabel("Please select the Analysis Set Definition File (*.xml) you wish to import"),BorderLayout.NORTH);
		pack();
		setVisible(true);
	}
	
}

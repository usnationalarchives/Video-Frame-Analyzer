package gov.nara.nwts.util;
import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog file that will prompt a user to select a specific file
 * @author TBrady
 *
 */
public class FileSelect extends JDialog {
	private static final long serialVersionUID = 1L;
	JTextField result;
	public FileSelect(JFrame parent, JTextField result, String title) {
		super(parent, title);
		this.setModal(true);
		this.result = result;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel(new BorderLayout());
		add(p);
		JFileChooser jfc = new JFileChooser() {
			private static final long serialVersionUID = 1L;
			public void cancelSelection() {
				FileSelect.this.dispose();
			}
			public void approveSelection() {
				FileSelect.this.setVisible(false);
				FileSelect.this.result.setText(this.getSelectedFile().getAbsolutePath());
				FileSelect.this.result.firePropertyChange("text", true, false);
				FileSelect.this.dispose();
			}
		};
		String root = FileSelect.this.result.getText();
		if (root != null){
			jfc.setCurrentDirectory(new File(root));
		}
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		p.add(jfc, BorderLayout.CENTER);
		p.add(new JLabel(title),BorderLayout.NORTH);
		pack();
		setVisible(true);
	}
	
}

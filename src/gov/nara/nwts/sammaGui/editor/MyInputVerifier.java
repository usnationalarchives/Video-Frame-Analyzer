package gov.nara.nwts.sammaGui.editor;

import javax.swing.InputVerifier;
import javax.swing.JComponent;

/** Event handler object that triggers a status update of an Analysis object any time that changes are made within the Analysis gui*/
class MyInputVerifier extends InputVerifier {
	AnalysisSetEditor ase;
	MyInputVerifier(AnalysisSetEditor ase) {
		this.ase = ase;
	}
	public boolean verify(JComponent arg0) {
		ase.changeMade();
		return true;		}
	
}

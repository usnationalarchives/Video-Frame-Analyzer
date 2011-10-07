package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.AverageXmlAnalysis;
import gov.nara.nwts.sammaGui.analysis.NewAnalysis;
import gov.nara.nwts.sammaGui.analysis.PassFailXmlAnalysis;
import gov.nara.nwts.sammaGui.analysis.XmlAnalysis;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;

/** Panel for defining a new Analysis object in which the underlying Analysis type has not yet been selected.  Once saved, the Analysis type will be set.*/
class TabbedAnalysisPanel extends AnalysisPanel {
	private static final long serialVersionUID = 1L;
	JTabbedPane tp;
	AnalysisPanel pframe = null;
	AnalysisPanel pavg = null; 
	AnalysisPanel pfile = null;
	AnalysisPanel pskip = null;
	
	TabbedAnalysisPanel(AnalysisSetEditor ase, Analysis a) {
		super(ase, a, false);
		setLayout(new BorderLayout());
		tp = new JTabbedPane();
		add(tp, BorderLayout.CENTER);
		pframe = new AnalysisPanel(ase, a, false);
		pavg = new AnalysisPanel(ase, a, false);
		pfile = new AnalysisPanel(ase, a, false);
		if (a instanceof NewAnalysis) {
			NewAnalysis na = (NewAnalysis)a;
			pframe = new FrameAnalysisPanel(ase, na.frameAnalysis, false);
			pavg = new FrameAnalysisPanel(ase, na.averageAnalysis, true);
			pfile = new FileAnalysisPanel(ase, na.fileAnalysis);
		} else if (a instanceof PassFailXmlAnalysis) {
			pfile = new FileAnalysisPanel(ase, a);				
		} else if (a instanceof AverageXmlAnalysis) {
			pavg = new FrameAnalysisPanel(ase, a, true);				
		} else if (a instanceof XmlAnalysis) {
			pframe = new FrameAnalysisPanel(ase, a, false);				
		}
		tp.add(pframe, "Frame Attribute Analysis");
		tp.add(pavg, "Frame Average Attribute Analysis");
		tp.add(pfile, "File Attribute Analysis");
		setTab();
	}
	
	void setTab() {
		if (a instanceof PassFailXmlAnalysis) {
			setTab(2);			
		} else if (a instanceof AverageXmlAnalysis) {
			setTab(1);
		} else if (a instanceof XmlAnalysis){
			setTab(0);
		}			
	}
	void setTab(int index) {
		tp.setEnabledAt(0, index==0);
		tp.setEnabledAt(1, index==1);
		tp.setEnabledAt(2, index==2);
		tp.setSelectedIndex(index);
	}
	public void savePanel(AnalysisSetDefinition asd) {
		AnalysisPanel ap = (AnalysisPanel)tp.getSelectedComponent();
		if (a instanceof NewAnalysis) {
			asd.root.appendChild(ap.root);
			a = ap.a;
			setTab(); 
		}
		ap.savePanel(asd);
	}	

	public boolean isDeleted() {
		return ((AnalysisPanel)tp.getSelectedComponent()).cb.isSelected();
	}
}

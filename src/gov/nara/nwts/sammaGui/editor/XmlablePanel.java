package gov.nara.nwts.sammaGui.editor;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;

import javax.swing.JPanel;

import org.w3c.dom.Element;

/** Defines functions for Panels built from XML and saved to XML*/
public interface XmlablePanel {
	public Element getPanelElement();
	public JPanel createPanel();
	public void savePanel(AnalysisSetDefinition asd);
}

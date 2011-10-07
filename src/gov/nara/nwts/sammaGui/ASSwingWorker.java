package gov.nara.nwts.sammaGui;
import gov.nara.nwts.sammaGui.analysis.AnalysisResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingWorker;

/**
* The file analysis is a long running process which is initiated in a separate thread so that the main GUI window will remain responsive to user actions.
* A SwingWorker thread is used to initiate long running processes in a separate thread from the GUI thread.
* Status updates are periodically returned to the GUI thread through functions within this thread.
*/
public class ASSwingWorker extends SwingWorker<List<ASSwingWorker.Message>,ASSwingWorker.Message> {
	AnalyzeSammaGui asg;
	SammaReportGUI report;
	HashMap<File,Vector<AnalysisResult>> results;
	String input;
	
	public class Message {
		File f;
		String msg;
		Message(File f, String s) {
			this.f = f;
			this.msg = s;
			publish(this);
		}
	}
	
	ASSwingWorker(AnalyzeSammaGui asg){
		this.asg = asg;
		this.input = asg.input.getText();
		results = new HashMap<File,Vector<AnalysisResult>>();
		String s = asg.num.getText();
		if (s.length() > 0) {
			int x = Integer.parseInt(s);
			asg.analyzeSamma.setMaxFiles(x);
		}
		asg.summary.setText("Starting...\n");
		report = new SammaReportGUI(asg.analyzeSamma, this);
		asg.tabs.add(report, asg.analyzeSamma.analyses.def.name());
		asg.tabs.setSelectedIndex(asg.tabs.getComponentCount()-1);
		asg.tabs.setEnabledAt(0,false);
		report.save.setEnabled(false);
		report.cancel.setEnabled(true);
	}
	protected List<Message> doInBackground()  {
		try {
			new Message(null,null);
			asg.analyzeSamma.doFile();
		} catch (Exception e) {
			new Message(null,"Error parsing XML file: "+e.getMessage());	
		}
		return null;
	}
	protected void process(List<Message> msgs) {
		for(Iterator<Message>i=msgs.iterator(); i.hasNext();){
			Message m = i.next();
			String s = m.msg;
			if (s==null){
				asg.summary.setText("");
				asg.run.setEnabled(false);
			}else {
				asg.summary.append(s);
				asg.summary.append("\n");
			}
			if (m.f!=null) {
				Vector<AnalysisResult> rowresults = results.get(m.f);
				if (rowresults != null) {
					report.update(m.f, rowresults);
				}
			}
			
		}
	}
	protected void done() {
		asg.run.setEnabled(true);
		report.save.setEnabled(true);
		report.cancel.setEnabled(false);
		asg.tabs.setEnabledAt(0,true);
	}

	public ArrayList<File> listFiles() {
		ArrayList<File> allfiles = new ArrayList<File>();
		listFile(allfiles,new File(input));
		return allfiles;
	}
	public void listFile(List<File> allfiles, File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				listFile(allfiles, files[i]);
			}
		} else if (f.getName().endsWith(".xml")) {
			allfiles.add(f);
		}
		
	}
}

package gov.nara.nwts.sammaGui;
import gov.nara.nwts.sammaGui.analysis.Analysis;
import gov.nara.nwts.sammaGui.analysis.AnalysisResult;
import gov.nara.nwts.sammaGui.analysis.AnalysisSet;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinition;
import gov.nara.nwts.sammaGui.analysis.AnalysisSetDefinitionFile;
import gov.nara.nwts.sammaGui.analysis.SammaDocument;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/** Traverses the input directory for the analysis and initiates the analysis of each file.*/
public class AnalyzeSamma {
	
	boolean test = false;

	public AnalysisSet analyses;
	ASSwingWorker sw;
	AnalysisSetDefinition asd;
	String asdfName = "";
	boolean doCancel = false;

	public static SimpleDateFormat df;
	public static SimpleDateFormat dtf;
	public static NumberFormat nf;
	public static NumberFormat pf;
	static {
		df = new SimpleDateFormat("MM-dd-yyyy");
		dtf = new SimpleDateFormat("HH:mm:ss");
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		pf = NumberFormat.getPercentInstance();
		pf.setMaximumFractionDigits(0);	
	}

	Element table;
	int countFileFail = 0;
	int countFileProcessed = 0;

	public static boolean testPass = false;

	public static String defAnalysis = "DefaultAnalysis.xml";
	AnalysisSetDefinitionFile asdf;
	public AnalyzeSamma(String analysisDef) throws IOException, SAXException {
		setAnalyzisDefinitionFile(analysisDef);
		if (asdf.list.size() > 0) {
			setAnalysisSetDefinition(asdf.list.get(0));
		}
	}
	

	public void setAnalyzisDefinitionFile(String s) throws SAXException, IOException {
		asdf = new AnalysisSetDefinitionFile(new File(s));		
	}

	public void setAnalyzisDefinitionFile(File f) throws SAXException, IOException {
		if (!asdfName.equals(f.getAbsolutePath())) {
			asdf = new AnalysisSetDefinitionFile(f);
			asdfName = f.getAbsolutePath();
		}
	}

	public void setAnalysisSetDefinition(AnalysisSetDefinition asd) throws SAXException, IOException {
		if (asd != this.asd) {
			this.asd = asd;
			if (this.asd!=null){
				analyses = new AnalysisSet(asd);
			}
		}
	}
	
	public void setMaxFiles(int max) {
		asdf.maxFiles = max;
	}
	
	public int getMaxFiles() {
		return asdf.maxFiles;
	}
	
	/** Initiates the file traversal using the input directory specified in the GUI*/
	public void doFile() throws SAXException, ClassCastException,
	ClassNotFoundException, InstantiationException,
	IllegalAccessException, XPathExpressionException {
		countFileProcessed = 0;
		doFile(new File(sw.input));
		if (doCancel) doCancel = false;
	}
	
	/** This function is recursively called to traverse directories; as files are found the actual analysis is triggered*/
	public void doFile(File f) throws SAXException, ClassCastException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, XPathExpressionException {
		if (doCancel) return;
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				doFile(files[i]);
			}
		} else if (f.getName().endsWith(".xml")) {
			try {
				doSammaFile(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(f.getAbsolutePath() + " " + e.getMessage());
			}
		}
	}

	/** For each xml file that is found, perform the SAMMA Analysis by parsing the document.
	 *  For each file that is processed, an {@link AnalysisResult} will be created for each Analysis Task.
	 */
	public void doSammaFile(File f) throws IOException, SAXException,
			ClassCastException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, XPathExpressionException {
		if (asdf.maxFiles > 0) {
			if (countFileProcessed++ >= asdf.maxFiles){
				return;
			}
		}
		if (sw!=null) {
			sw.new Message(f, "Processing "+f.getAbsolutePath());
		}

		SammaDocument doc = new SammaDocument(this, f);
		
		Vector<AnalysisResult> fileResults = new Vector<AnalysisResult>();
		if (sw!=null) {
			sw.results.put(f, fileResults);
		}
		int err = 0;
		for (Iterator<Analysis> i = analyses.iterator(); i.hasNext();) {
			Analysis a = i.next();
			a.eof(doc);
			err += a.errbuf.getRangeCount(true);
			fileResults.add(a.errbuf);
			a.clearBuf();
		}
		if (err > 0){
			countFileFail++;
		}
		if (sw!=null) {
			sw.new Message(f, "Completing "+f.getAbsolutePath());
		}
	}



}

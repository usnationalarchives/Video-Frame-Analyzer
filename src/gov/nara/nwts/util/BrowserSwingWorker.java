package gov.nara.nwts.util;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
/**
 * This thread is used to load content open a file in a separate window.
 * A SwingWorker thread is used to initiate long running processes in a separate thread from the GUI thread.
 * Status updates are periodically returned to the GUI thread through functions within this thread.
 * @author TBrady
 *
 */
public class BrowserSwingWorker extends SwingWorker<Void, Void> {
	URI uri;
	File file;
	boolean test = false;

	public BrowserSwingWorker(URI uri) {
		this.uri = uri;
	}

	public BrowserSwingWorker(URL url) throws URISyntaxException {
		this(url.toURI());
	}

	public BrowserSwingWorker(String s) throws URISyntaxException {
		this(new URI(s));
	}

	public BrowserSwingWorker(File f) {
		file = f;
		uri = f.toURI();
	}

	protected Void doInBackground() {
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			try {
				String cmd = "cmd.exe /C start \"Open file\" \""
						+ uri.toString() + "\"";
				Runtime.getRuntime().exec(cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"File opening is currently only supported on Windows.\nPlease copy and paste the path name in order to open the file of intereste.");
		}
		return null;
	}

}

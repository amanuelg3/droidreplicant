/**
 * 
 */
package it.ninniuz.droidreplicant;

import javax.swing.SwingWorker;

import com.android.chimpchat.core.IChimpDevice;

/**
 * @author inq_targets_admin
 *
 */
public class InstallApplicationTask extends SwingWorker<Boolean, Integer> {

	private IChimpDevice device;
	private String applicationPath;

	@SuppressWarnings("unused")
	private InstallApplicationTask() {}
	
	public InstallApplicationTask(IChimpDevice device, String applicationPath) {
		this.device = device;
		this.applicationPath = applicationPath;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Boolean doInBackground() throws Exception {
		
		int progress = 0;
		
		setProgress(progress);
		
		boolean result = device.installPackage(applicationPath);
		
		progress = 100;
		setProgress(progress);
		
		return result;
	}

}

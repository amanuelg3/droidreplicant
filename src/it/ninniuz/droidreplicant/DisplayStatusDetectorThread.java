package it.ninniuz.droidreplicant;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayStatusDetectorThread extends Thread {

	private DroidReplicantDevice mDevice;
	private Pattern mPattern;
	private ArrayList<IScreenStatusReceiver> mReceivers;
	private static final String regex = ".*\\s+mPowerState=(\\d).*"; //$NON-NLS-1$

	public DisplayStatusDetectorThread(DroidReplicantDevice device) {

		mDevice = device;
		mPattern = 	Pattern.compile(regex, Pattern.DOTALL);
		mReceivers = new ArrayList<IScreenStatusReceiver>();
	}
	
	public interface IScreenStatusReceiver {
		
		public void screenStatus(boolean isOn);

	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		do {

			String dumpsysOut = mDevice.shell("dumpsys power"); //$NON-NLS-1$
			
			boolean isScreenOn = isScreenOn(dumpsysOut);
			
			synchronized (mReceivers) {
				for (IScreenStatusReceiver receiver : mReceivers) {
					receiver.screenStatus(isScreenOn);
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (true);
	}
	
	public void addScreenStatusReceiver(IScreenStatusReceiver receiver) {
		
		synchronized (mReceivers) {
			mReceivers.add(receiver);
		}
	}
	
	public void removeScreenStatusReceiver(IScreenStatusReceiver receiver) {
		
		synchronized (mReceivers) {
			mReceivers.remove(receiver);
		}
	}

	private boolean isScreenOn(String dumpsysOut) {

		Matcher m = mPattern.matcher(dumpsysOut);
		int powerState = 1;

		if (m.matches()) {
			try {
				powerState = Integer.parseInt(m.group(1));
			} catch (NumberFormatException e) {
				powerState = 1;
			}
		}
		
		return powerState != 0;

	}
}

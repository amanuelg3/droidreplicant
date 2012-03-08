package it.ninniuz.droidreplicant;

import java.io.IOException;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.ddmlib.DdmPreferences;

public class DroidReplicantMain
{
	public static String sAdbLocation = null;
	public static final String ADB_PREF_KEY = "adb_location"; //$NON-NLS-1$
	public static final int ADB_COMMAND_TIMEOUT = 10 * 1000;

	DroidReplicantFrame jf;
	ChimpChatInjector mInjector;
	IChimpDevice mDevice;

	private ChimpChat mChimpchat;

	public DroidReplicantMain() throws IOException 
	{
		try {
			init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}

	private void init() throws IOException 
	{			
		TreeMap<String, String> options = new TreeMap<String, String>();
		options.put("backend", "adb"); //$NON-NLS-1$ //$NON-NLS-2$

		Preferences prefs = Preferences.systemNodeForPackage(getClass());
		sAdbLocation = prefs.get(ADB_PREF_KEY, null);

		if (sAdbLocation == null) 
		{
			JFileChooser chooser = new JFileChooser();

			int returnVal = chooser.showOpenDialog(jf);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				System.out.println("You chose to open this file:" + chooser.getSelectedFile().getAbsolutePath()); //$NON-NLS-1$
				sAdbLocation = chooser.getSelectedFile().getAbsolutePath();
				persistAdbLocation(sAdbLocation);
			}
			else {
				System.exit(0);
				return;
			}
		}

		options.put("adbLocation", sAdbLocation); //$NON-NLS-1$

		DdmPreferences.setTimeOut(ADB_COMMAND_TIMEOUT);

		mChimpchat = ChimpChat.getInstance(options);
		mDevice = new DroidReplicantDevice((AdbChimpDevice) mChimpchat.waitForConnection());

		if (mDevice == null) {
			System.exit(0);
			return;
		}

		// Start showing the device screen
		mInjector = new ChimpChatInjector(mDevice);
		mInjector.start();
		
		jf = new DroidReplicantFrame(this, mDevice, mInjector);
		jf.setTitle(Messages.getString("DroidReplicantMain.AppName")); //$NON-NLS-1$

		// Show window
		jf.setVisible(true);

		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}

	private void persistAdbLocation(String adbLocation) 
	{
		Preferences prefs = Preferences.systemNodeForPackage(getClass());
		prefs.put(ADB_PREF_KEY, adbLocation);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {

			e.printStackTrace();
		}
	}

	protected void close() 
	{
		System.out.println("cleaning up..."); //$NON-NLS-1$
		if (mInjector != null)
			mInjector.close();

		mChimpchat.shutdown();

		System.out.println("cleanup done, exiting..."); //$NON-NLS-1$
	}

	public static void main(String args[]) throws IOException
	{
		new DroidReplicantMain();
	}
}

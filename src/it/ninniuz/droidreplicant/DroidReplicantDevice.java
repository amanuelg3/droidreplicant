package it.ninniuz.droidreplicant;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.chimpchat.core.IChimpView;
import com.android.chimpchat.core.IMultiSelector;
import com.android.chimpchat.core.ISelector;
import com.android.chimpchat.core.PhysicalButton;
import com.android.chimpchat.core.TouchPressType;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.google.common.collect.Lists;

public class DroidReplicantDevice implements IChimpDevice {

	private IDevice mDdmsDevice;
	private IChimpDevice mAdbChimpDevice;
	
	private static final String[] ZERO_LENGTH_STRING_ARRAY = new String[0];


	public DroidReplicantDevice(AdbChimpDevice chimp) {
		mAdbChimpDevice = (AdbChimpDevice) chimp;
		extractDdmsDevice();	
	}

	private void extractDdmsDevice() {

		Field fieldDevice;

		try {
			fieldDevice = AdbChimpDevice.class.getDeclaredField("device"); //$NON-NLS-1$
			fieldDevice.setAccessible(true);
			IDevice ddmsDevice = (IDevice) fieldDevice.get(mAdbChimpDevice);
			mDdmsDevice = ddmsDevice;
		} catch (Exception e) {

			e.printStackTrace();
			mDdmsDevice = null;
		}
	}

	/**
	 * Output receiver for "am start" command line.
	 */
	private static final class StartActivityReceiver extends MultiLineReceiver {

		private static final String SUCCESS_OUTPUT = "Starting"; //$NON-NLS-1$
		private static final Pattern FAILURE_PATTERN = Pattern.compile("Error: (.*)"); //$NON-NLS-1$

		private String mErrorMessage = null;

		public StartActivityReceiver() {
		}

		@Override
		public void processNewLines(String[] lines) {
			for (String line : lines) {
				if (line.length() > 0) {
					if (line.startsWith(SUCCESS_OUTPUT)) {
						mErrorMessage = null;
					} else {
						Matcher m = FAILURE_PATTERN.matcher(line);
						if (m.matches()) {
							mErrorMessage = m.group(1);
						}
					}
				}
			}
		}

		public boolean isCancelled() {
			return false;
		}

		public String getErrorMessage() {
			return mErrorMessage;
		}
	}

	/**
	 * @return the mDdmsDevice
	 */
	public IDevice getDdmsDevice() {
		return mDdmsDevice;
	}
	
	public void removeApplication() {
		
		final String MANAGE_APP_ACTION = "android.settings.MANAGE_APPLICATIONS_SETTINGS"; //$NON-NLS-1$
		
		new StartActivityTask(this, null, MANAGE_APP_ACTION, null, null, null, null, null, 0).execute();
	}
	
	public InstallApplicationTask installApplication(String apkFullPath) {
		
		final InstallApplicationTask task = new InstallApplicationTask(this, apkFullPath);
		task.execute();
		
		return task;
	}
	
	public StartActivityTask exportContactsToSdCard() {
				
		String action = "android.intent.action.MAIN"; //$NON-NLS-1$
		String component = "com.android.contacts/.ExportVCardActivity"; //$NON-NLS-1$
		
		final StartActivityTask task = new StartActivityTask(this, null, action, null, null, null, null, component, 0);
		task.execute();
		
		return task;
	}

	@Override
	public void broadcastIntent(String arg0, String arg1, String arg2,
			String arg3, Collection<String> arg4, Map<String, Object> arg5,
			String arg6, int arg7) {

		mAdbChimpDevice.broadcastIntent(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}

	@Override
	public void dispose() {

		mAdbChimpDevice.dispose();
	}

	@Override
	public void drag(int arg0, int arg1, int arg2, int arg3, int arg4, long arg5) {

		mAdbChimpDevice.drag(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public HierarchyViewer getHierarchyViewer() {

		return mAdbChimpDevice.getHierarchyViewer();
	}

	@Override
	public ChimpManager getManager() {

		return mAdbChimpDevice.getManager();
	}

	@Override
	public String getProperty(String arg0) {

		return mAdbChimpDevice.getProperty(arg0);
	}

	@Override
	public Collection<String> getPropertyList() {

		return mAdbChimpDevice.getPropertyList();
	}

	@Override
	public IChimpView getRootView() {

		return mAdbChimpDevice.getRootView();
	}

	@Override
	public String getSystemProperty(String arg0) {

		return mAdbChimpDevice.getSystemProperty(arg0);
	}

	@Override
	public IChimpView getView(ISelector arg0) {

		return mAdbChimpDevice.getView(arg0);
	}

	@Override
	public Collection<String> getViewIdList() {

		return mAdbChimpDevice.getViewIdList();
	}

	@Override
	public Collection<IChimpView> getViews(IMultiSelector arg0) {

		return mAdbChimpDevice.getViews(arg0);
	}

	@Override
	public boolean installPackage(String arg0) {

		return mAdbChimpDevice.installPackage(arg0);
	}

	@Override
	public Map<String, Object> instrument(String arg0, Map<String, Object> arg1) {

		return mAdbChimpDevice.instrument(arg0, arg1);
	}

	@Override
	public void press(String arg0, TouchPressType arg1) {

		mAdbChimpDevice.press(arg0, arg1);
	}

	@Override
	public void press(PhysicalButton arg0, TouchPressType arg1) {

		mAdbChimpDevice.press(arg0, arg1);
	}

	@Override
	public void reboot(String arg0) {

		mAdbChimpDevice.reboot(arg0);
	}

	@Override
	public boolean removePackage(String arg0) {

		return mAdbChimpDevice.removePackage(arg0);
	}

	@Override
	public String shell(String arg0) {

		return mAdbChimpDevice.shell(arg0);
	}

	@Override
	public void startActivity(String arg0, String arg1, String arg2,
			String arg3, Collection<String> arg4, Map<String, Object> arg5,
			String arg6, int arg7) {

		mAdbChimpDevice.startActivity(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}

	public boolean startActivityWithResult(String arg0, String arg1, String arg2,
			String arg3, Collection<String> arg4, Map<String, Object> arg5,
			String arg6, int arg7) {

		List<String> intentArgs = buildIntentArgString(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		
		String shellCmd = getShellCmd(Lists.asList("am", "start", //$NON-NLS-1$ //$NON-NLS-2$
                intentArgs.toArray(ZERO_LENGTH_STRING_ARRAY)).toArray(ZERO_LENGTH_STRING_ARRAY));
		
		StartActivityReceiver receiver = new StartActivityReceiver();
		try {
			mDdmsDevice.executeShellCommand(shellCmd, receiver);
		} catch (TimeoutException e) {

			e.printStackTrace();
			return false;
		} catch (AdbCommandRejectedException e) {

			e.printStackTrace();
			return false;
		} catch (ShellCommandUnresponsiveException e) {

			e.printStackTrace();
			return false;
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		}
		
		return (receiver.getErrorMessage() == null);
	}

	private static boolean isNullOrEmpty(@Nullable String string) {
		return string == null || string.length() == 0;
	}

	private String getShellCmd(String... args) {
		StringBuilder cmd = new StringBuilder();
		for (String arg : args) {
			cmd.append(arg).append(" "); //$NON-NLS-1$
		}
		return cmd.toString();
	}

	private List<String> buildIntentArgString(String uri, String action, String data, String mimetype,
			Collection<String> categories, Map<String, Object> extras, String component,
			int flags) {
		
		List<String> parts = Lists.newArrayList();

		// from adb docs:
		//<INTENT> specifications include these flags:
		//    [-a <ACTION>] [-d <DATA_URI>] [-t <MIME_TYPE>]
		//    [-c <CATEGORY> [-c <CATEGORY>] ...]
		//    [-e|--es <EXTRA_KEY> <EXTRA_STRING_VALUE> ...]
		//    [--esn <EXTRA_KEY> ...]
		//    [--ez <EXTRA_KEY> <EXTRA_BOOLEAN_VALUE> ...]
		//    [-e|--ei <EXTRA_KEY> <EXTRA_INT_VALUE> ...]
		//    [-n <COMPONENT>] [-f <FLAGS>]
		//    [<URI>]

		if (!isNullOrEmpty(action)) {
			parts.add("-a"); //$NON-NLS-1$
			parts.add(action);
		}

		if (!isNullOrEmpty(data)) {
			parts.add("-d"); //$NON-NLS-1$
			parts.add(data);
		}

		if (!isNullOrEmpty(mimetype)) {
			parts.add("-t"); //$NON-NLS-1$
			parts.add(mimetype);
		}

		// Handle categories
		for (String category : categories) {
			parts.add("-c"); //$NON-NLS-1$
			parts.add(category);
		}

		// Handle extras
		for (Entry<String, Object> entry : extras.entrySet()) {
			// Extras are either boolean, string, or int.  See which we have
			Object value = entry.getValue();
			String valueString;
			String arg;
			if (value instanceof Integer) {
				valueString = Integer.toString((Integer) value);
				arg = "--ei"; //$NON-NLS-1$
			} else if (value instanceof Boolean) {
				valueString = Boolean.toString((Boolean) value);
				arg = "--ez"; //$NON-NLS-1$
			} else {
				// treat is as a string.
				valueString = value.toString();
				arg = "--es"; //$NON-NLS-1$
			}
			parts.add(arg);
			parts.add(entry.getKey());
			parts.add(valueString);
		}

		if (!isNullOrEmpty(component)) {
			parts.add("-n"); //$NON-NLS-1$
			parts.add(component);
		}

		if (flags != 0) {
			parts.add("-f"); //$NON-NLS-1$
			parts.add(Integer.toString(flags));
		}

		if (!isNullOrEmpty(uri)) {
			parts.add(uri);
		}

		return parts;
	}

	@Override
	public IChimpImage takeSnapshot() {
		
		return mAdbChimpDevice.takeSnapshot();
	}

	@Override
	public void touch(int arg0, int arg1, TouchPressType arg2) {

		mAdbChimpDevice.touch(arg0, arg1, arg2);
	}

	@Override
	public void type(String arg0) {

		mAdbChimpDevice.type(arg0);
	}

	@Override
	public void wake() {

		mAdbChimpDevice.wake();
	}

}

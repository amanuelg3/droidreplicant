package it.ninniuz.droidreplicant;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.PhysicalButton;
import com.android.chimpchat.core.TouchPressType;

public class ChimpChatInjector 
{
	private IChimpDevice mDevice;
	
	private ChimpChatScreenCaptureThread mScreencapture;

	private ChimpManager mChimpManager;

	private Method sendMonkeyEvent;
	
	public ChimpChatInjector(IChimpDevice d) throws IOException 
	{
		mDevice = d;
		mScreencapture = new ChimpChatScreenCaptureThread(mDevice);
		
		mChimpManager = d.getManager();
		try {
			sendMonkeyEvent = ChimpManager.class.getDeclaredMethod("sendMonkeyEvent", String.class); //$NON-NLS-1$
			sendMonkeyEvent.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see net.srcz.android.screencast.api.injector.Injector#start()
	 */
	public void start() {
		mScreencapture.start();
	}

	/* (non-Javadoc)
	 * @see net.srcz.android.screencast.api.injector.Injector#close()
	 */
	public void close() {
		mScreencapture.interrupt();
	}

	/* (non-Javadoc)
	 * @see net.srcz.android.screencast.api.injector.Injector#injectMouse(int, float, float)
	 */
	public void injectMouse(int action, float x, float y) throws IOException 
	{
		switch (action) {
		case ConstEvtMotion.ACTION_MOVE:
			mDevice.getManager().touchMove((int) x, (int) y);
			break;
		default:
			mDevice.touch((int) x, (int) y, getTouchPressType(action));
			break;
		}	
	}

	/* (non-Javadoc)
	 * @see net.srcz.android.screencast.api.injector.Injector#injectTrackball(float)
	 */
	public void injectTrackball(float amount) throws IOException {
		try {
			sendMonkeyEvent.invoke(mChimpManager, "trackball 0 " + (int) amount); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see net.srcz.android.screencast.api.injector.Injector#injectKeycode(int, int)
	 */
	public void injectKeycode(int type, String keyCode) 
	{	
		if (keyCode == null)
			return;
		
		try {
			if (type == ConstEvtMotion.ACTION_DOWN) {
				mDevice.getManager().keyDown(keyCode);
			}
			else if (type == ConstEvtMotion.ACTION_UP) {
				mDevice.getManager().keyUp(keyCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see net.srcz.android.screencast.api.injector.Injector#injectFlip(boolean)
	 */
	public void injectFlip(boolean keyboardOpen) 
	{
	}
	
	private TouchPressType getTouchPressType(int action)
	{
		TouchPressType result = TouchPressType.DOWN;
		
		switch (action) {
		case ConstEvtMotion.ACTION_DOWN: 
			result = TouchPressType.DOWN;
			break;
		case ConstEvtMotion.ACTION_UP:
			result = TouchPressType.UP;
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * @return the mScreencapture
	 */
	public ScreenCaptureInterface getScreencapture() {
		return mScreencapture;
	}

	public void injectButton(PhysicalButton button, TouchPressType type) 
	{
		mDevice.press(button, type);
	}

	public void injectButton(AdditionalPhysicalButton button, TouchPressType type) {

		injectButton(button.getKeyName(), type);
	}	
	
	public void injectButton(String buttonString, TouchPressType type)
	{
		mDevice.press(buttonString, type);
	}

	public void injectTypeChar(char keyChar) 
	{
		try {
			if (shouldInject(keyChar))
				mDevice.getManager().type(keyChar);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean shouldInject(char keyChar) 
	{
		boolean result = true;
		
		if (keyChar > 'ˆ')
			result = false;
		
		switch (keyChar) {
		case 8: /* Del */
		case 32: /* Space */
		case 127: /* Delete */
		case 732: /* Small Tilde */
		case 'ˆ':
		case '>':
		case '<':
		case '±': case '¤':
			result = false;
			break;
		default:
			break;
		}
		
		return result;
	}
}

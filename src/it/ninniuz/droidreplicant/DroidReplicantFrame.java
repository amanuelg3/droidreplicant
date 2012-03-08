package it.ninniuz.droidreplicant;

import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.PhysicalButton;
import com.android.chimpchat.core.TouchPressType;

import it.ninniuz.droidreplicant.DisplayStatusDetectorThread.IScreenStatusReceiver;
import it.ninniuz.droidreplicant.explorer.DeviceExplorerFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DroidReplicantFrame extends JFrame implements AncestorListener, IScreenStatusReceiver
{
	private static final long serialVersionUID = 6274356181677380213L;

	private static final String APK_DIR_PREF_KEY = "apk_dir"; //$NON-NLS-1$
	private static final String SCREENSHOT_DIR_PREF_KEY = "screenshot_dir"; //$NON-NLS-1$
	private static final String VIDEO_DIR_PREF_KEY = "video_dir"; //$NON-NLS-1$

	private DroidReplicantDisplay mDisplay = new DroidReplicantDisplay();

	private JToolBar mHardkeysToolBar;

	private DroidReplicantDevice mDevice;
	private ChimpChatInjector mInjector;
	private Dimension oldImageDimension = null;

	private boolean mIsVideoRecording;

	private JPanel mRecordingPanel;

	private DroidReplicantMain mControlMain;
	private JMenu menu_1;

	private JMenuItem mGrabVideoMenuItem;

	private JPanel mWakeUpPanel;

	public DroidReplicantFrame(DroidReplicantMain droidreplicantMain, IChimpDevice device, ChimpChatInjector injector) throws IOException 
	{
		mControlMain = droidreplicantMain;
		
		mDevice = (DroidReplicantDevice) device;
		
		setInjector(injector);
		
//		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e)
			{
				mControlMain.close();
				dispose();
			}
			
			public void windowLostFocus(WindowEvent e) 
			{
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
			
			public void windowDeactivated(WindowEvent e) 
			{
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		init();
		
		initMenu();

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
				new KeyEventDispatcher() 
				{
					public boolean dispatchKeyEvent(KeyEvent e) 
					{
						if (!DroidReplicantFrame.this.isActive())
							return false;
						if (mInjector == null)
							return false;
						if (e.getID() == KeyEvent.KEY_TYPED)
						{
							mInjector.injectTypeChar(e.getKeyChar());
							return true;
						}
						if (e.getID() == KeyEvent.KEY_PRESSED) {
							String code = KeyCodeConverter.getKeyCodeString(e);
							mInjector.injectKeycode(ConstEvtKey.ACTION_DOWN, code);
							return true;
						}
						if (e.getID() == KeyEvent.KEY_RELEASED) {
							String code = KeyCodeConverter.getKeyCodeString(e);
							mInjector.injectKeycode(ConstEvtKey.ACTION_UP, code);
							return true;
						}
						return false;
					}
				}
		);
	}


	public void init() throws IOException 
	{
		this.setResizable(false);
		
		mHardkeysToolBar = new JToolBar();
		mHardkeysToolBar.setLayout(new BorderLayout());
		mHardkeysToolBar.setFloatable(false);
		
		Container northContainer = new Container();
		northContainer.setLayout(new GridLayout(1, 4));
		
		createAddHardButton(Messages.getString("DroidReplicantFrame.btnMenu"), new PhysicalButtonMouseListener(PhysicalButton.MENU), northContainer) //$NON-NLS-1$
		.setIcon(new ImageIcon(DroidReplicantFrame.class.getResource("/it/ninniuz/droidreplicant/resources/ic_menu_moreoverflow.png"))); //$NON-NLS-1$
		createAddHardButton(Messages.getString("DroidReplicantFrame.btnHome"), new PhysicalButtonMouseListener(PhysicalButton.HOME), northContainer) //$NON-NLS-1$
		.setIcon(new ImageIcon(DroidReplicantFrame.class.getResource("/it/ninniuz/droidreplicant/resources/ic_menu_home.png"))); //$NON-NLS-1$
		createAddHardButton(Messages.getString("DroidReplicantFrame.btnBack"), new PhysicalButtonMouseListener(PhysicalButton.BACK), northContainer) //$NON-NLS-1$
		.setIcon(new ImageIcon(DroidReplicantFrame.class.getResource("/it/ninniuz/droidreplicant/resources/ic_menu_back.png"))); //$NON-NLS-1$
		createAddHardButton(Messages.getString("DroidReplicantFrame.btnSearch"), new PhysicalButtonMouseListener(PhysicalButton.SEARCH), northContainer) //$NON-NLS-1$
		.setIcon(new ImageIcon(DroidReplicantFrame.class.getResource("/it/ninniuz/droidreplicant/resources/ic_menu_search.png"))); //$NON-NLS-1$
		
		Container southContainer = new Container();
		southContainer.setLayout(new GridLayout(1, 3));
		
		createAddHardButton(Messages.getString("DroidReplicantFrame.btnCall"), new AdditionalPhysicalButtonMouseListener(AdditionalPhysicalButton.CALL), southContainer) //$NON-NLS-1$
		.setIcon(new ImageIcon(DroidReplicantFrame.class.getResource("/it/ninniuz/droidreplicant/resources/ic_menu_call.png"))); //$NON-NLS-1$
		createAddHardButton(Messages.getString("DroidReplicantFrame.btnEndCall"), new AdditionalPhysicalButtonMouseListener(AdditionalPhysicalButton.ENDCALL), southContainer) //$NON-NLS-1$
		.setIcon(new ImageIcon(DroidReplicantFrame.class.getResource("/it/ninniuz/droidreplicant/resources/ic_menu_endcall_small.png"))); //$NON-NLS-1$

		/* */
		mWakeUpPanel = new JPanel(new GridLayout(1,2));
		Image errorImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/it/ninniuz/droidreplicant/resources/error.png")); //$NON-NLS-1$
		JLabel wakeUpLabel = new JLabel(Messages.getString("DroidReplicantFrame.lblScreenOff")); //$NON-NLS-1$
		wakeUpLabel.setIcon(new ImageIcon(errorImage));
		mWakeUpPanel.add(wakeUpLabel);
		
		JButton wakeUpButton = new JButton(Messages.getString("DroidReplicantFrame.lblWakeScreen")); //$NON-NLS-1$
		wakeUpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				wakeUp();
			}
		});
		mWakeUpPanel.setVisible(false);
		mWakeUpPanel.setPreferredSize(new Dimension(0, 30));
		mWakeUpPanel.add(wakeUpButton);
		mDisplay.add(mWakeUpPanel, BorderLayout.NORTH);
		
		mHardkeysToolBar.add(northContainer, BorderLayout.CENTER);
		mHardkeysToolBar.add(southContainer, BorderLayout.SOUTH);
		mHardkeysToolBar.addAncestorListener(this);

		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/it/ninniuz/droidreplicant/resources/icon.png"))); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		/* Recording Panel */
		mRecordingPanel = new JPanel(new GridLayout(1,2));
		Image recordImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/it/ninniuz/droidreplicant/resources/record_small.png")); //$NON-NLS-1$
		JLabel recordingLabel = new JLabel(Messages.getString("DroidReplicantFrame.lblRecordingVideo")); //$NON-NLS-1$
		recordingLabel.setIcon(new ImageIcon(recordImage));
		mRecordingPanel.add(recordingLabel);
		
		JButton stopRecordingButton = new JButton(Messages.getString("DroidReplicantFrame.lblStopRecording")); //$NON-NLS-1$
		stopRecordingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				stopRecording();
			}
		});
		mRecordingPanel.setVisible(false);
		mRecordingPanel.add(stopRecordingButton);
		/* End of Recording Panel */
		
		getContentPane().add(mRecordingPanel, BorderLayout.NORTH);
		getContentPane().add(mHardkeysToolBar, BorderLayout.SOUTH);
		getContentPane().add(mDisplay, BorderLayout.CENTER);

		((ChimpChatScreenCaptureThread) mInjector.getScreencapture()).addScreenStatusReceiver(this);
		
		pack();
				
		MouseAdapter ma = new MouseAdapter() 
		{
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if (mInjector != null) {
					try {
						Point p2 = mDisplay.getRawPoint(arg0.getPoint());
						mInjector.injectMouse(ConstEvtMotion.ACTION_MOVE, p2.x, p2.y);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (mInjector != null) {
					try {
						Point p2 = mDisplay.getRawPoint(arg0.getPoint());
						mInjector.injectMouse(ConstEvtMotion.ACTION_DOWN, p2.x, p2.y);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (mInjector == null)
					return;
				try {
					Point p2 = mDisplay.getRawPoint(arg0.getPoint());
					mInjector.injectMouse(ConstEvtMotion.ACTION_UP, p2.x, p2.y);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (mInjector == null)
					return;
				try {
					mInjector.injectTrackball(arg0.getWheelRotation() < 0 ? -1f	: 1f);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};

		mDisplay.addMouseMotionListener(ma);
		mDisplay.addMouseListener(ma);
		mDisplay.addMouseWheelListener(ma);
	}
	
	private JButton createAddHardButton(String label, MouseListener mouseListener, Container container) 
	{
		JButton button = new JButton(label);
		button.addMouseListener(mouseListener);
		button.setFocusable(false);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		container.add(button);
		
		return button;
	}

	private void initMenu() {
		
		JMenuBar menuBar;
		final JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		menu = new JMenu(Messages.getString("DroidReplicantFrame.menuDevice")); //$NON-NLS-1$

		menu.setMnemonic(KeyEvent.VK_D);
		menu.getAccessibleContext().setAccessibleDescription( "The only menu in this program that has menu items"); //$NON-NLS-1$
		menuBar.add(menu);

		mGrabVideoMenuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuGrabVideo")); //$NON-NLS-1$
		mGrabVideoMenuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("DroidReplicantFrame.menuGrabVideoAcessibleDescr")); //$NON-NLS-1$
		mGrabVideoMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startRecording();
			}
		});
		menu.add(mGrabVideoMenuItem);
		
		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuScreenshot"), KeyEvent.VK_S); //$NON-NLS-1$
		menuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("DroidReplicantFrame.menuScreenshotAccessibleDescr")); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				takeScreenshot();
			}
		});
		menu.add(menuItem);
		
		/* Add a separator */
		menu.addSeparator();

		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuInstallApp")); //$NON-NLS-1$
		menuItem.getAccessibleContext().setAccessibleDescription("Take a screenshot of the device display"); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				installApplication();
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuRemoveApp")); //$NON-NLS-1$
		menuItem.getAccessibleContext().setAccessibleDescription(""); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeApplication();
			}
		});
		menu.add(menuItem);
		
		/* Add a separator */
		menu.addSeparator();
		
		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuWakeup")); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				wakeUp();
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuReboot")); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				reboot();
			}
		});
		menu.add(menuItem);

		menu_1 = new JMenu(Messages.getString("DroidReplicantFrame.menuActions")); //$NON-NLS-1$
		menu_1.setMnemonic(KeyEvent.VK_A);
		menu_1.getAccessibleContext().setAccessibleDescription( "The only menu in this program that has menu items"); //$NON-NLS-1$
		menuBar.add(menu_1);
		
		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuComposeSms")); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				composeSms();
			}
		});
		menu_1.add(menuItem);
		
		menuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuComposeEmail")); //$NON-NLS-1$
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				composeEmail();
			}
		});
		menu_1.add(menuItem);
		
		/* Add a separator */
		menu_1.addSeparator();
		
		JMenuItem menuExportContactsSD = new JMenuItem(Messages.getString("DroidReplicantFrame.menuExportContacts")); //$NON-NLS-1$
		menuExportContactsSD.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				new StartActivityTask(mDevice, null, "android.intent.action.MAIN", null, null, null, null, "com.android.contacts/.ExportVCardActivity", 0).execute(); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		menu_1.add(menuExportContactsSD);
		
		JMenuItem menuFileExplorer = new JMenuItem(Messages.getString("DroidReplicantFrame.menuExplorer")); //$NON-NLS-1$
		menuFileExplorer.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				openFileExplorer();
			}
		});
		menu_1.add(menuFileExplorer);
		
		/* Add a separator */
		menu_1.addSeparator();
		
		JMenu menuShortcuts = new JMenu(Messages.getString("DroidReplicantFrame.menuShortcuts")); //$NON-NLS-1$
		menu_1.add(menuShortcuts);
		
		JMenuItem mntmNewMenuItem = new JMenuItem(Messages.getString("DroidReplicantFrame.menuWifiSettings")); //$NON-NLS-1$
		mntmNewMenuItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				 new StartActivityTask(mDevice, null, "android.settings.WIFI_SETTINGS", null, null, null , null, null, 0).execute(); //$NON-NLS-1$
			}
		});
		menuShortcuts.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem(Messages.getString("DroidReplicantFrame.menuAirplaneSettings")); //$NON-NLS-1$
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				new StartActivityTask(mDevice, null, "android.settings.AIRPLANE_MODE_SETTINGS", null, null, null , null, null, 0).execute(); //$NON-NLS-1$
			}
		});
		menuShortcuts.add(mntmNewMenuItem_1);
		
		this.setJMenuBar(menuBar);
	}

	protected void openFileExplorer() {

		DeviceExplorerFrame explorer = new DeviceExplorerFrame(mDevice.getDdmsDevice());
		explorer.setVisible(true);
	}

	protected void composeEmail() {

		new EmailComposerFrame(mDevice);
	}

	protected void composeSms() {
		
		new SmsComposerFrame(mDevice);
	}

	protected void removeApplication() 
	{
		mDevice.removeApplication();
	}

	protected void installApplication() 
	{		
		JFileChooser apkChooser = new JFileChooser(retrievePathFromPrefs(APK_DIR_PREF_KEY, "")); //$NON-NLS-1$
		
		int returnVal = apkChooser.showOpenDialog(DroidReplicantFrame.this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		{
			File apkFile = apkChooser.getSelectedFile();
			String apkFullPath = apkFile.getAbsolutePath();
			
			persistPath(APK_DIR_PREF_KEY, apkFile.getParent());
			
			final InstallApplicationTask task = mDevice.installApplication(apkFullPath);
			
			final InstallApplicationProgress progress = new InstallApplicationProgress(this);
			progress.setVisible(true);
			
			task.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("progress")) { //$NON-NLS-1$
						if (task.getProgress() == 100) {
							try {
								boolean result = task.get();
								progress.dispose();
								JOptionPane.showMessageDialog(DroidReplicantFrame.this, result ? 
										Messages.getString("DroidReplicantFrame.dialogAppInstalled") : Messages.getString("DroidReplicantFrame.dialogAppNotInstalled")); //$NON-NLS-1$ //$NON-NLS-2$
							} catch (InterruptedException e) {

								e.printStackTrace();
							} catch (ExecutionException e) {

								e.printStackTrace();
							}
						}
					}
				}
			});
		}
		else
			return;
	}

	protected void reboot() {
		
		switch (JOptionPane.showConfirmDialog(this, Messages.getString("DroidReplicantFrame.dialogRebootConfirm"), Messages.getString("DroidReplicantFrame.dialogRebootTitle"), JOptionPane.OK_CANCEL_OPTION)) { //$NON-NLS-1$ //$NON-NLS-2$
		case JOptionPane.YES_OPTION:
			mDevice.reboot(""); //$NON-NLS-1$
			break;
		default:
			break;
		};
		
	}

	protected void wakeUp() {
		mDevice.wake();
	}
	
	private String retrievePathFromPrefs(String prefKey, String defaultValue)
	{
		Preferences prefs = Preferences.systemNodeForPackage(getClass());
		return prefs.get(prefKey, defaultValue);
	}
	
	private void persistPath(String prefKey, String path) 
	{
		Preferences prefs = Preferences.systemNodeForPackage(getClass());
		prefs.put(prefKey, path);
	}

	public class PhysicalButtonMouseListener implements MouseListener
	{
		PhysicalButton button;

		public PhysicalButtonMouseListener(PhysicalButton b) {
			 
			this.button = b;
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

			if (mInjector == null)
				return;

			mInjector.injectButton(button, TouchPressType.DOWN);
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if (mInjector == null)
				return;

			mInjector.injectButton(button, TouchPressType.UP);	
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}
	
	public class AdditionalPhysicalButtonMouseListener implements MouseListener
	{
		AdditionalPhysicalButton button;

		public AdditionalPhysicalButtonMouseListener(AdditionalPhysicalButton b) {

			this.button = b;
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

			if (mInjector == null)
				return;

			mInjector.injectButton(button, TouchPressType.DOWN);
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if (mInjector == null)
				return;

			mInjector.injectButton(button, TouchPressType.UP);	
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

	public void setInjector(ChimpChatInjector injector) 
	{
		mInjector = injector;
		mInjector.getScreencapture().setListener(
				new ScreenCaptureListener() 
				{
					public void handleNewImage(Dimension size, BufferedImage image, boolean landscape, double scaleX, double scaleY)
					{
						boolean firstDraw = false;
						
						if (oldImageDimension == null || !size.equals(oldImageDimension)) 
						{
							Dimension d = new Dimension(size.width, size.height);
							mDisplay.setPreferredSize(d);
							mDisplay.setScalingFactors(scaleX, scaleY);
							System.out.println("handleNewImage: " + size.width + "x" + size.height); //$NON-NLS-1$ //$NON-NLS-2$
							oldImageDimension = size;
							firstDraw = true;
						}
						
						mDisplay.setSize(size);
						mDisplay.handleNewImage(size, image, landscape);
						DroidReplicantFrame.this.pack();
						
						if (firstDraw) {
							DroidReplicantFrame.this.setCenterLocationOnScreen();
						}
					}
				});
	}
	
	@Override
	public void screenStatus(boolean isOn) {

		showWakeupPanel(!isOn);
	}

	private void startRecording() 
	{
		if (mIsVideoRecording)
			return;
		
		JFileChooser jFileChooser = new JFileChooser(retrievePathFromPrefs(VIDEO_DIR_PREF_KEY, "")); //$NON-NLS-1$
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Video file", "mov"); //$NON-NLS-1$ //$NON-NLS-2$
		jFileChooser.setFileFilter(filter);
		
		int returnVal = jFileChooser.showSaveDialog(DroidReplicantFrame.this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			mInjector.getScreencapture().startRecording(jFileChooser.getSelectedFile());
			persistPath(VIDEO_DIR_PREF_KEY, jFileChooser.getSelectedFile().getParent());
			mIsVideoRecording = true;
			showRecordingPanel(true);
		}
	}
	
	private void showWakeupPanel(boolean show) {
		
		mWakeUpPanel.setVisible(show);
		pack();
	}

	private void showRecordingPanel(boolean show) {
		
		mRecordingPanel.setVisible(show);
		mGrabVideoMenuItem.setEnabled(!show);
		pack();
	}
	
	private void stopRecording() 
	{
		mIsVideoRecording = false;
		showRecordingPanel(false);		
		mInjector.getScreencapture().stopRecording();
	}


	private void takeScreenshot() 
	{		
		BufferedImage screenshot = null;
		
		screenshot = mInjector.getScreencapture().takeScreenshot();
		Toolkit.getDefaultToolkit().beep();
		
		JFileChooser jfcFileChooser = new JFileChooser(retrievePathFromPrefs(SCREENSHOT_DIR_PREF_KEY, "")); //$NON-NLS-1$

		if (screenshot == null) {
			System.out.println("Could not get the screenshot through ADB!"); //$NON-NLS-1$
			return;
		}

		int returnVal = jfcFileChooser.showSaveDialog(DroidReplicantFrame.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIO.write(screenshot, "PNG", jfcFileChooser.getSelectedFile()); //$NON-NLS-1$
				persistPath(SCREENSHOT_DIR_PREF_KEY, jfcFileChooser.getSelectedFile().getParent());
			}
			catch (IOException exception) {
				System.out.println("Could not save screenshot!"); //$NON-NLS-1$
			}
		}
	}
	
	private void setCenterLocationOnScreen() {
		this.pack();
		setLocation(computeLocation(this, GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint()));
	}
	
	private Point computeLocation(Component c, Point center) {
		
		double offsetX = c.getWidth() / 2.0;
		double offsetY = c.getHeight() / 2.0;
		
		return new Point((int) (center.getX() - offsetX), (int) (center.getY() - offsetY));
	}

	@Override
	public void ancestorAdded(AncestorEvent arg0) {
		pack();
	}

	@Override
	public void ancestorMoved(AncestorEvent arg0) {
		pack();
	}

	@Override
	public void ancestorRemoved(AncestorEvent arg0) {
		pack();
	}
}

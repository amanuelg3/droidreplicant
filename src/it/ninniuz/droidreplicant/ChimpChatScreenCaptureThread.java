package it.ninniuz.droidreplicant;

import it.ninniuz.droidreplicant.DisplayStatusDetectorThread.IScreenStatusReceiver;
import it.ninniuz.droidreplicant.recording.QuickTimeOutputStream;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.android.chimpchat.adb.AdbChimpImage;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpImage;

public class ChimpChatScreenCaptureThread extends Thread implements ScreenCaptureInterface, IScreenStatusReceiver
{
	private BufferedImage image;
	private Dimension size;
	private IChimpDevice device;
	private boolean landscape = false;
	private ScreenCaptureListener listener = null;
	private Dimension screenSize = null;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private boolean shouldScale = false;
	
	private QuickTimeOutputStream qos;
	
	private boolean mScreenOn;
	private DisplayStatusDetectorThread mDisplayStatusDetector;
	
	@SuppressWarnings("unused")
	private ChimpChatScreenCaptureThread() {}
	
	public ChimpChatScreenCaptureThread(IChimpDevice device) 
	{
		super();
		this.device = device;
		image = null;
		size = new Dimension();
		mScreenOn = true;
		
		mDisplayStatusDetector = new DisplayStatusDetectorThread((DroidReplicantDevice) device);
		mDisplayStatusDetector.addScreenStatusReceiver(this);
	}

	@Override
	public ScreenCaptureListener getListener() 
	{
		return listener;
	}

	@Override
	public void setListener(ScreenCaptureListener listener) 
	{
		this.listener = listener;
	}
	
	public void addScreenStatusReceiver(IScreenStatusReceiver receiver) {
		
		mDisplayStatusDetector.addScreenStatusReceiver(receiver);
	}

	@Override
	public Dimension getPreferredSize() 
	{
		return this.size;
	}

	@Override
	public void startRecording(File f) {
		// TODO Auto-generated method stub
		try {
			if(!f.getName().toLowerCase().endsWith(".mov")) { //$NON-NLS-1$
				f = new File(f.getAbsolutePath() + ".mov"); //$NON-NLS-1$
			}
			qos = new QuickTimeOutputStream(f, QuickTimeOutputStream.VideoFormat.JPG);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		qos.setVideoCompressionQuality(1f);
		qos.setTimeScale(30); // 30 fps
	}

	@Override
	public void stopRecording() {
		// TODO Auto-generated method stub
		try {
			QuickTimeOutputStream o = qos;
			qos = null;
			o.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public BufferedImage takeScreenshot() 
	{
		BufferedImage deviceImage = device.takeSnapshot().getBufferedImage();
		return resizeImage(deviceImage, deviceImage.getWidth(), deviceImage.getHeight());
	}

	@Override
	public void toogleOrientation() {
		// TODO Auto-generated method stub

	}
	
	public void display(final IChimpImage displayImage) 
	{
		if (!mScreenOn) {
			image = createBlackImage(displayImage.getBufferedImage());
		}
		else {	
			AdbChimpImage chimpImage = (AdbChimpImage) displayImage;
			if (chimpImage.getRawImage().bpp == 32) {
				image = correctAlpha(displayImage.getBufferedImage());
			}
			else {
				image = displayImage.getBufferedImage();
			}
		}
				
		if (screenSize == null)
		{
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screenSize = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight() - 250);
		}
		
		if (shouldScale == false && (image.getWidth() > screenSize.getWidth() || image.getHeight() > screenSize.getHeight()))
		{
			shouldScale = true;
		}
		
		if (shouldScale) {
		
			image = resizeImage(image);
		}
		
		size.setSize(image.getWidth(), image.getHeight());
		
		try {
			if (qos != null && mScreenOn) {
				// We need to call resizeImage here to convert the image to RGB with a default color space
				// since otherwise the ImageIO plugin will complain about a bogus input colorspace
				// TODO: add another function with a proper name
				qos.writeFrame(resizeImage(image, image.getWidth(), image.getHeight()), 10);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if (listener != null) 
		{
			SwingUtilities.invokeLater(new Runnable() 
			{
				public void run() 
				{
					listener.handleNewImage(size, image, landscape, scaleX, scaleY);
					// jp.handleNewImage(size, image, landscape);
				}
			});
		}
	}
	
	/* Some device will report a RawImage.bpp == 32 and will set the alpha channel to 0x00 (transparent).
	 * Hence we need to correct the alpha value to 1.
	 * Probably there is a better way to do this.
	 * */
	private BufferedImage correctAlpha(BufferedImage chimpImage) {
		
		/* Let's try and set alpha to 0xff */
		int width = chimpImage.getWidth();
		int height = chimpImage.getHeight();
		
		BufferedImage corrected = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = chimpImage.getRGB(x, y);
				corrected.setRGB(x, y, argb | 0xFF000000);
			}
		}
		
		return corrected;
	}

	private BufferedImage createBlackImage(BufferedImage displayImage) {
		
		int width = displayImage.getWidth();
		int height = displayImage.getHeight();
		
		BufferedImage blackImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Paint scaled version of image to new image
		Graphics2D graphics2D = blackImage.createGraphics();
		graphics2D.setColor(Color.black);

		graphics2D.drawImage(blackImage, 0, 0, width, height, null);

		// clean up
		graphics2D.dispose();
		
		return blackImage;
	}

	private BufferedImage resizeImage(BufferedImage image) 
	{
		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		double aspectRatio = imageWidth / imageHeight;
		
		double resultWidth = imageWidth;
		double resultHeight = imageHeight;
		
		if (resultHeight > screenSize.getHeight())
		{
			resultHeight = screenSize.getHeight();
			resultWidth = resultHeight * aspectRatio;
		}
		
		if (resultWidth > screenSize.getWidth())
		{
			resultWidth = screenSize.getWidth();
			resultHeight = resultWidth * (1 / aspectRatio);
		}
		
		scaleX = resultWidth / imageWidth;
		scaleY = resultHeight / imageHeight;
		
//		return resizeImageWithScale(image, scaleX, scaleY);
		
		return resizeImage(image, (int) resultWidth, (int) resultHeight);
	}

//	@SuppressWarnings("restriction")
//	private BufferedImage resizeImageWithScale(BufferedImage image, double scaleX, double scaleY) {
//
//		RenderedOp renderedOp = ScaleDescriptor.create(resizeImage(image, image.getWidth(), image.getHeight()), new Float(scaleX), new Float(scaleY),
//                new Float(0.0f), new Float(0.0f), Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
//        
//        return renderedOp.getAsBufferedImage();
//	}

	private BufferedImage resizeImage(BufferedImage image, int width, int height) 
	{
		// Create new (blank) image of required (scaled) size
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Paint scaled version of image to new image
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, width, height, null);

		// clean up
		graphics2D.dispose();
		
		return scaledImage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() 
	{

		mDisplayStatusDetector.start();
		
		// TODO Auto-generated method stub
		do {
			try {
				
				boolean ok = fetchImage();
				if(!ok)
					break; 
			} catch (Exception e) 
			{
				e.printStackTrace();
			}

		} while (true);
	}

	private boolean fetchImage() 
	{
		if (device == null) 
		{
			// device not ready
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}

		IChimpImage displayImage = null;
		
		synchronized (device) {

			displayImage = device.takeSnapshot();
		}
		
		if (displayImage != null) {
			
			display(displayImage);
		} else {
			System.out.println("failed getting screenshot through ADB ok"); //$NON-NLS-1$
		}
		
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			return false;
		}

		return true;
	}

	@Override
	public void screenStatus(boolean isOn) {

		mScreenOn = isOn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		super.interrupt();
		mDisplayStatusDetector.interrupt();
	}
}

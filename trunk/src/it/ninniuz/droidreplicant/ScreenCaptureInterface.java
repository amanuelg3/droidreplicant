package it.ninniuz.droidreplicant;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

public interface ScreenCaptureInterface {

	void setListener(ScreenCaptureListener screenCaptureListener);

	void toogleOrientation();

	void startRecording(File selectedFile);

	void stopRecording();

	BufferedImage takeScreenshot();

	ScreenCaptureListener getListener();

	Dimension getPreferredSize();

}

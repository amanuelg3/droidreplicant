package it.ninniuz.droidreplicant;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public interface ScreenCaptureListener {

	void handleNewImage(Dimension size, BufferedImage bufferedImage, boolean landscape, double scaleX, double scaleY);

}

package it.ninniuz.droidreplicant;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JLabel;
import java.awt.GridLayout;

public class InstallApplicationProgress extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2932397275248299394L;

	/**
	 * Create the panel.
	 * @param droidControlFrame 
	 */
	public InstallApplicationProgress(JFrame droidControlFrame) {
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblWeAreInstalling = new JLabel(Messages.getString("InstallApplicationProgress.dialogWaitInstall")); //$NON-NLS-1$
		getContentPane().add(lblWeAreInstalling);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		getContentPane().add(progressBar);

		pack();
		setLocation(computeLocation(this, computeCenter(droidControlFrame)));
		setVisible(true);
	}
	
	private Point computeCenter(Component c) {
		Point location = c.getLocation();
		int h = c.getHeight();
		int w = c.getWidth();
		Point center = new Point((int) (location.getX() + w / 2.0), (int) (location.getY() + h / 2.0));
		return center;
	}
	
	private Point computeLocation(Component c, Point center) {
		
		double offsetX = c.getWidth() / 2.0;
		double offsetY = c.getHeight() / 2.0;
		
		return new Point((int) (center.getX() - offsetX), (int) (center.getY() - offsetY));
	}
}

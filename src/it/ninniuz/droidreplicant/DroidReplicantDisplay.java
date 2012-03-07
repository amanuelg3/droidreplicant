package it.ninniuz.droidreplicant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

public class DroidReplicantDisplay extends JPanel 
{
	private static final long serialVersionUID = 4159132468564841622L;

	int origX;
	int origY;
	boolean landscape;

	Dimension size = null;
	Image image = null;

	private double scaleX = 1.0;
	private double scaleY = 1.0;

	public DroidReplicantDisplay() 
	{
		this.setFocusable(true);
		this.setLayout(new BorderLayout());
	}

	public void handleNewImage(Dimension size, Image image, boolean landscape) 
	{
		this.landscape = landscape;
		this.size = size;
		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		if (size == null || size.height == 0) {
			return;
		}

		g.clearRect(0, 0, getWidth(), getHeight());

		origX = 0;
		origY = 0;

		g.drawImage(image, origX, origY, size.width, size.height, this);
	}

	public Point getRawPoint(Point p1) 
	{
		Point p2 = new Point();
		p2.x = (int) ((p1.x - origX) * (1 / scaleX));
		p2.y = (int) ((p1.y - origY) * (1 / scaleY));
		
		return p2;
	}

	public void setScalingFactors(double scaleX, double scaleY) 
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
}

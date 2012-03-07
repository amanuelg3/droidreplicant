package it.ninniuz.droidreplicant;

public class ScreenResolution {

	private int height;
	private int width;
	
	public ScreenResolution(int width, int height) 
	{
		this.height = height;
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}


	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return this.width + "x" + this.height; //$NON-NLS-1$
	}
	
	
}

package it.ninniuz.droidreplicant.explorer;

import it.ninniuz.droidreplicant.Messages;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;

public class FileDetailsContainer extends Container {

	public static final String FILE_DETAIL_NAME = Messages.getString("FileDetailsContainer.name"); //$NON-NLS-1$
	public static final String FILE_DETAIL_KIND = Messages.getString("FileDetailsContainer.kind"); //$NON-NLS-1$
	public static final String FILE_DETAIL_SIZE = Messages.getString("FileDetailsContainer.size"); //$NON-NLS-1$
	public static final String FILE_DETAIL_DATE = Messages.getString("FileDetailsContainer.date"); //$NON-NLS-1$
	public static final String FILE_DETAIL_TIME = Messages.getString("FileDetailsContainer.time"); //$NON-NLS-1$
	public static final String FILE_DETAIL_PERMISSIONS = Messages.getString("FileDetailsContainer.permissions"); //$NON-NLS-1$
	public static final String FILE_DETAIL_INFO = Messages.getString("FileDetailsContainer.info"); //$NON-NLS-1$
	private int mGridX;
	private int mGridY;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -813472714010366259L;

	public FileDetailsContainer() {
		super();
		this.setLayout(new GridBagLayout());
		mGridX = 0;
		mGridY = 0;
	}
	
	public void addFileDetail(String name, String value) {
		
		JLabel lblKey = new JLabel(name);
		lblKey.setFont(new Font("Lucida Grande", Font.BOLD, 13)); //$NON-NLS-1$
		GridBagConstraints gbc_lblKey = new GridBagConstraints();
		gbc_lblKey.anchor = GridBagConstraints.EAST;
		gbc_lblKey.insets = new Insets(0, 0, 5, 5);
		gbc_lblKey.gridx = mGridX;
		gbc_lblKey.gridy = mGridY;
		mGridX += 1;
		add(lblKey, gbc_lblKey);
		
		JLabel lblValue = new JLabel(value);
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.anchor = GridBagConstraints.WEST;
		gbc_lblValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblValue.gridx = mGridX;
		gbc_lblValue.gridy = mGridY;
		mGridY += 1;
		mGridX = 0;
		add(lblValue, gbc_lblValue);
		
	}
}

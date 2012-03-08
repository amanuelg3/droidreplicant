package it.ninniuz.droidreplicant;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import com.android.chimpchat.core.IChimpDevice;
import com.android.ddmlib.Log;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmailComposerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5075245367958212209L;
	private JPanel contentPane;
	private JTextField toField;
	private JTextField subjectField;
	private JLabel lblText;
	private JTextArea emailTextArea;
	private JScrollPane scrollPane;
	private JButton btnCancel;
	private JButton btnOk;
	private IChimpDevice mDevice;

	/**
	 * Create the frame.
	 */
	public EmailComposerFrame() 
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblTo = new JLabel(Messages.getString("EmailComposerFrame.lblToField")); //$NON-NLS-1$
		GridBagConstraints gbc_lblTo = new GridBagConstraints();
		gbc_lblTo.anchor = GridBagConstraints.WEST;
		gbc_lblTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblTo.gridx = 0;
		gbc_lblTo.gridy = 0;
		contentPane.add(lblTo, gbc_lblTo);
		
		toField = new JTextField();
		GridBagConstraints gbc_toField = new GridBagConstraints();
		gbc_toField.gridwidth = 3;
		gbc_toField.insets = new Insets(0, 0, 5, 5);
		gbc_toField.fill = GridBagConstraints.HORIZONTAL;
		gbc_toField.gridx = 0;
		gbc_toField.gridy = 1;
		contentPane.add(toField, gbc_toField);
		toField.setColumns(10);
		
		JLabel lblSubject = new JLabel(Messages.getString("EmailComposerFrame.lblSubjectField")); //$NON-NLS-1$
		GridBagConstraints gbc_lblSubject = new GridBagConstraints();
		gbc_lblSubject.anchor = GridBagConstraints.WEST;
		gbc_lblSubject.insets = new Insets(0, 0, 5, 5);
		gbc_lblSubject.gridx = 0;
		gbc_lblSubject.gridy = 2;
		contentPane.add(lblSubject, gbc_lblSubject);
		
		subjectField = new JTextField();
		GridBagConstraints gbc_subjectField = new GridBagConstraints();
		gbc_subjectField.gridwidth = 3;
		gbc_subjectField.insets = new Insets(0, 0, 5, 5);
		gbc_subjectField.fill = GridBagConstraints.HORIZONTAL;
		gbc_subjectField.gridx = 0;
		gbc_subjectField.gridy = 3;
		contentPane.add(subjectField, gbc_subjectField);
		subjectField.setColumns(10);
		
		lblText = new JLabel(Messages.getString("EmailComposerFrame.lblEmailField")); //$NON-NLS-1$
		GridBagConstraints gbc_lblText = new GridBagConstraints();
		gbc_lblText.anchor = GridBagConstraints.WEST;
		gbc_lblText.insets = new Insets(0, 0, 5, 5);
		gbc_lblText.gridx = 0;
		gbc_lblText.gridy = 4;
		contentPane.add(lblText, gbc_lblText);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		emailTextArea = new JTextArea();
		scrollPane.setViewportView(emailTextArea);
		
		btnCancel = new JButton(Messages.getString("EmailComposerFrame.bntCancel")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 6;
		contentPane.add(btnCancel, gbc_btnCancel);
		
		btnOk = new JButton(Messages.getString("EmailComposerFrame.btnOk")); //$NON-NLS-1$
		btnOk.setSelected(true);
		btnOk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				composeEmailOnDevice(toField.getText(), subjectField.getText(), emailTextArea.getText());
				dispose();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 6;
		contentPane.add(btnOk, gbc_btnOk);
		
		setVisible(true);
	}

	public EmailComposerFrame(IChimpDevice mDevice) {

		this();
		this.mDevice = mDevice;
	}
	
	private void composeEmailOnDevice(String toAddresses, String emailSubject, String emailText) {
		
		 Map<String, Object> extras = new HashMap<String, Object>();
		 if (!isEmpty(emailSubject)) {
			 extras.put("android.intent.extra.SUBJECT", getStringForExtra(emailSubject)); //$NON-NLS-1$
		 }
		 if (!isEmpty(emailText)) {
			 extras.put("android.intent.extra.TEXT", getStringForExtra(emailText)); //$NON-NLS-1$
		 }
		
		String emailDataUri = prepareEmailDataUri(toAddresses, emailSubject, emailText);
		String emailDataUriEncoded = emailDataUri;

		Log.e("", emailDataUriEncoded); //$NON-NLS-1$
		
		new StartActivityTask(mDevice, null, "android.intent.action.VIEW", emailDataUri, null, null, extras, null, 0).execute(); //$NON-NLS-1$
		
//		mDevice.startActivity(null, "android.intent.action.VIEW", "sms:", null, new ArrayList<String>(), extras, null, 0);
	}

	private boolean isEmpty(String emailSubject) {
		
		return emailSubject.trim().isEmpty();
	}

	private String getStringForExtra(String text) 
	{
		String delimiter = "\""; //$NON-NLS-1$
		return new StringBuilder().append(delimiter).append(text).append(delimiter).toString();
	}

	private String prepareEmailDataUri(String toAddresses, String emailSubject, String emailText) {
		
		StringBuilder builder = new StringBuilder("mailto:"); //$NON-NLS-1$
		
//		builder.append("?to=");
		
		if (toAddresses != null && toAddresses.trim().length() > 0) {
			
			builder.append(toAddresses.trim());
		}
		
//		builder.append("&subject=");
//		
//		if (emailSubject != null && emailSubject.trim().length() > 0) {
//			
//			builder.append(emailSubject.trim());
//		}
//			
//		builder.append("&body=");
//		
//		if (emailText != null && emailText.trim().length() > 0) {
//			
//			builder.append(emailText.trim());
//		}
		
		return builder.toString();
	}
}

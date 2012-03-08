package it.ninniuz.droidreplicant;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JTextArea;

import com.android.chimpchat.core.IChimpDevice;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SmsComposerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3501262802935529071L;
	private JPanel contentPane;
	private JTextField phoneTextField;
	private IChimpDevice mDevice;

	/**
	 * Create the frame.
	 */
	public SmsComposerFrame(IChimpDevice device) 
	{
		setTitle(Messages.getString("SmsComposerFrame.title")); //$NON-NLS-1$
		mDevice = device;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		});
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblPhoneNumber = new JLabel(Messages.getString("SmsComposerFrame.lblPhoneNumber")); //$NON-NLS-1$
		GridBagConstraints gbc_lblPhoneNumber = new GridBagConstraints();
		gbc_lblPhoneNumber.anchor = GridBagConstraints.WEST;
		gbc_lblPhoneNumber.insets = new Insets(0, 0, 5, 5);
		gbc_lblPhoneNumber.gridx = 0;
		gbc_lblPhoneNumber.gridy = 0;
		contentPane.add(lblPhoneNumber, gbc_lblPhoneNumber);
		
		phoneTextField = new JTextField();
		GridBagConstraints gbc_phoneTextField = new GridBagConstraints();
		gbc_phoneTextField.fill = GridBagConstraints.VERTICAL;
		gbc_phoneTextField.anchor = GridBagConstraints.WEST;
		gbc_phoneTextField.insets = new Insets(0, 0, 5, 5);
		gbc_phoneTextField.gridx = 0;
		gbc_phoneTextField.gridy = 1;
		contentPane.add(phoneTextField, gbc_phoneTextField);
		phoneTextField.setColumns(20);
		
		JLabel lblSmsText = new JLabel(Messages.getString("SmsComposerFrame.lblSmsText")); //$NON-NLS-1$
		GridBagConstraints gbc_lblSmsText = new GridBagConstraints();
		gbc_lblSmsText.insets = new Insets(0, 0, 5, 5);
		gbc_lblSmsText.anchor = GridBagConstraints.WEST;
		gbc_lblSmsText.gridx = 0;
		gbc_lblSmsText.gridy = 2;
		contentPane.add(lblSmsText, gbc_lblSmsText);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		final JTextArea smsTextArea = new JTextArea();
		smsTextArea.setWrapStyleWord(true);
		smsTextArea.setLineWrap(true);
		scrollPane.setViewportView(smsTextArea);
		
		JButton btnCancel = new JButton(Messages.getString("SmsComposerFrame.btnCancel")); //$NON-NLS-1$
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
		gbc_btnCancel.gridy = 4;
		contentPane.add(btnCancel, gbc_btnCancel);
		
		JButton btnOk = new JButton(Messages.getString("SmsComposerFrame.btnOk")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				composeSmsOnDevice(phoneTextField.getText(), smsTextArea.getText());
				dispose();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 4;
		contentPane.add(btnOk, gbc_btnOk);
		
		btnOk.setSelected(true);
		
		setVisible(true);
	}
	
	private void composeSmsOnDevice(String phoneNumber, String smsText) {

		Map<String, Object> extras = new HashMap<String, Object>();
		if (smsText.trim().length() > 0) {
			extras.put("sms_body", smsText); //$NON-NLS-1$
		}
		
		new StartActivityTask(mDevice, null, "android.intent.action.VIEW", "sms:" + phoneNumber, null, new ArrayList<String>(), extras, null, 0).execute(); //$NON-NLS-1$ //$NON-NLS-2$
	}
}

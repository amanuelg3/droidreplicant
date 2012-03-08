package it.ninniuz.droidreplicant.explorer;

import it.ninniuz.droidreplicant.Messages;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.android.ddmlib.FileListingService;
import com.android.ddmlib.FileListingService.FileEntry;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;
import com.android.ddmlib.SyncService;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.regex.Pattern;

public class DeviceExplorerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2863405142207311285L;
	
	DroidTree jt;
	JSplitPane jSplitPane;
	IDevice mCurrentDevice;
	
	@SuppressWarnings("serial")
	private class DroidTree extends JTree {

		public DroidTree(DeviceTreeModel deviceTreeModel) {
			
			super(deviceTreeModel);
		}

		/* (non-Javadoc)
		 * @see javax.swing.JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		@Override
		public String convertValueToText(Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {

			FileEntry entry = (FileEntry) value;
			return entry.getName();
		}
	}

	public DeviceExplorerFrame(IDevice device) 
	{
		this.mCurrentDevice = device;
		
		setTitle(Messages.getString("DeviceExplorerFrame.title")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());

		jt = new DroidTree(new DeviceTreeModel(device));
		jt.setRootVisible(true);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		final JButton btnPush = new JButton(""); //$NON-NLS-1$
		btnPush.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				Log.e("actionPerformed", ((FileEntry) jt.getLastSelectedPathComponent()).getName()); //$NON-NLS-1$
				pushIntoSelection();
			}
		});
		btnPush.setIcon(new ImageIcon(DeviceExplorerFrame.class.getResource("/it/ninniuz/droidreplicant/resources/push.png"))); //$NON-NLS-1$
		btnPush.setEnabled(false);
		toolBar.add(btnPush);
		
		final JButton btnPull = new JButton(""); //$NON-NLS-1$
		btnPull.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				pullSelection();
			}
		});
		btnPull.setIcon(new ImageIcon(DeviceExplorerFrame.class.getResource("/it/ninniuz/droidreplicant/resources/pull.png"))); //$NON-NLS-1$
		btnPull.setEnabled(false);
		toolBar.add(btnPull);
		
		final JPanel nodeDetailsPane = new JPanel();

		JScrollPane deviceFileSystemScrollPane = new JScrollPane(jt);
		final JScrollPane nodeDetailsScrollPane =  new JScrollPane(nodeDetailsPane);
		nodeDetailsScrollPane.setVisible(true);
		
//		GridBagLayout gbl_nodeDetailsPane = new GridBagLayout();
//		gbl_nodeDetailsPane.columnWidths = new int[]{0, 0, 0};
//		gbl_nodeDetailsPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
//		gbl_nodeDetailsPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
//		gbl_nodeDetailsPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
//
		jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, deviceFileSystemScrollPane, nodeDetailsScrollPane);
		
		getContentPane().add(jSplitPane, BorderLayout.CENTER);
		setSize(640, 480);
		setLocationRelativeTo(null);
		
		jt.addTreeSelectionListener(new TreeSelectionListener() {
			
			public void valueChanged(TreeSelectionEvent e) {
				
				FileEntry selectedNode = (FileEntry) jt.getLastSelectedPathComponent();
				Log.e("valueChanged", selectedNode.getName()); //$NON-NLS-1$
				
				nodeDetailsPane.removeAll();
				
				if (!selectedNode.isRoot()) {
					if (selectedNode.isDirectory()) {
						btnPush.setEnabled(true);
						btnPull.setEnabled(false);
					}
					// The node is a leaf, i.e. a simple file
					else {
						btnPush.setEnabled(false);
						btnPull.setEnabled(true);
					}

					Container c = getFileDetailsContainer(selectedNode);
					nodeDetailsPane.add(c);

				}
				else {
					btnPush.setEnabled(false);
					btnPull.setEnabled(false);
				}
				
				nodeDetailsScrollPane.validate();
				nodeDetailsScrollPane.repaint();
			}
		});
	}
	
    protected FileDetailsContainer getFileDetailsContainer(FileEntry selectedNode) {

    	FileDetailsContainer details = new FileDetailsContainer();
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_NAME, selectedNode.getName());
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_KIND, getFileTypeDescription(selectedNode));
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_SIZE, selectedNode.getSize());
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_DATE, selectedNode.getDate());
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_TIME, selectedNode.getTime());
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_PERMISSIONS, selectedNode.getPermissions());
		details.addFileDetail(FileDetailsContainer.FILE_DETAIL_INFO, selectedNode.getInfo());
		
		return details;
	}
    
    private String getFileTypeDescription(FileEntry node) {
    	
    	switch (node.getType()) {
        /** Entry type: File */
    	case FileListingService.TYPE_FILE:
    		return node.isAppFileName() ? Messages.getString("DeviceExplorerFrame.fileTypeAndroidApp") : Messages.getString("DeviceExplorerFrame.fileTypeFile"); //$NON-NLS-1$ //$NON-NLS-2$
		/** Entry type: Directory */
    	case FileListingService.TYPE_DIRECTORY:
    		return Messages.getString("DeviceExplorerFrame.fileTypeDir"); //$NON-NLS-1$
		default:
        	return Messages.getString("DeviceExplorerFrame.fileTypeOther"); //$NON-NLS-1$
    	}
    }

	/**
     * Pull the current selection on the local drive. This method displays
     * a dialog box to let the user select where to store the file(s) and
     * folder(s).
     */
    public void pullSelection() {
        // get the selection
        TreePath[] items = jt.getSelectionPaths();

        // name of the single file pull, or null if we're pulling a directory
        // or more than one object.
        String filePullName = null;
        FileEntry singleEntry = null;

        // are we pulling a single file?
        if (items.length == 1) {
            singleEntry = (FileEntry) items[0].getLastPathComponent();
            if (singleEntry.getType() == FileListingService.TYPE_FILE) {
                filePullName = singleEntry.getName();
            }
        }

//        // where do we save by default?
//        String defaultPath = mDefaultSave;
//        if (defaultPath == null) {
//            defaultPath = System.getProperty("user.home"); //$NON-NLS-1$
//        }

        if (filePullName != null) {
            JFileChooser fileDialog = new JFileChooser();

            fileDialog.setDialogTitle(Messages.getString("DeviceExplorerFrame.dialogPullFile")); //$NON-NLS-1$
            fileDialog.setCurrentDirectory(null);
            fileDialog.setSelectedFile(new File(filePullName));

            int returnValue = fileDialog.showSaveDialog(this);
            
            if (returnValue == JFileChooser.APPROVE_OPTION) {
            	pullFile(singleEntry, fileDialog.getSelectedFile().getAbsolutePath());
            }
            else {
            	return;
            }
        }
//            
//        } else {
//            DirectoryDialog directoryDialog = new DirectoryDialog(mParent.getShell(), SWT.SAVE);
//
//            directoryDialog.setText("Get Device Files/Folders");
//            directoryDialog.setFilterPath(defaultPath);
//
//            String directoryName = directoryDialog.open();
//            if (directoryName != null) {
//                pullSelection(items, directoryName);
//            }
//        }
    }

    /**
     * Pulls a file from a device.
     * @param remote the remote file on the device
     * @param local the destination filepath
     */
    private void pullFile(final FileEntry remote, final String local) {
        try {
            final SyncService sync = mCurrentDevice.getSyncService();
            if (sync != null) {
//                new ProgressMonitorDialog(mParent.getShell()).run(true, true,
//                        new IRunnableWithProgress() {
//                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//                        SyncResult result = sync.pullFile(remote, local, new SyncProgressMonitor(
//                                monitor, String.format("Pulling %1$s from the device",
//                                        remote.getName())));
//                        if (result.getCode() != SyncService.RESULT_OK) {
//                            DdmConsole.printErrorToConsole(String.format(
//                                    "Failed to pull %1$s: %2$s", remote, result.getMessage()));
//                        }
            	sync.pullFile(remote, local, SyncService.getNullProgressMonitor());

            	sync.close();
//                    }
//                });
            }
        } catch (Exception e) {
//            DdmConsole.printErrorToConsole( "Failed to pull selection");
//            DdmConsole.printErrorToConsole(e.getMessage());
        }
    }

    /**
     * Pushes several files and directory into a remote directory.
     * @param localFiles
     * @param remoteDirectory
     */
    @SuppressWarnings("unused")
	private void pushFiles(final String[] localFiles, final FileEntry remoteDirectory) {
        try {
            final SyncService sync = mCurrentDevice.getSyncService();
            if (sync != null) {
//                new ProgressMonitorDialog(mParent.getShell()).run(true, true,
//                        new IRunnableWithProgress() {
//                    public void run(IProgressMonitor monitor)
//                            throws InvocationTargetException,
//                            InterruptedException {
//                        SyncResult result = sync.push(localFiles, remoteDirectory,
//                                    new SyncProgressMonitor(monitor,
//                                            "Pushing file(s) to the device"));
//                        if (result.getCode() != SyncService.RESULT_OK) {
//                            DdmConsole.printErrorToConsole(String.format(
//                                    "Failed to push the items: %1$s", result.getMessage()));
//                        }
            	sync.push(localFiles, remoteDirectory, SyncService.getNullProgressMonitor());
            	((DeviceTreeModel) jt.getModel()).notifyInserted(remoteDirectory);
            	
            	sync.close();
//                    }
//                });
            }
        } catch (Exception e) {
//            DdmConsole.printErrorToConsole("Failed to push the items");
//            DdmConsole.printErrorToConsole(e.getMessage());
        }
    }

    /**
     * Pushes a file on a device.
     * @param local the local filepath of the file to push
     * @param remoteDirectory the remote destination directory on the device
     */
    private void pushFile(final String local, final FileEntry remoteDirectory) {
        try {
            final SyncService sync = mCurrentDevice.getSyncService();
            if (sync != null) {
//                new ProgressMonitorDialog(mParent.getShell()).run(true, true,
//                        new IRunnableWithProgress() {
//                    public void run(IProgressMonitor monitor)
//                            throws InvocationTargetException,
//                            InterruptedException {
//                        // get the file name
//                        String[] segs = local.split(Pattern.quote(File.separator));
//                        String name = segs[segs.length-1];
//                        String remoteFile = remoteDirectory + FileListingService.FILE_SEPARATOR
//                                + name;
//
//                        SyncResult result = sync.pushFile(local, remoteFile,
//                                    new SyncProgressMonitor(monitor,
//                                            String.format("Pushing %1$s to the device.", name)));
//                        if (result.getCode() != SyncService.RESULT_OK) {
//                            DdmConsole.printErrorToConsole(String.format(
//                                    "Failed to push %1$s on %2$s: %3$s",
//                                    name, mCurrentDevice.getSerialNumber(), result.getMessage()));
//                        }
//
//                        sync.close();
//                    }
//                });
              String[] segs = local.split(Pattern.quote(File.separator));
              String name = segs[segs.length-1];
              String remoteFile = remoteDirectory.getFullPath() + FileListingService.FILE_SEPARATOR + name;

              sync.pushFile(local, remoteFile, SyncService.getNullProgressMonitor());
              
              ((DeviceTreeModel) jt.getModel()).notifyInserted(remoteDirectory);
            }
        } catch (Exception e) {
//            DdmConsole.printErrorToConsole("Failed to push the item(s).");
//            DdmConsole.printErrorToConsole(e.getMessage());
        }
    }
    
//    /**
//     * Pull the current selection on the local drive. This method displays
//     * a dialog box to let the user select where to store the file(s) and
//     * folder(s).
//     */
//    public void pullSelection() {
//        // get the selection
//        TreeItem[] items = mTree.getSelection();
//
//        // name of the single file pull, or null if we're pulling a directory
//        // or more than one object.
//        String filePullName = null;
//        FileEntry singleEntry = null;
//
//        // are we pulling a single file?
//        if (items.length == 1) {
//            singleEntry = (FileEntry)items[0].getData();
//            if (singleEntry.getType() == FileListingService.TYPE_FILE) {
//                filePullName = singleEntry.getName();
//            }
//        }
//
//        // where do we save by default?
//        String defaultPath = mDefaultSave;
//        if (defaultPath == null) {
//            defaultPath = System.getProperty("user.home"); //$NON-NLS-1$
//        }
//
//        if (filePullName != null) {
//            FileDialog fileDialog = new FileDialog(mParent.getShell(), SWT.SAVE);
//
//            fileDialog.setText("Get Device File");
//            fileDialog.setFileName(filePullName);
//            fileDialog.setFilterPath(defaultPath);
//
//            String fileName = fileDialog.open();
//            if (fileName != null) {
//                mDefaultSave = fileDialog.getFilterPath();
//
//                pullFile(singleEntry, fileName);
//            }
//        } else {
//            DirectoryDialog directoryDialog = new DirectoryDialog(mParent.getShell(), SWT.SAVE);
//
//            directoryDialog.setText("Get Device Files/Folders");
//            directoryDialog.setFilterPath(defaultPath);
//
//            String directoryName = directoryDialog.open();
//            if (directoryName != null) {
//                pullSelection(items, directoryName);
//            }
//        }
//    }
//
    /**
     * Push new file(s) and folder(s) into the current selection. Current
     * selection must be single item. If the current selection is not a
     * directory, the parent directory is used.
     * This method displays a dialog to let the user choose file to push to
     * the device.
     */
    public void pushIntoSelection() {
    	
        // get the name of the object we're going to pull
        FileEntry entry = (FileEntry) jt.getLastSelectedPathComponent();

        JFileChooser dlg = new JFileChooser();
        dlg.setMultiSelectionEnabled(false);
        
        String fileName;

        dlg.setDialogTitle(Messages.getString("DeviceExplorerFrame.dialogPushFile")); //$NON-NLS-1$

//        // There should be only one.
//        FileEntry entry = (FileEntry)items[0].getData();
//        dlg.setFileName(entry.getName());
//
//        String defaultPath = mDefaultSave;
//        if (defaultPath == null) {
//            defaultPath = System.getProperty("user.home"); //$NON-NLS-1$
//        }
//        dlg.setFilterPath(defaultPath);

        if (dlg.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = dlg.getSelectedFile();
        	fileName = selectedFile.getAbsolutePath();
        }
        else
        	return;
        
//        fileName = dlg.open();
        if (fileName != null) {
//            mDefaultSave = dlg.getFilterPath();

            // we need to figure out the remote path based on the current selection type.
            @SuppressWarnings("unused")
			String remotePath;
            FileEntry toRefresh = entry;
            if (entry.isDirectory()) {
                remotePath = entry.getFullPath();
            } else {
                toRefresh = entry.getParent();
                remotePath = toRefresh.getFullPath();
            }

            pushFile(fileName, toRefresh);
//          mTreeViewer.refresh(toRefresh);
        }
    }
}

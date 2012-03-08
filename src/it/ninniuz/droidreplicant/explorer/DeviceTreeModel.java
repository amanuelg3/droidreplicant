package it.ninniuz.droidreplicant.explorer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.android.ddmlib.FileListingService;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;
import com.android.ddmlib.FileListingService.FileEntry;

public class DeviceTreeModel implements TreeModel {

	private FileListingService mFileListingService;
	private List<TreeModelListener> mTreeModelListeners;
    
	public DeviceTreeModel(IDevice mDevice) {
		super();
		this.mTreeModelListeners = new ArrayList<TreeModelListener>();
		this.mFileListingService = mDevice.getFileListingService();
	}

	@Override
	public Object getRoot() {
		
		return mFileListingService.getRoot();
	}
	
	private Object[] getChildren(Object parentElement) {
	
		if (parentElement instanceof FileEntry) {
			FileEntry parentEntry = (FileEntry) parentElement;

			Object[] oldEntries = parentEntry.getCachedChildren();
			Object[] newEntries = mFileListingService.getChildren(parentEntry, true, null);

			if (newEntries != null) {
				return newEntries;
			} else {
				// if null was returned, this means the cache was not valid,
				// and a thread was launched for ls. sListingReceiver will be
				// notified with the new entries.
				return oldEntries;
			}
		}
		return new Object[0];
	}

	@Override
	public Object getChild(Object parent, int index) {

		return getChildren(parent)[index];
	}

	@Override
	public int getChildCount(Object parent) {

		return getChildren(parent).length;
	}

	@Override
	public boolean isLeaf(Object node) {
		
        if (node instanceof FileEntry) {
            FileEntry entry = (FileEntry) node;

            return entry.getType() != FileListingService.TYPE_DIRECTORY;
        }
        return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {

		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {

		synchronized (mTreeModelListeners) {
			mTreeModelListeners.add(l);
		}
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {

		synchronized (mTreeModelListeners) {
			mTreeModelListeners.remove(l);
		}
	}
	
	protected void notifyInserted(FileEntry root) {
		synchronized (mTreeModelListeners) {
			for (TreeModelListener l : mTreeModelListeners) {
				TreeModelEvent e = new TreeModelEvent(this, findPathToNode(root));
				l.treeStructureChanged(e);
				Log.e("", "called");  //$NON-NLS-1$//$NON-NLS-2$
			}
		}
	}
	
	private FileEntry[] findPathToNode(FileEntry node) {
		
		ArrayList<FileEntry> pathList = new ArrayList<FileEntry>();
		FileEntry parentNode = null;
		FileEntry currentNode = node;
		
		pathList.add(0, currentNode);
		
		while (!currentNode.getParent().isRoot()) {
			
			parentNode = currentNode.getParent();
			pathList.add(0, parentNode);
			currentNode = parentNode;
		}
		
		// Add the root node
		pathList.add(0, currentNode.getParent());
		
		return pathList.toArray(new FileEntry[0]);
	}

}

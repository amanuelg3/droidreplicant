/**
 * 
 */
package it.ninniuz.droidreplicant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import com.android.chimpchat.core.IChimpDevice;

/**
 * @author inq_targets_admin
 *
 */
public class StartActivityTask extends SwingWorker<Boolean, Void> {

	private IChimpDevice device;
	private Map<String, Object> extras;
	private String uri;
	private String action;
	private String data;
	private String mimetype;
	private List<String> categories;
	private String component;
	private int flags;
	
	@SuppressWarnings("unused")
	private StartActivityTask() {}
	
	public StartActivityTask(IChimpDevice device, String uri, String action, String data, String mimetype,
            List<String> categories, Map<String, Object> extras, String component, int flags) 
	{
		this.device = device;
		this.uri = uri;
		this.action = action;
		this.data = data;
		this.mimetype = mimetype;
		/* We need this to cope with IChimpDevice.startActivity faults in case categories is null */
		this.categories = (categories == null) ? new ArrayList<String>() : categories;
		this.extras = (extras == null) ? new HashMap<String, Object>() : extras;
		this.component = component;
		this.flags = flags;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Boolean doInBackground() throws Exception 
	{		
        boolean result = ((DroidReplicantDevice) device).startActivityWithResult(uri, action, data, mimetype, categories, extras, component, flags);
        
        setProgress(100);
        
		return result;
	}

 
}

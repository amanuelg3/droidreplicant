/**
 * 
 */
package it.ninniuz.droidreplicant;

/**
 * @author inq_targets_admin
 *
 */
public enum AdditionalPhysicalButton {
	CALL("KEYCODE_CALL"), //$NON-NLS-1$
	ENDCALL("KEYCODE_ENDCALL"), //$NON-NLS-1$
	POWER("KEYCODE_POWER"), //$NON-NLS-1$
	VOLUME_UP("KEYCODE_VOLUME_UP"), //$NON-NLS-1$
	VOLUME_DOWN("KEYCODE_VOLUME_DOWN"), //$NON-NLS-1$
	CAMERA("KEYCODE_CAMERA"); //$NON-NLS-1$
	
	private String keyName;
	
	private AdditionalPhysicalButton(String keyName) {
		this.keyName = keyName;
	}
	
	public String getKeyName() {
		return keyName;
	}
}

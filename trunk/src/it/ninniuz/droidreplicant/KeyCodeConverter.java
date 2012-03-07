package it.ninniuz.droidreplicant;

import java.awt.event.KeyEvent;

public class KeyCodeConverter {

	public static int getKeyCode(KeyEvent e) {
		
		char c = e.getKeyChar();
		int code = 0;
		
		if(Character.isLetter(c))
			code = ConstEvtKey.KEYCODE_A + (Character.toLowerCase(c)-'a');
		if(Character.isDigit(c))
			code = ConstEvtKey.KEYCODE_0 + (c-'0');
		
		if(c == '\n')
			code = ConstEvtKey.KEYCODE_ENTER;

		if(c == ' ')
			code = ConstEvtKey.KEYCODE_SPACE; 

		if(c == '\b')
			code = ConstEvtKey.KEYCODE_DEL; 

		if(c == '\t')
			code = ConstEvtKey.KEYCODE_TAB; 

		if(c == '/')
			code = ConstEvtKey.KEYCODE_SLASH; 

		if(c == '\\')
			code = ConstEvtKey.KEYCODE_BACKSLASH; 

		if(c == ',')
			code = ConstEvtKey.KEYCODE_COMMA; 

		if(c == ';')
			code = ConstEvtKey.KEYCODE_SEMICOLON; 

		if(c == '.')
			code = ConstEvtKey.KEYCODE_PERIOD; 

		if(c == '*')
			code = ConstEvtKey.KEYCODE_STAR; 

		if(c == '+')
			code = ConstEvtKey.KEYCODE_PLUS; 

		if(c == '-')
			code = ConstEvtKey.KEYCODE_MINUS; 

		if(c == '=')
			code = ConstEvtKey.KEYCODE_EQUALS; 

		if(e.getKeyCode() == KeyEvent.VK_HOME)
			code = ConstEvtKey.KEYCODE_HOME; 

		if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
			code = ConstEvtKey.KEYCODE_MENU; 

		if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
			code = ConstEvtKey.KEYCODE_STAR; 

		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			code = ConstEvtKey.KEYCODE_BACK; 

		if(e.getKeyCode() == KeyEvent.VK_F3)
			code = ConstEvtKey.KEYCODE_CALL; 

		if(e.getKeyCode() == KeyEvent.VK_F4)
			code = ConstEvtKey.KEYCODE_ENDCALL; 

		if(e.getKeyCode() == KeyEvent.VK_F5)
			code = ConstEvtKey.KEYCODE_SEARCH; 

		if(e.getKeyCode() == KeyEvent.VK_F7)
			code = ConstEvtKey.KEYCODE_POWER; 

		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			code = ConstEvtKey.KEYCODE_DPAD_RIGHT; 

		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			code = ConstEvtKey.KEYCODE_DPAD_LEFT;

		if(e.getKeyCode() == KeyEvent.VK_UP)
			code = ConstEvtKey.KEYCODE_DPAD_UP; 

		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			code = ConstEvtKey.KEYCODE_DPAD_DOWN; 

		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
			code = ConstEvtKey.KEYCODE_SHIFT_LEFT; 

		return code;
	}
	
	public static String getKeyCodeString(KeyEvent e) {
		
		int keyCode = e.getKeyCode();
		String code = null;

		switch(keyCode) {
		
		case 8: /* Del key */
			code = "del"; //$NON-NLS-1$
			break;
		case KeyEvent.VK_HOME:
			code = "home"; //$NON-NLS-1$
			break;
		case KeyEvent.VK_PAGE_UP:
			code = "menu";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_PAGE_DOWN: 
			break;
		case KeyEvent.VK_ESCAPE:
			code = "back";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_SPACE:
			code = "space";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_F3:
			code = "call";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_F4:
			code = "endcall";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_F5:
			code = "search";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_F7:
			code = "power";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_RIGHT:
			code = "dpad_right";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_LEFT:
			code = "dpad_left"; //$NON-NLS-1$
			break;
		case KeyEvent.VK_UP:
			code = "dpad_up";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_DOWN:
			code = "dpad_down";  //$NON-NLS-1$
			break;
		case KeyEvent.VK_SHIFT:
			break;
		}
		
		return code;
	}

}

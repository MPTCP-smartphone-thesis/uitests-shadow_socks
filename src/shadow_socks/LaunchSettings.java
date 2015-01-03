package shadow_socks;

import utils.Utils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class LaunchSettings extends UiAutomatorTestCase {

	private static final String ID_SWITCH_BUTTON = "com.github.shadowsocks:id/switchButton";
	private static final String ID_CONNECTING = "android:id/message";
	private static final String TEXT_CONNECTING = "Connecting…";
	private static final String ID_LISTVIEW = "android:id/list";
	private static final String TEXT_AUTO_CONNECT = "Auto Connect";

	/**
	 * Start proxy, restart it if already enabled
	 * @param button: checkbox to enable the proxy
	 * @return true if ok
	 * @throws UiObjectNotFoundException
	 */
	protected boolean startProxy(UiObject button)
			throws UiObjectNotFoundException {
		System.out.println("Start Proxy");
		// already started?
		if (button.isChecked())
			return true; // great, nothing more to do (not a tunnel)

		return Utils.clickAndWaitLoadingWindow(this, button, ID_CONNECTING,
				TEXT_CONNECTING, true);
	}

	protected void stopProxy(UiObject button) throws UiObjectNotFoundException {
		System.out.println("Stop Proxy");
		if (! button.isChecked())
			return;

		assertTrue(
				"Not able to stop proxy",
				Utils.clickAndWaitLoadingWindow(this, button, ID_CONNECTING,
						TEXT_CONNECTING, false) && !button.isChecked());
	}

	private void autoConnect(UiObject button, boolean enable)
			throws UiObjectNotFoundException {
		System.out.println("AutoConnect " + enable);

		boolean running = button.isChecked();
		if (running) // cannot change options if enable...
			stopProxy(button);

		UiObject checkBox = Utils.findCheckBoxInListWithTitle(ID_LISTVIEW,
				TEXT_AUTO_CONNECT, null);
		assertTrue("Unable to find element", checkBox != null);
		Utils.checkBox(checkBox, enable);

		if (running) // restart proxy
			startProxy(button);
	}

	public void testDemo() throws UiObjectNotFoundException {
		assertTrue("OOOOOpps",
				Utils.openApp(this, "Shadowsocks",
						"com.github.shadowsocks",
						"com.github.shadowsocks.Shadowsocks",
						false)); // not kill it before
		sleep(1000);
		// Utils.listMoveUp(ID_LISTVIEW); autoconnect is the last elem

		// Get button: always visible
		UiObject button = Utils.getObjectWithId(ID_SWITCH_BUTTON);

		String action = getParams().getString("action");
		if (action == null) // default: start
			action = "start";

		if (action.equalsIgnoreCase("stop"))
			stopProxy(button);
		else if (action.equalsIgnoreCase("autoconnect"))
			autoConnect(button, true);
		else if (action.equalsIgnoreCase("notautoconnect"))
			autoConnect(button, false);
		else if (action.equalsIgnoreCase("stopnotautoconnect")) {
			stopProxy(button);
			autoConnect(button, false);
		} else if (action.equalsIgnoreCase("startautoconnect")) {
			autoConnect(button, true);
			startProxy(button);
		}
		else
			assertTrue("Not able to (re)start the proxy", startProxy(button));
	}
}

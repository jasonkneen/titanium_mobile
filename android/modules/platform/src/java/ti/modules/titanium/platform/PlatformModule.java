/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.platform;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiPlatformHelper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;

@Kroll.module
public class PlatformModule extends KrollModule
{
	private static final String TAG = "PlatformModule";

	@Kroll.constant
	public static final int BATTERY_STATE_UNKNOWN = 0;
	@Kroll.constant
	public static final int BATTERY_STATE_UNPLUGGED = 1;
	@Kroll.constant
	public static final int BATTERY_STATE_CHARGING = 2;
	@Kroll.constant
	public static final int BATTERY_STATE_FULL = 3;

	protected DisplayCapsProxy displayCaps;

	protected int batteryState;
	protected double batteryLevel;
	protected boolean batteryStateReady;

	protected BroadcastReceiver batteryStateReceiver;

	public PlatformModule()
	{
		super();

		batteryState = BATTERY_STATE_UNKNOWN;
		batteryLevel = -1;
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getName()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getName();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getOsname()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getName();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getLocale()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getLocale();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public DisplayCapsProxy getDisplayCaps()
	// clang-format on
	{
		if (displayCaps == null) {
			displayCaps = new DisplayCapsProxy();
			displayCaps.setActivity(TiApplication.getInstance().getCurrentActivity());
		}
		return displayCaps;
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public int getProcessorCount()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getProcessorCount();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getUsername()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getUsername();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getVersion()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getVersion();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public double getAvailableMemory()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getAvailableMemory();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getModel()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getModel();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getManufacturer()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getManufacturer();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getOstype()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getOstype();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getArchitecture()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getArchitecture();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getAddress()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getIpAddress();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getNetmask()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getNetmask();
	}

	@Kroll.method
	public boolean is24HourTimeFormat()
	{
		TiApplication app = TiApplication.getInstance();
		if (app != null) {
			return android.text.format.DateFormat.is24HourFormat(app.getApplicationContext());
		}
		return false;
	}

	@Kroll.method
	public String createUUID()
	{
		return TiPlatformHelper.getInstance().createUUID();
	}

	@Kroll.method
	public boolean openURL(String url)
	{
		Log.d(TAG, "Launching viewer for: " + url, Log.DEBUG_MODE);
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		try {
			Activity activity = TiApplication.getAppRootOrCurrentActivity();

			if (activity != null) {
				activity.startActivity(intent);
			} else {
				throw new ActivityNotFoundException("No valid root or current activity found for application instance");
			}
			return true;
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "Activity not found: " + url, e);
		}
		return false;
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getMacaddress()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getMacaddress();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getId()
	// clang-format on
	{
		return TiPlatformHelper.getInstance().getMobileId();
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setBatteryMonitoring(boolean monitor)
	// clang-format on
	{
		if (monitor && batteryStateReceiver == null) {
			registerBatteryStateReceiver();
		} else if (!monitor && batteryStateReceiver != null) {
			unregisterBatteryStateReceiver();
			batteryStateReceiver = null;
		}
	}
	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public boolean getBatteryMonitoring()
	// clang-format on
	{
		return batteryStateReceiver != null;
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public int getBatteryState()
	// clang-format on
	{
		return batteryState;
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public double getBatteryLevel()
	// clang-format on
	{
		return batteryLevel;
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getRuntime()
	// clang-format on
	{
		return KrollRuntime.getInstance().getRuntimeName();
	}

	protected void registerBatteryStateReceiver()
	{
		batteryStateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent)
			{
				int scale = intent.getIntExtra(TiC.PROPERTY_SCALE, -1);
				batteryLevel = convertBatteryLevel(intent.getIntExtra(TiC.PROPERTY_LEVEL, -1), scale);
				batteryState = convertBatteryStatus(intent.getIntExtra(TiC.PROPERTY_STATUS, -1));

				KrollDict event = new KrollDict();
				event.put(TiC.PROPERTY_LEVEL, batteryLevel);
				event.put(TiC.PROPERTY_STATE, batteryState);
				fireEvent(TiC.EVENT_BATTERY, event);
			}
		};

		registerBatteryReceiver(batteryStateReceiver);
	}

	protected void unregisterBatteryStateReceiver()
	{
		getActivity().unregisterReceiver(batteryStateReceiver);
	}

	@Override
	public void eventListenerAdded(String type, int count, final KrollProxy proxy)
	{
		super.eventListenerAdded(type, count, proxy);
		if (TiC.EVENT_BATTERY.equals(type) && batteryStateReceiver == null) {
			registerBatteryStateReceiver();
		}
	}

	@Override
	public void eventListenerRemoved(String type, int count, KrollProxy proxy)
	{
		super.eventListenerRemoved(type, count, proxy);
		if (TiC.EVENT_BATTERY.equals(type) && count == 0 && batteryStateReceiver != null) {
			unregisterBatteryStateReceiver();
			batteryStateReceiver = null;
		}
	}

	private int convertBatteryStatus(int status)
	{
		int state = BATTERY_STATE_UNKNOWN;
		switch (status) {
			case BatteryManager.BATTERY_STATUS_CHARGING: {
				state = BATTERY_STATE_CHARGING;
				break;
			}
			case BatteryManager.BATTERY_STATUS_FULL: {
				state = BATTERY_STATE_FULL;
				break;
			}
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING: {
				state = BATTERY_STATE_UNPLUGGED;
				break;
			}
		}
		return state;
	}

	private double convertBatteryLevel(int level, int scale)
	{
		int l = -1;
		if (level >= 0 && scale > 0) {
			l = (level * 100) / scale;
		}
		return l;
	}

	private void registerBatteryReceiver(BroadcastReceiver batteryReceiver)
	{
		Activity a = getActivity();
		IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		a.registerReceiver(batteryReceiver, batteryFilter);
	}

	@Override
	public void onResume(Activity activity)
	{
		super.onResume(activity);
		if (batteryStateReceiver != null) {
			Log.i(TAG, "Reregistering battery changed receiver", Log.DEBUG_MODE);
			registerBatteryReceiver(batteryStateReceiver);
		}
	}

	@Override
	public void onPause(Activity activity)
	{
		super.onPause(activity);
		if (batteryStateReceiver != null) {
			unregisterBatteryStateReceiver();
			batteryStateReceiver = null;
		}
	}

	@Override
	public void onDestroy(Activity activity)
	{
		super.onDestroy(activity);
		if (batteryStateReceiver != null) {
			unregisterBatteryStateReceiver();
			batteryStateReceiver = null;
		}
	}

	@Override
	public String getApiName()
	{
		return "Ti.Platform";
	}
}

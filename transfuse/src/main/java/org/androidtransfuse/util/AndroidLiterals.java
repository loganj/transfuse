/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.util;

import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public final class AndroidLiterals {

    public static final ASTType OBJECT = new ASTStringType("java.lang.Object");
    public static final ASTType ACTIVITY = new ASTStringType("android.app.Activity");
    public static final ASTType LIST_ACTIVITY = new ASTStringType("android.app.ListActivity");
    public static final ASTType APPLICATION = new ASTStringType("android.app.Application");
    public static final ASTType CONTEXT = new ASTStringType("android.content.Context");
    public static final ASTType BUNDLE = new ASTStringType("android.os.Bundle");
    public static final ASTType CONTENT_CONFIGURATION = new ASTStringType("android.content.res.Configuration");
    public static final ASTType BROADCAST_RECEIVER = new ASTStringType("android.content.BroadcastReceiver");
    public static final ASTType INTENT = new ASTStringType("android.content.Intent");
    public static final ASTType SERVICE = new ASTStringType("android.app.Service");
    public static final ASTType INTENT_SERVICE = new ASTStringType("android.app.IntentService");
    public static final ASTType IBINDER = new ASTStringType("android.os.IBinder");
    public static final ASTType FRAGMENT = new ASTStringType("android.support.v4.app.Fragment");
    public static final ASTType FRAGMENT_MANAGER = new ASTStringType("android.support.v4.app.FragmentManager");
    public static final ASTType LIST_FRAGMENT = new ASTStringType("android.support.v4.app.ListFragment");
    public static final ASTType LAYOUT_INFLATER = new ASTStringType("android.view.LayoutInflater");
    public static final ASTType VIEW = new ASTStringType("android.view.View");
    public static final ASTType ADAPTER_VIEW = new ASTStringType("android.widget.AdapterView");
    public static final ASTType ABS_LIST_VIEw = new ASTStringType("android.widget.AbsListView");
    public static final ASTType VIEW_GROUP = new ASTStringType("android.view.ViewGroup");
    public static final ASTType LIST_VIEW = new ASTStringType("android.widget.ListView");
    public static final ASTType TEXT_VIEW = new ASTStringType("android.widget.TextView");
    public static final ASTType VIEW_ON_CLICK_LISTENER = new ASTStringType("android.view.View.OnClickListener");
    public static final ASTType VIEW_ON_LONG_CLICK_LISTENER = new ASTStringType("android.view.View.OnLongClickListener");
    public static final ASTType VIEW_ON_CREATE_CONTEXT_MENU_LISTENER = new ASTStringType("android.view.View.OnCreateContextMenuListener");
    public static final ASTType VIEW_ON_KEY_LISTENER = new ASTStringType("android.view.View.OnKeyListener");
    public static final ASTType VIEW_ON_TOUCH_LISTENER = new ASTStringType("android.view.View.OnTouchListener");
    public static final ASTType VIEW_ON_FOCUS_CHANGE_LISTENER = new ASTStringType("android.view.View.OnFocusChangeListener");
    public static final ASTType ADAPTER_VIEW_ON_ITEM_CLICK_LISTENER = new ASTStringType("android.widget.AdapterView.OnItemClickListener");
    public static final ASTType ADAPTER_VIEW_ON_ITEM_LONG_CLICK_LISTENER = new ASTStringType("android.widget.AdapterView.OnItemLongClickListener");
    public static final ASTType ADAPTER_VIEW_ON_ITEM_SELECTED_LISTENER = new ASTStringType("android.widget.AdapterView.OnItemSelectedListener");
    public static final ASTType ABS_LIST_VIEW_ON_SCROLL_LISTENER = new ASTStringType("android.widget.AbsListView.OnScrollListener");
    public static final ASTType ABS_LIST_VIEW_MULTI_CHOICE_MODE_LISTENER = new ASTStringType("android.widget.AbsListView.MultiChoiceModeListener");
    public static final ASTType ABS_LIST_VIEW_RECYCLER_LISTENER = new ASTStringType("android.widget.AbsListView.RecyclerListener");
    public static final ASTType TEXT_VIEW_ON_EDITOR_ACTION_LISTENER = new ASTStringType("android.widget.TextView.OnEditorActionListener");
    public static final ASTType PREFERENCE_ACTIVITY = new ASTStringType("android.preference.PreferenceActivity");
    public static final ASTType ACTIVITY_GROUP = new ASTStringType("android.app.ActivityGroup");
    public static final ASTType RESOURCES = new ASTStringType("android.content.res.Resources");
    public static final ASTType PREFERENCE_MANAGER = new ASTStringType("android.preference.PreferenceManager");
    public static final ASTType SHARED_PREFERENCES = new ASTStringType("android.content.SharedPreferences");
    public static final ASTType MENU_INFLATER = new ASTStringType("android.view.MenuInflater");
    public static final ASTType PARCELABLE = new ASTStringType("android.os.Parcelable");
    public static final ASTType COLOR_STATE_LIST = new ASTStringType("android.content.res.ColorStateList");
    public static final ASTType GRAPHICS_MOVIE = new ASTStringType("android.graphics.Movie");
    public static final ASTType GRAPHICS_DRAWABLE = new ASTStringType("android.graphics.drawable.Drawable");
    public static final ASTType ANIMATION = new ASTStringType("android.view.animation.Animation");
    public static final ASTType ANIMATION_UTILS = new ASTStringType("android.view.animation.AnimationUtils");
    public static final ASTType ACCESSIBILITY_MANAGER = new ASTStringType("android.view.accessibility.AccessibilityManager");
    public static final ASTType ACCOUNT_MANAGER = new ASTStringType("android.accounts.AccountManager");
    public static final ASTType ACTIVITY_MANAGER = new ASTStringType("android.app.ActivityManager");
    public static final ASTType ALARM_MANAGER = new ASTStringType("android.app.AlarmManager");
    public static final ASTType AUDIO_MANAGER = new ASTStringType("android.media.AudioManager");
    public static final ASTType CLIPBOARD_MANAGER = new ASTStringType("android.text.ClipboardManager");
    public static final ASTType CONNECTIVITY_MANAGER = new ASTStringType("android.net.ConnectivityManager");
    public static final ASTType DEVICE_POLICY_MANAGER = new ASTStringType("android.app.admin.DevicePolicyManager");
    public static final ASTType DOWNLOAD_MANAGER = new ASTStringType("android.app.DownloadManager");
    public static final ASTType DROP_BOX_MANAGER = new ASTStringType("android.os.DropBoxManager");
    public static final ASTType INPUT_METHOD_MANAGER = new ASTStringType("android.view.inputmethod.InputMethodManager");
    public static final ASTType INPUT_MANAGER = new ASTStringType("android.hardware.input.InputManager");
    public static final ASTType KEYGUARD_MANAGER = new ASTStringType("android.app.KeyguardManager");
    public static final ASTType LOCATION_MANAGER = new ASTStringType("android.location.LocationManager");
    public static final ASTType MEDIA_ROUTER = new ASTStringType("android.media.MediaRouter");
    public static final ASTType NFC_MANAGER = new ASTStringType("android.nfc.NfcManager");
    public static final ASTType NSD_MANAGER = new ASTStringType("android.net.nsd.NsdManager");
    public static final ASTType NOTIFICATION_MANAGER = new ASTStringType("android.app.NotificationManager");
    public static final ASTType POWER_MANAGER = new ASTStringType("android.os.PowerManager");
    public static final ASTType SEARCH_MANAGER = new ASTStringType("android.app.SearchManager");
    public static final ASTType SENSOR_MANAGER = new ASTStringType("android.hardware.SensorManager");
    public static final ASTType STORAGE_MANAGER = new ASTStringType("android.os.storage.StorageManager");
    public static final ASTType TELEPHONY_MANAGER = new ASTStringType("android.telephony.TelephonyManager");
    public static final ASTType TEXT_SERVICES_MANAGER = new ASTStringType("android.view.textservice.TextServicesManager");
    public static final ASTType UI_MODE_MANAGER = new ASTStringType("android.app.UiModeManager");
    public static final ASTType USB_MANAGER = new ASTStringType("android.hardware.usb.UsbManager");
    public static final ASTType VIBRATOR = new ASTStringType("android.os.Vibrator");
    public static final ASTType WALLPAPER_SERVICE = new ASTStringType("android.service.wallpaper.WallpaperService");
    public static final ASTType WIFI_P2P_MANAGER = new ASTStringType("android.net.wifi.p2p.WifiP2pManager");
    public static final ASTType WIFI_MANAGER = new ASTStringType("android.net.wifi.WifiManager");
    public static final ASTType WINDOW_MANAGER = new ASTStringType("android.view.WindowManager");

    private AndroidLiterals(){
        //private utility class constructor
    }
}

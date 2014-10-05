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
package org.androidtransfuse.plugins;

import com.google.common.collect.ImmutableMap;
import com.sun.codemodel.JExpr;
import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.DescriptorBuilder;
import org.androidtransfuse.TransfusePlugin;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.gen.variableBuilder.InjectionBindingBuilder;
import org.androidtransfuse.tomove.ComponentDescriptor;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author John Ericksen
 */
@Bootstrap
public class SystemServicePlugin implements TransfusePlugin {

    public static final java.lang.String POWER_SERVICE = "power";
    public static final java.lang.String WINDOW_SERVICE = "window";
    public static final java.lang.String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final java.lang.String ACCOUNT_SERVICE = "account";
    public static final java.lang.String ACTIVITY_SERVICE = "activity";
    public static final java.lang.String ALARM_SERVICE = "alarm";
    public static final java.lang.String NOTIFICATION_SERVICE = "notification";
    public static final java.lang.String ACCESSIBILITY_SERVICE = "accessibility";
    public static final java.lang.String KEYGUARD_SERVICE = "keyguard";
    public static final java.lang.String LOCATION_SERVICE = "location";
    public static final java.lang.String SEARCH_SERVICE = "search";
    public static final java.lang.String SENSOR_SERVICE = "sensor";
    public static final java.lang.String STORAGE_SERVICE = "storage";
    public static final java.lang.String WALLPAPER_SERVICE = "wallpaper";
    public static final java.lang.String VIBRATOR_SERVICE = "vibrator";
    public static final java.lang.String CONNECTIVITY_SERVICE = "connectivity";
    public static final java.lang.String WIFI_SERVICE = "wifi";
    public static final java.lang.String WIFI_P2P_SERVICE = "wifip2p";
    public static final java.lang.String NSD_SERVICE = "servicediscovery";
    public static final java.lang.String AUDIO_SERVICE = "audio";
    public static final java.lang.String MEDIA_ROUTER_SERVICE = "media_router";
    public static final java.lang.String TELEPHONY_SERVICE = "phone";
    public static final java.lang.String CLIPBOARD_SERVICE = "clipboard";
    public static final java.lang.String INPUT_METHOD_SERVICE = "input_method";
    public static final java.lang.String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
    public static final java.lang.String DROPBOX_SERVICE = "dropbox";
    public static final java.lang.String DEVICE_POLICY_SERVICE = "device_policy";
    public static final java.lang.String UI_MODE_SERVICE = "uimode";
    public static final java.lang.String DOWNLOAD_SERVICE = "download";
    public static final java.lang.String NFC_SERVICE = "nfc";
    public static final java.lang.String USB_SERVICE = "usb";
    public static final java.lang.String INPUT_SERVICE = "input";

    private static final ImmutableMap<String, ASTType> SYSTEM_SERVICES;

    static{
        ImmutableMap.Builder<String, ASTType> systemServiceBuilder = ImmutableMap.builder();
        systemServiceBuilder.put(ACCESSIBILITY_SERVICE, AndroidLiterals.ACCESSIBILITY_MANAGER);
        systemServiceBuilder.put(ACCOUNT_SERVICE, AndroidLiterals.ACCOUNT_MANAGER);
        systemServiceBuilder.put(ACTIVITY_SERVICE, AndroidLiterals.ACTIVITY_MANAGER);
        systemServiceBuilder.put(ALARM_SERVICE, AndroidLiterals.ALARM_MANAGER);
        systemServiceBuilder.put(AUDIO_SERVICE, AndroidLiterals.AUDIO_MANAGER);
        systemServiceBuilder.put(CLIPBOARD_SERVICE, AndroidLiterals.CLIPBOARD_MANAGER);
        systemServiceBuilder.put(CONNECTIVITY_SERVICE, AndroidLiterals.CONNECTIVITY_MANAGER);
        systemServiceBuilder.put(DEVICE_POLICY_SERVICE, AndroidLiterals.DEVICE_POLICY_MANAGER);
        systemServiceBuilder.put(DOWNLOAD_SERVICE, AndroidLiterals.DOWNLOAD_MANAGER);
        systemServiceBuilder.put(DROPBOX_SERVICE, AndroidLiterals.DROP_BOX_MANAGER);
        systemServiceBuilder.put(INPUT_METHOD_SERVICE, AndroidLiterals.INPUT_METHOD_MANAGER);
        systemServiceBuilder.put(INPUT_SERVICE, AndroidLiterals.INPUT_MANAGER);
        systemServiceBuilder.put(KEYGUARD_SERVICE, AndroidLiterals.KEYGUARD_MANAGER);
        systemServiceBuilder.put(LAYOUT_INFLATER_SERVICE, AndroidLiterals.LAYOUT_INFLATER);
        systemServiceBuilder.put(LOCATION_SERVICE, AndroidLiterals.LOCATION_MANAGER);
        systemServiceBuilder.put(MEDIA_ROUTER_SERVICE, AndroidLiterals.MEDIA_ROUTER);
        systemServiceBuilder.put(NFC_SERVICE, AndroidLiterals.NFC_MANAGER);
        systemServiceBuilder.put(NSD_SERVICE, AndroidLiterals.NSD_MANAGER);
        systemServiceBuilder.put(NOTIFICATION_SERVICE, AndroidLiterals.NOTIFICATION_MANAGER);
        systemServiceBuilder.put(POWER_SERVICE, AndroidLiterals.POWER_MANAGER);
        systemServiceBuilder.put(SEARCH_SERVICE, AndroidLiterals.SEARCH_MANAGER);
        systemServiceBuilder.put(SENSOR_SERVICE, AndroidLiterals.SENSOR_MANAGER);
        systemServiceBuilder.put(STORAGE_SERVICE, AndroidLiterals.STORAGE_MANAGER);
        systemServiceBuilder.put(TELEPHONY_SERVICE, AndroidLiterals.TELEPHONY_MANAGER);
        systemServiceBuilder.put(TEXT_SERVICES_MANAGER_SERVICE, AndroidLiterals.TEXT_SERVICES_MANAGER);
        systemServiceBuilder.put(UI_MODE_SERVICE, AndroidLiterals.UI_MODE_MANAGER);
        systemServiceBuilder.put(USB_SERVICE, AndroidLiterals.USB_MANAGER);
        systemServiceBuilder.put(VIBRATOR_SERVICE, AndroidLiterals.VIBRATOR);
        systemServiceBuilder.put(WALLPAPER_SERVICE, AndroidLiterals.WALLPAPER_SERVICE);
        systemServiceBuilder.put(WIFI_P2P_SERVICE, AndroidLiterals.WIFI_P2P_MANAGER);
        systemServiceBuilder.put(WIFI_SERVICE, AndroidLiterals.WIFI_MANAGER);
        systemServiceBuilder.put(WINDOW_SERVICE, AndroidLiterals.WINDOW_MANAGER);

        SYSTEM_SERVICES = systemServiceBuilder.build();
    }

    @Inject
    InjectionBindingBuilder injectionBindingBuilder;

    @Override
    public void run(ConfigurationRepository repository) {
        for (final Map.Entry<String, ASTType> systemServiceEntry : SYSTEM_SERVICES.entrySet()) {
            repository.add(new DescriptorBuilder() {
                @Override
                public void buildDescriptor(ComponentDescriptor descriptor, ASTType type, Class<? extends Annotation> componentAnnotation) {
                    descriptor.getAnalysisContext().getInjectionNodeBuilders().putType(systemServiceEntry.getValue(),
                            injectionBindingBuilder.dependency(AndroidLiterals.CONTEXT)
                                    .invoke(new ASTStringType("java.lang.Object"), "getSystemService").arg(JExpr.lit(systemServiceEntry.getKey())).build());
                }
            });
        }
    }
}

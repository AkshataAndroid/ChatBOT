package com.unfyd.unfydChatBot.Service;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ChatService implements ViewTreeObserver.OnWindowFocusChangeListener {

    Context context;
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "user_name";
    public static final String USER_EMAIL_KEY = "user_email";
    public static final String DEVICE_NAME_KEY = "device_name";
    public static final String DEVICE_ID_KEY = "device_id";
    public static Boolean serviceStatus = false;
    static final String TAG = "chatAPPLib";
    public static boolean m_started = false;

    private Map<String, Object> m_customData;
    private static final ChatService m_instance = new ChatService();
//    private ControlInjector m_controlInjection = new ControlInjector();

    public static Activity m_currentActivity;
    private BluetoothClass.Device m_device;
    public static String pkgName = null;

    public static ChatService instance() {
        return m_instance;
    }

    static String version() {
        return "1.0.0";
    }

    private void reinitialize() {
        Log.d("TAG", "message Yo yo yo");
        // this.m_device.runRegistrationLoop();
    }

    public ChatService start(Application application) {
       // this.m_customData = customData;
        if (this.m_started) {
            return this;
        }
        this.m_started = true;
        Log.i("Cobrowse", "Initialising Cobrowse " + version());
        context = application.getApplicationContext();
        //context.startService(new Intent(context, ChatBotService.class));
        Intent startServerIntent = new Intent(context, ChatBotService.class);
        startServerIntent.setAction("START");
       context.startService(startServerIntent);
        reinitialize();
        return this;
    }


    public ChatService start(Activity currentActivity, boolean b) {
       // this.m_customData = customData;
        this.m_started = b;
        //this.m_currentActivity = null;
        start(currentActivity.getApplication());
        setActivity(currentActivity);
        return this;
    }

    void setActivity(Activity activity) {
        if (this.m_currentActivity == activity) {
            return;
        }
        this.m_currentActivity = activity;
//        this.m_controlInjection.setActivity(activity);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public String api() {
        if (pkgName == null) {
            pkgName = context.getPackageName();
        }
        ApplicationInfo app = null;
        try {
            app = context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = app.metaData;

        String mData = bundle.getString("chaturl");
        // return "http://13.76.0.218:80/";
        //return "http://10.10.110.233:4300/";
        //return "http://192.168.2.57:4300/";
        return mData;
    }

    public ChatService customData(Map<String, Object> customData) {
        this.m_customData = customData;
        if (this.m_device != null) {
          //  this.m_device.updateRegistration();
        }
        return this;
    }

    public Map<String, Object> customData() {
        return this.m_customData;
    }

    Activity getActivity() {
        return this.m_currentActivity;
    }

    public void Expermemnt(Activity mainActivity) {
        //   Toast.makeText(mainActivity, "Hello there", Toast.LENGTH_SHORT).show();
    }

    public void stopSharing(Activity mainActivity) {
//        if (CobrowseService.sMediaProjection != null) {
//            CobrowseService.sMediaProjection.stop();
//            CobrowseService.mProjectionManager = null;
//        }
//        this.m_currentActivity = null;
//
//        if (CobrowseService.stream != null) {
//            CobrowseService.stream.emit("disconnect", CobrowseService.mUsername);
//            CobrowseService.stream.disconnect();
//            CobrowseService.stream.off();
//            if (CobrowseService.timer != null) {
//                CobrowseService.timer.cancel();
//            }
//            if (CobrowseService.ddrawOn) {
//                if (CobrowseService.myv.isShown()) {
//                    CobrowseService.wm.removeView(CobrowseService.myv);
//                }
//            }
//            if (CobrowseService.ldrawOn) {
//                if (CobrowseService.lmyv.isShown()) {
//                    CobrowseService.wm.removeView(CobrowseService.lmyv);
//                }
//            }
//            Toast.makeText(context, "CoBrowse Session Closed", Toast.LENGTH_SHORT).show();
//            context.stopService(new Intent(context, CobrowseService.class));
//            if (CobrowseService.mNotificationManager != null) {
//                CobrowseService.mNotificationManager.cancel(CobrowseService.NOTIFICATION_ID);
//            }
        //}
    }
}

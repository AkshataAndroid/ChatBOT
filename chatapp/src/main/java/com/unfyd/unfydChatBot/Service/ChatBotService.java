package com.unfyd.unfydChatBot.Service;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.unfyd.unfydChatBot.Activity.LoginActivity;
import com.unfyd.unfydChatBot.Activity.MainActivity;
import com.unfyd.unfydChatBot.Activity.MainFragment;
import com.unfyd.unfydChatBot.Classes.Constants;
import com.unfyd.unfydChatBot.Classes.clsCarousel;
import com.unfyd.unfydChatBot.Database.clsChatDetail;
import com.unfyd.unfydChatBot.R;
import com.unfyd.unfydChatBot.preferences.UserPreferences;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
public class ChatBotService extends Service {
    //public static MyView myv;
    //  public static LaserView lmyv;
    //PointF position;
    public int count = 0;
    public static boolean ddrawOn = false, ldrawOn = false;
    public static Socket mSocket = null;
    private Boolean isConnected = false;
    public static String mUsername = "android-mobile";
    private static final String TAG = "CobrowseService";
    private int numUsers;
    private String conversationId = null, agentName;
    public static Boolean ConversationActivity = false;
    WindowManager.LayoutParams params;
    Context context;

    public static final String CHANNNEL_ID = "sct_03";
    public static final String CHANNEL_NAME = "sct_screen";
    public static final String CHANNEL_DESC = "This is a channel for Screen Notifications";

    private static final String SCREENCAP_NAME = "screencap";
    //    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
//
//    private Handler mHandler;
//    public static NotificationManager mNotificationManager;
//    public static MediaProjection sMediaProjection;
//    public static MediaProjectionManager mProjectionManager;
//    private ImageReader mImageReader;
//    private Display mDisplay;
//    private Display mDisplay2;
//    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    //private OrientationChangeCallback mOrientationChangeCallback;
    public static int hideScreen = 0;
    byte[] imageBytes = null;
    //   private OrientationEventListener myOrientationEventListener;

    public static final String KEY_INTENT_STOP = "keyintentstop";
    public static final int REQUEST_CODE_STOP = 101;
    public static int NOTIFICATION_ID;
    Integer xc = 0;
    Integer yc = 0;
    Integer x1 = 0, y1 = 0, x2 = 0, y2 = 0;
    //    static ArrayList<XYCordinates> dataList = new ArrayList<>();
//    static ArrayList<XYCordinatesLaser> laserDataList = new ArrayList<>();
//    Bitmap bitmap = null, cmpBitmap = null;
    Long tsLong, diff, newtsLong;

    static ChatBotService cbs;
    static Activity activity;

    // private Location mLocation;
    //private LocationManager lm;

    private static String currentOrientation = "Portrait";
    private static String currentRotation = "OFF";

    private String urlstr = "https://json.geoiplookup.io/?callback=";
    public static String longitude, latitude, user_id, user_email, device_id, device_name;

    int delay = 1000; // delay for 0 sec.
    int period = 10100; // repeat every 10 sec.
    public static Timer timer;
    public static WindowManager wm;
    long BeforeTime, TotalTxBeforeTest, TotalRxBeforeTest;
    long TotalTxAfterTest, TotalRxAfterTest, AfterTime;
    public static String apiUrl = null;
    List<String> mOptionS = new ArrayList<>();
    List<String> mImage = new ArrayList<>();
    List<String> mWord = new ArrayList<>();
    List<String> mPdf = new ArrayList<>();
    List<String> mExcel = new ArrayList<>();
    List<String> mPpt = new ArrayList<>();
    List<String> mAudio = new ArrayList<>();
    List<String> mVideo = new ArrayList<>();
    List<clsCarousel> mclsCarouselLIST = new ArrayList<>();
    private boolean mTyping = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    context=this;
        if (intent != null && intent.getAction().equals("START")) {
            cbs = new ChatBotService();

            Log.i("Chat", "started ChatBOTservice");


            String apiStatus = UserPreferences.getPreferences(getApplicationContext(), Constants.KEY_API_URL);
            if (apiStatus == null) {
                apiUrl = ChatService.instance().api();
                UserPreferences.setPreferences(getApplicationContext(), Constants.KEY_API_URL, apiUrl);
            } else {
                apiUrl = apiStatus;
            }
           // attachLifecycleListeners();
            socketConnections();
            //deviceOrientation();
        }
//        if (intent.getAction().equals("OVERLAY")) {
//            wmOverView();
//            displayNotification();
//        }

        return START_NOT_STICKY;
    }
    public  Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (null != mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });

        }
    };
    public Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    public Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }

            });
        }
    };
    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    String type;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                        type = data.getString("type");
                        JSONArray jsonOptions = data.getJSONArray("option");
                        if (jsonOptions != null) {

                            if (type.equalsIgnoreCase("button")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    JSONObject optionsObj = jsonOptions.getJSONObject(i);
                                    String name = optionsObj.getString("value");
                                    mOptionS.add(name);

                                }
                            } else if (type.equalsIgnoreCase("carousel")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    JSONObject optionsObj = jsonOptions.getJSONObject(i);
                                    clsCarousel objclsCarousel = new clsCarousel();
                                    objclsCarousel.setImage(optionsObj.getString("image"));
                                    objclsCarousel.setKey(optionsObj.getString("key"));
                                    objclsCarousel.setTooltip(optionsObj.getString("tooltip"));
                                    objclsCarousel.setValue(optionsObj.getString("value"));
                                    mclsCarouselLIST.add(objclsCarousel);
                                }
                            } else if (type.equalsIgnoreCase("image")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mImage.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            } else if (type.equalsIgnoreCase("word")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mWord.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            } else if (type.equalsIgnoreCase("pdf")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mPdf.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            } else if (type.equalsIgnoreCase("excel")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mExcel.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            } else if (type.equalsIgnoreCase("ppt")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mPpt.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            } else if (type.equalsIgnoreCase("audio")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mAudio.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            } else if (type.equalsIgnoreCase("video")) {
                                for (int i = 0; i < jsonOptions.length(); i++) {
                                    mVideo.add(jsonOptions.getString(i));
                                    //mImage.add()
                                }
                            }

                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                   MainFragment.removeTyping(username);
                    MainFragment.addMessage(username, message, type, mOptionS, mclsCarouselLIST, mImage,
                            mWord, mPdf, mExcel, mPpt, mAudio, mVideo);
//                    try {
//                        List mChatDetail = new ArrayList<>();
//                        mChatDetail.addAll(MainFragment.dbHelper.getAll(clsChatDetail.class));
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }

                }
            });
        }
    };


    public Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    MainFragment.addLog(getResources().getString(R.string.message_user_joined, username));
                   MainFragment. addParticipantsLog(numUsers);
                }
            });
        }
    };
     Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    MainFragment.addLog(getResources().getString(R.string.message_user_left, username));
                    MainFragment.addParticipantsLog(numUsers);
                    MainFragment.removeTyping(username);
                }
            });
        }
    };
    public Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    MainFragment.addTyping(username);
                }
            });
        }
    };
     Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    MainFragment.removeTyping(username);
                }
            });
        }
    };
    public Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };

    private void socketConnections() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = false;
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            opts.reconnectionDelayMax = 5000;
            opts.reconnectionAttempts = 99999;
            mSocket = IO.socket(apiUrl, opts);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //TODO Remove this Function No Longer used
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR,onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        // mSocket.on("new message", onNewMessage);
        mSocket.on("bot_new_message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);

        mSocket.connect();
       // Intent myIntent = new Intent(this, LoginActivity.class);
       //  startActivity(myIntent );
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent );

        // MainFragment.startSignIn(getApplicationContext());
        //mProjectionManager = (MediaProjectionManager) getSystemService(getApplicationContext().MEDIA_PROJECTION_SERVICE);

    }



    public static long INTERVAL = 100L;


    public void clearActivity(Activity activity) {
        Activity current = ChatService.instance().getActivity();
        if (current == activity) {
            //LogDetail.LogD("CobrowseService", "clearing active activity " + activity);
            ChatService.instance().setActivity(null);
        }
    }

    public void setActivity(Activity activity) {
        ChatService.instance().setActivity(activity);
    }

    private void attachLifecycleListeners() {
        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.i("Cobrowse", "activity created " + activity);
            }

            public void onActivityStarted(Activity activity) {
                Log.i("Cobrowse", "activity started " + activity);
            }

            public void onActivityResumed(Activity activity) {
                Log.i("Cobrowse", "activity resumed " + activity);
                ChatBotService.this.setActivity(activity);
            }

            public void onActivityPaused(Activity activity) {
                Log.i("Cobrowse", "activity paused " + activity);
                ChatBotService.this.clearActivity(activity);
            }

            public void onActivityStopped(Activity activity) {
                Log.i("Cobrowse", "activity stopped " + activity);
                ChatBotService.this.clearActivity(activity);
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Log.i("Cobrowse", "activity save " + activity);
            }

            public void onActivityDestroyed(Activity activity) {
                Log.i("Cobrowse", "activity destroy " + activity);
                ChatBotService.this.clearActivity(activity);
            }
        });
    }




    /////////////////////////////////////////////////





}


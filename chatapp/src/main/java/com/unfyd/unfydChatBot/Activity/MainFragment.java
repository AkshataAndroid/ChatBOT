package com.unfyd.unfydChatBot.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.unfyd.unfydChatBot.Adapter.MessageAdapter;
import com.unfyd.unfydChatBot.Classes.ChatApplication;
import com.unfyd.unfydChatBot.Classes.Constants;
import com.unfyd.unfydChatBot.Classes.Message;
import com.unfyd.unfydChatBot.Classes.clsCarousel;
import com.unfyd.unfydChatBot.Database.DBHelper;
import com.unfyd.unfydChatBot.Database.clsChatDetail;
import com.unfyd.unfydChatBot.R;
import com.unfyd.unfydChatBot.Service.ChatBotService;
import com.unfyd.unfydChatBot.Service.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A chat fragment containing messages view and input form.
 */
public class MainFragment extends Fragment implements MessageAdapter.ListItemListener {

    private static final String TAG = "MainFragment";

    private static final int REQUEST_LOGIN = 0;
    ImageButton imgBtnDownload;
    private static final int TYPING_TIMER_LENGTH = 600;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    GridView mButtonmessages;
    List<String> mOptionS = new ArrayList<>();
    List<String> mImage = new ArrayList<>();
    List<String> mWord = new ArrayList<>();
    List<String> mPdf = new ArrayList<>();
    List<String> mExcel = new ArrayList<>();
    List<String> mPpt = new ArrayList<>();
    List<String> mAudio = new ArrayList<>();
    List<String> mVideo = new ArrayList<>();
   static String strIsLocationShared = "";

    List<clsCarousel> mclsCarouselLIST = new ArrayList<>();
   static Context context;
    private static RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private Button BtnYes, BtnNO;
    private static List<Message> mMessages = new ArrayList<Message>();
    private static RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private static String mUsername;
    private Socket mSocket;
    private Boolean isConnected = true;
    LocationManager manager;
    static String formattedDate = "";
    SimpleDateFormat df = null;
    Calendar cal = null;
    public  Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (null != mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getActivity().getApplicationContext(),
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    public Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                    removeTyping(username);
                    addMessage(username, message, type, mOptionS, mclsCarouselLIST, mImage,
                            mWord, mPdf, mExcel, mPpt, mAudio, mVideo);
//                    try {
//                        List mChatDetail = new ArrayList<>();
//                        mChatDetail.addAll(dbHelper.getAll(clsChatDetail.class));
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }

                }
            });
        }
    };

    private static void insertINTODB(String username, String message, String type) {
        try {
            DBHelper dbHelper = new DBHelper(context);
            formattedDate = Constants.df.format(Constants.cal.getTime());
            clsChatDetail objclsChatDetail = new clsChatDetail();

            objclsChatDetail.setUserName(username);
            objclsChatDetail.setMessage(message);
            objclsChatDetail.setMessageType(type);
            objclsChatDetail.setIsLocationShared(strIsLocationShared);
            if (strIsLocationShared.equalsIgnoreCase("1")) {
                objclsChatDetail.setLatitude(String.valueOf(LocationService.latitude));
                objclsChatDetail.setLongitude(String.valueOf(LocationService.longitude));
            } else {
                objclsChatDetail.setLatitude("");
                objclsChatDetail.setLongitude("");
            }


            objclsChatDetail.setResponseDateTime("");
            objclsChatDetail.setCreatedDateTime(formattedDate);
            objclsChatDetail.setIsFileDownloaded("0");
            objclsChatDetail.setFilePath("");
            try {
                dbHelper.createOrUpdate(objclsChatDetail);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };
    public Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
                }
            });
        }
    };
    public Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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
                    addTyping(username);
                }
            });
        }
    };
    public Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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
                    removeTyping(username);
                }
            });
        }
    };
    public Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

          //  mTyping = false;
            //
            //
            // mSocket.emit("stop typing");
        }
    };

//    public MainFragment() {
//       // super();
//    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        mAdapter = new MessageAdapter(context, mMessages, this, this);
        if (context instanceof Activity) {
            //this.listener = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {


            //setHasOptionsMenu(true);

//            ChatApplication app = (ChatApplication) getActivity().getApplication();
//            mSocket = app.getSocket();
//            mSocket.on(Socket.EVENT_CONNECT, onConnect);
//            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
//            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//            // mSocket.on("new message", onNewMessage);
//            mSocket.on("bot_new_message", onNewMessage);
//            mSocket.on("user joined", onUserJoined);
//            mSocket.on("user left", onUserLeft);
//            mSocket.on("typing", onTyping);
//            mSocket.on("stop typing", onStopTyping);
//            mSocket.connect();

          //startSignIn(context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            context = getActivity();
            strIsLocationShared = "0";

            // System.out.println("Current time => "+cal.getTime());

            //df = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");

            View view = inflater.inflate(R.layout.fragment_main, container, false);
            FirebaseApp.initializeApp(context);
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("Firbase id login", "Refreshed token: " + refreshedToken);
            return view;
        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        mSocket.disconnect();
//
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        // mSocket.off("new message", onNewMessage);
//        mSocket.off("bot_new_message", onNewMessage);
//        mSocket.off("user joined", onUserJoined);
//        mSocket.off("user left", onUserLeft);
//        mSocket.off("typing", onTyping);
//        mSocket.off("stop typing", onStopTyping);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
            BtnYes = (Button) view.findViewById(R.id.btnYES);
            BtnNO = (Button) view.findViewById(R.id.btnNO);
            // mButtonmessages = (GridView) view.findViewById(R.id. gridbuttons);

            // mButtonmessages.setLayoutManager(new LinearLayoutManager(getActivity()));
            //  mButtonmessages.setAdapter(mAdapter);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setStackFromEnd(true);
            mMessagesView.setLayoutManager(mLayoutManager);
            mMessagesView.setAdapter(mAdapter);

            mInputMessageView = (EditText) view.findViewById(R.id.message_input);
            mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                    if (id == R.id.send || id == EditorInfo.IME_NULL) {
                        attemptSend();
                        return true;
                    }
                    return false;
                }
            });
            mInputMessageView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (null == mUsername) return;
                    if (!ChatBotService.mSocket.connected()) return;

                    if (!mTyping) {
                        mTyping = true;
                       ChatBotService.mSocket.emit("typing");
                    }

                    mTypingHandler.removeCallbacks(onTypingTimeout);
                    mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptSend();
                }
            });
            BtnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strIsLocationShared = "1";
                    Intent intent = new Intent(getContext(), LocationService.class);
                    Bundle data1 = new Bundle();
                    data1.putString("startService", "clicked");
                    getContext().startService(intent);
                }
            });
            BtnNO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        strIsLocationShared = "0";
                        Intent intent = new Intent(getContext(), LocationService.class);
                        intent.putExtra("stopService", "notclicked");
                        getContext().stopService(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void turnONGPS() {
        LocationManager locationManager;
        boolean GpsStatus;
        try {
            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                Toast.makeText(getActivity(), "Gps already enabled", Toast.LENGTH_SHORT).show();
                //getActivity().finish();
            }
            // Todo Location Already on  ... end

            if (!hasGPSDevice(getActivity())) {
                Toast.makeText(getActivity(), "Gps not Supported", Toast.LENGTH_SHORT).show();
            }

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                Log.e("keshav", "Gps already enabled");
                Toast.makeText(getActivity(), "Gps not enabled", Toast.LENGTH_SHORT).show();
                enableLoc();
            } else {
                Log.e("keshav", "Gps already enabled");
                Toast.makeText(getActivity(), "Gps already enabled", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), REQUEST_LOCATION);

                                // getActivity().finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);

        // addLog(getResources().getString(R.string.message_welcome));
        //  addParticipantsLog(numUsers);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
        //    leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public static void addParticipantsLog(int numUsers) {
        addLog(context.getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    public static void addMessage(String username, String message, String type,
                           List<String> lst, List<clsCarousel> carouselLIST,
                           List<String> mImage, List<String> mWord, List<String> mPdfList,
                           List<String> mExcelLst, List<String> mPPtList, List<String> mAudioLst, List<String> mVideoLst) {
        if (lst != null && lst.size() > 0 || carouselLIST != null &&
                carouselLIST.size() > 0 || mImage != null && mImage.size() > 0
                || mWord != null && mWord.size() > 0 || mPdfList != null && mPdfList.size() > 0
                || mExcelLst != null && mExcelLst.size() > 0
                || mPPtList != null && mPPtList.size() > 0 || mAudioLst != null && mAudioLst.size() > 0 || mVideoLst != null && mVideoLst.size() > 0) {
            insertINTODB(username, message, type);
            mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                    .username(username).message(message).typeValue(type).option(lst).carousel(carouselLIST)
                    .image(mImage).word(mWord).pdf(mPdfList).excel(mExcelLst).ppt(mPPtList).audio((mAudioLst)).video(mVideoLst).build());
        } else {
            mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                    .username(username).message(message).typeValue(type).build());
        }

        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public static void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public static void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void attemptSend() {
        try {


            //if (null == mUsername) return;
            //if (!mSocket.connected()) return;
            mUsername = "aaa";
            mTyping = false;

            String message = mInputMessageView.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                mInputMessageView.requestFocus();
                return;
            }

            mInputMessageView.setText("");
            addMessage(mUsername, message, "", null, null,
                    null, null, null, null, null, null, null);

            // perform the sending message attempt.
            ChatBotService.mSocket.emit("bot_new_message", message);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void startSignIn(Context context) {
        mUsername = null;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void leave() {
        mUsername = null;
      //  mSocket.disconnect();
        //mSocket.connect();
      //  startSignIn();
    }

    private static void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void ListItemClick(int position) {
        try {
            String url = mMessages.get(0).getImageLst().get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //downloadImage(url);
    }


}


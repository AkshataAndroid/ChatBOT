package com.unfyd.unfydChatBot.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
//import android.support.v7.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


//import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.BuildConfig;
import com.unfyd.unfydChatBot.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private boolean mAlreadyStartedService = false;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);
            startStep1();

            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container,new MainFragment());
            transaction.commit();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
       //     getSupportActionBar().setDisplayHomeAsUpEnabled(false);
         //   getSupportActionBar().setDisplayShowHomeEnabled(false);
            //changeToolBarText("Survey");
            //finish();
            //finish();
        }
    }
    public void switchContent(int id, Fragment fragment) {
        try {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(id, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {
            //Passing null to indicate that it is executing for the first time.
            startStep2(null);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }



    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        TelephonyManager telephonyManager= (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        int networkType=telephonyManager.getNetworkType();
        System.out.println(mobile + " net " + isWifi);

        if (mobile == true)
        {
            if(networkType==TelephonyManager.NETWORK_TYPE_LTE){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //mNetworkStrength.setText("4G is Connected  and Signel Strength "+telephonyManager.getSignalStrength().getEvdoDbm());
                }
                // mNetworkStrength.setText("4G is Connected");
            }else if( networkType==TelephonyManager.NETWORK_TYPE_LTE){
                //mNetworkStrength.setText("3G is Connected");
            }else if(networkType==TelephonyManager.NETWORK_TYPE_GPRS){
                //mNetworkStrength.setText("2G is Connected");
            }
            //mNetworkType.setText("Mobile Network is connected");
            Toast.makeText(getApplicationContext(),"Mobile Network is connectd",Toast.LENGTH_LONG).show();
        }
        else if(isWifi==true)
        {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
            List<ScanResult> scanResult = wifiManager.getScanResults();
            for (int i = 0; i < scanResult.size(); i++) {
               // mNetworkStrength.setText("Wifi Strength "+scanResult.get(i).level+"");
                Log.d("scanResult", "Speed of wifi"+scanResult.get(i).level);//The db level of signal
            }

           // mNetworkType.setText("Wifi is connected");
            Toast.makeText(getApplicationContext(),"WiFi  is connected",Toast.LENGTH_LONG).show();
        }
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }
        //Yes there is active internet connection. Next check Location is granted by user or not.
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }
    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title_alert_no_internet);
        builder.setMessage(R.string.msg_alert_no_internet);
        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {
                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {
        if (!mAlreadyStartedService) {
            //mMsgView.setText(R.string.msg_location_service_started);
            Intent intent;
            //Start location sharing service to app server.........
            Bundle extras = getIntent().getExtras();
            if (Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
//                intent = new Intent(this, LocationService.class);
//                stopService(intent);
            }
            else{
//                intent = new Intent(this, LocationService.class);
//                startService(intent);
            }
            mAlreadyStartedService = false;
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }



    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionState3 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permissionState4 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissionState1 == PackageManager.PERMISSION_GRANTED
                && permissionState2 == PackageManager.PERMISSION_GRANTED
                && permissionState3 == PackageManager.PERMISSION_GRANTED
                && permissionState4 == PackageManager.PERMISSION_GRANTED;

    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        boolean shouldProvideRationale3 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean shouldProvideRationale4 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
        if (shouldProvideRationale || shouldProvideRationale2 || shouldProvideRationale3 || shouldProvideRationale4) {
            //  Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION ,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @SuppressLint("WrongConstant")
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //   Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();
//                MainFragment fragment=new MainFragment();
//                  FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
//                transaction.add(R.id.container,fragment);
//                transaction.commit();
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

}

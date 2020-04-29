package com.unfyd.unfydChatBot.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.unfyd.unfydChatBot.Classes.ChatApplication;
import com.unfyd.unfydChatBot.R;
import com.unfyd.unfydChatBot.Service.ChatBotService;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;
        private int color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameView = (EditText) findViewById(R.id.username_input);
        try {
//            Bundle bundle=getIntent().getExtras();
//            if(bundle!=null)
//            {
//                color=bundle.getInt("color",-1);
//            }
//            mUsernameView.setBackgroundColor(color);
          //  ChatApplication app = (ChatApplication) getApplication();
           // mSocket = app.getSocket();

            // Set up the login form.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });
            }

            Button signInButton = (Button) findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatBotService. mSocket.on("login", onLogin);
                  attemptLogin();

                    //String stringsplit="Helloo,Amruta Madhr,Raj,Mains,Pria";
                   // String[] splirarr=stringsplit.split(String.valueOf(',')).slice(1).join('_');
                   // Log.d("Pring",splirarr);
                    //UserLogin ul = new UserLogin();
                    //ul.execute();
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class UserLogin extends AsyncTask<Void, Void, String> {

        ProgressBar progressBar;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // progressBar = (ProgressBar) findViewById(R.id.progressBar);
          //  progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            //creating request handler object
            RequestHandler requestHandler = new RequestHandler();

            //creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("emailID", "ashwinr@unfyd.com");
            params.put("password", "c48f3ed0835f9fa74b0f8b62025ccfc8");
            response = requestHandler.sendPostRequest("https://demo15.unfyd.com/unfydcrm-unfyd/api/AdminHandler?Action=LoginUser");
            //returing the response
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            try {
                //converting response to json object

                JSONObject obj = new JSONObject(" ");

                //if no error in response
                if (obj.getBoolean("Result")) {
                    Toast.makeText(getApplicationContext(), obj.getString("Result"), Toast.LENGTH_SHORT).show();

                    // JSONObject object = new JSONObject(s);
                    JSONArray userArray = obj.getJSONArray("data");
                    // implement for loop for getting users list data
                    for (int i = 0; i < userArray.length(); i++) {
                        JSONObject userJson = userArray.getJSONObject(i);
//                        User user = new User(
//                                userJson.getString("RoleName"),
//                                userJson.getString("Email"),
//                                userJson.getString("RoleID"),
//                                userJson.getString("Capacity"),
//                                userJson.getString("Mobile"),
//                                userJson.getString("UserID"),
//                                userJson.getString("UserName")
//                        );
                        // create a object for getting contact data from JSONObject
                        //  SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                    }

                    // finish();
                    // startActivity(new Intent(getApplicationContext(), DetailsActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

       ChatBotService.mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        Toast.makeText(LoginActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        // perform the user login attempt.
        ChatBotService.mSocket.emit("add user", username);
       FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.relativelyout,new MainFragment());
        transaction.commit();


    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}




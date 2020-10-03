package com.example.interactiveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    TextView sign;
    EditText email,pass;
    Button login;
    LinearLayout login_linear_layout,login_btn_layout;
    Animation fromBottom,fromTop;
    String email_text, pass_text;
    SharedPreferences sp;
    static final String userData= "userData";
    static final String userId= "userId";
    static final String token= "token";
    static final String name= "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sp = getSharedPreferences(userData,Context.MODE_PRIVATE);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/myFont.ttf");
        sign = findViewById(R.id.sign);
        email = (EditText) findViewById(R.id.et_email);
        pass = (EditText) findViewById(R.id.et_pass);
        login_btn_layout = findViewById(R.id.login_btn_layout);
        login_linear_layout = findViewById(R.id.login_linear_layout);
        fromTop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        login_linear_layout.setAnimation(fromTop);

        login = findViewById(R.id.login_btn);

        fromBottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        login_btn_layout.setAnimation(fromBottom);

        sign.setTypeface(typeface);
        email.setTypeface(typeface);
        pass.setTypeface(typeface);
        login.setTypeface(typeface);





        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_text = email.getText().toString();
                pass_text = pass.getText().toString();
                loginRequest(email_text,pass_text);

            }
        });


    }

    private void loginRequest(final String email_txt,final String pass_txt){
        String url = "http://13.235.247.139:9527/api/v1/auth/login";


        Map<String, String> params = new HashMap();
        params.put("email",email_txt );
        params.put("password", pass_txt);
        JSONObject parameters = new JSONObject(params);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                try{
                JSONObject object = response.getJSONObject("data");
                 String user_id =  object.getString("userId");

                    JSONObject user_data =  object.getJSONObject("user");
                    String username = user_data.getString("name");
                    String token = object.getString("token");

                    saveData(username,user_id,token);

                    Intent intent = new Intent(LoginActivity.this,Dashboard.class);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("username",username);

                    startActivity(intent);

                    finish();

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"error parsing json", Toast.LENGTH_SHORT).show();
}


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
                Toast.makeText(getApplicationContext(),"Wrong coordinates or network error, please try again.", Toast.LENGTH_SHORT).show();

            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

  /*  private void sendPostRequstV2(final String email_txt,final String pass_txt){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://13.235.247.139:9527/api/v1/auth/login";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email_txt);
            jsonBody.put("password", pass_txt);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{
                        JSONObject object = new JSONObject(response);
                        Log.d("object",object.toString());

                    }catch (JSONException e){
                        Toast.makeText(getApplicationContext(),"Unable to parse the data", Toast.LENGTH_SHORT).show();
                        Log.d("object",e+"");
                    }




                    Intent intent = new Intent(LoginActivity.this,Dashboard.class);
                    startActivity(intent);
                    finish();



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ttt", error.toString());
                    Toast.makeText(getApplicationContext(),"Wrong coordinates !", Toast.LENGTH_SHORT).show();
                }
            }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    }

  /*  private void sendPostRequest() {
       // Log.d("ttt",email_txt+" send method "+pass_txt);
        String url = "http://13.235.247.139:9527/api/v1/auth/login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(LoginActivity.this,"success",Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            ///    Log.d("RVA", "error:" + error);

                int errorCode = 0;
                if (error instanceof TimeoutError) {
                    errorCode = -7;
                } else if (error instanceof NoConnectionError) {
                    errorCode = -1;
                } else if (error instanceof AuthFailureError) {
                    errorCode = -6;
                } else if (error instanceof ServerError) {
                    errorCode = 20;
                } else if (error instanceof NetworkError) {
                    errorCode = -1;
                } else if (error instanceof ParseError) {
                    errorCode = -8;
                }
                Toast.makeText(getApplicationContext(), errorCode+" ", Toast.LENGTH_LONG).show();


            }
        }){
            @Override
            protected Map<String,String> getParams() {
               // Log.d("ttt",email_txt+" get params "+pass_txt);
            Map<String,String> params = new HashMap<String,String>();
            params.put("email",email_txt);
            params.put("password",pass_txt);
            return params;
            }

             /*   @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                    headers.put("accept","application/json");
                    headers.put("Content-Type","application/json");
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }*/

    public void onBackPressed() {
    }

    private void saveData(String username, String userId1, String token1){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name,username);
        editor.putString(userId,userId1);
        editor.putString(token,token1);
        editor.commit();

    }

    private void retrieveData(){

        sp = getSharedPreferences(userData,Context.MODE_PRIVATE);
        if(sp.contains(name)){
            String username = sp.getString(name, "not exist");

        }
        if(sp.contains(userId)){
            String user_Id = sp.getString(userId, "not exist");
        }
        if(sp.contains(token)){
            String token1 = sp.getString(token,"not exist");
        }

    }
}
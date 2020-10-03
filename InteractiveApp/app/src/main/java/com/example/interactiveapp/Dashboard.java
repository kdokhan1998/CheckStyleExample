/**
 * Info about this package doing something for package-info.java file.
 */
package com.example.interactiveapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.interactiveapp.ExamActivity.userData;
import static com.example.interactiveapp.ExamActivity.userId;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Toolbar toolbar;
    LinearLayout linearLayout, linearLayout2, linearLayout3;
    Animation fromBottom, fromTop, fromRight;
    String username;;
    String e_total, e_month, e_completed, a_total, a_month, a_completed, m_total, m_month, m_completed;
    TextView mcq_tv, assignment_tv, exam_tv, main_txt, e_total_tv, e_completed_tv, e_month_number_tv, a_total_tv, a_completed_tv, a_month_number_tv, m_total_tv, m_completed_tv, m_month_number_tv, current_month_tv3, current_month_tv2, current_month_tv;

    SharedPreferences sp;
    static final String token= "token";
    String user_Id;
    String savedToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        defineViews();

        sp = getSharedPreferences(userData,MODE_PRIVATE);
        user_Id = sp.getString(userId,"-1");
        savedToken = sp.getString(token,"bad token");

        fetchDashboardData();

        linearLayout=findViewById(R.id.linearLayout);
        linearLayout2=findViewById(R.id.linearLayout2);
        linearLayout3=findViewById(R.id.linearLayout3);
        main_txt=findViewById(R.id.main_txt);
        exam_tv=findViewById(R.id.exam_tv);
        assignment_tv=findViewById(R.id.assignment_tv);
        mcq_tv=findViewById(R.id.mcq_tv);

        fromBottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        linearLayout.setAnimation(fromBottom);
        linearLayout2.setAnimation(fromBottom);
        linearLayout3.setAnimation(fromBottom);

        fromRight= AnimationUtils.loadAnimation(this,R.anim.fromright);
        exam_tv.setAnimation(fromRight);
        assignment_tv.setAnimation(fromRight);
        mcq_tv.setAnimation(fromRight);

        fromTop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        main_txt.setAnimation(fromTop);



        if(getIntent() != null){
           Intent i =  getIntent();
           username = i.getStringExtra("username");
        }


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
       toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.username_tv);
        navUsername.setText("Hi "+username);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);







    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_exam:
                startActivity(new Intent(Dashboard.this,ExamActivity.class));
                break;

            case R.id.nav_logout:
                logout();

                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchDashboardData(){
        String URL = "http://13.235.247.139:9527/api/v1/users/2/dashboard";
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        final String month_name = month_date.format(cal.getTime());

        Map<String, String> params = new HashMap();
        params.put(userId,user_Id);
        JSONObject parameters = new JSONObject(params);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
               try{

                   JSONObject object = response.getJSONObject("data");

                   JSONObject dashboard = object.getJSONObject("dashboard");

                   JSONObject exam = dashboard.getJSONObject("exam");
                   JSONObject assignment = dashboard.getJSONObject("assignment");
                   JSONObject mcq = dashboard.getJSONObject("mcq");

                    e_total = exam.getString("total");
                   e_total_tv.setText(e_total);

                    e_month = exam.getString("month");
                    e_month_number_tv.setText(e_month);

                    e_completed = exam.getString("completed");
                    e_completed_tv.setText(e_completed);

                    a_total = assignment.getString("total");
                      a_total_tv.setText(a_total);

                    a_month = assignment.getString("month");
                     a_month_number_tv.setText(a_month);

                    a_completed = assignment.getString("completed");
                     a_completed_tv.setText(a_completed);

                    m_total = mcq.getString("total");
                     m_total_tv.setText(m_total);

                    m_month = mcq.getString("month");
                    m_month_number_tv.setText(m_month);

                    m_completed = mcq.getString("completed");
                      m_completed_tv.setText(m_completed);

                   current_month_tv.setText(month_name);
                   current_month_tv2.setText(month_name);
                   current_month_tv3.setText(month_name);


                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Check your internet connection please !", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
                Log.d("courses", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + savedToken);
                    return params;

            }
        };


        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void defineViews(){
        e_total_tv = findViewById(R.id.e_total_tv);
        e_completed_tv = findViewById(R.id.e_completed_tv);
        e_month_number_tv = findViewById(R.id.e_month_number_tv);


        a_total_tv = findViewById(R.id.a_total_tv);
        a_completed_tv = findViewById(R.id.a_completed_tv);
        a_month_number_tv = findViewById(R.id.a_month_number_tv);



        m_total_tv = findViewById(R.id.m_total_tv);
        m_completed_tv = findViewById(R.id.m_completed_tv);
        m_month_number_tv = findViewById(R.id.m_month_number_tv);

        current_month_tv = findViewById(R.id.current_month_tv);
        current_month_tv2 = findViewById(R.id.current_month_tv2);
        current_month_tv3 = findViewById(R.id.current_month_tv3);




    }

    private void logout(){

        String url = "http://13.235.247.139:9527/api/v1/auth/logout";


        Map<String, String> params = new HashMap();
        params.put(userId,user_Id );
        JSONObject parameters = new JSONObject(params);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
                try{
                JSONObject object = response.getJSONObject("data");
                Log.d("response1",object.toString());

                Toast.makeText(getApplicationContext(),"Logged out successfully !",Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Dashboard.this,LoginActivity.class));
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
                Log.d("ttt", error.toString());
                Toast.makeText(getApplicationContext(),"Check your internet connection to logout", Toast.LENGTH_SHORT).show();
            }
        }


        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+savedToken);
                return params;
            }
        };


        Volley.newRequestQueue(this).add(jsonRequest);
    }





    }




package com.example.interactiveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;

    private ExpandableListView listView;
    private ExpandableListAdapter adapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    Toolbar toolbar;
    SharedPreferences sp;
    static final String userData= "userData";
    static final String userId= "userId";
    static final String token= "token";
    static final String COURSE_ID = "courseId";
    static final String name= "name";
    static final String course_name= "name";
    static final String course_id= "id";

    List<String> courses_name;
    List<String> courses_IDs;

    String user_Id;
    String savedToken;

    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.lvExp);
        courses_name = new ArrayList<>();
        courses_IDs = new ArrayList<>();
        spinner = findViewById(R.id.spinner);

        fetchUserCourses();



        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent i2 = new Intent(getApplicationContext(),ExamSubmit.class);
                startActivity(i2);

                return true;
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        initData();

        adapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(adapter);

        sp = getSharedPreferences(userData,MODE_PRIVATE);
        user_Id = sp.getString(userId,"-1");
        savedToken = sp.getString(token,"bad token");




    }

    private void fetchUserExams(String courseId){
        String URL = "http://13.235.247.139:9527/api/v1/users/2/course/0/exams";

        Map<String, String> params = new HashMap();
        params.put(userId,user_Id);
        params.put(COURSE_ID,courseId);
        JSONObject parameters = new JSONObject(params);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
             /*   try{
                    JSONObject object = response.getJSONObject("data");
                    JSONArray course = object.getJSONArray("list");
                    Log.d("response",course.toString());
                    for(int i=0;i<course.length();i++){
                        JSONObject course_data =  course.getJSONObject(i);

                        JSONObject course_details = course_data.getJSONObject("coursesDTO");
                        String c_id = course_details.getString("id");
                        String c_name = course_details.getString("name");
                        Log.d("response",c_id+"+"+c_name);
                        courses_name.add(c_name);
                        courses_IDs.add(c_id);
                        saveCourseData(c_name,c_id);

                        ArrayAdapter<String> coursesAdapter = new ArrayAdapter<>(ExamActivity.this,android.R.layout.simple_spinner_item,courses_name);
                        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinner.setAdapter(coursesAdapter);
                        spinner.setOnItemSelectedListener(ExamActivity.this);

                    }



                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"error parsing json", Toast.LENGTH_SHORT).show();
                }*/


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
                Toast.makeText(getApplicationContext(),"Check your internet connection to fetch exams", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+savedToken);
                return params;
            }
        };


        Volley.newRequestQueue(this).add(jsonRequest);

    }

    private void fetchUserCourses() {
        String URL = "http://13.235.247.139:9527/api/v1/users/2/courses?page=0&size=10";

        Map<String, String> params = new HashMap();
        params.put(userId,user_Id);
        JSONObject parameters = new JSONObject(params);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success
               try{
                   JSONObject object = response.getJSONObject("data");
                   JSONArray course = object.getJSONArray("list");
                   Log.d("response",course.toString());
                   for(int i=0;i<course.length();i++){
                       JSONObject course_data =  course.getJSONObject(i);

                       JSONObject course_details = course_data.getJSONObject("coursesDTO");
                       String c_id = course_details.getString("id");
                       String c_name = course_details.getString("name");
                       Log.d("response",c_id+"+"+c_name);
                       courses_name.add(c_name);
                       courses_IDs.add(c_id);
                       saveCourseData(c_name,c_id);

                       ArrayAdapter<String> coursesAdapter = new ArrayAdapter<String>(ExamActivity.this,android.R.layout.simple_spinner_item,courses_name) {

                          @NonNull
                           @Override
                           public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                               typeface = Typeface.createFromAsset(getAssets(),"font/myFont.ttf");
                               TextView v = (TextView) super.getView(position, convertView, parent);
                               v.setTypeface(typeface);
                               return v;
                           }

                           public View getDropDownView(int position, View convertView, ViewGroup parent)
                           {
                               TextView v = (TextView) super.getView(position,convertView,parent);
                               v.setTextColor(Color.BLUE);
                               v.setTypeface(typeface);
                               return v;
                           }
                       };
                       coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                       spinner.setAdapter(coursesAdapter);
                       spinner.setOnItemSelectedListener(ExamActivity.this);

                   }



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
                Toast.makeText(getApplicationContext(),"Check your internet connection to fetch courses", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+savedToken);
                return params;
            }
        };


        Volley.newRequestQueue(this).add(jsonRequest);
    }



    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("MCQ");
        listDataHeader.add("Assignments");
        listDataHeader.add("Writing Exams");
        listDataHeader.add("Test1");
        listDataHeader.add("Test2");
        listDataHeader.add("Test3");

        List<String> mcq = new ArrayList<>();
        mcq.add("MCQ 1 (Note : All items are clickable)");
        mcq.add("MCQ 2");
        mcq.add("MCQ 3");

        List<String> assignments = new ArrayList<>();
        assignments.add("assignments 1");
        assignments.add("assignments 3");
        assignments.add("assignments 4");

        List<String> writingEx = new ArrayList<>();
        writingEx.add("test exam");
        writingEx.add("test exam 2");
        writingEx.add("test exam 3");
        List<String> writingEx2 = new ArrayList<>();
        writingEx2.add("test exam");
        writingEx2.add("test exam 2");
        writingEx2.add("test exam 3");
        List<String> writingEx3 = new ArrayList<>();
        writingEx3.add("test exam");
        writingEx3.add("test exam 2");
        writingEx3.add("test exam 3");
        List<String> writingEx4 = new ArrayList<>();
        writingEx4.add("test exam");
        writingEx4.add("test exam 2");
        writingEx4.add("test exam 3");

        listHash.put(listDataHeader.get(0), mcq);
        listHash.put(listDataHeader.get(1), assignments);
        listHash.put(listDataHeader.get(2), writingEx);
        listHash.put(listDataHeader.get(3), writingEx2);
        listHash.put(listDataHeader.get(4), writingEx3);
        listHash.put(listDataHeader.get(5), writingEx4);
    }

    private void saveCourseData(String courseName,String courseId){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(course_name,courseName);
        editor.putString(course_id,courseId);
        editor.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(this, "Selected item +"+courses_name.get(i), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, courses_name.get(i)+" is selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
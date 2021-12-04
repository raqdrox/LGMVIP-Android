package com.example.lgmvip_covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Hashtable<String, List<Model>> dataDict =new Hashtable<>();
    private RVAdapter rvAdapter;
    private Spinner stateSelectionSpin;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=(RecyclerView) findViewById(R.id.StateListRV);
        stateSelectionSpin = (Spinner)findViewById(R.id.StateSelectView);
        stateSelectionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = (String)parent.getItemAtPosition(pos);
                ShowData(item);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        fetchData();
    }

    private void fetchData()
    {
        String url ="https://data.covid19india.org/state_district_wise.json";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());

                    Iterator<String> iterStates = jsonObject.keys();
                    List<CharSequence> StateList = new ArrayList<>();
                    iterStates.next();
                    while(iterStates.hasNext())
                    {
                        String StateName = iterStates.next();
                        List<Model> modelList = new ArrayList<>();
                        JSONObject districtJSON =jsonObject.getJSONObject(StateName).getJSONObject("districtData");
                        Iterator<String> iterDist = districtJSON.keys();
                        while(iterDist.hasNext())
                        {
                            String district = iterDist.next();
                            JSONObject distDataJSON= districtJSON.getJSONObject(district);
                            int active = distDataJSON.getInt("active");
                            int confirmed = distDataJSON.getInt("confirmed");
                            int deceased = distDataJSON.getInt("deceased");
                            int recovered = distDataJSON.getInt("recovered");
                            modelList.add(new Model(district,active,confirmed,deceased,recovered));
                        }
                        dataDict.put(StateName,modelList);
                        StateList.add(StateName);
                    }
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, StateList);
                    stateSelectionSpin.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    void ShowData(String selection)
    {
        System.out.println(selection);
        rvAdapter = new RVAdapter(dataDict.get(selection));
        recyclerView.setAdapter(rvAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

    }


}
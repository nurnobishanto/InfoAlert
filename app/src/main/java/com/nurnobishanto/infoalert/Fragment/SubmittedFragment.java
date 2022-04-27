package com.nurnobishanto.infoalert.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nurnobishanto.infoalert.Adapter.Adapter;
import com.nurnobishanto.infoalert.Constant;
import com.nurnobishanto.infoalert.Model.Model;
import com.nurnobishanto.infoalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SubmittedFragment extends Fragment {

    private ArrayList<Object> mListItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String deviceID;
    @SuppressLint("HardwareIds")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_submitted, container, false);
        mRecyclerView = view.findViewById(R.id.myRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout.setRefreshing(false);
        getData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        deviceID = Settings.Secure.getString(getContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        return view;
    }

    private  void getData()
    {
        swipeRefreshLayout.setRefreshing(true);
        mListItems.clear();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.SUBMITTED, response ->{

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("status")){
                    JSONArray array = object.getJSONArray("complain");
                    for (int i = 0;i<array.length();i++){
                        JSONObject obj = array.getJSONObject(i);
                        Model item = new Model();

                        item.setId(obj.getString("id"));
                        item.setTitle(obj.getString("title"));
                        item.setComplain(obj.getString("complain"));
                        item.setStatus(obj.getString("type"));
                        item.setCategory(obj.getString("category"));
                        item.setDate(obj.getString("created_at"));
                        item.setImage(obj.getString("image"));
                        item.setDevice(obj.getString("device"));

                        mListItems.add(item);
                    }


                    //Create adapter
                    adapter = new Adapter(mListItems, new Adapter.MyRecyclerViewItemClickListener()
                    {
                        //Handling clicks
                        @Override
                        public void onItemClicked(Model model)
                        {


                        }
                    });

                    //Set adapter to RecyclerView
                    mRecyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);


                }


            }catch (JSONException e){
                e.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);

            }

        },error -> {
            error.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("device", deviceID);
                return  map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }
}
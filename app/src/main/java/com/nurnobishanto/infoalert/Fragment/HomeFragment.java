package com.nurnobishanto.infoalert.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nurnobishanto.infoalert.Constant;
import com.nurnobishanto.infoalert.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private TextView changeImage;
    private ImageView postImage;
    private TextInputLayout postTileLayout,postDescLayout;
    private TextInputEditText postTitle,postDesc;
    private Button addPostBtn;
    private static final int PICK_POST_IMAGE = 100;
    Uri postimageUri;
    Bitmap postbitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        changeImage = view.findViewById(R.id.changeImage);
        postImage = view.findViewById(R.id.postImage);
        addPostBtn = view.findViewById(R.id.addPost);
        postTitle = view.findViewById(R.id.postTitle);
        postDesc = view.findViewById(R.id.postDesc);
        postTileLayout = view.findViewById(R.id.titleInputLayout);
        postDescLayout = view.findViewById(R.id.descInputLayout);


        addPostBtn.setOnClickListener(v -> {
            if(Validate()){

                AddPost();
            }
        });
        changeImage.setOnClickListener(v -> {

            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_POST_IMAGE);

        });


        postTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (postTitle.getText().toString().length()<5){
                    postTileLayout.setErrorEnabled(true);
                    postTileLayout.setError("Post Title must be at least 5 Character!");
                }else {
                    postTileLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (postDesc.getText().toString().length()<50){
                    postDescLayout.setErrorEnabled(true);
                    postDescLayout.setError("Post Description must be at least 50 Character!");
                }else {
                    postDescLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void AddPost() {

        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Posting");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_COMPLAIN, response ->{

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("status")){
                    Toast.makeText(getContext(),object.getString("message"),Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(getContext(),object.getString("message"),Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();

            }
            Toast.makeText(getContext(),"50",Toast.LENGTH_LONG).show();
            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            Toast.makeText(getContext(),"10",Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }){


            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("title",postTitle.getText().toString());
                map.put("desc",postDesc.getText().toString());
                if (postbitmap!=null){
                    map.put("image",BitmapToString(postbitmap));
                }
                return  map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private boolean Validate() {
        if (postTitle.getText().toString().isEmpty()){
            postTileLayout.setErrorEnabled(true);
            postTileLayout.setError("Title is required!");
            return false;
        }
        else if (postTitle.getText().toString().length()<5){
            postTileLayout.setErrorEnabled(true);
            postTileLayout.setError("Post Title must be at least 5 Character!");
            return false;
        }
        else if (postDesc.getText().toString().isEmpty()){
            postTileLayout.setErrorEnabled(false);
            postDescLayout.setErrorEnabled(true);
            postDescLayout.setError("Post Description is required!");
            return false;
        }
        else if (postDesc.getText().toString().length()<50){
            postTileLayout.setErrorEnabled(false);
            postDescLayout.setErrorEnabled(true);
            postDescLayout.setError("Post Description must be at least 50 Character!");
            return false;
        }
        else {
            postDescLayout.setErrorEnabled(false);
            return true;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_POST_IMAGE){
            postimageUri = data.getData();
            Picasso.get().load(postimageUri).into(postImage);


            try {
                postbitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),postimageUri);


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return result;
    }



}
package com.nurnobishanto.infoalert.Fragment;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nurnobishanto.infoalert.AppRelatedActivity;
import com.nurnobishanto.infoalert.Constant;
import com.nurnobishanto.infoalert.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private TextView changeImage;
    private ImageView postImage;
    private TextInputLayout postTileLayout,postDescLayout,phoneLayout,addressLayout;
    private TextInputEditText postTitle,postDesc,locationet,phone,address;
    private Button addPostBtn;
    private static final int PICK_POST_IMAGE = 105;

    Uri postimageUri;
    Bitmap postbitmap = null;
    double latitude;
    double longitude;
    int PERMISSION_ID = 10;
    String deviceID;
    StorageReference storageReference;
    FusedLocationProviderClient mFusedLocationClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        changeImage = view.findViewById(R.id.changeImage);
        postImage = view.findViewById(R.id.postImage);
        addPostBtn = view.findViewById(R.id.addPost);
        postTitle = view.findViewById(R.id.postTitle);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        phoneLayout = view.findViewById(R.id.phoneInputLayout);
        addressLayout = view.findViewById(R.id.addrInputLayout);
        postDesc = view.findViewById(R.id.postDesc);
        postTileLayout = view.findViewById(R.id.titleInputLayout);
        postDescLayout = view.findViewById(R.id.descInputLayout);
        locationet = view.findViewById(R.id.location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLastLocation();
        storageReference = FirebaseStorage.getInstance().getReference("Image");
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate()){
                    if (postimageUri != null){
                        UploadImage();
                    }else {
                        AddPost("null");
                    }


                }
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_POST_IMAGE);


            }
        });


        postTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (postTitle.getText().toString().length()<5){
                    postTileLayout.setErrorEnabled(true);
                    postTileLayout.setError("Title must be at least 5 Character!");
                }else {
                    postTileLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone.getText().toString().length()<11){
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError("Phone must be at least 11 Character!");
                }else {
                    phoneLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (address.getText().toString().length()<10){
                    addressLayout.setErrorEnabled(true);
                    addressLayout.setError("Address must be at least 10 Character!");
                }else {
                    addressLayout.setErrorEnabled(false);
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
                if (postDesc.getText().toString().length()<10){
                    postDescLayout.setErrorEnabled(true);
                    postDescLayout.setError("Complain must be at least 10 Character!");
                }else {
                    postDescLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        deviceID = Settings.Secure.getString(getContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        return view;
    }

    private void AddPost(String url) {
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
                    postTitle.setText("");
                    postDesc.setText("");
                    phone.setText("");
                    address.setText("");
                    postImage.setImageDrawable(null);
                    //finish();
                }
                else {
                    Toast.makeText(getContext(),object.getString("message"),Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();

            }

            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }){


            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("title", Objects.requireNonNull(postTitle.getText()).toString());
                map.put("complain", Objects.requireNonNull(postDesc.getText()).toString());
                map.put("phone", Objects.requireNonNull(phone.getText()).toString());
                map.put("address", Objects.requireNonNull(address.getText()).toString());
                map.put("device", deviceID);
                map.put("latitude", latitude+"");
                map.put("longitude", longitude+"");
                map.put("category", "GENERAL");
                if (!url.equals("null")) {
                    map.put("pic", url);
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
            postTileLayout.setError("Complain Title must be at least 5 Character!");
            return false;
        }
        else if (postDesc.getText().toString().isEmpty()){
            postTileLayout.setErrorEnabled(false);
            postDescLayout.setErrorEnabled(true);
            postDescLayout.setError("Complain Description is required!");
            return false;
        }
        else if (postDesc.getText().toString().length()<10){
            postTileLayout.setErrorEnabled(false);
            postDescLayout.setErrorEnabled(true);
            postDescLayout.setError("Complain Description must be at least 10 Character!");
            return false;
        }
        else if (address.getText().toString().length()<10){
            postDescLayout.setErrorEnabled(false);
            addressLayout.setErrorEnabled(true);
            addressLayout.setError("Address must be at least 10 Character!");
            return false;
        }
        else if (phone.getText().toString().length()<10){
            addressLayout.setErrorEnabled(false);
            phoneLayout.setErrorEnabled(true);
            phoneLayout.setError("Phone must be at least 10 Character!");
            return false;
        }
        else {
            phoneLayout.setErrorEnabled(false);
            return true;
        }

    }

    @SuppressLint({"SetTextI18n", "LongLogTag"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_POST_IMAGE){
            postimageUri = data.getData();
            try {
                //Getting the Bitmap from Gallery
                postbitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), postimageUri);
                //Setting the Bitmap to ImageView
                postImage.setImageBitmap(postbitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }



        }
    }







    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        locationet.setText(latitude+","+longitude);

                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            locationet.setText(latitude+","+longitude);
        }
    };

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }


    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity() , new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImage() {


        if (postimageUri != null) {

            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Image Uploading..");
            pd.show();
            final StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(postimageUri));

            postImage.setDrawingCacheEnabled(true);
            postImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask = ref.putFile(postimageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                        AddPost(downloadUri.toString());
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded Failed ", Toast.LENGTH_LONG).show();
                    }
                    pd.dismiss();
                }
            });



        }
    }





}
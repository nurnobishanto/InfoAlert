package com.nurnobishanto.infoalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NecessaryActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Necessary Complain");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_necessary);

        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        phoneLayout = findViewById(R.id.phoneInputLayout);
        addressLayout = findViewById(R.id.addrInputLayout);
        changeImage = findViewById(R.id.changeImage);
        postImage = findViewById(R.id.postImage);
        addPostBtn = findViewById(R.id.addPost);
        postTitle = findViewById(R.id.postTitle);
        postDesc = findViewById(R.id.postDesc);
        postTileLayout = findViewById(R.id.titleInputLayout);
        postDescLayout = findViewById(R.id.descInputLayout);
        locationet = findViewById(R.id.location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        deviceID = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
    }
    private void AddPost(String url) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Posting");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_COMPLAIN, response ->{

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("status")){
                    Toast.makeText(this,object.getString("message"),Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(this,object.getString("message"),Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();

            }

            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
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
                map.put("category", "NECESSARY");
                if (!url.equals("null")) {
                    map.put("pic", url);
                }
                return  map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_POST_IMAGE){
            postimageUri = data.getData();
            try {
                //Getting the Bitmap from Gallery
                postbitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), postimageUri);
                //Setting the Bitmap to ImageView
                postImage.setImageBitmap(postbitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }



        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImage() {


        if (postimageUri != null) {

            final ProgressDialog pd = new ProgressDialog(this);
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
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        AddPost(downloadUri.toString());

                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(getApplicationContext(), "Image Uploaded Failed ", Toast.LENGTH_LONG).show();

                    }
                    pd.dismiss();
                }
            });



        }
    }



}
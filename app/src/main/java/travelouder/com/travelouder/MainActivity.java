package travelouder.com.travelouder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.net.sip.SipSession;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static travelouder.com.travelouder.ImageDetailActivity.TAGPOST;

public class MainActivity extends BaseActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private FloatingActionButton uploadButton;
    private FloatingActionButton likeButton;
    private Place place;
    FirebaseUser fbUser;
    DatabaseReference database;
    PostAdapter adapter;
    String caption;
    boolean isHomeMode;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    private static final int RESULT_LOAD_IMAGE = 3;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

//        String commonUrl = "https://www.taketours.com/images/slides/alaska.png";

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }
        mGoogleSignInClient=AuthenticationHelper.getmGoogleSignInClient(getApplicationContext());
        uploadButton = findViewById(R.id.uploadButton);
        likeButton = findViewById(R.id.likeButton);
        recyclerView = findViewById(R.id.rv_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        adapter = new PostAdapter(getApplicationContext(), width, new PostAdapter.ClickHandler() {
            @Override
            public void onClick(Image imagePost) {
                Bundle args= new Bundle ();
                args.putParcelable(TAGPOST,imagePost);
                Intent i = new Intent(getApplicationContext(),
                        ImageDetailActivity.class);
                i.putExtras(args);
                startActivity(i);
            }
        },isHomeMode);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    uploadButton.hide();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    uploadButton.show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();

        readAllImages();
    }

    public void readAllImages(){

        Log.d("FEO55", "Current user : " + fbUser.getUid());
        Query imagesQuery = database.child("images").orderByKey().limitToFirst(100);
        imagesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Image image = dataSnapshot.getValue(Image.class);
                boolean isCurrentUser=fbUser.getUid().equals(image.userId);
                Log.d("FEO55" , "equals: " + image.userId);
                    adapter.addImage(image,isCurrentUser);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                isHomeMode=!isHomeMode;
                item.setIcon(isHomeMode?R.drawable.baseline_home_black_48:R.drawable.baseline_perm_identity_black_48);
                adapter.setMode(isHomeMode);
                break;
            case R.id.action_sign_out:
                showProgressDialog();
                signOut();
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
                hideProgressDialog();
                break;
            // Something else
            case R.id.action_settings:
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadImage(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
       }
    }
    public void onLikeClick(View view) {

        Toast.makeText(getApplicationContext(),"Like Button clicked!",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        }
    }

    Uri currentlyUsedUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            currentlyUsedUri = data.getData();
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                currentlyUsedUri = null;
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                currentlyUsedUri = null;
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);

                UploadPostDialog uploadPostDialog = UploadPostDialog.getInstance(this, new OnCaptionReveive() {
                    @Override
                    public void onCaptionSubmitted(String caption) {
                        MainActivity.this.caption = caption;
                        uploadToServer();
                    }
                });
                uploadPostDialog.show(getSupportFragmentManager(), "uploadPostDialogTag");



            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());
                currentlyUsedUri = null;

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                currentlyUsedUri = null;
            }
        }
    }

    private  void uploadToServer(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        final StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(currentlyUsedUri);
        showProgressDialog();
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    hideProgressDialog();
                    Uri downloadUri = task.getResult();
                    // save image to database
                    String userEmail= fbUser.getEmail();
                    String key = database.child("images").push().getKey();
                    LatLng latLng = place.getLatLng();
                    Double lat=latLng.latitude;
                    Double lon=latLng.longitude;
                    String placeName = place.getName().toString();
                    Image image = new Image(userEmail,key, fbUser.getUid(), downloadUri.toString(), caption,lat,lon, placeName);
                    database.child("images").child(key).setValue(image);

                    Toast.makeText(MainActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                    // save image to database
                   /* String key = database.child("images").push().getKey();
                    Image image = new Image(key, fbUser.getUid(), downloadUrl.toString());
                    database.child("images").child(key).setValue(image);*/
                    currentlyUsedUri = null;
                } else {
                    // Handle failures
                    // ...
                    // Handle unsuccessful uploads
                    hideProgressDialog();
                    Toast.makeText(MainActivity.this, "Upload failed!", Toast.LENGTH_LONG).show();
                    currentlyUsedUri = null;
                }
            }
        });

    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut();
    }


}

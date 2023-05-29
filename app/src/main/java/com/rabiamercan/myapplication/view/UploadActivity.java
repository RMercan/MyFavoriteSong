package com.rabiamercan.myapplication.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rabiamercan.myapplication.R;
import com.rabiamercan.myapplication.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Uri myFavoriteSongUri;
    private Button button;


    // İzin başlatıcıları
    ActivityResultLauncher<Intent> activityResultLauncher;
    //ActivityResultLauncher<Intent> audioPickerLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private ActivityUploadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void btnUploadClicked(View view){

        button = findViewById(R.id.btnRating);

        if(myFavoriteSongUri != null){
            //universal uniq id
            UUID uuid = UUID.randomUUID();
            String audioName = "audios/" + uuid + ".mp3";

            storageReference.child(audioName).putFile(myFavoriteSongUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //download url
                    StorageReference newReference = firebaseStorage.getReference(audioName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            String bilgiler = binding.editSongName.getText().toString();

                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("useremail", email);
                            postData.put("downloadurl", downloadUrl);
                            postData.put("songInfo", bilgiler);
                            postData.put("date", FieldValue.serverTimestamp());
                            postData.put("score", 0.0f);

                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void selectSong(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //android 33 ve+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                    Snackbar.make(view, "Permission needed for audio files", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // İzin isteme işlemini gerçekleştir
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
                        }
                    }).show();
                } else {
                    // İzin isteme işlemini gerçekleştir
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
                }
            } else {
                Intent intentToAudioFile = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToAudioFile);
            }
        } else {
            //android 33-
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view, "Permission needed for audio files", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // İzin isteme işlemini gerçekleştir
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    // İzin isteme işlemini gerçekleştir
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                Intent intentToAudioFile = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToAudioFile);
            }

        }

    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                       /* // Add a specific media item.
                        ContentResolver resolver = getApplicationContext()
                                .getContentResolver();

// Find all audio files on the primary external storage device.
                        Uri audioCollection;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            audioCollection = MediaStore.Audio.Media
                                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                        } else {
                            audioCollection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

// Publish a new song.
                        ContentValues newSongDetails = new ContentValues();
                        newSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME,
                                "My Song.mp3");

// Keep a handle to the new song's URI in case you need to modify it
// later.
                        myFavoriteSongUri = resolver
                                .insert(audioCollection, newSongDetails);*/
                        myFavoriteSongUri = intentFromResult.getData();
                        //seçilen urili dosyayı göster sonra da veritabanına kaydetcez
                        binding.selectSong.setImageURI(myFavoriteSongUri);
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentToAudioFile = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToAudioFile);
                } else {
                    Toast.makeText(UploadActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

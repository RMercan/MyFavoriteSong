package com.rabiamercan.myapplication.view;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriMatcher;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rabiamercan.myapplication.R;
import com.rabiamercan.myapplication.adapter.PostAdapter;
import com.rabiamercan.myapplication.databinding.ActivityListenAndRateBinding;
import com.rabiamercan.myapplication.model.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.DESKeySpec;

public class listenAndRateActivity extends UploadActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private MediaPlayer mediaPlayer;
    private ActivityListenAndRateBinding binding;
    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postsCollection = firebaseFirestore.collection("Posts");
    //private PostAdapter a;

    RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
    Button btnRating = (Button) findViewById(R.id.btnRating);
    TextView ratingDisplayTextView = (TextView) findViewById(R.id.ratingDisplayTextView);
    String downloadUrl;
    String scoreString;
    Double ratingScore;

    // private RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListenAndRateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        getData();





       /* ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingDisplayTextView.setText("Verilen puan: " + rating);

                ratingScore =(double) rating;
            }

        }); */
       /*
      db.collection("Posts").addSnapshotListener((snapshot, error) -> {

            if (error != null) {
                Toast.makeText(listenAndRateActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else {
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        Object scoreObj = document.get("score");
                        if (scoreObj instanceof String) {
                            String score = (String) scoreObj;
                            // Diğer işlemleri burada yapabilirsiniz.

                            postsCollection.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    double totalScore = 0.0;
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            double docScore = documentSnapshot.getDouble("score");
                                            totalScore += docScore;
                                        }
                                        double newScore = totalScore + rating;

                                        DocumentReference documentRef = firebaseFirestore.collection("Posts").document("documentId"); // Belge referansını doğru belgeye ayarlayın
                                        documentRef.update("score", newScore)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Score başarıyla güncellendi
                                                    Log.d("Firebase", "Score başarıyla güncellendi");
                                                    //viewHolder.recyclerRowBinding.recyclerViewScoreText.setText(String.valueOf(newScore));
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Score güncelleme başarısız oldu
                                                    Log.e("Firebase", "Score güncelleme başarısız oldu", e);
                                                });
                                    /*Intent intent = new Intent(listenAndRateActivity.this, PostAdapter.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                    } else {
                                        // Koleksiyon boş veya belge bulunamadı
                                        Log.d("Firebase", "Koleksiyon boş veya belge bulunamadı");
                                    }
                                } else {
                                    // Okuma işlemi başarısız oldu
                                    Log.d("Firebase", "Okuma işlemi başarısız oldu");
                                }
                            });
                        }
                    }
                }
            }

        });
*/

    }



    /* @Override
            public void onClick(View v) {
                float yeniPuan = ratingBar.getRating();



                // İlgili belgeye erişmek için uygun bir yol seçin, örneğin belge kimliğini veya başka bir özelliğini kullanabilirsiniz
                // Bu örnekte belge kimliğini kullanıyorum, ancak ihtiyacınıza göre değiştirebilirsiniz
                String postId = "Posts";

                // Belge referansını oluşturun
                DocumentReference docRef = db.collection("Posts").document(postId);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Mevcut puanı alın ve Object türünden String türüne dönüştürün
                            Object mevcutPuanObj = documentSnapshot.get("score");
                            if (mevcutPuanObj instanceof Double) {
                                Double mevcutPuanDouble = (Double) mevcutPuanObj;
                                String mevcutPuan = String.valueOf(mevcutPuanDouble);

                                // Yeni puanı toplamaya ekleyin
                                float mevcutPuanFloat = Float.parseFloat(mevcutPuan);
                                float toplamPuan = mevcutPuanFloat + yeniPuan;

                                // Güncellemek için veri haritası oluşturun
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("score", toplamPuan);

                                // Firestore üzerinde belgeyi güncelleyin
                                docRef.update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Başarılı bir şekilde güncellendi
                                                Intent intent = new Intent(listenAndRateActivity.this, PostAdapter.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(listenAndRateActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(listenAndRateActivity.this, "Mevcut puan Double türünde değil", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(listenAndRateActivity.this, "Belge bulunamadı", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Belge alınamadığında veya hata olduğunda işlemler
                        Toast.makeText(listenAndRateActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        */



    private void getData(){
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Toast.makeText(listenAndRateActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();

                        downloadUrl = (String) data.get("downloadurl");
                        Object scoreObj = data.get("score");
                        scoreString = String.valueOf(scoreObj);

                    }

                }
            }
        });
    }

   public void btnRatingClicked(View view){
        ratingScore = (double) ratingBar.getRating();
        scoreString += ratingScore;

            firebaseFirestore.collection("Posts").addSnapshotListener((snapshot, error) -> {

                if (error != null) {
                    Toast.makeText(listenAndRateActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Object scoreObj = document.get("score");
                            if (scoreObj instanceof String) {
                                String score = (String) scoreObj;
                                // Diğer işlemleri burada yapabilirsiniz.

                                postsCollection.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        double totalScore = 0.0;
                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                double docScore = documentSnapshot.getDouble("score");
                                                totalScore += docScore;
                                            }

                                            DocumentReference documentRef = firebaseFirestore.collection("Posts").document("documentId"); // Belge referansını doğru belgeye ayarlayın
                                            documentRef.update("score", scoreString)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Score başarıyla güncellendi
                                                        Log.d("Firebase", "Score başarıyla güncellendi");
                                                        //viewHolder.recyclerRowBinding.recyclerViewScoreText.setText(String.valueOf(newScore));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Score güncelleme başarısız oldu
                                                        Log.e("Firebase", "Score güncelleme başarısız oldu", e);
                                                    });
                                            Intent intent = new Intent(listenAndRateActivity.this, PostAdapter.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        } else {
                                            // Koleksiyon boş veya belge bulunamadı
                                            Log.d("Firebase", "Koleksiyon boş veya belge bulunamadı");
                                        }
                                    } else {
                                        // Okuma işlemi başarısız oldu
                                        Log.d("Firebase", "Okuma işlemi başarısız oldu");
                                    }
                                });
                            }
                        }
                    }
                }

            });


    }

    public void dinleClicked(View view) throws IOException {
        Uri myUri = Uri.parse(downloadUrl); // initialize Uri here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

}


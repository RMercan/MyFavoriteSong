package com.rabiamercan.myapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriMatcher;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rabiamercan.myapplication.R;
import com.rabiamercan.myapplication.adapter.PostAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.DESKeySpec;

public class listenAndRateActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_and_rate);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        float toplamPuan = 0.0f;

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button btnRating = (Button) findViewById(R.id.btnRating);
        final TextView ratingDisplayTextView = (TextView) findViewById(R.id.ratingDisplayTextView);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingDisplayTextView.setText("Verilen puan: " + rating);
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float yeniPuan = ratingBar.getRating();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

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





    }
}
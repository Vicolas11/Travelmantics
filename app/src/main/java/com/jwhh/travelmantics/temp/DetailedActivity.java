package com.jwhh.travelmantics.temp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jwhh.travelmantics.R;
import com.jwhh.travelmantics.TravelDetail;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private Boolean isVisible = false;
    TextView mTvDealUpdate, mTvDescUpdate, mTvPriceUpdate;
    EditText mEtDeal, mEtDesc, mEtPrice;
    ProgressBar mProgressBar;
    ImageView mImageView, mImageViewUpdate;
    Button mUpdateBtn;
    View mLayout;
    DatabaseReference mDatabaseRef, mDeleteRef;
    StorageReference mStorageRef, mStoreRef;
    public Uri mImgUri;
    public String mTravelKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        mTvDealUpdate = (TextView) findViewById(R.id.tvDeal);
        mTvDescUpdate = (TextView) findViewById(R.id.tvDesc);
        mTvPriceUpdate = (TextView) findViewById(R.id.tvPrice);
        mEtDeal = (EditText) findViewById(R.id.etDeal);
        mEtDesc = (EditText) findViewById(R.id.etDescription);
        mEtPrice = (EditText) findViewById(R.id.etPrice);
        mImageViewUpdate = (ImageView) findViewById(R.id.uploadImgViewUpdate);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mUpdateBtn = (Button) findViewById(R.id.chooseImgBtnUpdate);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarUpdate);
        mLayout = (View) findViewById(R.id.updateLayout);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("traveldeal");
        mTravelKey = getIntent().getStringExtra("TravelKey");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("traveldeal").child(mTravelKey);
        mStoreRef = FirebaseStorage.getInstance().getReference().child("traveldeal");
        mDeleteRef = mDatabaseRef.child(mTravelKey);

        mDatabaseRef.child(mTravelKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Get values from each Unique push key
                        String etDeal = snapshot.child("deal").getValue(String.class);
                        String etPrice = snapshot.child("price").getValue(String.class);
                        String etDesc = snapshot.child("description").getValue(String.class);
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                        //Assigned the values gotten into each Widgets referenced
                        mTvDealUpdate.setText(etDeal);
                        mTvPriceUpdate.setText(etPrice);
                        mTvDescUpdate.setText(etDesc);
                        Picasso.get().load(imageUrl).into(mImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mImageViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileImg();
            }
        });

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContent();
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getMimeTypeFromExtension(cr.getType(uri));
    }

    private void cleanContent() {
        mEtDeal.setText("");
        mEtPrice.setText("");
        mEtDesc.setText("");
    }

    private void updateContent() {

        String key = mDatabaseRef.push().getKey();
        final StorageReference fileReference = mStorageRef.child(key + "." + getFileExtension(mImgUri));
        //Pick the image selected from phone and upload it to FireBase
        fileReference.putFile(mImgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(0);
                                    }
                                }, 1000);

                                String etDeal = mEtDeal.getText().toString().trim();
                                String etPrice = mEtPrice.getText().toString().trim();
                                String etDesc = mEtDesc.getText().toString().trim();
                                String imageUrl = uri.toString();
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("deal", etDeal);
                                hashMap.put("price",etPrice);
                                hashMap.put("description", etDesc);
                                hashMap.put("imageUrl",imageUrl);
                                mDatabaseRef.child(mTravelKey).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DetailedActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        cleanContent();
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount() * 100.0);
                        mProgressBar.setProgress((int) progress);
                        String indicate = (int) progress + "%";
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void openFileImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImgUri = data.getData();
            Picasso.get().load(mImgUri).into(mImageViewUpdate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_action) {
            deleteContent();
            startActivity(new Intent(DetailedActivity.this, RecyclerActivity.class));
            Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.update_action) {
            toggleUpdateContent();
            return true;
        }
        else {
        return super.onOptionsItemSelected(item); }
    }

    private void toggleUpdateContent() {
        isVisible = !isVisible;
        if (isVisible) mLayout.setVisibility(View.VISIBLE);
        else mLayout.setVisibility(View.INVISIBLE);
    }

    private void deleteContent() {
        mDeleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //After Successful Delete Go back
                    }
                });
            }
        });

    }
}

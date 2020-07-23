package com.jwhh.travelmantics.temp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jwhh.travelmantics.R;
import com.jwhh.travelmantics.TravelDetail;
import com.squareup.picasso.Picasso;


public class InputActivity extends AppCompatActivity {

    EditText etDeal, etPrice, etDescription;
    TextView mTextView;
    Button mChooseImgBtn;
    ImageView mUpLoadImgView;
    ProgressBar mProgressBar;
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    private static final int PUT_REQUEST = 1;
    Uri mUri;
    public String mKey;
    public Uri mDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        etDeal = findViewById(R.id.etDeal);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        mChooseImgBtn = findViewById(R.id.chooseImgBtn);
        mUpLoadImgView = findViewById(R.id.uploadImgView);
        mProgressBar = findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.progressInt);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("traveldeal");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("traveldeal");

        mUpLoadImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImgFile();
            }
        });

        //Upload Image from file
        mChooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = v.findViewById(R.id.textView2);
                textView.setVisibility(View.INVISIBLE);
                chooseImgFile();
            }
        });
    }

    private void chooseImgFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PUT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUT_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            Picasso.get().load(mUri).into(mUpLoadImgView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_menu) {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(InputActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                saveContent();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void clearContent() {
        etDeal.setText("");
        etPrice.setText("");
        etDescription.setText("");
        mUpLoadImgView.setBackgroundResource(R.drawable.rounded_imgview);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(uri));
    }

    private void saveContent() {
        if (etDeal != null && etPrice != null && etDescription != null && mUri != null) {
            try {
                String uniqueId = mDatabaseRef.push().getKey();
                final StorageReference fileReference = mStorageRef.child(uniqueId + "." + getFileExtension(mUri));
                mUploadTask = fileReference.putFile(mUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mDownloadUrl = uri;
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressBar.setProgress(0);
                                            }
                                        }, 1000);

                                        Toast.makeText(InputActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                                        String deal = etDeal.getText().toString().trim();
                                        String price = String.valueOf(etPrice.getText());
                                        String desc = etDescription.getText().toString().trim();
                                        String imageUrl = mDownloadUrl.toString();
                                        Log.d("ImageUrl = ", imageUrl);
                                        TravelDetail travelDetail = new TravelDetail(deal, price, desc, imageUrl);
                                        String key = mDatabaseRef.push().getKey();
                                        mDatabaseRef.child(key).setValue(travelDetail);
                                        clearContent();
                                    }

                                });
                                //Move to the next Activity
                                Intent intent = new Intent(InputActivity.this, RecyclerActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InputActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount() * 100.0);
                                mProgressBar.setProgress((int) progress);
                                String indicate = (int) progress + "%";
                                mTextView.setText(indicate);
                                mProgressBar.setVisibility(View.VISIBLE);
                                mTextView.setVisibility(View.VISIBLE);
                            }
                        });

            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            }
        } else {
            //Onchange of the EditText set color to blue and padding
            etDeal.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        etDeal.setBackgroundResource(R.drawable.error_rounded_corner);
                        etPrice.setBackgroundResource(R.drawable.error_rounded_corner);
                        etDescription.setBackgroundResource(R.drawable.error_rounded_corner);
                        etDeal.setPadding(10, 10,10,10);
                        etPrice.setPadding(10, 10,10,10);
                        etDescription.setPadding(10, 10,10,10);
                    } else {
                        etDeal.setBackgroundResource(R.drawable.rounded_corner);
                        etPrice.setBackgroundResource(R.drawable.rounded_corner);
                        etDescription.setBackgroundResource(R.drawable.rounded_corner);
                        etDeal.setPadding(10, 10,10,10);
                        etPrice.setPadding(10, 10,10,10);
                        etDescription.setPadding(10, 10,10,10);}
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            Toast.makeText(getApplicationContext(), "Please fill in Empty Fields", Toast.LENGTH_LONG).show();
        }
    }
}

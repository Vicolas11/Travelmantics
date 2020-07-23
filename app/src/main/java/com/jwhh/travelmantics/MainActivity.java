package com.jwhh.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText metDeal, metPrice, metDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("travelmantics-112ec");
        metDeal = (EditText) findViewById(R.id.etDeal);
        metPrice = (EditText) findViewById(R.id.etPrice);
        metDescription = (EditText) findViewById(R.id.etDescription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_menu) {
            //saveDeal();
            //cleanText();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void cleanText() {
        metDeal.setText("");
        metPrice.setText("");
        metDescription.setText("");
        metDeal.requestFocus();
    }

    void saveDeal() {
        String deal = metDeal.getText().toString().trim();
        String price = metPrice.getText().toString().trim();
        String description = metDescription.getText().toString().trim();
        //TravelDetail travelDetail = new TravelDetail(deal, price, description, "");
       // mDatabaseReference.push().setValue(travelDetail);
    }
}

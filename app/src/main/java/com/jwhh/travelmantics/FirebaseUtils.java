package com.jwhh.travelmantics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtils {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseUtils mFirebaseUtils;
    public static ArrayList<TravelDetail> mDeal;
    private FirebaseUtils() {}

    public static void openFBDatabase(String ref) {
        if (mFirebaseUtils == null) {
            mFirebaseUtils = new FirebaseUtils();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDeal = new ArrayList<TravelDetail>();
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
}

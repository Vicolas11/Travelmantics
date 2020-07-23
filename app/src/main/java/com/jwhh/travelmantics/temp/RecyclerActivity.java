package com.jwhh.travelmantics.temp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jwhh.travelmantics.R;
import com.jwhh.travelmantics.TravelDetail;
import com.squareup.picasso.Picasso;

public class RecyclerActivity extends AppCompatActivity {

    DatabaseReference dataRef;
    RecyclerView mRecyclerView;
    FirebaseRecyclerOptions<TravelDetail> options;
    FirebaseRecyclerAdapter<TravelDetail, MyViewHolder> adapter;
    StorageReference mStoreRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        mStoreRef = FirebaseStorage.getInstance().getReference().child("traveldeal");
        mRecyclerView = (RecyclerView) findViewById(R.id.rView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),0));
        loadData();

    }

    private void loadData() {
        dataRef = FirebaseDatabase.getInstance().getReference().child("traveldeal");
        options = new FirebaseRecyclerOptions.Builder<TravelDetail>().setQuery(dataRef, TravelDetail.class).build();
        adapter = new FirebaseRecyclerAdapter<TravelDetail, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull TravelDetail model) {
                holder.mTvDeal.setText(model.getDeal());
                holder.mTvPrice.setText(String.valueOf(model.getPrice()));
                holder.mTvDesc.setText(model.getDescription());
                if (model.getImageUrl().isEmpty())
                    holder.mImageView.setImageResource(R.drawable.ic_directions_car_black_24dp);
                else Picasso.get().load(model.getImageUrl()).into(holder.mImageView);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RecyclerActivity.this, DetailedActivity.class);
                        intent.putExtra("TravelKey", getRef(position).getKey());
                        startActivity(intent);
                        notifyDataSetChanged();
                    }
                });
                //On Long Pressed Show a PopupMenu and An AlertBox for confirmation
                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showPopupMenu(v, getRef(position).getKey());
                        notifyDataSetChanged();
                        return true;
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_content,parent,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    private void showPopupMenu(View view, final String refPosition) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        popup.inflate(R.menu.popup_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.update_action:
                        Intent intent = new Intent(RecyclerActivity.this, DetailedActivity.class);
                        intent.putExtra("TravelKey", refPosition);
                        startActivity(intent);
                    case R.id.delete_action:
                        alertDialog(refPosition);
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void alertDialog(final String refPosition) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(RecyclerActivity.this);
        alertBuild.setTitle("Delete Deal")
                .setMessage("Are you sure, you want to delete?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataRef.child(refPosition).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RecyclerActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                        mStoreRef.child(refPosition).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                    }
                                });
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                AlertDialog dialog = alertBuild.create();
                dialog.show();
        }


}

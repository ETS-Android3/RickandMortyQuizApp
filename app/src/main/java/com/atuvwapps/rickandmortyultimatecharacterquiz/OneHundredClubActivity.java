package com.atuvwapps.rickandmortyultimatecharacterquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.atuvwapps.rickandmortyultimatecharacterquiz.databinding.ActivityOneHundredClubBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class OneHundredClubActivity extends AppCompatActivity {

    private ActivityOneHundredClubBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MyRecyclerViewAdapter adapter;
    private List<Upload> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOneHundredClubBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.namesRv.setLayoutManager(new LinearLayoutManager(this));
        uploads = new ArrayList<>();
        binding.namesRv.setVisibility(View.INVISIBLE);
        binding.progressCircle.setVisibility(View.VISIBLE);
        db.collection("100Club")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot:queryDocumentSnapshots) {
                            uploads.add(snapshot.toObject(Upload.class));
                        }
                        adapter = new MyRecyclerViewAdapter(OneHundredClubActivity.this, uploads);
                        binding.namesRv.setAdapter(adapter);
                        binding.progressCircle.setVisibility(View.INVISIBLE);
                        binding.namesRv.setVisibility(View.VISIBLE);
                    }
                });
        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenu();
            }
        });
    }

    public void mainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        mainMenu();
    }
}
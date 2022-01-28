package com.atuvwapps.rickandmortyultimatecharacterquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.atuvwapps.rickandmortyultimatecharacterquiz.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
        binding.instructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstructions();
            }
        });
        binding.rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateMe();
            }
        });
    }

    public void startQuiz(){
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    public void openInstructions(){
        Intent intent = new Intent(this, OneHundredClubActivity.class);
        startActivity(intent);
    }

    public void rateMe(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}

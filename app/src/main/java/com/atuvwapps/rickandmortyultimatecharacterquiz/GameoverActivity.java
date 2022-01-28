package com.atuvwapps.rickandmortyultimatecharacterquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.atuvwapps.rickandmortyultimatecharacterquiz.databinding.ActivityGameoverBinding;

public class GameoverActivity extends AppCompatActivity {

    private ActivityGameoverBinding binding;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameoverBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        int score = intent.getIntExtra("CURRENTQ", 0);

        binding.finalScoreTxt.setText(Integer.toString(score));
        binding.mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainMenu();
            }
        });
        binding.playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgain();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            startMainMenu();
            return;
        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void startMainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void playAgain(){
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }
}
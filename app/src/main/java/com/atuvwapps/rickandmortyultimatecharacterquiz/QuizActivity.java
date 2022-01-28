package com.atuvwapps.rickandmortyultimatecharacterquiz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.atuvwapps.rickandmortyultimatecharacterquiz.databinding.ActivityQuizBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Question> newQs;
    private ArrayList<Question> questions;
    private List<Integer> answerShuffler;
    private MediaPlayer correctAudioYes;
    private MediaPlayer wrongAudio;
    private int currentQ = 0;
    private int endingScore = 100;
    private int lives = 3;
    private long backPressedTime;
    private Toast backToast;
    private InterstitialAd myInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        newQs = new ArrayList<Question>();
        questions = new ArrayList<Question>();
        answerShuffler = Arrays.asList(0,1,2,3);
        correctAudioYes = MediaPlayer.create(this, R.raw.snap_yes);
        wrongAudio = MediaPlayer.create(this, R.raw.aw_jeez_rick);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adBanner.loadAd(adRequest);

        InterstitialAd.load(this,"[adUnitId]", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        myInterstitialAd = interstitialAd;
                        Log.i("ad", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("ad", loadAdError.getMessage());
                        myInterstitialAd = null;
                    }
                });

        if(isOnline()) {
            binding.errorMsg.setVisibility(View.INVISIBLE);
            fetchStage("stage1");
        }else{
            binding.errorMsg.setVisibility(View.VISIBLE);
            UIVisibility(false);
        }
        binding.answer1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(v,0);
            }
        });

        binding.answer2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(v,1);
            }
        });

        binding.answer3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(v,2);
            }
        });

        binding.answer4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(v,3);
            }
        });
    }

    //Temporarily disable the buttons after an answer is selected
    public void enableButtons(boolean enable){
        binding.answer1Btn.setEnabled(enable);
        binding.answer2Btn.setEnabled(enable);
        binding.answer3Btn.setEnabled(enable);
        binding.answer4Btn.setEnabled(enable);
    }

    public void checkAnswer(final View v, int optionNumber){
        if(questions.get(currentQ).options.get(answerShuffler.get(optionNumber)).equals(questions.get(currentQ).answer))
        {
            playSound(true);
            v.setBackgroundResource(R.drawable.custom_button_correct);
            enableButtons(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.setBackgroundResource(R.drawable.custom_button_main);
                    enableButtons(true);
                    currentQ++;
                    if(currentQ == 10){
                        displayInterstitial();
                        loadQuestion();
                    }
                    else if(currentQ == 20){
                        displayInterstitial();
                        fetchStage("stage2");
                    }
                    else if(currentQ == 30){
                        displayInterstitial();
                        loadQuestion();
                    }
                    else if(currentQ == 40){
                        displayInterstitial();
                        fetchStage("stage3");
                    }
                    else if(currentQ == 50){
                        displayInterstitial();
                        loadQuestion();
                    }
                    else if(currentQ == 60){
                        displayInterstitial();
                        fetchStage("stage4");
                    }
                    else if(currentQ == 70){
                        displayInterstitial();
                        loadQuestion();
                    }
                    else if(currentQ == 80){
                        displayInterstitial();
                        fetchStage("stage5");
                    }
                    else if(currentQ == 90){
                        displayInterstitial();
                        loadQuestion();
                    }
                    else if(currentQ == endingScore){
                        newMember();
                    }
                    else {
                        loadQuestion();
                    }
                }
            }, 500);
        }
        else{
            playSound(false);
            v.setBackgroundResource(R.drawable.custom_button_wrong);
            enableButtons(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.setBackgroundResource(R.drawable.custom_button_main);
                    enableButtons(true);
                    lives--;
                    if(lives == 2) {
                        binding.livesThreeTxt.setVisibility(View.INVISIBLE);
                        binding.livesTwoTxt.setBackgroundResource(R.drawable.custom_lives_two);
                        binding.livesOneTxt.setBackgroundResource(R.drawable.custom_lives_two);
                    }else if(lives == 1) {
                        binding.livesTwoTxt.setVisibility(View.INVISIBLE);
                        binding.livesOneTxt.setBackgroundResource(R.drawable.custom_life_one);
                    }else{
                        gameOver();
                    }
                }
            }, 500);
        }
    }

    public void displayInterstitial(){
        if (myInterstitialAd != null) {
            myInterstitialAd.show(QuizActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    public void onBackPressed(){
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void fetchStage(String stage) {
        UIVisibility(false);
        db.collection(stage)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot:queryDocumentSnapshots) {
                            newQs.add(snapshot.toObject(Question.class));
                        }
                        Collections.shuffle(newQs);
                        for(int i = 0; i < 20; i++){
                            questions.add(newQs.get(i));
                        }
                        newQs.clear();
                        UIVisibility(true);
                        loadQuestion();
                    }
                });
    }

    public void UIVisibility(boolean visible){
        if(visible){
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.questionImgBackground.setVisibility(View.VISIBLE);
            binding.scoreTxt.setVisibility(View.VISIBLE);
            binding.questionImg.setVisibility(View.VISIBLE);
            binding.questionTxt.setVisibility(View.VISIBLE);
            binding.answer1Btn.setVisibility(View.VISIBLE);
            binding.answer2Btn.setVisibility(View.VISIBLE);
            binding.answer3Btn.setVisibility(View.VISIBLE);
            binding.answer4Btn.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.questionImgBackground.setVisibility(View.INVISIBLE);
            binding.scoreTxt.setVisibility(View.INVISIBLE);
            binding.questionImg.setVisibility(View.INVISIBLE);
            binding.questionTxt.setVisibility(View.INVISIBLE);
            binding.answer1Btn.setVisibility(View.INVISIBLE);
            binding.answer2Btn.setVisibility(View.INVISIBLE);
            binding.answer3Btn.setVisibility(View.INVISIBLE);
            binding.answer4Btn.setVisibility(View.INVISIBLE);
        }
    }

    public void loadQuestion(){
        Collections.shuffle(answerShuffler);
        Picasso.get().load("https://rickandmortyapi.com/api/character/avatar/" + questions.get(currentQ).character_id + ".jpeg").into(binding.questionImg);
        binding.questionTxt.setText(questions.get(currentQ).title);
        binding.answer1Btn.setText(questions.get(currentQ).options.get(answerShuffler.get(0)));
        binding.answer2Btn.setText(questions.get(currentQ).options.get(answerShuffler.get(1)));
        binding.answer3Btn.setText(questions.get(currentQ).options.get(answerShuffler.get(2)));
        binding.answer4Btn.setText(questions.get(currentQ).options.get(answerShuffler.get(3)));
        binding.scoreTxt.setText(Integer.toString(currentQ));
    }

    public void gameOver(){
        Intent intent = new Intent(this, GameoverActivity.class);
        intent.putExtra("CURRENTQ", currentQ);
        startActivity(intent);
    }

    public void newMember(){
        Intent intent = new Intent(this, NewMemberActivity.class);
        startActivity(intent);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void playSound(boolean correct){
        if(correct){
            correctAudioYes.start();
        }else {
            wrongAudio.start();
        }
    }
}

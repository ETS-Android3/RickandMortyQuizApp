package com.atuvwapps.rickandmortyultimatecharacterquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.atuvwapps.rickandmortyultimatecharacterquiz.databinding.ActivityNewMemberBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class NewMemberActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ActivityNewMemberBinding binding;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private boolean filechosen = false;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewMemberBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        db = FirebaseFirestore.getInstance();

        binding.getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.nameEdittxt.getText().length() > 25){
                    binding.errorMessageTxt.setText(R.string.new_member_name_length_error);
                } else if(!filechosen){
                    binding.errorMessageTxt.setText(R.string.new_member_pic_empty_error);
                }else{
                    uploadFile();
                    binding.getImage.setEnabled(false);
                    binding.upload.setEnabled(false);
                    binding.errorMessageTxt.setText("");
                }
            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(binding.preview);
            filechosen = true;
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile(){
        if(imageUri != null){
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()+"."
            +getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.progressBar.setProgress(0);
                                }
                            }, 1000);

                            Toast.makeText(NewMemberActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", binding.nameEdittxt.getText().toString());
                            user.put("imageUrl", downloadUrl.toString());
                            db.collection("100Club").add(user);

                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(NewMemberActivity.this, OneHundredClubActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewMemberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            binding.progressBar.setProgress((int) progress);
                        }
                    });
        }else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            mainMenu();
        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void mainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
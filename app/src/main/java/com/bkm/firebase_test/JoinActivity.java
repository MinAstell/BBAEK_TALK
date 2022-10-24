package com.bkm.firebase_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ImageView myProfile;
    private Uri imageUri;

    private boolean chk = false;

    TextInputEditText etId, etPw, etName, etConditionMessage;

    AppCompatButton btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        myProfile = (ImageView) findViewById(R.id.join_myProfile);

        btn_add = (AppCompatButton) findViewById(R.id.btn_add);

        etId = (TextInputEditText) findViewById(R.id.etId);
        etPw = (TextInputEditText) findViewById(R.id.etPw);
        etName = (TextInputEditText) findViewById(R.id.etName);
        etConditionMessage = (TextInputEditText) findViewById(R.id.etConditionMessage);

        Glide.with(getApplication()).load(R.drawable.profile_simple).apply(new RequestOptions().circleCrop()).into(myProfile);

        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String user_id = etId.getText().toString().trim();
                String user_pw = etPw.getText().toString().trim();
                String user_name = etName.getText().toString();
                String user_conditionMessage = etConditionMessage.getText().toString();

                if(user_id.equals("")) {

                    showAlert("아이디를 입력하세요.", 0);
                    return;
                }

                if(user_pw.equals("")) {

                    showAlert("비밀번호를 입력하세요.", 0);
                    return;
                }

                if(user_name.equals("")) {

                    showAlert("닉네임을 입력하세요.", 0);
                    return;
                }


                mAuth.createUserWithEmailAndPassword(user_id, user_pw)
                        .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    if(imageUri != null) {

                                        FirebaseStorage.getInstance().getReference().child("userProfile").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                String imageUrl = "https://firebasestorage.googleapis.com/v0/b/fir-test-d8431.appspot.com/o/userProfile%2F"+ uid + "?alt=media";

                                                if(user_conditionMessage.equals("")) {

                                                    Map<String, String> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                                    postValues.put("userId", user_id);
                                                    postValues.put("userPassword", user_pw);
                                                    postValues.put("userName", user_name);
                                                    postValues.put("profileImageUrl", imageUrl);
                                                    postValues.put("conditionMessage", "false");

                                                    mDatabase.child(uid).setValue(postValues);
                                                }
                                                else {

                                                    Map<String, String> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                                    postValues.put("userId", user_id);
                                                    postValues.put("userPassword", user_pw);
                                                    postValues.put("userName", user_name);
                                                    postValues.put("profileImageUrl", imageUrl);
                                                    postValues.put("conditionMessage", user_conditionMessage);

                                                    mDatabase.child(uid).setValue(postValues);
                                                }

                                                showAlert("회원가입에 성공하였습니다.", 0);

                                                Intent ex1 = new Intent(JoinActivity.this, LoginActivity.class);
                                                startActivity(ex1);
                                                finish();
                                            }
                                        });
                                    }
                                    else {

                                        if(user_conditionMessage.equals("")) {

                                            Map<String, String> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                            postValues.put("userId", user_id);
                                            postValues.put("userPassword", user_pw);
                                            postValues.put("userName", user_name);
                                            postValues.put("profileImageUrl", "false");
                                            postValues.put("conditionMessage", "false");

                                            mDatabase.child(uid).setValue(postValues);
                                        }
                                        else {

                                            Map<String, String> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                            postValues.put("userId", user_id);
                                            postValues.put("userPassword", user_pw);
                                            postValues.put("userName", user_name);
                                            postValues.put("profileImageUrl", "false");
                                            postValues.put("conditionMessage", user_conditionMessage);

                                            mDatabase.child(uid).setValue(postValues);
                                        }

                                        showAlert("기본 프로필 이미지가 적용된 상태로 회원가입에 성공하였습니다.", 1);
                                    }
                                }
                                else {
                                    showAlert("회원가입에 실패하였습니다.", 0);

                                    etId.setText("");
                                    etPw.setText("");
                                }
                            }
                        });
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);  // 사진 가져오기(앨범)
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {

//            myProfile.setImageURI(data.getData());  // 프로필 뷰를 바꿈

            Glide.with(getApplication()).load(data.getData()).apply(new RequestOptions().circleCrop()).into(myProfile);

            imageUri = data.getData();  // 이미지 경로원본
        }
    }

    public void showAlert(String msg, int chk) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("").setMessage(msg);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                if(chk == 1) {

                    Intent ex1 = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(ex1);
                    finish();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
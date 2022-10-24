package com.bkm.firebase_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private boolean saveLoginData;
    private SharedPreferences appData;

    private static String myName_add;

    private String myName;

    boolean checked = false;

    private ImageView iv_loginLogo;

    private static boolean checked_logout = false;

    public boolean isChecked_logout() {
        return checked_logout;
    }

    public void setChecked_logout(boolean checked_logout) {
        this.checked_logout = checked_logout;
    }

    public String getMyName_add() {
        return myName_add;
    }

    public void setMyName_add(String myName_add) {
        this.myName_add = myName_add;
    }

    String myId, myPw;

    String uid;
    String myUid;

    public String myToken = "";

    private DatabaseReference mDatabase;

    TextInputEditText etId_Login, etPw_Login;
    CheckBox cbSave;

    AppCompatButton btn_login, btn_login_join;

    ArrayList<DataList> listFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        appData = getSharedPreferences("appData", MODE_PRIVATE);

        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_login_join = (AppCompatButton) findViewById(R.id.btn_login_join);

        iv_loginLogo = (ImageView) findViewById(R.id.iv_loginLogo);

        etId_Login = (TextInputEditText) findViewById(R.id.etId_Login);
        etPw_Login = (TextInputEditText) findViewById(R.id.etPw_Login);

        cbSave = (CheckBox) findViewById(R.id.cbSave);

        Glide.with(getApplication()).load(R.drawable.login_logo3).into(iv_loginLogo);

        load();

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String user_id = etId_Login.getText().toString().trim();
                String user_pw = etPw_Login.getText().toString().trim();

                if(user_id.equals("")) {

                    showAlert("아이디를 입력하세요.");
                    return;
                }

                if(user_pw.equals("")) {

                    showAlert("비밀번호를 입력하세요.");
                    return;
                }

                mAuth.signInWithEmailAndPassword(user_id, user_pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            SharedPreferences.Editor editor = appData.edit();

                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task2 -> {
                                if (!task2.isSuccessful()) {
                                    Log.w("FirebaseSettingEx", "getInstanceId failed", task2.getException());
                                    return;
                                }


                                myToken = task2.getResult().getToken();
                                Log.d("myToken", myToken);

                                Map<String, Object> map = new HashMap<>();
                                map.put("userToken", myToken);

                                mDatabase.child(uid).updateChildren(map);

                                mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.getChildrenCount() > 0) {

                                            listFriends.clear();

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                DataList dataList = snapshot.getValue(DataList.class);

                                                listFriends.add(dataList);
                                            }

                                            for (int i = 0; i < listFriends.size(); i++) {

                                                mDatabase.child(listFriends.get(i).getUserUid()).child("friends").child(myName).updateChildren(map);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });


                            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                                // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.getChildrenCount() > 0) {

                                        String myName = dataSnapshot.child("userName").getValue().toString();

                                        editor.putString("myName", myName);
                                        editor.putString("myUid", uid);
                                        editor.apply();

                                        myName_add = appData.getString("myName", "");
                                        myUid = appData.getString("myUid", "");


//                                        Intent ex1 = new Intent(LoginActivity.this, Chat_home_Main.class);

                                        Intent ex1 = new Intent(LoginActivity.this, HomeController.class);
                                        ex1.putExtra("myName", myName_add);
                                        ex1.putExtra("myUid", myUid);
                                        ex1.putExtra("myToken", myToken);
                                        startActivity(ex1);
                                        finish();

                                    }
                                    else {

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            showAlert("로그인에 실패했습니다.");

                            etId_Login.setText("");
                            etPw_Login.setText("");
                        }
                    }
                });
            }
        });

        btn_login_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), JoinActivity.class);
                startActivity(intent);
            }
        });

        cbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbSave.isChecked() == true) {

                    saveId();

                    if(checked) {

                        showAlert("사용자 정보가 저장되었습니다.");
                    }

                    if(!checked) {
                        cbSave.setChecked(false);
                    }
                }
                else {

                    canceled();
                    etId_Login.setText("");
                    etPw_Login.setText("");

                    showAlert("사용자 정보를 지웁니다.");
                }
            }
        });
    }

    // 설정값을 저장하는 함수
    private void saveId() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        String myId = etId_Login.getText().toString().trim();

        if(!myId.equals("")) {

            // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
            // 저장시킬 이름이 이미 존재하면 덮어씌움
            editor.putBoolean("SAVE_LOGIN_DATA", cbSave.isChecked());
            editor.putString("ID", etId_Login.getText().toString().trim());  // trim : 공백제거
            editor.putString("PW", etPw_Login.getText().toString().trim());

            // apply, commit 을 안하면 변경된 내용이 저장되지 않음
            editor.apply();
            checked = true;
        }
        else {
            showAlert("아이디를 입력 후 시도하세요.");
            checked = false;
            return;
        }
    }

    // 해제
    private void canceled() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        editor.clear();
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private void load() {

        SharedPreferences.Editor editor = appData.edit();

        if(checked_logout) {
            editor.putString("myName", "");
            editor.apply();
            checked_logout = false;
        }

        myName = appData.getString("myName", "");
        myUid = appData.getString("myUid", "");

        if(!myName.equals("")) {

//            Intent intent = new Intent(getApplication(), Chat_home_Main.class);

            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task2 -> {
                if (!task2.isSuccessful()) {
                    Log.w("FirebaseSettingEx", "getInstanceId failed", task2.getException());
                    return;
                }


                myToken = task2.getResult().getToken();
                Log.d("myToken", myToken);

                Map<String, Object> map = new HashMap<>();
                map.put("userToken", myToken);

                mDatabase.child(myUid).updateChildren(map);

                mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() > 0) {

                            listFriends.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                DataList dataList = snapshot.getValue(DataList.class);

                                listFriends.add(dataList);
                            }

                            for (int i = 0; i < listFriends.size(); i++) {

                                mDatabase.child(listFriends.get(i).getUserUid()).child("friends").child(myName).updateChildren(map);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

            Intent intent = new Intent(getApplication(), HomeController.class);
            intent.putExtra("myName", myName);
            intent.putExtra("myUid", myUid);
            intent.putExtra("myToken", myToken);
            startActivity(intent);
            finish();
        }

        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        myId = appData.getString("ID", "");
        myPw = appData.getString("PW", "");

        //체크박스가 체크되었던 값으로 기억시 체크됨.
        if(saveLoginData){cbSave.setChecked(true);}

        //처음로드시 값이 공백이면 무시 값이 있다면 아이디 가져와서 셋팅

        if(!myId.equals("")) {
            etId_Login.setText(myId);
            etPw_Login.setText(myPw);
        }
    }

    public void showAlert(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("").setMessage(msg);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
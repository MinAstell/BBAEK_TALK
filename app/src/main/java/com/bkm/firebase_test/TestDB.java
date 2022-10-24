package com.bkm.firebase_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TestDB extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    Button btnInsert, btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSelect = (Button) findViewById(R.id.btnSelect);



        btnInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String user_id = "aaa";
                String user_pw = "qwer";
                String user_name = "min";

                Map<String, String > postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                postValues.put("userPassword", user_pw);
                postValues.put("userName", user_name);

                mDatabase.child(user_id).setValue(postValues);
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String user_id = "aaa123";
                String user_pw = "qwerr";
                String user_name = "min1";

                mDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {

                    // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() > 0) {

                            String userName = dataSnapshot.child("userName").getValue().toString();
                            String userPassword = dataSnapshot.child("userPassword").getValue().toString();

                            if(userName.equals(user_name)) {
                                if(userPassword.equals(user_pw)) {
                                    Toast.makeText(getApplication(), "성공적으로 데이터 조회 완료!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplication(), "비번이 틀림", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplication(), "데이터가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplication(), "검색할 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
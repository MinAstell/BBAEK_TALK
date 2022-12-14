package com.bkm.firebase_test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TestChatDB extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    Button btnSend;

    EditText etMent, etNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        btnSend = (Button) findViewById(R.id.btnSend);
        etMent = (EditText) findViewById(R.id.etMent);
        etNick = (EditText) findViewById(R.id.etNick);

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String userName = etNick.getText().toString().trim();
                String userComments = etMent.getText().toString().trim();

//                ChatTestRoom_Data chatTestRoom_data = new ChatTestRoom_Data();
//                chatTestRoom_data.setUserName(userName);
//                chatTestRoom_data.setUserContents(userComments);

                ChatTestRoom_Data2.Comment comment = new ChatTestRoom_Data2.Comment();
                comment.userName = userName;
                comment.userContents = userComments;
                comment.readUsers.put(userName, true);

                mDatabase.child("Comment").push().setValue(comment);
            }
        });


//        btnSelect.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                String user_id = "aaa123";
//                String user_pw = "qwerr";
//                String user_name = "min1";
//
//                mDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    // DB 2. ????????????????????? ?????????????????? ???????????? ???????????? ????????? onDataChange??? ???????????????.
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        if(dataSnapshot.getChildrenCount() > 0) {
//
//                            String userName = dataSnapshot.child("userName").getValue().toString();
//                            String userPassword = dataSnapshot.child("userPassword").getValue().toString();
//
//                            if(userName.equals(user_name)) {
//                                if(userPassword.equals(user_pw)) {
//                                    Toast.makeText(getApplication(), "??????????????? ????????? ?????? ??????!", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    Toast.makeText(getApplication(), "????????? ??????", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(getApplication(), "???????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getApplication(), "????????? ???????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
    }
}
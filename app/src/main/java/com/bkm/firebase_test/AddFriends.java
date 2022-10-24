package com.bkm.firebase_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFriends extends AppCompatActivity {

    private FirebaseAuth mAuth;  // 파이어베이스 인증처리
    private DatabaseReference mDatabase;  // 실시간 데이터베이스
    private DataList dataList;

    TextInputEditText etID_add;

    String user_id;
    String myName;
    String myUid;
    String myId;

    String friendName;

    String friendUid;

    boolean pass = true;
    boolean checked = false;

    ArrayList<String> friendsList = new ArrayList<>();
    ArrayList<DataList> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        etID_add = (TextInputEditText) findViewById(R.id.etID_add);

        findViewById(R.id.btn_add).setOnClickListener(mClick);
        findViewById(R.id.btn_back).setOnClickListener(mClick);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        myName = bundle.getString("myName");
        myUid = bundle.getString("myUid");

        getMyId();
    }

    View.OnClickListener mClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.btn_add:

                    user_id = etID_add.getText().toString().trim();

                    if(user_id.equals("")) {

                        showAlert("친구의 아이디를 입력하세요."); return;
                    }

                    selUser();
                    break;

                case R.id.btn_back:

                    finish();
            }
        }
    };

    public void getMyId() {

        mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {

            // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    myId = dataSnapshot.child("userId").getValue().toString();
                }
                else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkedAble() {

        mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {

            // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    friendsList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        friendsList.add(snapshot.getKey());
                    }

                    if (myId.equals(user_id)) {
                        showAlert("자신은 친구로 추가할 수 없습니다.");
                        return;
                    }

                    for (int i = 0; i < friendsList.size(); i++) {

                        if (friendName.equals(friendsList.get(i))) {

                            showAlert("이미 등록된 친구입니다.");
                            return;
                        }
                    }


                    mDatabase.orderByChild("userId").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() > 0) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    friendUid = snapshot.getKey();
                                }


                                mDatabase.child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getChildrenCount() > 0) {

                                            String friendToken = dataSnapshot.child("userToken").getValue().toString();
                                            String friendProfile = dataSnapshot.child("profileImageUrl").getValue().toString();
                                            String friendConditionMessage = dataSnapshot.child("conditionMessage").getValue().toString();

                                            Map<String, Object> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                            postValues.put("userToken", friendToken);
                                            postValues.put("profileImageUrl", friendProfile);
                                            postValues.put("userUid", friendUid);
                                            postValues.put("conditionMessage", friendConditionMessage);

                                            mDatabase.child(myUid).child("friends").child(friendName).updateChildren(postValues);


                                            mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.getChildrenCount() > 0) {

                                                        String myToken = dataSnapshot.child("userToken").getValue().toString();
                                                        String myProfile = dataSnapshot.child("profileImageUrl").getValue().toString();
                                                        String myConditionMessage = dataSnapshot.child("conditionMessage").getValue().toString();

                                                        Map<String, Object> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                                        postValues.put("userToken", myToken);
                                                        postValues.put("profileImageUrl", myProfile);
                                                        postValues.put("userUid", myUid);
                                                        postValues.put("conditionMessage", myConditionMessage);

                                                        mDatabase.child(friendUid).child("friends").child(myName).updateChildren(postValues);

                                                        showAlert("친구가 추가되었습니다.");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {

                    if (myId.equals(user_id)) {

                        showAlert("자신은 친구로 추가할 수 없습니다.");
                        return;
                    }


                    mDatabase.orderByChild("userId").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() > 0) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    friendUid = snapshot.getKey();
                                }


                                mDatabase.child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getChildrenCount() > 0) {

                                            String friendToken = dataSnapshot.child("userToken").getValue().toString();
                                            String friendProfile = dataSnapshot.child("profileImageUrl").getValue().toString();
                                            String friendConditionMessage = dataSnapshot.child("conditionMessage").getValue().toString();

                                            Map<String, Object> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                            postValues.put("userToken", friendToken);
                                            postValues.put("profileImageUrl", friendProfile);
                                            postValues.put("userUid", friendUid);
                                            postValues.put("conditionMessage", friendConditionMessage);

                                            mDatabase.child(myUid).child("friends").child(friendName).updateChildren(postValues);


                                            mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.getChildrenCount() > 0) {

                                                        String myToken = dataSnapshot.child("userToken").getValue().toString();
                                                        String myProfile = dataSnapshot.child("profileImageUrl").getValue().toString();
                                                        String myConditionMessage = dataSnapshot.child("conditionMessage").getValue().toString();

                                                        Map<String, Object> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
                                                        postValues.put("userToken", myToken);
                                                        postValues.put("profileImageUrl", myProfile);
                                                        postValues.put("userUid", myUid);
                                                        postValues.put("conditionMessage", myConditionMessage);

                                                        mDatabase.child(friendUid).child("friends").child(myName).updateChildren(postValues);

                                                        showAlert("친구가 추가되었습니다.");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void selUser() {

        Query myTopPostsQuery = mDatabase.orderByChild("userId").equalTo(user_id);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getChildrenCount() > 0) {

                    userList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        DataList dataList = dataSnapshot.getValue(DataList.class);
                        userList.add(dataList);
                    }

                    friendName = userList.get(0).getUserName();

                    checkedAble();
                }
                else {

                    showAlert("검색한 대상을 찾을 수 없습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
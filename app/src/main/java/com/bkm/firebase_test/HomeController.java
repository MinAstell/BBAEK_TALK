package com.bkm.firebase_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeController extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private LinearLayout friendsList_btn;
    private LinearLayout chatList_btn;
    private LinearLayout settings_btn;
    private LinearLayout iv_home;

    private LinearLayout settings_profile;
    private LinearLayout settings_conditionMessage;
    private LinearLayout settings_password;
    private LinearLayout settings_logout;
    private LinearLayout modify_profile;
    private LinearLayout myInfo;

    private EditText et_modifyConditionMessage;

    private CountDownTimer countDownTimer;

    private RecyclerView rv_frame;
    private LinearLayout settings_frame;

    private TextView topLeftTitle, tv_userName, tv_myName, tv_myConditionMessage;
    private ImageView topRightTitle, iv_uploadProfile, iv_myProfileImage, myInfo_btn;

    private Button modify_profile_saveBtn;

    private DatabaseReference mDatabase;

    private ArrayList<ChatList_Data> arrayListChat;
    private ArrayList<FriendsList_Data> arrayListFriends;
    private ChatList_Adapter chatList_adapter;
    private FriendsList_Adapter friendsList_adapter;
    private LinearLayoutManager linearLayoutManager;

    private String myName;
    private String myUid;
    private String myToken;
    private String friendProfileImageUrl = "";
    private String friendName = "";
    private String friendConditionMessage = "";
    private int subI = 0;

    private Uri imageUri;

    private boolean chkChange = false;

    ArrayList<DataList> listChat = new ArrayList<>();
    ArrayList<DataList> listFriend = new ArrayList<>();
    ArrayList<String> listFriendsName = new ArrayList<>();
    ArrayList<String> listChatName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_controller);

        friendsList_btn = (LinearLayout) findViewById(R.id.friendsList_btn);
        chatList_btn = (LinearLayout) findViewById(R.id.chatList_btn);
        settings_btn = (LinearLayout) findViewById(R.id.settings_btn);

        settings_profile = (LinearLayout) findViewById(R.id.settings_profile);
        settings_conditionMessage = (LinearLayout) findViewById(R.id.settings_conditionMessage);
        settings_password = (LinearLayout) findViewById(R.id.settings_password);
        settings_logout = (LinearLayout) findViewById(R.id.settings_logout);

        iv_home = (LinearLayout) findViewById(R.id.iv_home);

        rv_frame = (RecyclerView) findViewById(R.id.rv_frame);
        settings_frame = (LinearLayout) findViewById(R.id.settings_frame);
        modify_profile = (LinearLayout) findViewById(R.id.modify_profile);
        myInfo = (LinearLayout) findViewById(R.id.myInfo);

        et_modifyConditionMessage = (EditText) findViewById(R.id.et_ModifyMyConditionMessage);

        topLeftTitle = (TextView) findViewById(R.id.topLeftTitle);
        topRightTitle = (ImageView) findViewById(R.id.topRightTitle);

        modify_profile_saveBtn = (Button) findViewById(R.id.modify_profile_saveBtn);
        iv_uploadProfile = (ImageView) findViewById(R.id.iv_uploadProfile);
        tv_userName = (TextView) findViewById(R.id.tv_userName);

        iv_myProfileImage = (ImageView) findViewById(R.id.iv_myProfileImage);
        tv_myName = (TextView) findViewById(R.id.tv_myName);
        tv_myConditionMessage = (TextView) findViewById(R.id.tv_myConditionMessage);

        myInfo_btn = (ImageView) findViewById(R.id.myInfo_btn);

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        linearLayoutManager = new LinearLayoutManager(this);
        rv_frame.setLayoutManager(linearLayoutManager);
        rv_frame.setHasFixedSize(true);

        arrayListChat = new ArrayList<>();
        arrayListFriends = new ArrayList<>();

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        myName = bundle.getString("myName");
        myUid = bundle.getString("myUid");
        myToken = bundle.getString("myToken");

        setFriendsList();

        topRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent2 = new Intent(getApplication(), AddFriends.class);
                intent2.putExtra("myName", myName);
                intent2.putExtra("myUid", myUid);
                startActivity(intent2);
            }
        });

        friendsList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("눌림", "눌림");

                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);
                iv_home.setVisibility(View.INVISIBLE);
                topLeftTitle.setVisibility(View.VISIBLE);

                topLeftTitle.setText("친구");
                topRightTitle.clearColorFilter();
                topRightTitle.setImageResource(R.drawable.add_friend);
                myInfo_btn.clearColorFilter();
                myInfo_btn.setImageResource(R.drawable.account);

                topRightTitle.setEnabled(true);
                myInfo_btn.setEnabled(true);

                setFriendsList();

                rv_frame.setVisibility(View.VISIBLE);
            }
        });

        chatList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("눌림", "눌림");

                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);
                iv_home.setVisibility(View.INVISIBLE);
                topLeftTitle.setVisibility(View.VISIBLE);

                topLeftTitle.setText("채팅");
                topRightTitle.setColorFilter(getColor(R.color.white));
                myInfo_btn.setColorFilter(getColor(R.color.white));

                topRightTitle.setEnabled(false);
                myInfo_btn.setEnabled(false);

                setChatList();

                rv_frame.setVisibility(View.VISIBLE);
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("눌림", "눌림");

                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);
                iv_home.setVisibility(View.INVISIBLE);
                topLeftTitle.setVisibility(View.VISIBLE);

                topLeftTitle.setText("설정");
                topRightTitle.setColorFilter(getColor(R.color.white));
                myInfo_btn.setColorFilter(getColor(R.color.white));

                topRightTitle.setEnabled(false);
                myInfo_btn.setEnabled(false);

                settings_frame.setVisibility(View.VISIBLE);
            }
        });

        settings_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);

                topRightTitle.setColorFilter(getColor(R.color.white));
                myInfo_btn.setColorFilter(getColor(R.color.white));

                topLeftTitle.setVisibility(View.INVISIBLE);
                iv_home.setVisibility(View.VISIBLE);

                topRightTitle.setEnabled(false);
                myInfo_btn.setEnabled(false);

                mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String myProfileImage = snapshot.child("profileImageUrl").getValue().toString();
                        String myConditionMessage = snapshot.child("conditionMessage").getValue().toString();
                        String myName = snapshot.child("userName").getValue().toString();

                        tv_userName.setText(myName);
                        et_modifyConditionMessage.setText(myConditionMessage);

                        if(!myProfileImage.equals("false")) {

                            Glide.with(getApplication()).load(myProfileImage).apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop()).into(iv_uploadProfile);
                        }

                        if(myProfileImage.equals("false")) {

                            Glide.with(getApplication()).load(R.drawable.profile_simple).apply(new RequestOptions().circleCrop()).into(iv_uploadProfile);
                        }

                        modify_profile.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        settings_conditionMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);

                topRightTitle.setColorFilter(getColor(R.color.white));
                myInfo_btn.setColorFilter(getColor(R.color.white));

                topLeftTitle.setVisibility(View.INVISIBLE);
                iv_home.setVisibility(View.VISIBLE);

                topRightTitle.setEnabled(false);
                myInfo_btn.setEnabled(false);

                mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String myProfileImage = snapshot.child("profileImageUrl").getValue().toString();
                        String myConditionMessage = snapshot.child("conditionMessage").getValue().toString();
                        String myName = snapshot.child("userName").getValue().toString();

                        tv_userName.setText(myName);
                        et_modifyConditionMessage.setText(myConditionMessage);

                        if(!myProfileImage.equals("false")) {

                            Glide.with(getApplication()).load(myProfileImage).apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop()).into(iv_uploadProfile);
                        }

                        if(myProfileImage.equals("false")) {

                            Glide.with(getApplication()).load(R.drawable.profile_simple).apply(new RequestOptions().circleCrop()).into(iv_uploadProfile);
                        }

                        modify_profile.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        myInfo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);

                topRightTitle.setColorFilter(getColor(R.color.white));
                myInfo_btn.setColorFilter(getColor(R.color.white));

                topLeftTitle.setVisibility(View.INVISIBLE);
                iv_home.setVisibility(View.VISIBLE);

                topRightTitle.setEnabled(false);
                myInfo_btn.setEnabled(false);

                mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String myProfileImage = snapshot.child("profileImageUrl").getValue().toString();
                        String myConditionMessage = snapshot.child("conditionMessage").getValue().toString();
                        String myName = snapshot.child("userName").getValue().toString();

                        tv_myName.setText("이름 : " + myName);
                        tv_myConditionMessage.setText("상태메시지 : " + myConditionMessage);

                        if(!myProfileImage.equals("false")) {

                            Glide.with(getApplication()).load(myProfileImage).apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop()).into(iv_myProfileImage);
                        }

                        if(myProfileImage.equals("false")) {

                            Glide.with(getApplication()).load(R.drawable.profile_simple).apply(new RequestOptions().circleCrop()).into(iv_myProfileImage);
                        }

                        myInfo.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iv_home.setVisibility(View.INVISIBLE);
                myInfo.setVisibility(View.INVISIBLE);
                rv_frame.setVisibility(View.INVISIBLE);
                settings_frame.setVisibility(View.INVISIBLE);
                modify_profile.setVisibility(View.INVISIBLE);

                topLeftTitle.setVisibility(View.VISIBLE);

                topLeftTitle.setText("친구");
                topRightTitle.clearColorFilter();
                topRightTitle.setImageResource(R.drawable.add_friend);
                myInfo_btn.clearColorFilter();
                myInfo_btn.setImageResource(R.drawable.account);

                topRightTitle.setEnabled(true);
                myInfo_btn.setEnabled(true);

                rv_frame.setVisibility(View.VISIBLE);
            }
        });

        iv_uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);  // 사진 가져오기(앨범)
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        modify_profile_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String etModifyConditionMessage = et_modifyConditionMessage.getText().toString();

                if(imageUri != null) {

                    FirebaseStorage.getInstance().getReference().child("userProfile").child(myUid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/fir-test-d8431.appspot.com/o/userProfile%2F"+ myUid + "?alt=media";

                            Map<String, Object> map = new HashMap<>();
                            map.put("profileImageUrl", imageUrl);

                            mDatabase.child(myUid).updateChildren(map);

                            listFriend.clear();

                            mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.getChildrenCount() > 0) {

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            DataList dataList = snapshot.getValue(DataList.class);

                                            listFriend.add(dataList);
                                        }

                                        for (int i = 0; i < listFriend.size(); i++) {

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("profileImageUrl", imageUrl);

                                            mDatabase.child(listFriend.get(i).getUserUid()).child("friends").child(myName).updateChildren(map);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
                else {

                }

                mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String myConditionMessage = snapshot.child("conditionMessage").getValue().toString();
                        String myName = snapshot.child("userName").getValue().toString();

                        if(etModifyConditionMessage.equals(myConditionMessage)) {

                            if(imageUri != null) {

                                showAlert("프로필 변경이 완료되었습니다.");
                                imageUri = null;
                            }
                            else {

                                showAlert("변경사항이 없습니다.");
                            }
                        }
                        else {

                            Map<String, Object> map = new HashMap<>();
                            map.put("conditionMessage", etModifyConditionMessage);

                            mDatabase.child(myUid).updateChildren(map);

                            listFriend.clear();

                            mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.getChildrenCount() > 0) {

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            DataList dataList = snapshot.getValue(DataList.class);

                                            listFriend.add(dataList);
                                        }

                                        for (int i = 0; i < listFriend.size(); i++) {

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("conditionMessage", etModifyConditionMessage);

                                            mDatabase.child(listFriend.get(i).getUserUid()).child("friends").child(myName).updateChildren(map);
                                        }

                                        showAlert("프로필 변경이 완료되었습니다.");
                                        imageUri = null;
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
        });

        settings_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        settings_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginActivity loginActivity = new LoginActivity();
                loginActivity.setChecked_logout(true);

                FirebaseAuth.getInstance().signOut();

                Intent intent1 = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {

//            iv_uploadProfile.setImageURI(data.getData());  // 프로필 뷰를 바꿈

            Glide.with(getApplication()).load(data.getData()).apply(new RequestOptions().circleCrop()).into(iv_uploadProfile);

            imageUri = data.getData();  // 이미지 경로원본
        }
    }

    private void setChatList() {

        chatList_btn.setEnabled(false);

        mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {

            // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    listChat.clear();
                    arrayListChat.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        listChatName.add(snapshot.getKey());

                        DataList dataList = snapshot.getValue(DataList.class);

                        listChat.add(dataList);
                    }

                    for(int i=0; i<listChat.size(); i++) {

                        friendProfileImageUrl = listChat.get(i).getProfileImageUrl();
                        friendName = listChatName.get(i);

                        ChatList_Data chatList_data = new ChatList_Data(friendProfileImageUrl, friendName, "대화를 시작해보세요.");
                        arrayListChat.add(chatList_data);

                        chatList_adapter = new ChatList_Adapter(arrayListChat, HomeController.this, myName, myUid);
                        rv_frame.setAdapter(chatList_adapter);
                    }

                    countDownTimer = new CountDownTimer(500, 100) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            chatList_btn.setEnabled(true);
                        }
                    }.start();

                    Log.d("arrayListFriends 리스트 사이즈,", "" + arrayListFriends.size());

//                    friendsList_adapter.setBinding(arrayListFriends);

                }
                else {

                    countDownTimer = new CountDownTimer(500, 100) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            chatList_btn.setEnabled(true);
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFriendsList() {

        friendsList_btn.setEnabled(false);

        mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {

            // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    listFriend.clear();
                    arrayListFriends.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        listFriendsName.add(snapshot.getKey());
                        Log.d("listFriendsName", listFriendsName.toString());

                        DataList dataList = snapshot.getValue(DataList.class);

                        listFriend.add(dataList);
                    }

                    for(int i=0; i<listFriend.size(); i++) {

                        friendProfileImageUrl = listFriend.get(i).getProfileImageUrl();
                        friendConditionMessage = listFriend.get(i).getConditionMessage();
                        friendName = listFriendsName.get(i);

                        if(friendConditionMessage.equals("false")) {

                            FriendsList_Data friends_List_data = new FriendsList_Data(friendProfileImageUrl, friendName, "친구가 아직 상태메시지를 등록하지 않았습니다.");
                            arrayListFriends.add(friends_List_data);

                            friendsList_adapter = new FriendsList_Adapter(arrayListFriends, HomeController.this, myName, myUid);
                            rv_frame.setAdapter(friendsList_adapter);
                        }
                        else {

                            FriendsList_Data friends_List_data = new FriendsList_Data(friendProfileImageUrl, friendName, friendConditionMessage);
                            arrayListFriends.add(friends_List_data);

                            friendsList_adapter = new FriendsList_Adapter(arrayListFriends, HomeController.this, myName, myUid);
                            rv_frame.setAdapter(friendsList_adapter);
                        }
                    }

                    countDownTimer = new CountDownTimer(500, 100) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            friendsList_btn.setEnabled(true);
                        }
                    }.start();

                    Log.d("arrayListFriends 리스트 사이즈,", "" + arrayListFriends.size());

//                    friendsList_adapter.setBinding(arrayListFriends);
                }
                else {

                    countDownTimer = new CountDownTimer(500, 100) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            friendsList_btn.setEnabled(true);
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
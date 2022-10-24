package com.bkm.firebase_test;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TestChatRoom_Read extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ValueEventListener valueEventListener;

    public String friendName;

    public String myProfileImageUrl;

    private ArrayList<ChatTestRoom_Data2.Comment> arrayList;
    private ChatRoomTest_Adapter chatRoomTest_adapter;
    private RecyclerView recyclerView;
    public  RecyclerView.Adapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    ImageView btnSend;
    TextView tv_friendName;
    EditText etComments;

    public String myToken;
    public String myName = "";
    public String myUid = "";
    public String chatRoomPath = "";
    public boolean chk_path;
    public boolean chk_checkedChatRoomPath = false;
    public boolean chk_checkedChatRoomPath2 = false;
    public boolean chk_getLead = false;

    public String myComment;

    public String friendToken = "";

    public int readChk = 0;

    boolean chk = false;

    ArrayList<Map<String, Object>> list = new ArrayList<>();

    private ArrayList<String> key = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();   // ChatTestRoom_Data 객체를 담을 리스트

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        btnSend = (ImageView) findViewById(R.id.iv_btnSend);
        etComments = (EditText) findViewById(R.id.etComments);
        tv_friendName = (TextView) findViewById(R.id.tv_friendName);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        chatRoomPath = bundle.getString("chatRoomPath");
        myName = bundle.getString("myName");
        friendName = bundle.getString("friendName");
        myUid = bundle.getString("myUid");

        tv_friendName.setText(friendName);

        getMyProfileImage();

        valueEventListener = mDatabase.child("chatRoom").child(chatRoomPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    getLead();

                    arrayList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ChatTestRoom_Data2.Comment comment = snapshot.getValue(ChatTestRoom_Data2.Comment.class);

                        if (!comment.createBy.equals(myName) && !comment.createBy.equals(friendName)) {
                            arrayList.add(comment);
                        }
                    }

                    mAdapter = new ChatRoomTest_Adapter(arrayList, getApplication(), myName);
                    recyclerView.setAdapter(mAdapter);

                    recyclerView.scrollToPosition(arrayList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        mDatabase.child("Comment").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.getChildrenCount() > 0) {
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        String userName = snapshot.child("userName").getValue().toString();
//                        String userContents = snapshot.child("userContents").getValue().toString();
//
//                        Map<String, Object> postValues = new HashMap<>(); // Map<key, value> 형태로 저장합니다.
//
//                        postValues.put("userName", userName);
//                        postValues.put("userContents", userContents);
//
//                        list.add(postValues);
//                    }
//
//                    Toast.makeText(getApplication(), "대화내용 있고 list에 저장됨.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplication(), "list size : " + list.size(), Toast.LENGTH_SHORT).show();
//
//                    for (int i=0; i<list.size(); i++) {
//
//                        Map<String, Object> m = list.get(i);
//
//                        String userName = (String) m.get("userName");
//                        String userContents = (String) m.get("userContents");
//
//                        ChatTestRoom_Data chatTestRoom_data = new ChatTestRoom_Data(userName, userContents);
////                        arrayList.add(chatTestRoom_data);
//
//                        ((ChatRoomTest_Adapter) mAdapter).addChat(chatTestRoom_data);
//                    }
//
////                    ChatRoomTest_Adapter chatRoomTest_adapter = new ChatRoomTest_Adapter(arrayList, myName);
////
////                    chatRoomTest_adapter.notifyItemInserted(arrayList.size()-1);
//
//                } else {
//                    Toast.makeText(getApplication(), "대화내용이 없습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 hh:mm");
                String getTime = dateFormat.format(date);

                myComment = etComments.getText().toString();

                ChatTestRoom_Data2.Comment comment = new ChatTestRoom_Data2.Comment();
                comment.userName = myName;
                comment.userContents = myComment;
                comment.readUsers.put(myName, true);
                comment.userProfileImageUrl = myProfileImageUrl;
                comment.timestamp = getTime;

                mDatabase.child("chatRoom").child(chatRoomPath).push().setValue(comment);

                pushMsg();

                etComments.setText("");
            }
        });
    }


    public void getLead() {

        mDatabase.child("chatRoom").child(chatRoomPath).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ChatTestRoom_Data2.Comment comment = snapshot.getValue(ChatTestRoom_Data2.Comment.class);

                        if (!comment.createBy.equals(myName) && !comment.createBy.equals(friendName)) {

                            Map<String, Object> map = new HashMap<>();
                            map.clear();
                            map.put(myName, true);

                            mDatabase.child("chatRoom").child(chatRoomPath).child(snapshot.getKey()).child("readUsers").updateChildren(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed(){

        mDatabase.child("chatRoom").child(chatRoomPath).removeEventListener(valueEventListener);
        finish();
        return;
    }

    public void pushMsg() {

        mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    DataList dataList = dataSnapshot.child(friendName).getValue(DataList.class);
                    friendToken = dataList.getUserToken();

                    Gson gson = new Gson();

                    NotificationModel notificationModel = new NotificationModel();

                    notificationModel.to = friendToken;
                    notificationModel.data.title = "보낸 사람 : " + myName;
                    notificationModel.data.body = myComment;
                    notificationModel.data.sendingUser = myName;

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

                    Request request = new Request.Builder()
                            .header("Content-Type", "application/json")
                            .addHeader("Authorization", "key=AAAAc50w8r8:APA91bFzio9VB00R0bAvaE0TdMymdCuZWe9C625aizgAVxXRp2SyETZc2fKM0kAMZ3dnOp3yIOXUJXTslJaqhjOASj6mxeKT6uj8KALMymT1Ac0saoT2EvG8u7uRFCQ_pVjJ0Qq_3pLa")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(requestBody)
                            .build();

                    OkHttpClient okHttpClient = new OkHttpClient();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d("post", "error");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.d("post", "success");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMyProfileImage() {

        mDatabase.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    myProfileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
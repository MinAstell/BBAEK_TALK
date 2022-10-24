package com.bkm.firebase_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat_home_Main extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private ArrayList<ChatHome_Data> arrayList;
    private ChatHome_Adapter chatHome_adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public static String friendName;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    int finished;

    private static String user_name;

    String myName;
    private static String myUid;

    public void setChecked(int count) {
        this.count = count;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public Chat_home_Main() {

    }

    private static int count = 0;

    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        arrayList = new ArrayList<>();

        Button btn_sel = (Button) findViewById(R.id.btn_sel);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        myName = bundle.getString("myName");
        myUid = bundle.getString("myUid");

        setList();


        btn_sel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent ex1 = new Intent(Chat_home_Main.this, AddFriends.class);
                ex1.putExtra("myName", myName);
                ex1.putExtra("myUid", myUid);
                startActivity(ex1);
                finish();
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent logout = new Intent(getApplication(), LoginActivity.class);
                LoginActivity loginActivity = new LoginActivity();
                loginActivity.setChecked_logout(true);
                startActivity(logout);
                finish();
            }
        });
    }

    private void setList() {

        mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {

            // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    list.clear();
                    arrayList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String friendName = snapshot.getKey();

                        list.add(friendName);
                    }

                    for(int i=0; i<list.size(); i++) {

                        ChatHome_Data chatHome_data = new ChatHome_Data(R.drawable.ic_launcher_background, list.get(i), "대화내용 없음");
                        arrayList.add(chatHome_data);
                    }

                    chatHome_adapter = new ChatHome_Adapter(arrayList, Chat_home_Main.this, myName);
                    recyclerView.setAdapter(chatHome_adapter);
                }
                else {
                    Toast.makeText(getApplication(), "친구가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
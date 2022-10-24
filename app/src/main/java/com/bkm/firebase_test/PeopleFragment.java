package com.bkm.firebase_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PeopleFragment extends Fragment {

    private DatabaseReference mDatabase;

    private static String myName;
    private static String myUid;

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    ArrayList<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peopleFragment_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PeopleFragmentRecyclerViewAdapter() {

            myUid = "8o7oVmVmwRgJGmc2Fq9iQBypt2l1";

            mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

            mDatabase.child(myUid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {

                // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getChildrenCount() > 0) {

                        list.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            String friendName = snapshot.getKey();

                            list.add(friendName);
                        }

                        Log.d("adapter", "friendName : " + list.toString());

                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);

            return new CustomViewHolder2(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ((CustomViewHolder2)holder).tv_friendName.setText("프레그먼트 됨");
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class CustomViewHolder2 extends RecyclerView.ViewHolder {

            public TextView tv_friendName;
            public TextView tv_friendConditionMessage;
            public ImageView iv_friendProfile;

            public CustomViewHolder2(@NonNull View view) {
                super(view);

//                tv_friendName = (TextView) view.findViewById(R.id.tv_friendName);
                tv_friendConditionMessage = (TextView) view.findViewById(R.id.tv_friendConditionMessage);
//                iv_friendProfile = (ImageView) view.findViewById(R.id.iv_friendProfile);
            }
        }
    }
}

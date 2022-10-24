package com.bkm.firebase_test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsList_Adapter extends RecyclerView.Adapter<FriendsList_Adapter.CustomViewHolder> {

    public DatabaseReference mDatabase;

    private ArrayList<FriendsList_Data> arrayList;
    private Context context;
    public String myName;
    public String myUid;
    public String friendName = "";
    public String chatRoomPath = "";
    public String myUid2;

    public String myName2 = "";
    private boolean chk_overlap = false;

    public FriendsList_Adapter(ArrayList<FriendsList_Data> arrayList, Context context, String myName, String myUid) {
        this.arrayList = arrayList;
        this.context = context;
        this.myName = myName;
        this.myUid = myUid;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.tv_friendName.setText(arrayList.get(position).getTv_friendName());
        holder.tv_friendConditionMessage.setText(arrayList.get(position).getTv_friendConditionMessage());

        if(!arrayList.get(position).getIv_friendProfileImage().equals("false")) {
            Glide.with(holder.itemView.getContext()).load(arrayList.get(position).getIv_friendProfileImage()).apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop()).into(holder.iv_friendProfile);
        }
        if(arrayList.get(position).getIv_friendProfileImage().equals("false")) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.profile_simple).apply(new RequestOptions().circleCrop()).into(holder.iv_friendProfile);
        }

        holder.itemView.setTag(position);

        holder.iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myName2 = myName;
                friendName = holder.tv_friendName.getText().toString();
                myUid2 = myUid;

                chatRoomPathChk();
            }
        });


//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View view) {
//
//                remove(holder.getAdapterPosition());
//
//                return true;
//            }
//        });
    }

    @Override
    public int getItemCount() {

        return (null != arrayList ? arrayList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);  // 새로고침
        }catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_friendName;
        protected TextView tv_friendConditionMessage;
        protected ImageView iv_friendProfile;
        protected  ImageView iv_chat;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_friendName = (TextView) itemView.findViewById(R.id.tv_friendName_friendList);
            this.tv_friendConditionMessage = (TextView) itemView.findViewById(R.id.tv_friendConditionMessage);
            this.iv_friendProfile = (ImageView) itemView.findViewById(R.id.iv_friendProfile_friendList);
            this.iv_chat = (ImageView) itemView.findViewById(R.id.iv_chat);
        }
    }

    public void setBinding(ArrayList<FriendsList_Data> arrayList){

        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public void chatRoomPathChk() {

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        mDatabase.child("chatRoom").child(myName2+"_"+friendName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    chatRoomPath = myName2+"_"+friendName;

                    Intent intent = new Intent(context, TestChatRoom_Read.class);
                    intent.putExtra("chatRoomPath", chatRoomPath);
                    intent.putExtra("myName", myName2);
                    intent.putExtra("friendName", friendName);
                    intent.putExtra("myUid", myUid2);
                    context.startActivity(intent);

                    return;
                }
                else {

                    mDatabase.child("chatRoom").child(friendName+"_"+myName2).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getChildrenCount() > 0) {

                                chatRoomPath = friendName+"_"+myName2;

                                Intent intent = new Intent(context, TestChatRoom_Read.class);
                                intent.putExtra("chatRoomPath", chatRoomPath);
                                intent.putExtra("myName", myName2);
                                intent.putExtra("friendName", friendName);
                                intent.putExtra("myUid", myUid2);
                                context.startActivity(intent);

                                return;
                            }
                            else {

                                Map<String, Object> map = new HashMap<>();
                                map.clear();
                                map.put("createBy", myName2);

                                mDatabase.child("chatRoom").child(myName2 + "_" + friendName).push().setValue(map);
                                chatRoomPath = myName2 + "_" + friendName;

                                Intent intent = new Intent(context, TestChatRoom_Read.class);
                                intent.putExtra("chatRoomPath", chatRoomPath);
                                intent.putExtra("myName", myName2);
                                intent.putExtra("friendName", friendName);
                                intent.putExtra("myUid", myUid2);
                                context.startActivity(intent);
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
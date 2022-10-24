package com.bkm.firebase_test;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatHome_Adapter extends RecyclerView.Adapter<ChatHome_Adapter.CustomViewHolder> {

    public DatabaseReference mDatabase;

    private ArrayList<com.bkm.firebase_test.ChatHome_Data> arrayList;
    private Context context;
    public String myName;
    public String friendName = "";
    public String chatRoomPath = "";

    public String myName2 = "";
    private boolean chk_overlap = false;

    public ChatHome_Adapter(ArrayList<com.bkm.firebase_test.ChatHome_Data> arrayList, Context context, String myName) {
        this.arrayList = arrayList;
        this.context = context;
        this.myName = myName;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

//        holder.iv_profile.setImageResource(arrayList.get(position).getIv_profile());
        holder.tv_nick.setText(arrayList.get(position).getTv_nick());
        holder.tv_talk.setText(arrayList.get(position).getTv_talk());

        holder.itemView.setTag(position);

        holder.btn_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myName2 = myName;
                friendName = holder.tv_nick.getText().toString();

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

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);  // 새로고침
        }catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_profile;
        protected TextView tv_nick;
        protected TextView tv_talk;
        protected Button btn_talk;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = (ImageView) itemView.findViewById(R.id.iv_profile);
            this.tv_nick = (TextView) itemView.findViewById(R.id.tv_nick);
            this.tv_talk = (TextView) itemView.findViewById(R.id.tv_talk);
            this.btn_talk = (Button) itemView.findViewById(R.id.btn_talk);
        }
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
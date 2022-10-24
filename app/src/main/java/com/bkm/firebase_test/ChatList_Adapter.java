package com.bkm.firebase_test;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class ChatList_Adapter extends RecyclerView.Adapter<ChatList_Adapter.CustomViewHolder> {

    public DatabaseReference mDatabase;

    private ArrayList<ChatList_Data> arrayList;
    private Context context;
    public String myName;
    private String myUid;
    public String friendName = "";
    public String chatRoomPath = "";

    public String myName2 = "";
    public String myUid2 = "";
    public String lastMsg = "";
    public String lastMsgTimestamp = "";
    private static String equalFriendName = "";
    private boolean chk_overlap = false;

    public String getEqualFriendName() {
        return equalFriendName;
    }

    public void setEqualFriendName(String equalFriendName) {
        this.equalFriendName = equalFriendName;
    }

    private ArrayList<ChatTestRoom_Data2.Comment> listLastMsg;

    public ChatList_Adapter(ArrayList<ChatList_Data> arrayList, Context context, String myName, String myUid) {
        this.arrayList = arrayList;
        this.context = context;
        this.myName = myName;
        this.myUid = myUid;
    }

    public ChatList_Adapter() {

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist, parent, false);
        CustomViewHolder holder2 = new CustomViewHolder(view);

        return holder2;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder2, int position) {

        friendName = arrayList.get(position).getTv_friendName();
        myName2 = myName;


//        if(friendName.equals(getEqualFriendName())) {
//
//            String getAlamNum = holder2.tv_alam.getText().toString();
//
//            holder2.tv_alam.setBackgroundResource(R.drawable.alambadge);
//
//            if(getAlamNum.equals("")) {
//
//                holder2.tv_alam.setText("1");
//            }
//            else {
//
//                int alamNum = Integer.parseInt(getAlamNum);
//                String finalAlamNum = String.valueOf(alamNum + 1);
//
//                holder2.tv_alam.setText(finalAlamNum);
//            }
//
//            equalFriendName = "";
//        }


        holder2.tv_friendName.setText(arrayList.get(position).getTv_friendName());

        if(!arrayList.get(position).getIv_friendProfile().equals("false")) {
            Glide.with(holder2.itemView.getContext()).load(arrayList.get(position).getIv_friendProfile()).apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop()).into(holder2.iv_friendProfile);
        }
        if(arrayList.get(position).getIv_friendProfile().equals("false")) {
            Glide.with(holder2.itemView.getContext()).load(R.drawable.profile_simple).apply(new RequestOptions().circleCrop()).into(holder2.iv_friendProfile);
        }

        holder2.itemView.setTag(position);

        holder2.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                holder2.tv_alam.setText("");
//                holder2.tv_alam.setBackgroundResource(R.color.white);

                myName2 = myName;
                myUid2 = myUid;
                friendName = holder2.tv_friendName.getText().toString();

                chatRoomPathChk();
            }
        });

//        listLastMsg.clear();

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

        mDatabase.child("chatRoom").child(myName2+"_"+friendName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0) {

                    chatRoomPath = myName2+"_"+friendName;

                    mDatabase.child("chatRoom").child(chatRoomPath).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() > 0) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    ChatTestRoom_Data2.Comment comment = snapshot.getValue(ChatTestRoom_Data2.Comment.class);

                                    Log.d("comment", comment.userContents);

                                    lastMsg = comment.userContents;
                                    lastMsgTimestamp = comment.timestamp;
                                }

                                holder2.tv_lastMessage.setText(lastMsg);
                                holder2.tv_lastMsgTimestamp.setText(lastMsgTimestamp);
                            }
                            else {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    return;
                }
                else {

                    mDatabase.child("chatRoom").child(friendName+"_"+myName2).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getChildrenCount() > 0) {

                                chatRoomPath = friendName+"_"+myName2;

                                mDatabase.child("chatRoom").child(chatRoomPath).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.getChildrenCount() > 0) {

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                ChatTestRoom_Data2.Comment comment = snapshot.getValue(ChatTestRoom_Data2.Comment.class);

                                                Log.d("comment", comment.userContents);

                                                lastMsg = comment.userContents;
                                                lastMsgTimestamp = comment.timestamp;
                                            }

                                            holder2.tv_lastMessage.setText(lastMsg);
                                            holder2.tv_lastMsgTimestamp.setText(lastMsgTimestamp);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                return;
                            }
                            else {

                                holder2.tv_lastMessage.setText("아직 대화를 나누지 않았어요.");
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

        public TextView tv_friendName;
        public TextView tv_lastMessage;
        public ImageView iv_friendProfile;
        public TextView tv_alam;
        public TextView tv_lastMsgTimestamp;

        public CustomViewHolder(@NonNull View itemView2) {
            super(itemView2);

            tv_friendName = (TextView) itemView2.findViewById(R.id.tv_friendName_chatList);
            tv_lastMessage = (TextView) itemView2.findViewById(R.id.tv_lastMessage);
            iv_friendProfile = (ImageView) itemView2.findViewById(R.id.iv_friendProfile_chatList);
            tv_lastMsgTimestamp = (TextView) itemView2.findViewById(R.id.tv_lastMsgTimestamp);
            tv_alam = (TextView) itemView2.findViewById(R.id.tv_alam);
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
                    intent.putExtra("myUid", myUid2);
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
                                intent.putExtra("myUid", myUid2);
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
                                intent.putExtra("myUid", myUid2);
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
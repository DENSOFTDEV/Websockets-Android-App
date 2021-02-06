package com.densoft.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private WebSocket webSocket;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView messageList = findViewById(R.id.messageList);
        EditText messageBox = findViewById(R.id.messagebox);
        TextView send = findViewById(R.id.send);

        instantiateWebSocket();

        adapter = new MessageAdapter();
        messageList.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageBox.getText().toString();
                if (!message.isEmpty()) {
                    webSocket.send(message);
                    messageBox.setText("");

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", message);
                        jsonObject.put("byServer", false);

                        adapter.addItem(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void instantiateWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.8.100:8080").build();
        SocketListener socketListener = new SocketListener(this);
        webSocket = client.newWebSocket(request, socketListener);
    }

    public class SocketListener extends WebSocketListener {

        private MainActivity activity;

        public SocketListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", text);
                        jsonObject.put("byServer", true);

                        adapter.addItem(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "connection established", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class MessageAdapter extends BaseAdapter {

        List<JSONObject> messageList = new ArrayList<>();


        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int position) {
            return messageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.message_list_item, parent, false);
            }

            TextView sentMessage = convertView.findViewById(R.id.sent_message);
            TextView recievedMessage = convertView.findViewById(R.id.received_message);

            JSONObject item = messageList.get(position);
            try {
                if (item.getBoolean("byServer")) {
                    recievedMessage.setVisibility(View.VISIBLE);
                    recievedMessage.setText(item.getString("message"));
                    sentMessage.setVisibility(View.INVISIBLE);
                } else {
                    sentMessage.setVisibility(View.VISIBLE);
                    sentMessage.setText(item.getString("message"));
                    recievedMessage.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

        void addItem(JSONObject item) {
            messageList.add(item);
            notifyDataSetChanged();
        }
    }
}
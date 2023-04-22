package com.example.euc;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messages = new ArrayList<>();
    private final String currentUserId;

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    public MessagesAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_SENDER) {
            view = layoutInflater.inflate(R.layout.sender_message_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            view = layoutInflater.inflate(R.layout.receiver_message_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).bind(message);
        } else {
            ((ReceiverViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isSentBy(currentUserId)) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {

        private final TextView messageTextView;
        private final TextView timestampTextView;

        SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textView_message);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }

        public void bind(Message message) {
            if (messageTextView != null) {
                messageTextView.setText(message.getText());
            }
            if (timestampTextView != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                timestampTextView.setText(sdf.format(new Date(message.getTimestamp())));
            }
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        private final TextView messageTextView;
        private final TextView timestampTextView;

        ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textView_message);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }

        public void bind(Message message) {
            if (messageTextView != null) {
                messageTextView.setText(message.getText());
            }
            if (timestampTextView != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                timestampTextView.setText(sdf.format(new Date(message.getTimestamp())));
            }
        }

    }
}

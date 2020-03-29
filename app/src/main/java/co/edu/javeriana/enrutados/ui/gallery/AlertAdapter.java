package co.edu.javeriana.enrutados.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.model.Alert;

public class AlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_MESSAGE = 1;
    private static int TYPE_EVENT = 2;

    private List<Alert> alerts;
    private Context context;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView replyName, replyText, messageName, messageText, messageTime, replyTime;
        LinearLayout left,right,cardLeft,cardRight;

        public MessageViewHolder(@NonNull View v) {
            super(v);
            replyName = v.findViewById(R.id.reply_name);
            replyText = v.findViewById(R.id.reply_text);
            replyTime = v.findViewById(R.id.reply_time);
            messageName = v.findViewById(R.id.message_name);
            messageText = v.findViewById(R.id.message_text);
            messageTime = v.findViewById(R.id.message_time);
            left = v.findViewById(R.id.layout_left);
            right = v.findViewById(R.id.layout_right);
            cardLeft = v.findViewById(R.id.card_left);
            cardRight = v.findViewById(R.id.card_right);
        }

        private void setMessageDetails(Alert alert) {
            LinearLayout.LayoutParams moreWeight = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    5.0f
            );

            LinearLayout.LayoutParams lessWeight = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );

            if (alert.isReply()) {
                replyName.setText(alert.getName());
                replyText.setText(alert.getText());
                replyTime.setText(alert.getTime());
                replyName.setVisibility(View.VISIBLE);
                replyText.setVisibility(View.VISIBLE);
                replyTime.setVisibility(View.VISIBLE);
                messageName.setVisibility(View.INVISIBLE);
                messageText.setVisibility(View.GONE);
                messageTime.setVisibility(View.GONE);
                cardRight.setBackground(null);
                left.setLayoutParams(moreWeight);
                right.setLayoutParams(lessWeight);
            } else {

                messageText.setText(alert.getText());
                messageTime.setText(alert.getTime());
                replyName.setVisibility(View.INVISIBLE);
                replyText.setVisibility(View.GONE);
                replyTime.setVisibility(View.GONE);
                messageName.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                messageTime.setVisibility(View.VISIBLE);
                cardLeft.setBackground(null);
                left.setLayoutParams(lessWeight);
                right.setLayoutParams(moreWeight);
            }
        }
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventText, eventTime;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventText = itemView.findViewById(R.id.event_text);
            eventTime = itemView.findViewById(R.id.event_time);
        }

        public void setEventDetails(Alert alert) {
            eventText.setText(alert.getText());
            eventTime.setText(alert.getTime());
        }
    }

    public AlertAdapter(Context context, List<Alert> alerts) {
        this.alerts = alerts;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

        if (viewType == TYPE_MESSAGE) {
            view = LayoutInflater.from(context).inflate(R.layout.alert_item, viewGroup, false);
            return new MessageViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.event_item, viewGroup, false);
            return new EventViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        /*if (position == 0) {

        } else if (position > 0) {}*/

        if (getItemViewType(position) == TYPE_MESSAGE) {
            ((MessageViewHolder) viewHolder).setMessageDetails(alerts.get(position));
        } else if(getItemViewType(position) == TYPE_EVENT) {
            ((EventViewHolder) viewHolder).setEventDetails(alerts.get(position));
        }
    }



    @Override
    public int getItemCount() {
        return alerts.size();
    }

    @Override
    public int getItemViewType(int i) {
        Alert alert = alerts.get(i);

        if (alert.getAlertType() == Alert.MESSAGE) {
            return TYPE_MESSAGE;
        } else {
            return TYPE_EVENT;
        }
    }
}

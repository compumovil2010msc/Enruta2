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

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private List<Alert> alerts;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView replyName, replyText, messageName, messageText, messageTime, replyTime;
        LinearLayout left,right,cardLeft,cardRight;

        public ViewHolder(@NonNull View v) {
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
    }

    public AlertAdapter(Context context, List<Alert> alerts) {
        this.alerts = alerts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.alert_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = i;
        Alert alert = alerts.get(i);

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
            viewHolder.replyName.setText(alert.getName());
            viewHolder.replyText.setText(alert.getText());
            viewHolder.replyTime.setText(alert.getTime());
            viewHolder.replyName.setVisibility(View.VISIBLE);
            viewHolder.replyText.setVisibility(View.VISIBLE);
            viewHolder.replyTime.setVisibility(View.VISIBLE);
            viewHolder.messageName.setVisibility(View.INVISIBLE);
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageTime.setVisibility(View.GONE);
            viewHolder.cardRight.setBackground(null);
            viewHolder.left.setLayoutParams(moreWeight);
            viewHolder.right.setLayoutParams(lessWeight);
        } else {

            viewHolder.messageText.setText(alert.getText());
            viewHolder.messageTime.setText(alert.getTime());
            viewHolder.replyName.setVisibility(View.INVISIBLE);
            viewHolder.replyText.setVisibility(View.GONE);
            viewHolder.replyTime.setVisibility(View.GONE);
            viewHolder.messageName.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageTime.setVisibility(View.VISIBLE);
            viewHolder.cardLeft.setBackground(null);
            viewHolder.left.setLayoutParams(lessWeight);
            viewHolder.right.setLayoutParams(moreWeight);
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

}

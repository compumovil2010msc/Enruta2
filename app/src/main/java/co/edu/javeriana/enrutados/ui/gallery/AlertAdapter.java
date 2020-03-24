package co.edu.javeriana.enrutados.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        TextView replyName, messageName, replyText, messageText;

        public ViewHolder(@NonNull View v) {
            super(v);
            replyName = v.findViewById(R.id.reply_name);
            messageName = v.findViewById(R.id.message_name);
            replyText = v.findViewById(R.id.reply_text);
            messageText = v.findViewById(R.id.message_text);
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

        if (alert.isReply()) {
            viewHolder.replyName.setText(alert.getName());
            viewHolder.replyText.setText(alert.getText());

            viewHolder.replyName.setVisibility(View.VISIBLE);
            viewHolder.replyText.setVisibility(View.VISIBLE);

            viewHolder.messageName.setVisibility(View.INVISIBLE);
            viewHolder.messageText.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.messageName.setText(alert.getName());
            viewHolder.messageText.setText(alert.getText());

            viewHolder.replyName.setVisibility(View.INVISIBLE);
            viewHolder.replyText.setVisibility(View.INVISIBLE);

            viewHolder.messageName.setVisibility(View.VISIBLE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

}

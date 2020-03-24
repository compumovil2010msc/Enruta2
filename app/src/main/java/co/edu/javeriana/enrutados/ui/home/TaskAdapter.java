package co.edu.javeriana.enrutados.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.TabbedActivity;
import co.edu.javeriana.enrutados.model.Task;

public class TaskAdapter extends RecyclerView.Adapter <TaskAdapter.MyViewHolder>{

    private List<Task> tasks;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDetail;
        ImageView doneStatus;
        public MyViewHolder (View v) {
            super(v);
            taskTitle = (TextView) v.findViewById(R.id.task_title);
            taskDetail = (TextView) v.findViewById(R.id.task_title);
            doneStatus = (ImageView) v.findViewById(R.id.done_status);
        }
    }

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    void startTaskActivity(int i) {
        //TODO: Add tab activity
        Intent intent = new Intent(context, TabbedActivity.class);
        context.startActivity(intent);
//        Intent intent = new Intent(context, DetailActivity.class);
//        intent.putExtra("NAME", products.get(i).getName());
//        intent.putExtra("DESCRIPTION", products.get(i).getDescripcion());
//        intent.putExtra("PRICE", products.get(i).getPrice());
//        intent.putExtra("IMAGE", products.get(i).getImagepath());
//        context.startActivity(intent);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.task_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.taskTitle.setText(tasks.get(i).getTitle());
        myViewHolder.taskDetail.setText(tasks.get(i).getDescription());
        if (tasks.get(i).isDone()) {
            myViewHolder.doneStatus.setImageResource(R.drawable.ic_check_circle_black_24dp);
            myViewHolder.doneStatus.setColorFilter(R.color.positive_color);
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaskActivity(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

}

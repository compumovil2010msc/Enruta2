package co.edu.javeriana.enrutados.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.TabbedActivity;
import co.edu.javeriana.enrutados.model.Point;

public class PointAdapter extends RecyclerView.Adapter <PointAdapter.MyViewHolder>{

    private List<Point> points;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pointName;
        TextView pointDescription;
        ImageView doneStatus, pointImage;

        public MyViewHolder (View v) {
            super(v);
            pointName = v.findViewById(R.id.task_title);
            pointDescription = v.findViewById(R.id.task_detail);
            doneStatus = v.findViewById(R.id.done_status);
            pointImage = v.findViewById(R.id.point_image);
        }
    }

    public PointAdapter(Context context, List<Point> points) {
        this.context = context;
        this.points = points;
    }

    void startPointActivity(int i) {
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
        myViewHolder.pointName.setText(points.get(i).getName());
        myViewHolder.pointDescription.setText(points.get(i).getDescription());

        if (points.get(i).getFinished()) {
            myViewHolder.doneStatus.setImageResource(R.drawable.ic_check_circle_black_24dp);
            myViewHolder.doneStatus.setColorFilter(R.color.positive_color);
        }

        new DownloadImageTask(myViewHolder.pointImage)
                .execute(points.get(i).getImage());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPointActivity(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

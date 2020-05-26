package co.edu.javeriana.enrutados.ui.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import co.edu.javeriana.enrutados.R;

public class ImageEvidenceAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<String> filepaths;

    // 1
    public ImageEvidenceAdapter(Context context, List<String> filepaths) {
        this.mContext = context;
        this.filepaths = filepaths;
    }

    // 2
    @Override
    public int getCount() {
        return filepaths.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String filepath = filepaths.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.evidence_image_item, null);
        }

        final ImageView preview = (ImageView)convertView.findViewById(R.id.image_evidence_preview);
        preview.setImageResource(R.drawable.ic_menu_camera);

        return convertView;
    }
}

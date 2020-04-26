package co.edu.javeriana.enrutados.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.model.Alert;

public class AlertsFragment extends Fragment {

    private static final String FIREBASE_ROOM = "alerts";

    private GalleryViewModel galleryViewModel;
    private RecyclerView rvAlerts;
    private AlertAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText messageText;
    private ImageButton sendBtn;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        sendBtn = root.findViewById(R.id.button_send_message);
        messageText = root.findViewById(R.id.edit_text_message);
        rvAlerts = root.findViewById(R.id.list_of_alerts);
        rvAlerts.setHasFixedSize(true);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(FIREBASE_ROOM);

        adapter = new AlertAdapter(root.getContext());
        layoutManager = new LinearLayoutManager(root.getContext());
        rvAlerts.setLayoutManager(layoutManager);
        rvAlerts.setAdapter(adapter);

        adapter.addAlert(new Alert("Han llegado los repuestos!", "Mar 29, 8:47"));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new Alert("Han llegado los repuestos!", "Mar 29, 8:47") //Example for event alert
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM");
                String m = messageText.getText().toString();

                databaseReference.push().setValue(
                        new Alert(false, "Ramón", m, dateFormat.format(cal.getTime()))
                );

                messageText.setText("");
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Alert alert = dataSnapshot.getValue(Alert.class);
                adapter.addAlert(alert);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }

    private void setScrollBar() {
        rvAlerts.scrollToPosition(adapter.getItemCount() - 1);
    }

}

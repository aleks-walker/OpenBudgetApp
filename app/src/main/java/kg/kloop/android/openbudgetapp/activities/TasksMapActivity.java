package kg.kloop.android.openbudgetapp.activities;

import androidx.fragment.app.FragmentActivity;
import kg.kloop.android.openbudgetapp.R;
import kg.kloop.android.openbudgetapp.objects.TenderTask;
import kg.kloop.android.openbudgetapp.objects.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TasksMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = TasksMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CollectionReference tasksColRef;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_map);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tasksColRef = db.collection("tasks");
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final ArrayList<TenderTask> tasks = new ArrayList<>();
        tasksColRef
                .whereEqualTo("completed", false)
                .whereEqualTo("needModeration", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        tasks.clear();
                        tasks.addAll(queryDocumentSnapshots.toObjects(TenderTask.class));
                        displayMarkers(tasks);
                    }
                });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG, "onInfoWindowClick: click");
                try {
                    Intent intent = new Intent(TasksMapActivity.this, WorkActivity.class);
                    intent.putExtra("task", tasks.get(Integer.valueOf(marker.getTag().toString())));
                    intent.putExtra("user", user);
                    startActivity(intent);
                    marker.getTag().toString();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        });
    }

    private void displayMarkers(ArrayList<TenderTask> tasks) {
        LatLng country = new LatLng(41.4313021, 72.6613778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(country, 7.14f));
        for (TenderTask task : tasks) {
            LatLng position = new LatLng(task.getLatitude(), task.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(task.getDescription()));
            marker.setTag(tasks.indexOf(task));
        }
    }

}
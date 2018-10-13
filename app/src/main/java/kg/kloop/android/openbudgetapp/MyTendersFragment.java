package kg.kloop.android.openbudgetapp;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MyTendersFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TendersRecyclerViewAdapter adapter;
    private ArrayList<Tender> tenderArrayList;
    private static final String TAG = MyTendersFragment.class.getSimpleName();

    public MyTendersFragment() {
    }

    public static Fragment newInstance() {
        return new MyTendersFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_tenders, container, false);
        RecyclerView myTendersRecyclerView = view.findViewById(R.id.my_tenders_recycler_view);
        final CollectionReference tendersCollectionReference = db.collection("tenders");
        ViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        MutableLiveData<String> userIdLiveData = ((MainViewModel) viewModel).getUserIdLiveData();
        tenderArrayList = new ArrayList<>();
        adapter = new TendersRecyclerViewAdapter(getContext(), tenderArrayList);
        userIdLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@android.support.annotation.Nullable String userId) {
                CollectionReference usersTendersCollectionRef = db.collection("users/" + userId + "/tenders");
                usersTendersCollectionRef.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot tenderId : queryDocumentSnapshots) {
                                if (tenderId.exists()) {
                                    tendersCollectionReference.whereEqualTo("id", tenderId.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                //TODO: implement correctly https://firebase.google.com/docs/firestore/query-data/listen
                                                tenderArrayList.clear();
                                                tenderArrayList.addAll(task.getResult().toObjects(Tender.class));
                                                adapter.notifyDataSetChanged();
                                            } else Log.d(TAG, task.getException().getMessage());
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });


        myTendersRecyclerView.setHasFixedSize(true);
        myTendersRecyclerView.setAdapter(adapter);
        myTendersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

}
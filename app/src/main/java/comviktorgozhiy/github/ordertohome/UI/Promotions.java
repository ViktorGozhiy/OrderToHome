package comviktorgozhiy.github.ordertohome.UI;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import comviktorgozhiy.github.ordertohome.Models.Promotion;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebasePromotionViewHolder;


public class Promotions extends Fragment {

    private FirebaseDatabase fb;
    private RecyclerView rvList;
    private FirebaseRecyclerAdapter<Promotion, FirebasePromotionViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promotions, container, false);
        getActivity().setTitle(getString(R.string.promotions));
        fb = FirebaseDatabase.getInstance();
        rvList = v.findViewById(R.id.rvList);
        setupList();
        return v;
    }

    private void setupList() {
        Query query = fb.getReference("promotions");
        FirebaseRecyclerOptions<Promotion> options = new FirebaseRecyclerOptions.Builder<Promotion>()
                .setQuery(query, Promotion.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Promotion, FirebasePromotionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebasePromotionViewHolder holder, int position, @NonNull Promotion model) {
                holder.bindPromotion(model);
            }

            @NonNull
            @Override
            public FirebasePromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_item_card, parent, false);
                FirebasePromotionViewHolder firebasePromotionViewHolder = new FirebasePromotionViewHolder(v);
                firebasePromotionViewHolder.setOnClickListener(new FirebasePromotionViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(String promo) {
                        Intent intent = new Intent(getActivity(), PromoInfo.class);
                        intent.putExtra("promo", promo);
                        startActivity(intent);
                    }
                });
                return firebasePromotionViewHolder;
            }
        };
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        rvList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}



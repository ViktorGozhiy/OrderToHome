package comviktorgozhiy.github.ordertohome.Presenters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class PromoListPresenter extends BasePresenter<PromoListPresenter.PromoListCallback> {

    public interface PromoListCallback {
        void setAdapter(RecyclerView.Adapter adapter);
        void setPromo(String promo);
        void setVisibleProgressbar(boolean flag);
    }

    private FirebaseDatabase fb;
    private FirebaseRecyclerAdapter<Promotion, FirebasePromotionViewHolder> adapter;

    public PromoListPresenter() {
        fb = FirebaseDatabase.getInstance();
    }

    public void setupList() {
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
                        getView().setPromo(promo);
                    }
                });
                getView().setVisibleProgressbar(false);
                return firebasePromotionViewHolder;
            }
        };
        getView().setAdapter(adapter);
        adapter.startListening();
    }

    public void startListening() {
        adapter.startListening();
    }

    public void stopListening() {
        adapter.stopListening();
    }

    private PromoListCallback getView() {
        if (view != null) return view;
        else return new PromoListCallback() {
            @Override
            public void setAdapter(RecyclerView.Adapter adapter) {

            }

            @Override
            public void setPromo(String promo) {

            }

            @Override
            public void setVisibleProgressbar(boolean flag) {

            }
        };
    }
}

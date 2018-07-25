package comviktorgozhiy.github.ordertohome.Presenters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import comviktorgozhiy.github.ordertohome.MainActivity;
import comviktorgozhiy.github.ordertohome.Models.Category;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Models.Product;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.UI.CategoryMenu;
import comviktorgozhiy.github.ordertohome.UI.ProductView;
import comviktorgozhiy.github.ordertohome.Utils.BadgeUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebaseProductViewHolder;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CategoryMenuPresenter extends BasePresenter<CategoryMenuPresenter.CategoryMenuCallback> {

    public interface CategoryMenuCallback {
        void setAdapter(RecyclerView.Adapter adapter);
        void setVisibleProgressbar(boolean flag);
        void setProduct(String productTitle, String category);
    }

    private FirebaseDatabase fb;
    private FirebaseRecyclerAdapter<Product, FirebaseProductViewHolder> adapter;
    private String uid;

    public CategoryMenuPresenter(Context context) {
        fb = FirebaseDatabase.getInstance();
        uid = UserUtils.getUid(context, FirebaseAuth.getInstance().getCurrentUser());
    }

    public void setupList(final String category, final int sortParameter, final Context context) {
        Query query;
        switch (sortParameter) {
            case 0: // name
                query = fb.getReference("menu").child(category).child("content").orderByChild("title");
                break;
            case 1: // weight
                query = fb.getReference("menu").child(category).child("content").orderByChild("weight");
                break;
            case 2: // price
                query = fb.getReference("menu").child(category).child("content").orderByChild("price");
                break;
            default:
                query = fb.getReference("menu").child(category).child("content");
                break;
        }
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Product, FirebaseProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseProductViewHolder holder, int position, @NonNull Product model) {
                holder.bindProduct(model, uid, category, context);
            }

            @NonNull
            @Override
            public FirebaseProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_card, parent, false);
                FirebaseProductViewHolder firebaseProductViewHolder = new FirebaseProductViewHolder(v);
                firebaseProductViewHolder.setOnClickListener(new FirebaseProductViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView tvTitle = view.findViewById(R.id.tvTitle);
                        String productTitle = tvTitle.getText().toString();
                        getView().setProduct(productTitle, category);
                    }
                });
                getView().setVisibleProgressbar(false);
                return firebaseProductViewHolder;
            }
        };
        adapter.notifyDataSetChanged();
        getView().setAdapter(adapter);
    }

    public void getCurrentOrderCounter(final LayerDrawable icon, final Context context) {
        Query query = fb.getReference("clients")
                .child(uid)
                .child("currentOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order.Item item = snapshot.getValue(Order.Item.class);
                    if (item != null) {
                        i = i + item.getQuantity();
                    }
                }
                BadgeUtils.setBadgeCount(context, icon, Integer.toString(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private CategoryMenuCallback getView() {
        if (view != null) return view;
        else return new CategoryMenuCallback() {
            @Override
            public void setAdapter(RecyclerView.Adapter adapter) {

            }

            @Override
            public void setVisibleProgressbar(boolean flag) {

            }

            @Override
            public void setProduct(String productTitle, String category) {

            }
        };
    }

    public void startListening() {
        adapter.startListening();
    }

    public void stopListening() {
        adapter.stopListening();
    }
}

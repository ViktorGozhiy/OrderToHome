package comviktorgozhiy.github.ordertohome.UI;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.Models.Promotion;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.BadgeDrawable;
import comviktorgozhiy.github.ordertohome.Models.Category;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebaseCategoryViewHolder;


public class MainMenu extends Fragment {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FirebaseDatabase fb;
    private FirebaseRecyclerAdapter<Category, FirebaseCategoryViewHolder> adapter;
    private RecyclerView recyclerView;
    private ViewFlipper viewFlipper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        getActivity().setTitle(getString(R.string.menu));
        setHasOptionsMenu(true);
        fb = FirebaseDatabase.getInstance();
        viewFlipper = v.findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_transition_in_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_transition_out_right));
        setupSlider();
        recyclerView = v.findViewById(R.id.recyclerView);
        setupList();
        return v;
    }

    private void setupSlider() {
        Query query = fb.getReference("promotions");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final ImageView imageView = new ImageView(getActivity());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(snapshot.getValue(Promotion.class).getImagePath());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageView);
                            flipperImages(imageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void flipperImages(ImageView imageView) {
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();
    }

    private void setupList() {
        Query query = fb.getReference("menu");
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, FirebaseCategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseCategoryViewHolder holder, int position, @NonNull Category model) {
                holder.bindCategory(model);
            }

            @NonNull
            @Override
            public FirebaseCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_card, parent, false);
                FirebaseCategoryViewHolder firebaseCategoryViewHolder = new FirebaseCategoryViewHolder(v);
                firebaseCategoryViewHolder.setOnClickListener(new FirebaseCategoryViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(String category) {
                        Intent intent = new Intent(getActivity(), CategoryMenu.class);
                        intent.putExtra("category", category);
                        startActivity(intent);
                    }
                });
                return firebaseCategoryViewHolder;
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cart, menu);
        MenuItem cart = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) cart.getIcon();
        setBadgeCount(getActivity(), icon, "2");
    }


    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.badge, badge);
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

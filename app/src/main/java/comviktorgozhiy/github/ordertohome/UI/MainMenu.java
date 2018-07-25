package comviktorgozhiy.github.ordertohome.UI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.MainActivity;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Models.Order.Item;
import comviktorgozhiy.github.ordertohome.Models.Promotion;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.BadgeDrawable;
import comviktorgozhiy.github.ordertohome.Models.Category;
import comviktorgozhiy.github.ordertohome.Utils.BadgeUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebaseCategoryViewHolder;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class MainMenu extends Fragment {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FirebaseDatabase fb;
    private FirebaseRecyclerAdapter<Category, FirebaseCategoryViewHolder> adapter;
    private RecyclerView recyclerView;
    private ViewFlipper viewFlipper;
    private String uid;
    private AppBarLayout slider;
    private FrameLayout splashScreen;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        getActivity().setTitle(getString(R.string.menu));
        setHasOptionsMenu(true);
        slider = v.findViewById(R.id.slider);
        splashScreen = v.findViewById(R.id.splashScreen);
        fb = FirebaseDatabase.getInstance();
        uid = UserUtils.getUid(getActivity(), FirebaseAuth.getInstance().getCurrentUser());
        viewFlipper = v.findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_transition_in_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_transition_out_right));
        recyclerView = v.findViewById(R.id.recyclerView);
        if (getArguments() != null) {
            if (getArguments().getBoolean("showSplash", false))
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            else displayUI();
        } else displayUI();
        setupSlider();
        setupList();
        return v;
    }

    private void setupSlider() {
        Query query = fb.getReference("promotions");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (getActivity() != null) {
                        final ImageView imageView = new ImageView(getActivity());
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setBackgroundResource(R.drawable.ic_launcher_background);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(snapshot.getValue(Promotion.class).getImagePath());
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).fit().into(imageView);
                                flipperImages(imageView);
                                displayUI();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayUI() {
            splashScreen.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            slider.setVisibility(View.VISIBLE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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
                        startActivityForResult(intent, MainActivity.ORDER_REQEST_CODE);
                    }
                });
                return firebaseCategoryViewHolder;
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cart, menu);
        MenuItem cart = menu.findItem(R.id.action_cart);
        getCurrentOrderCounter((LayerDrawable) cart.getIcon());
        //setBadgeCount(getActivity(), icon, "2");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) openCart();
        return super.onOptionsItemSelected(item);
    }

    private void openCart() {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container_main, new MyOrder()).addToBackStack(null).commit();
    }

    private void getCurrentOrderCounter(final LayerDrawable icon) {
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
                if (getActivity() != null) {
                    BadgeUtils.setBadgeCount(getActivity(), icon, Integer.toString(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.ORDER_REQEST_CODE && resultCode == RESULT_OK) openCart();
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

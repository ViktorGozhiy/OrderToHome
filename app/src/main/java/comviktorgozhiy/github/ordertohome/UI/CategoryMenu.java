package comviktorgozhiy.github.ordertohome.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.BadgeDrawable;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebaseProductViewHolder;
import comviktorgozhiy.github.ordertohome.Models.Product;

public class CategoryMenu extends AppCompatActivity {

    private FirebaseDatabase fb;
    private FirebaseRecyclerAdapter<Product, FirebaseProductViewHolder> adapter;
    private RecyclerView recyclerView;
    private String category;
    private int sortParameter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_menu);
        fb = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        category = getIntent().getStringExtra("category");
        setTitle(category);
        setupList(category, sortParameter);
    }

    private void setupList(final String category, final int sortParameter) {
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
                holder.bindProduct(model);
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
                        Intent intent = new Intent(CategoryMenu.this, ProductInfo.class);
                        intent.putExtra("productTitle", productTitle);
                        intent.putExtra("category", category);
                        startActivity(intent);
                    }
                });
                return firebaseProductViewHolder;
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_sort, menu);
        MenuItem cart = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) cart.getIcon();
        setBadgeCount(this, icon, "2");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        if (id == R.id.action_cart) openCart();
        if (id == R.id.action_sort) drawSortDialog();
        return super.onOptionsItemSelected(item);
    }

    private void openCart() {
        Toast.makeText(this, "cart", Toast.LENGTH_SHORT).show();
    }

    private void drawSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sorting)
        .setSingleChoiceItems(R.array.sort_array, sortParameter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sortParameter = i;
                setupList(category, sortParameter);
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
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

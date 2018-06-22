package comviktorgozhiy.github.ordertohome.UI;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.BadgeDrawable;
import comviktorgozhiy.github.ordertohome.Models.Product;

public class ProductInfo extends AppCompatActivity implements View.OnClickListener {

    private Product product;
    private FirebaseDatabase fb;
    private ImageView imageView;
    private TextView tvTitle, tvPrice, tvOldPrice, tvDiscount, tvWeight, tvContent, tvMinus, tvCount, tvPlus;
    private LinearLayout stickerHit, stickerNew, stickerDiscount, ltBtns;
    private FloatingActionButton fabAdd;
    private int count = 0;
    private Animation showAnim;
    private Animation hideAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_product_info);
        showAnim = AnimationUtils.loadAnimation(this, R.anim.show_anim);
        hideAnim = AnimationUtils.loadAnimation(this, R.anim.hide_anim);
        String productTitle = getIntent().getStringExtra("productTitle");
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), "TEST_STRING");
        supportPostponeEnterTransition();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(productTitle);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorBarText));
        fb = FirebaseDatabase.getInstance();
        imageView = findViewById(R.id.imageView);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvOldPrice = findViewById(R.id.tvOldPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvWeight = findViewById(R.id.tvWeight);
        tvContent = findViewById(R.id.tvContent);
        stickerHit = findViewById(R.id.stickerHit);
        stickerNew = findViewById(R.id.stickerNew);
        stickerDiscount = findViewById(R.id.stickerDiscount);
        ltBtns = findViewById(R.id.ltBtns);
        fabAdd = findViewById(R.id.fabAdd);
        tvMinus = findViewById(R.id.tvMinus);
        tvPlus = findViewById(R.id.tvPlus);
        tvCount = findViewById(R.id.tvCount);
        fabAdd.setOnClickListener(this);
        tvMinus.setOnClickListener(this);
        tvPlus.setOnClickListener(this);
        getProduct(getIntent().getStringExtra("category"), productTitle);
    }

    private void getProduct(String category, String productTitle) {
        Query query = fb.getReference("menu").child(category).child("content").child(productTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                showInfo(product);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showInfo(Product product) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(product.getImagePath());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });
        tvTitle.setText(product.getTitle());
        tvPrice.setText(product.getPrice() + " " + product.getMonetaryUnit());
        tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvOldPrice.setText(product.getOldPrice() + " " + product.getMonetaryUnit());
        tvDiscount.setText(product.getDiscount() + "%");
        tvWeight.setText(getString(R.string.weight) + " " +product.getWeight() + " " + product.getWeightUnit());
        tvContent.setText(product.getContent());
        if (product.isHitLabel()) stickerHit.setVisibility(View.VISIBLE);
        if (product.isNewLabel()) stickerNew.setVisibility(View.VISIBLE);
        if (product.getDiscount() != 0) {
            stickerDiscount.setVisibility(View.VISIBLE);
            tvOldPrice.setVisibility(View.VISIBLE);
        }
    }

    @Override public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
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
        return super.onOptionsItemSelected(item);
    }

    private void openCart() {

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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fabAdd:
                ltBtns.setVisibility(View.VISIBLE);
                fabAdd.setClickable(false);
                tvMinus.setClickable(true);
                ltBtns.startAnimation(showAnim);
                fabAdd.startAnimation(hideAnim);
                fabAdd.setVisibility(View.GONE);
                count ++;
                tvCount.setText(String.valueOf(count));
                break;
            case R.id.tvMinus:
                count --;
                if (count < 1) {
                    count = 0;
                    fabAdd.setClickable(true);
                    tvMinus.setClickable(false);
                    ltBtns.startAnimation(hideAnim);
                    ltBtns.setVisibility(View.GONE);
                    fabAdd.setVisibility(View.VISIBLE);
                    fabAdd.startAnimation(showAnim);
                }
                tvCount.setText(String.valueOf(count));
                break;
            case R.id.tvPlus:
                count ++;
                if (count > 9) count = 9;
                tvCount.setText(String.valueOf(count));
                break;
        }
    }
}

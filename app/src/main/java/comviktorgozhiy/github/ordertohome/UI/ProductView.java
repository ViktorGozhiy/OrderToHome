package comviktorgozhiy.github.ordertohome.UI;

import android.graphics.Paint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Presenters.ProductPresenter;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Models.Product;
import comviktorgozhiy.github.ordertohome.Utils.BadgeUtils;
import comviktorgozhiy.github.ordertohome.Utils.FirebaseUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;

public class ProductView extends AppCompatActivity implements View.OnClickListener, ProductPresenter.ProductViewCallback {

    private Animation showAnim;
    private Animation hideAnim;
    private ProductPresenter presenter;

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvPrice) TextView tvPrice;
    @BindView(R.id.tvOldPrice) TextView tvOldPrice;
    @BindView(R.id.tvDiscount) TextView tvDiscount;
    @BindView(R.id.tvWeight) TextView tvWeight;
    @BindView(R.id.tvContent) TextView tvContent;
    @BindView(R.id.stickerHit) LinearLayout stickerHit;
    @BindView(R.id.stickerNew) LinearLayout stickerNew;
    @BindView(R.id.stickerDiscount) LinearLayout stickerDiscount;
    @BindView(R.id.ltBtns) LinearLayout ltBtns;
    @BindView(R.id.fabAdd) FloatingActionButton fabAdd;
    @BindView(R.id.tvMinus) TextView tvMinus;
    @BindView(R.id.tvPlus) TextView tvPlus;
    @BindView(R.id.tvCount) TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_product_info);
        ButterKnife.bind(this);
        presenter = new ProductPresenter(this);
        presenter.attachView(this);
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
        fabAdd.setOnClickListener(this);
        tvMinus.setOnClickListener(this);
        tvPlus.setOnClickListener(this);
        presenter.getProduct(getIntent().getStringExtra("category"), productTitle);
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
        presenter.getCurrentOrderCounter((LayerDrawable) cart.getIcon(), ProductView.this);
        //setBadgeCount(this, icon, "0");
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
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fabAdd:
                enableCounter();
                presenter.addButton();
                break;
            case R.id.tvMinus:
                if (!tvPlus.isClickable()) tvPlus.setClickable(true);
                presenter.minusButton();
                break;
            case R.id.tvPlus:
                presenter.plusButton();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void setImage(Uri uri) {
        Picasso.get().load(uri).fit().into(imageView);
    }

    @Override
    public void setProductTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setProductPrice(float price) {
        tvPrice.setText(price + " " + getString(R.string.monetary_unit));
    }

    @Override
    public void setProductOldPrice(float price) {
        tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvOldPrice.setText(price + " " + getString(R.string.monetary_unit));
    }

    @Override
    public void setDiscount(int discount) {
        tvDiscount.setText(discount + "%");
    }

    @Override
    public void setWeight(int weight, String unit) {
        tvWeight.setText(getString(R.string.weight) + " " + weight + " " + unit);
    }

    @Override
    public void setContent(String content) {
        tvContent.setText(content);
    }

    @Override
    public void enableStickerHit(boolean flag) {
        if (flag) stickerHit.setVisibility(View.VISIBLE);
        else stickerHit.setVisibility(View.GONE);
    }

    @Override
    public void enableStickerNew(boolean flag) {
        if (flag) stickerNew.setVisibility(View.VISIBLE);
        else stickerNew.setVisibility(View.GONE);
    }

    @Override
    public void enableStickerDiscount(boolean flag) {
        if (flag) {
            stickerDiscount.setVisibility(View.VISIBLE);
            tvOldPrice.setVisibility(View.VISIBLE);
        } else {
            stickerDiscount.setVisibility(View.GONE);
            tvOldPrice.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCount(int count) {
        tvCount.setText(String.valueOf(count));
    }

    @Override
    public void resetCounter() {
        fabAdd.setClickable(true);
        tvMinus.setClickable(false);
        ltBtns.startAnimation(hideAnim);
        ltBtns.setVisibility(View.GONE);
        fabAdd.setVisibility(View.VISIBLE);
        fabAdd.startAnimation(showAnim);
    }

    @Override
    public void enableIncCount(boolean flag) {
        tvPlus.setClickable(flag);
    }

    @Override
    public void enableCounter() {
        ltBtns.setVisibility(View.VISIBLE);
        fabAdd.setClickable(false);
        tvMinus.setClickable(true);
        ltBtns.startAnimation(showAnim);
        fabAdd.startAnimation(hideAnim);
        fabAdd.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.deattachView();
    }
}

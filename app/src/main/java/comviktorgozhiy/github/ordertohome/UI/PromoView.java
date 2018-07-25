package comviktorgozhiy.github.ordertohome.UI;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.Presenters.PromoPresenter;
import comviktorgozhiy.github.ordertohome.R;

public class PromoView extends AppCompatActivity implements PromoPresenter.PromoCallback {

    private TextView tvTitle, tvContent;
    private ImageView imageView;
    private PromoPresenter promoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_info);
        String promo = getIntent().getStringExtra("promo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(promo);
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        imageView = findViewById(R.id.imageView);
        promoPresenter = new PromoPresenter();
        promoPresenter.attachView(this);
        promoPresenter.loadPromotion(promo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        promoPresenter.deattachView();
    }

    @Override
    public void setPromoTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setPromoContent(String content) {
        tvContent.setText(content);
    }

    @Override
    public void loadPromoImage(Uri uri) {
        Picasso.get().load(uri).fit().into(imageView);
    }

    @Override
    public void onLoadSuccess() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    @Override
    public void onLoadError() {
        Toast.makeText(this, "Error loading info", Toast.LENGTH_SHORT).show();
    }
}

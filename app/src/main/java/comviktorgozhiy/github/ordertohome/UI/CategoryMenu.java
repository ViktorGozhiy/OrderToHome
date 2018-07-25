package comviktorgozhiy.github.ordertohome.UI;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import comviktorgozhiy.github.ordertohome.MainActivity;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Presenters.CategoryMenuPresenter;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.BadgeUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebaseProductViewHolder;
import comviktorgozhiy.github.ordertohome.Models.Product;

public class CategoryMenu extends AppCompatActivity implements CategoryMenuPresenter.CategoryMenuCallback {

    private String category;
    private int sortParameter = 0;
    private CategoryMenuPresenter presenter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        category = getIntent().getStringExtra("category");
        setTitle(category);
        recyclerView.setVisibility(View.VISIBLE);
        presenter = new CategoryMenuPresenter(this);
        presenter.attachView(this);
        presenter.setupList(category, sortParameter, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_sort, menu);
        MenuItem cart = menu.findItem(R.id.action_cart);
        presenter.getCurrentOrderCounter((LayerDrawable) cart.getIcon(), this);
        //setBadgeCount(this, icon, "2");
        return super.onCreateOptionsMenu(menu);
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
        setResult(RESULT_OK);
        finish();
    }

    private void drawSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sorting)
        .setSingleChoiceItems(R.array.sort_array, sortParameter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sortParameter = i;
                presenter.setupList(category, sortParameter, getApplicationContext());
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.ORDER_REQEST_CODE && resultCode == RESULT_OK) openCart();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stopListening();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        presenter.startListening();
    }

    @Override
    public void setVisibleProgressbar(boolean flag) {
        if (flag) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setProduct(String productTitle, String category) {
        Intent intent = new Intent(CategoryMenu.this, ProductView.class);
        intent.putExtra("productTitle", productTitle);
        intent.putExtra("category", category);
        startActivityForResult(intent, MainActivity.ORDER_REQEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.deattachView();
    }
}

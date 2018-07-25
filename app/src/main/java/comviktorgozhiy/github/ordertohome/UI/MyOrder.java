package comviktorgozhiy.github.ordertohome.UI;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Models.Order.Item;
import comviktorgozhiy.github.ordertohome.Presenters.MyOrderPresenter;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.UI.Profile.Authentication;
import comviktorgozhiy.github.ordertohome.Utils.FirebaseUtils;
import comviktorgozhiy.github.ordertohome.Utils.StringUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrder extends Fragment implements View.OnClickListener, MyOrderPresenter.MyOrderCallback {

    private LayoutInflater inflater;
    private MyOrderPresenter presenter;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.empty_order_layout) ConstraintLayout emptyOrderLayout;
    @BindView(R.id.order_list) ConstraintLayout orderList;
    @BindView(R.id.order_list_layout) LinearLayout container;
    @BindView(R.id.tvTotal) TextView tvTotal;
    @BindView(R.id.btnAuth) Button btnAuth;
    @BindView(R.id.btnMenu) Button btnMenu;
    @BindView(R.id.confirmLayout) LinearLayout confirmLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_order, container, false);
        ButterKnife.bind(this, v);
        getActivity().setTitle(getString(R.string.my_order));
        setHasOptionsMenu(true);
        confirmLayout.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnAuth.setOnClickListener(this);
        this.inflater = inflater;
        presenter = new MyOrderPresenter();
        presenter.attachView(this);
        presenter.checkCurrentUserState(getActivity());
        presenter.loadOrder();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_order_menu, menu);
        MenuItem item = menu.findItem(R.id.action_clear);
        if (presenter.getCount() > 0) item.setVisible(true);
        else item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear) clearCurrentOrder();
        return super.onOptionsItemSelected(item);
    }

    private void clearCurrentOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.clear_order_dialog);
        builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.clearOrder();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmOrder() {
        Intent intent = new Intent(getActivity(), ConfirmOrder.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnMenu) getFragmentManager().beginTransaction().replace(R.id.container_main, new MainMenu()).addToBackStack(null).commit();
        if (id == R.id.btnAuth) getFragmentManager().beginTransaction().replace(R.id.container_main, new Authentication()).addToBackStack(null).commit();
        if (id == R.id.confirmLayout) confirmOrder();
    }

    @Override
    public void addItem(final Item item) {
        View v = inflater.inflate(R.layout.order_item, null);
        TextView tvCategory = v.findViewById(R.id.tvCategory);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvCount= v.findViewById(R.id.tvCount);
        TextView tvTotal = v.findViewById(R.id.tvTotal);
        tvCategory.setText(item.getCategoryName());
        tvTitle.setText(item.getTitle());
        tvCount.setText(Integer.toString(item.getQuantity()));
        String total = StringUtils.formatPrice(item.getPrice()) + " " + getString(R.string.monetary_unit) + " x "
                + Integer.toString(item.getQuantity()) + " = "
                + StringUtils.formatPrice(item.getPrice() * item.getQuantity()) + " " + getString(R.string.monetary_unit);
        tvTotal.setText(total);
        final Button btnPlus = v.findViewById(R.id.btnPlus);
        final Button btnMinus = v.findViewById(R.id.btnMinus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnPlusPressed(item);
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnMinusPressed(item);
            }
        });
        container.addView(v);
    }

    @Override
    public void removeItems() {
        container.removeAllViews();
    }

    @Override
    public void setTotalPrice(int count, float totalPrice) {
        tvTotal.setText(getString(R.string.total) + Integer.toString(count) + getString(R.string.dishes_on) + StringUtils.formatPrice(totalPrice) + " " + getString(R.string.monetary_unit));
    }

    @Override
    public void setEmptyOrder(boolean flag) {
        getActivity().invalidateOptionsMenu();
        if (!flag) {
            emptyOrderLayout.setVisibility(View.GONE);
            orderList.setVisibility(View.VISIBLE);
            confirmLayout.setVisibility(View.VISIBLE);
        } else {
            emptyOrderLayout.setVisibility(View.VISIBLE);
            orderList.setVisibility(View.GONE);
            confirmLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnonymousUser() {
        btnAuth.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAuthorizedUser() {
        btnAuth.setVisibility(View.GONE);
    }

    @Override
    public void onLoadSuccess() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadError() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.deattachView();
    }
}

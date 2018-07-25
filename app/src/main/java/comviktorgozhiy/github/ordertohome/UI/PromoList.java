package comviktorgozhiy.github.ordertohome.UI;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import comviktorgozhiy.github.ordertohome.Models.Promotion;
import comviktorgozhiy.github.ordertohome.Presenters.PromoListPresenter;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.ViewHolders.FirebasePromotionViewHolder;


public class PromoList extends Fragment implements PromoListPresenter.PromoListCallback {

    private PromoListPresenter presenter;

    @BindView(R.id.rvList) RecyclerView rvList;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promotions, container, false);
        ButterKnife.bind(this, v);
        getActivity().setTitle(getString(R.string.promotions));
        presenter = new PromoListPresenter();
        presenter.attachView(this);;
        presenter.setupList();
        return v;
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
    public void onDestroyView() {
        super.onDestroyView();
        presenter.deattachView();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        rvList.setHasFixedSize(true);
        rvList.setItemViewCacheSize(20);
        rvList.setDrawingCacheEnabled(true);
        rvList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvList.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        rvList.setAdapter(adapter);
    }

    @Override
    public void setPromo(String promo) {
        Intent intent = new Intent(getActivity(), PromoView.class);
        intent.putExtra("promo", promo);
        startActivity(intent);
    }

    @Override
    public void setVisibleProgressbar(boolean flag) {
        if (flag) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }
}



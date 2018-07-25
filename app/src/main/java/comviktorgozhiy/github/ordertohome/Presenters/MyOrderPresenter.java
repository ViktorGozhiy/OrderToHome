package comviktorgozhiy.github.ordertohome.Presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Utils.FirebaseUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;

public class MyOrderPresenter extends BasePresenter<MyOrderPresenter.MyOrderCallback> {

    private FirebaseDatabase fb;
    private String uid;
    private int count;
    private float totalPrice;

    public interface MyOrderCallback {
        void addItem(Order.Item item);
        void removeItems();
        void setTotalPrice(int count, float totalPrice);
        void setEmptyOrder(boolean flag);
        void onAnonymousUser();
        void onAuthorizedUser();
        void onLoadSuccess();
        void onLoadError();
    }

    private MyOrderCallback getView() {
        if (view != null) return view;
        else return new MyOrderCallback() {
            @Override
            public void addItem(Order.Item item) {

            }

            @Override
            public void removeItems() {

            }

            @Override
            public void setTotalPrice(int count, float totalPrice) {

            }

            @Override
            public void setEmptyOrder(boolean flag) {

            }

            @Override
            public void onAnonymousUser() {

            }

            @Override
            public void onAuthorizedUser() {

            }

            @Override
            public void onLoadSuccess() {

            }

            @Override
            public void onLoadError() {

            }
        };
    }

    public MyOrderPresenter() {
        fb = FirebaseDatabase.getInstance();
    }

    public void checkCurrentUserState(Context context) {
        uid = UserUtils.getUid(context, FirebaseAuth.getInstance().getCurrentUser());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) getView().onAuthorizedUser();
        else getView().onAnonymousUser();
    }

    public void loadOrder() {
        Query query = fb.getReference("clients").child(uid).child("currentOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getView().removeItems();
                getView().onLoadSuccess();
                if (dataSnapshot.getValue() != null) {
                    getView().setEmptyOrder(false);
                    count = 0;
                    totalPrice = 0f;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order.Item item = snapshot.getValue(Order.Item.class);
                        count += item.getQuantity();
                        totalPrice += item.getPrice() * item.getQuantity();
                        getView().addItem(item);
                    }
                    getView().setTotalPrice(count, totalPrice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getView().onLoadError();
            }
        });
    }

    public void btnPlusPressed(Order.Item item) {
        item.setQuantity(item.getQuantity() + 1);
        FirebaseUtils.replaceItemInOrder(item, uid);
    }

    public void btnMinusPressed(Order.Item item) {
        if (item.getQuantity() == 1) FirebaseUtils.removeItemFromOrder(item, uid);
        else {
            item.setQuantity(item.getQuantity() - 1);
            FirebaseUtils.replaceItemInOrder(item, uid);
        }
    }

    public void clearOrder() {
        FirebaseUtils.clearCurrentOrder(uid);
        count = 0;
        getView().setEmptyOrder(true);
    }

    public int getCount() {
        return count;
    }
}

package comviktorgozhiy.github.ordertohome.Presenters;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;

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

import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Models.Product;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.UI.ProductView;
import comviktorgozhiy.github.ordertohome.Utils.BadgeUtils;
import comviktorgozhiy.github.ordertohome.Utils.FirebaseUtils;
import comviktorgozhiy.github.ordertohome.Utils.StringUtils;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;

public class ProductPresenter extends BasePresenter<ProductPresenter.ProductViewCallback> {

    public interface ProductViewCallback {
        void setImage(Uri uri);
        void setProductTitle(String title);
        void setProductPrice(float price);
        void setProductOldPrice(float price);
        void setDiscount(int discount);
        void setWeight(int weight, String unit);
        void setContent(String content);
        void enableStickerHit(boolean flag);
        void enableStickerNew(boolean flag);
        void enableStickerDiscount(boolean flag);
        void setCount(int count);
        void resetCounter();
        void enableIncCount(boolean flag);
        void enableCounter();
    }

    private FirebaseDatabase fb;
    private Product product;
    private String uid;
    private int count;
    private String categoryName;

    public ProductPresenter(Context context) {
        fb = FirebaseDatabase.getInstance();
        uid = UserUtils.getUid(context, FirebaseAuth.getInstance().getCurrentUser());
    }

    public void getProduct(String category, final String productTitle) {
        categoryName = category;
        Query query = fb.getReference("menu").child(category).child("content").child(productTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                showProductInfo(product);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getProductInCurrentOrder(productTitle);
    }

    private void getProductInCurrentOrder(final String productTitle) {
        Query query = fb.getReference("clients")
                .child(uid)
                .child("currentOrder");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order.Item item = snapshot.getValue(Order.Item.class);
                    if (item != null) {
                        if (item.getTitle().equals(productTitle)){
                            count = item.getQuantity();
                            if (count > 0) getView().enableCounter();
                            getView().setCount(count);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCurrentOrderCounter(final LayerDrawable icon, final Context context) {
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
                BadgeUtils.setBadgeCount(context, icon, Integer.toString(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showProductInfo(Product product) {
        this.product = product;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(product.getImagePath());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                getView().setImage(uri);
            }
        });
        getView().setProductTitle(product.getTitle());
        getView().setProductPrice(product.getPrice());
        getView().setProductOldPrice(product.getOldPrice());
        getView().setDiscount(product.getDiscount());
        getView().setWeight(product.getWeight(), product.getWeightUnit());
        getView().setContent(product.getContent());
        getView().enableStickerHit(product.isHitLabel());
        getView().enableStickerNew(product.isNewLabel());
        getView().enableStickerDiscount(product.getDiscount() != 0);

    }

    private ProductViewCallback getView() {
        if (view != null) return view;
        else return new ProductViewCallback() {
            @Override
            public void setImage(Uri uri) {

            }

            @Override
            public void setProductTitle(String title) {

            }

            @Override
            public void setProductPrice(float price) {

            }

            @Override
            public void setProductOldPrice(float price) {

            }

            @Override
            public void setDiscount(int discount) {

            }

            @Override
            public void setWeight(int weight, String unit) {

            }

            @Override
            public void setContent(String content) {

            }

            @Override
            public void enableStickerHit(boolean flag) {

            }

            @Override
            public void enableStickerNew(boolean flag) {

            }

            @Override
            public void enableStickerDiscount(boolean flag) {

            }

            @Override
            public void setCount(int count) {

            }

            @Override
            public void resetCounter() {

            }

            @Override
            public void enableIncCount(boolean flag) {

            }

            @Override
            public void enableCounter() {

            }
        };
    }

    public void removeProductFromOrder() {
        fb.getReference("clients")
                .child(uid)
                .child("currentOrder")
                .child(product.getTitle())
                .removeValue();
    }

    public void addButton() {
        count++;
        getView().setCount(count);
        FirebaseUtils.addProductToOrder(product, uid, categoryName, count);
    }

    public void plusButton() {
        if (count > 8) getView().enableIncCount(false);
        else {
            count++;
            FirebaseUtils.addProductToOrder(product, uid, categoryName, count);
        }
        getView().setCount(count);
    }

    public void minusButton() {
        count--;
        if (count < 1) {
            count = 0;
            getView().resetCounter();
            removeProductFromOrder();
        } else FirebaseUtils.addProductToOrder(product, uid, categoryName, count);
        getView().setCount(count);
    }
}

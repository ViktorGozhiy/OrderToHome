package comviktorgozhiy.github.ordertohome.Presenters;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import comviktorgozhiy.github.ordertohome.Models.Promotion;

public class PromoPresenter extends BasePresenter<PromoPresenter.PromoCallback> {

    private FirebaseDatabase fb;

    public interface PromoCallback {
        void setPromoTitle(String title);
        void setPromoContent(String content);
        void loadPromoImage(Uri uri);
        void onLoadSuccess();
        void onLoadError();
    }

    public PromoPresenter() {
        fb = FirebaseDatabase.getInstance();
    }

    public void loadPromotion(final String promoTitle) {
        Query query = fb.getReference("promotions").child(promoTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Promotion promotion = dataSnapshot.getValue(Promotion.class);
                    getView().setPromoTitle(promotion.getTitle());
                    getView().setPromoContent(promotion.getContent());
                    getView().onLoadSuccess();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(promotion.getImagePath());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getView().loadPromoImage(uri);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getView().onLoadError();
            }
        });
    }

    private PromoCallback getView() {
        if (view != null) return view;
        else return new PromoCallback() {
            @Override
            public void setPromoTitle(String title) {

            }

            @Override
            public void setPromoContent(String content) {

            }

            @Override
            public void loadPromoImage(Uri uri) {

            }

            @Override
            public void onLoadSuccess() {

            }

            @Override
            public void onLoadError() {

            }
        };
    }
}

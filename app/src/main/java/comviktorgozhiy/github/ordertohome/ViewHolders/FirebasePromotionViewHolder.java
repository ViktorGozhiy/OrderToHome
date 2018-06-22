package comviktorgozhiy.github.ordertohome.ViewHolders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.Models.Promotion;
import comviktorgozhiy.github.ordertohome.R;

public class FirebasePromotionViewHolder extends RecyclerView.ViewHolder {

    private TextView tvTitle;
    private ImageView imageView;

    public FirebasePromotionViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        imageView = itemView.findViewById(R.id.imageView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(tvTitle.getText().toString());
            }
        });
    }

    public void bindPromotion(Promotion promo) {
        tvTitle.setText(promo.getTitle());
        setImage(promo.getImagePath());
    }

    private void setImage(String imagePath) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imagePath);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });
    }

    private FirebasePromotionViewHolder.ClickListener mClickListener;
    public interface ClickListener {
        public void onItemClick(String promo);
    }

    public void setOnClickListener(FirebasePromotionViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}

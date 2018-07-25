package comviktorgozhiy.github.ordertohome.ViewHolders;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.Models.Product;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.FirebaseUtils;

public class FirebaseProductViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView tvTitle, tvContent, tvWeight, tvPrice, tvOldPrice, tvDiscount;
    private Button btnAdd;
    private LinearLayout stickerHit, stickerNew, stickerDiscount;
    private Product product;
    private String uid;
    private String categoryName;


    public FirebaseProductViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvContent = itemView.findViewById(R.id.tvContent);
        tvWeight = itemView.findViewById(R.id.tvWeight);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
        tvDiscount = itemView.findViewById(R.id.tvDiscount);
        stickerHit = itemView.findViewById(R.id.stickerHit);
        stickerNew = itemView.findViewById(R.id.stickerNew);
        stickerDiscount = itemView.findViewById(R.id.stickerDiscount);
        btnAdd = itemView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation pushAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.push_anim);
                btnAdd.startAnimation(pushAnim);
                addToOrder(view);
            }
        });
        imageView = itemView.findViewById(R.id.imageView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
    }

    public void bindProduct(Product product, String uid, String categoryName, Context context) {
        this.uid = uid;
        this.categoryName = categoryName;
        this.product = product;
        tvTitle.setText(product.getTitle());
        tvContent.setText(product.getContent());
        tvPrice.setText(product.getPrice() + " " + context.getString(R.string.monetary_unit));
        tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvOldPrice.setText(product.getOldPrice() + " " + context.getString(R.string.monetary_unit));
        tvWeight.setText(product.getWeight() + " " + product.getWeightUnit());
        tvDiscount.setText("-" + product.getDiscount() + "%");
        if(product.isHitLabel()) stickerHit.setVisibility(View.VISIBLE);
        if(product.isNewLabel()) stickerNew.setVisibility(View.VISIBLE);
        if(product.getDiscount() != 0) {
            stickerDiscount.setVisibility(View.VISIBLE);
            tvOldPrice.setVisibility(View.VISIBLE);
        }
        setImage(product.getImagePath());

    }

    private void addToOrder(View view) {
        FirebaseUtils.addOneProductToOrder(product, uid, categoryName);
    }

    private void setImage(String imagePath) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imagePath);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resizeDimen(R.dimen.product_card_image_width, R.dimen.product_card_image_hight).centerCrop().into(imageView);
            }
        });
    }


    private FirebaseProductViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnClickListener(FirebaseProductViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}

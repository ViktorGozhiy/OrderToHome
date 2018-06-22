package comviktorgozhiy.github.ordertohome.ViewHolders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Models.Category;

public class FirebaseCategoryViewHolder extends ViewHolder {

    private TextView tvName;
    private ImageView imageView;

    public FirebaseCategoryViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        imageView = itemView.findViewById(R.id.imageView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(tvName.getText().toString());
            }
        });
    }

    public void bindCategory(Category category) {
        tvName.setText(category.getName());
        setImage(category.getImagePath());
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

    public void setTitle(String title) {
        tvName.setText(title);
    }

    private FirebaseCategoryViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        public void onItemClick(String category);
    }

    public void setOnClickListener(FirebaseCategoryViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}

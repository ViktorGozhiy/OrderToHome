package comviktorgozhiy.github.ordertohome.UI;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Models.Order.Item;
import comviktorgozhiy.github.ordertohome.R;

public class ConfirmOrder extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.checkBox) CheckBox checkBox;
    @BindView(R.id.edPhone) EditText edPhone;
    @BindView(R.id.edName) EditText edName;
    @BindView(R.id.edOrderComment) EditText edOrderComment;
    @BindView(R.id.edStreet) EditText edStreet;
    @BindView(R.id.edBuilding) EditText edBuilding;
    @BindView(R.id.edFlat) EditText edFlat;
    @BindView(R.id.edDeliveryComment) EditText edDeliveryComment;
    @BindView(R.id.layoutConfirm) LinearLayout confirmLayout;
    private FirebaseDatabase fb;
    private String uid;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.bind(this);
        setTitle(getString(R.string.confirmation_order));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    delayedDelivery(b);
            }
        });
        confirmLayout.setOnClickListener(this);
        fb = FirebaseDatabase.getInstance();
        order = new Order();
        //getOrderContent();
    }

    private void getOrderContent() {
        Query query = fb.getReference("clients").child(uid).child("currentOrder");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order.Item item = snapshot.getValue(Order.Item.class);
                    order.content.put(item.getTitle(), item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void delayedDelivery(boolean flag) {

    }

    private void orderConfirm() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.confirmLayout) orderConfirm();
    }
}

package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class PaymentMethodsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        setupPaymentMethods();
    }

    private void setupPaymentMethods() {
        LinearLayout paymentContainer = findViewById(R.id.paymentMethodsContainer);
        
        String[] methods = {"bKash", "Nagad", "Rocket", "Credit/Debit Card", "Cash on Delivery"};
        String[] descriptions = {"Mobile Banking", "Mobile Banking", "Mobile Banking", "Visa, MasterCard", "Pay when delivered"};
        
        for (int i = 0; i < methods.length; i++) {
            addPaymentMethod(paymentContainer, methods[i], descriptions[i]);
        }
    }

    private void addPaymentMethod(LinearLayout container, String method, String description) {
        LinearLayout methodLayout = new LinearLayout(this);
        methodLayout.setOrientation(LinearLayout.HORIZONTAL);
        methodLayout.setPadding(32, 24, 32, 24);
        methodLayout.setBackgroundResource(R.drawable.rounded_background);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        methodLayout.setLayoutParams(params);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView methodName = new TextView(this);
        methodName.setText(method);
        methodName.setTextSize(16);
        methodName.setTextColor(getResources().getColor(R.color.black));

        TextView methodDesc = new TextView(this);
        methodDesc.setText(description);
        methodDesc.setTextSize(14);
        methodDesc.setTextColor(getResources().getColor(R.color.teal_200));

        textContainer.addView(methodName);
        textContainer.addView(methodDesc);
        methodLayout.addView(textContainer);

        container.addView(methodLayout);
    }
}

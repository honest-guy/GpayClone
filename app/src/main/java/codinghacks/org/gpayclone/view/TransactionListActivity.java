package codinghacks.org.gpayclone.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import codinghacks.org.gpayclone.R;
import codinghacks.org.gpayclone.adapters.TransactionAdapter;
import codinghacks.org.gpayclone.model.Transaction;
import codinghacks.org.gpayclone.utils.DataManager;
import codinghacks.org.gpayclone.viewmodel.UserViewModel;
import codinghacks.org.gpayclone.viewmodel.ViewModelFactory;

public class TransactionListActivity extends AppCompatActivity {
    private ListView transactionListView;
    private UserViewModel userViewModel;

    DataManager dataManager;

    private TextView defaultAmountTextView;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        floatingActionButton = findViewById(R.id.fab);

        transactionListView = findViewById(R.id.transactionListView);

        defaultAmountTextView = findViewById(R.id.defaultAmount);
        
        dataManager = new DataManager(this);
        userViewModel = new ViewModelProvider(this, new ViewModelFactory(dataManager)).get(UserViewModel.class);



        userViewModel.getUserBalance().observe(this, balance -> {
            if (balance != null) {
                defaultAmountTextView.setText(String.format("%.2f", balance));
            }
        });


        userViewModel.getTransactions().observe(this, transactions -> {
            TransactionAdapter adapter = new TransactionAdapter(this, transactions);
            transactionListView.setAdapter(adapter);
        });

        userViewModel.getTransferSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Transaction successful", Toast.LENGTH_SHORT).show();
                userViewModel.loadTransactions();
            }
        });

        userViewModel.getTransferError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });


        userViewModel.loadTransactions();

        userViewModel.loadUserBalance();


        floatingActionButton.setOnClickListener(v -> showTransactionDialog());

        userViewModel.getTransferSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Transaction successful", Toast.LENGTH_SHORT).show();
                userViewModel.loadTransactions();
                userViewModel.loadUserBalance();
            }
        });

        userViewModel.getTransferError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTransactionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Transaction");


        View dialogView = getLayoutInflater().inflate(R.layout.activity_transfer, null);
        builder.setView(dialogView);


        EditText recipientPhoneInput = dialogView.findViewById(R.id.recipientPhoneInput);
        EditText amountInput = dialogView.findViewById(R.id.amountInput);


        builder.setPositiveButton("Transfer", (dialog, which) -> {
            String recipientPhone = recipientPhoneInput.getText().toString();
            String amountText = amountInput.getText().toString();


            if (!recipientPhone.isEmpty() && !amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    String senderPhone = dataManager.getPhoneNumber();
                    if (senderPhone != null) {
                        userViewModel.transferAmount(senderPhone, recipientPhone, amount);
                    } else {
                        Toast.makeText(this, "Current user's phone number not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());


        builder.create().show();
    }






}
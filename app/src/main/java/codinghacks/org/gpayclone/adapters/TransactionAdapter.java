package codinghacks.org.gpayclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import codinghacks.org.gpayclone.R;
import codinghacks.org.gpayclone.model.Transaction;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private final Context context;
    private final List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, R.layout.transaction_item, transactions);
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.transaction_item, parent, false);
        }

        Transaction transaction = transactions.get(position);

        TextView fromTextView = convertView.findViewById(R.id.fromTextView);
        TextView toTextView = convertView.findViewById(R.id.toTextView);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);

        fromTextView.setText("From: " + transaction.getFrom());
        toTextView.setText("To: " + transaction.getTo());
        amountTextView.setText("Amount: " + transaction.getAmount());

        return convertView;
    }
}


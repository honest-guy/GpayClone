package codinghacks.org.gpayclone.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import codinghacks.org.gpayclone.utils.DataManager;
import codinghacks.org.gpayclone.R;
import codinghacks.org.gpayclone.utils.EncryptionHelper;
import codinghacks.org.gpayclone.viewmodel.UserViewModel;
import codinghacks.org.gpayclone.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity {
    private EditText editTextPhone, editTextPin;
    private Button buttonLogin;
    private UserViewModel loginViewModel;
    private boolean isFirstTimeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPin = findViewById(R.id.editTextPin);
        buttonLogin = findViewById(R.id.buttonLogin);


        DataManager dataManager = new DataManager(this);


        loginViewModel = new ViewModelProvider(this, new ViewModelFactory(dataManager) {}).get(UserViewModel.class);

        isFirstTimeLogin = dataManager.isFirstTimeLogin();

        if (isFirstTimeLogin) {
            editTextPhone.setVisibility(View.VISIBLE);
        } else {
            editTextPhone.setVisibility(View.GONE);
        }


        loginViewModel.getLoginSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                goToNextActivity();
            }
        });

        loginViewModel.getLoginError().observe(this, errorMessage -> {
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        });

        buttonLogin.setOnClickListener(view -> {
            String pin = editTextPin.getText().toString().trim();
            String phoneNum;

            if (isFirstTimeLogin) {
                phoneNum = editTextPhone.getText().toString().trim();
                loginViewModel.loginWithPhoneAndPin(phoneNum, pin);
            } else {
                loginViewModel.loginWithPin(pin);
            }
        });
    }

    private void goToNextActivity() {
        Intent intent = new Intent(MainActivity.this, TransactionListActivity.class);
        startActivity(intent);
        finish();
    }
}
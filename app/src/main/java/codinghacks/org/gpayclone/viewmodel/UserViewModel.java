package codinghacks.org.gpayclone.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.crypto.SecretKey;

import codinghacks.org.gpayclone.model.Transaction;
import codinghacks.org.gpayclone.model.User;
import codinghacks.org.gpayclone.utils.DataManager;
import codinghacks.org.gpayclone.utils.EncryptionHelper;

public class UserViewModel extends ViewModel {
    private  DataManager dataManager;
    Context context;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> loginError = new MutableLiveData<>();

    private final MutableLiveData<Double> userBalance = new MutableLiveData<>();

    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> transferError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> transferSuccess = new MutableLiveData<>();

    private static final double DEFAULT_AMOUNT = 500;

    public UserViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactionsLiveData;
    }

    public LiveData<String> getTransferError() {
        return transferError;
    }

    public LiveData<Boolean> getTransferSuccess() {
        return transferSuccess;
    }


    public LiveData<Double> getUserBalance() {
        return userBalance;
    }

    public void loadUserBalance() {
        String phoneNum = dataManager.getPhoneNumber();
        if (phoneNum != null) {
            User user = dataManager.getUser(phoneNum);
            if (user != null) {
                userBalance.setValue(user.getAvailableAmount());
            }
        }
    }

    public void loginWithPhoneAndPin(String phoneNum, String pin) {
        if (validatePhoneNumber(phoneNum) && validatePin(pin)) {
            User existingUser = dataManager.getUser(phoneNum);
            if (existingUser == null) {
                User newUser = new User(phoneNum, DEFAULT_AMOUNT);
                dataManager.createUser(newUser);
                dataManager.savePhoneNumber(phoneNum);
                dataManager.saveUserCredentials(phoneNum, pin);
                userBalance.setValue(DEFAULT_AMOUNT);
            } else {
                userBalance.setValue(existingUser.getAvailableAmount());
            }
            loginSuccess.setValue(true);
        } else {
            loginError.setValue("Please enter a valid phone number and 4-digit PIN");
        }
    }

    public void loginWithPin(String pin) {
        try {

            String phoneNum = dataManager.getPhoneNumber(); // Adjust this method if necessary
            Log.d("DataManager", "Retrieved Phone Number from shared preference: " + phoneNum); // Log the phone number for debugging

            if (phoneNum != null) {
                if (!validatePin(pin)) {
                    loginError.setValue("Please enter a valid 4-digit PIN");
                    return;
                }

                String storedEncryptedPin = dataManager.getEncryptedPin(phoneNum);


                if (storedEncryptedPin != null) {
                    try {
                        SecretKey encryptionKey = dataManager.getEncryptionKey();
                        if (encryptionKey == null) {
                            loginError.setValue("Encryption key not found");
                            Log.e("DataManager", "Encryption key is null.");
                            return;
                        }

                        // Decrypt the stored PIN
                        String storedPin = EncryptionHelper.decrypt(storedEncryptedPin, encryptionKey);



                        if (storedPin.equals(pin)) {
                            loginSuccess.setValue(true);
                        } else {
                            loginError.setValue("Invalid PIN");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Log the exact exception and set a specific error message for debugging
                        Log.e("DataManager", "Decryption error: " + e.getMessage());
                        loginError.setValue("Error during PIN decryption");
                    }
                } else {
                    loginError.setValue("Phone number not registered");
                }
            } else {
                loginError.setValue("Phone number not found");
                Log.e("DataManager", "Phone number is null or not stored.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginError.setValue("Error during PIN validation");
        }
    }
    public void loadTransactions() {
        List<Transaction> transactions = dataManager.getTransactions();
        transactionsLiveData.setValue(transactions);
    }

    public void transferAmount(String fromPhone, String toPhone, double amount) {
        if (!validatePhoneNumber(fromPhone) || !validatePhoneNumber(toPhone)) {
            transferError.setValue("Invalid phone number format. Please enter a 10-digit number.");
            return;
        }

        if (fromPhone.equals(toPhone)) {
            transferError.setValue("Cannot transfer to your own account.");
            return;
        }

        User sender = dataManager.getUser(fromPhone);

        if (sender == null) {
            transferError.setValue("Sender not found");
            return;
        }


        User recipient = dataManager.getUser(toPhone);


        if (recipient == null) {
            recipient = new User(toPhone, 0);
            dataManager.createUser(recipient);
        }


        if (sender.getAvailableAmount() < amount) {
            transferError.setValue("Insufficient balance");
            return;
        }

        if (sender.getAvailableAmount() <= 0) {
            transferError.setValue("Invalid amount");
            return;
        }

        sender.setAvailableAmount(sender.getAvailableAmount() - amount);
        recipient.setAvailableAmount(recipient.getAvailableAmount() + amount);

        dataManager.saveUser(sender);
        dataManager.saveUser(recipient);


        Transaction transaction = new Transaction(fromPhone, toPhone, amount);
        dataManager.saveTransaction(transaction);

        transferSuccess.setValue(true);
    }



    private boolean validatePhoneNumber(String phoneNum) {
        return phoneNum != null && phoneNum.matches("^[0-9]{10}$");
    }

    private boolean validatePin(String pin) {
        return pin != null && pin.matches("^[0-9]{4}$");
    }

}


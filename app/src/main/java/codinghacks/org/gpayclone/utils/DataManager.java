package codinghacks.org.gpayclone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import codinghacks.org.gpayclone.model.Transaction;
import codinghacks.org.gpayclone.model.User;

public class DataManager {
    private final SharedPreferences sharedPreferences;


    private final Gson gson;

    private final SecretKey encryptionKey;
    public DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences("WalletApp", Context.MODE_PRIVATE);
        gson = new Gson();

        try {
            encryptionKey = loadOrGenerateKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load or generate encryption key");
        }
    }

    private SecretKey loadOrGenerateKey() throws Exception {

        String storedKey = sharedPreferences.getString("encryption_key", null);
        if (storedKey != null) {
            byte[] decodedKey = Base64.decode(storedKey, Base64.DEFAULT);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } else {

            SecretKey newKey = EncryptionHelper.generateKey();
            saveEncryptionKey(newKey);
            return newKey;
        }
    }

    private void saveEncryptionKey(SecretKey key) {
        String encodedKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("encryption_key", encodedKey);
        editor.apply();
    }


    public boolean isFirstTimeLogin() {
        String savedPhone = sharedPreferences.getString("phone_number", null);
        return savedPhone == null;
    }


    public void saveUserCredentials(String phoneNum, String pin) {
        try {
            String encryptedPin = EncryptionHelper.encrypt(pin, encryptionKey);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phoneNumber", phoneNum); // Store plain phone number
            editor.putString("pin_" + phoneNum, encryptedPin); // Store encrypted pin
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to encrypt and save user credentials");
        }
    }



    public boolean validateLoginPin(String inputPin) {
        try {
            String phoneNum = sharedPreferences.getString("phone_number", null);
            if (phoneNum != null) {
                String savedEncryptedPin = sharedPreferences.getString("pin_" + phoneNum, null);
                if (savedEncryptedPin != null) {
                    String decryptedPin = EncryptionHelper.decrypt(savedEncryptedPin, encryptionKey);
                    return decryptedPin.equals(inputPin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public User getUser(String phoneNum) {
        try {
            String userData = sharedPreferences.getString("user_" + phoneNum, null);
            if (userData != null) {
                return gson.fromJson(userData, User.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void saveUser(User user) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_" + user.getPhoneNum(), gson.toJson(user));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveTransaction(Transaction transaction) {
        List<Transaction> transactions = getTransactions();
        transactions.add(transaction);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("transactions", gson.toJson(transactions));
        editor.apply();
    }


    public List<Transaction> getTransactions() {
        String transactionData = sharedPreferences.getString("transactions", null);
        if (transactionData != null) {
            Type type = new TypeToken<List<Transaction>>() {}.getType();
            return gson.fromJson(transactionData, type);
        } else {
            return new ArrayList<>();
        }
    }


    public void createUser(User newUser) {
        saveUser(newUser);
    }

    public String getEncryptedPin(String phoneNum) {
        return sharedPreferences.getString("pin_" + phoneNum, null);
    }


    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }
    public void savePhoneNumber(String phoneNum) {
        try {
            String encryptedPhone = EncryptionHelper.encrypt(phoneNum, encryptionKey);
            Log.d("DataManager", "Encrypted Phone Number: " + encryptedPhone);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone_number", encryptedPhone);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DataManager", "Error saving phone number: " + e.getMessage());
        }
    }







    public String getPhoneNumber() {
        try {
            String encryptedPhone = sharedPreferences.getString("phone_number", null);
            Log.d("DataManager", "Retrieved encrypted phone number: " + encryptedPhone);

            if (encryptedPhone != null) {
                String decryptedPhone = EncryptionHelper.decrypt(encryptedPhone, encryptionKey);
                Log.d("DataManager", "Decrypted phone number: " + decryptedPhone);
                return decryptedPhone;
            } else {
                Log.e("DataManager", "Phone number is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DataManager", "Error decrypting phone number: " + e.getMessage());
        }
        return null;
    }










}






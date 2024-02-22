package io.xdiad.recognizetheapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeScreen extends AppCompatActivity {

    private TextView statusTextView, noContactsTextView,Name,Email;
    private RecyclerView contactsRecyclerView;
    private Button logoutUser,SignOut;

    private ContactsAdapter contactsAdapter;
    private List<String> contactsList = new ArrayList<>();

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen); // Make sure you have this layout file

        statusTextView = findViewById(R.id.statusTextView);
        noContactsTextView = findViewById(R.id.noContactsTextView);
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        logoutUser = findViewById(R.id.logoutUser);
        Name = findViewById(R.id.DisplayName);
        Email = findViewById(R.id.DisplayEmail);
        SignOut = findViewById(R.id.SignOut);

        setupRecyclerView();
        checkPermissionsAndLoadContacts();

        logoutUser.setOnClickListener(v -> logoutUser());




        //Google Sign out
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String PersonName = acct.getDisplayName();
            String PersonEmail = acct.getEmail();

            Name.setText(PersonName);
            Email.setText(PersonEmail);
        }

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomeScreen.this,EnterPhoneNumber.class);
                startActivity(i);
            }
        });


    }


    private void setupRecyclerView() {
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsAdapter = new ContactsAdapter(contactsList); // Implement ContactsAdapter based on your requirement
        contactsRecyclerView.setAdapter(contactsAdapter);
    }

    private void checkPermissionsAndLoadContacts() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContactsAndCompare();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContactsAndCompare();
        } else {
            Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
        }
    }


    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   /*
    //This will display names that user's save in their contact list start

    private void loadContactsAndCompare() {
        Map<String, String> deviceContacts = getDeviceContacts();
        fetchRegisteredContacts(deviceContacts);
    }

    private Map<String, String> getDeviceContacts() {
        Map<String, String> contactDetails = new HashMap<>();
        try (Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            if (phones != null) {
                int indexNumber = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int indexName = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
                while (phones.moveToNext()) {
                    String number = phones.getString(indexNumber);
                    String name = phones.getString(indexName);
                    String normalizedNumber = normalizePhoneNumber(number);
                    // Map normalized number to contact name, avoiding duplicate numbers
                    if (!contactDetails.containsKey(normalizedNumber)) {
                        contactDetails.put(normalizedNumber, name);
                    }
                }
            }
        }
        return contactDetails;
    }

    private void fetchRegisteredContacts(Map<String, String> deviceContacts) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Contacts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> displayContacts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String firebaseNumber = normalizePhoneNumber(snapshot.getKey());
                    // Check if the normalized firebase number is in the device contacts
                    if (deviceContacts.containsKey(firebaseNumber)) {
                        // Fetch and use the contact name as saved in the device
                        String contactName = deviceContacts.get(firebaseNumber);
                        displayContacts.add(contactName != null ? contactName : "Unknown");
                        Log.d("MatchedContacts", "Matched Contact: " + contactName + ", Number: " + firebaseNumber);
                    }
                }
                updateUIWithRegisteredContacts(displayContacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("HomeScreen", "Firebase read failed", databaseError.toException());
            }
        });
    }

    //This will display names that user's save in their contact list end
    */
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^




    //###################################################################################################################################################################

  //Give/display the name whatever user save while register in the app start

  private void loadContactsAndCompare() {
        List<String> deviceContacts = getDeviceContacts();
        fetchRegisteredContacts(deviceContacts);
    }

    private ArrayList<String> getDeviceContacts() {
        ArrayList<String> contactNumbers = new ArrayList<>();
        try (Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            if (phones != null) {
                int indexNumber = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (phones.moveToNext()) {
                    String number = phones.getString(indexNumber);
                    String normalizedNumber = normalizePhoneNumber(number);
                    if (!contactNumbers.contains(normalizedNumber)) {
                        contactNumbers.add(normalizedNumber);
                        Log.d("DeviceContacts", "Normalized Number: " + normalizedNumber); // Logging normalized device contact
                    }
                }
            }
        }
        return contactNumbers;
    }

    private void fetchRegisteredContacts(List<String> deviceContacts) {
        String excludeNumber = getUserPhoneNumber(); // Fetch the user's number to exclude
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Contacts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> registeredContacts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String firebaseNumber = normalizePhoneNumber(snapshot.getKey());
                    Contacts_ contact = snapshot.getValue(Contacts_.class); // Assuming Contacts_ is your model class
                    if (contact != null && !normalizePhoneNumber(contact.getPhone_number()).equals(excludeNumber)) { // Check if the number matches the excluded number
                        // Logging Firebase contact details
                        Log.d("FirebaseContacts", "Name: " + contact.getName() + ", Number: " + firebaseNumber);
                        if (deviceContacts.contains(firebaseNumber)) {
                            registeredContacts.add(contact.getName());
                        }
                    }
                }
                updateUIWithRegisteredContacts(registeredContacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("HomeScreen", "Firebase read failed", databaseError.toException());
            }
        });
    }

      //Give/display the name whatever user save while register in the app end

    //###################################################################################################################################################################

//This will compare to registered number with the firebase number (My contact number = firebase contact number)
    private String getUserPhoneNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppNamePrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userPhoneNumber", "");
    }


    private String normalizePhoneNumber(String number) {
        // Remove all non-digit characters
        String digits = number.replaceAll("[^\\d]", "");
        // Extract the last 10 digits
        if (digits.length() > 10) {
            digits = digits.substring(digits.length() - 10);
        }
        return digits;
    }



    private void updateUIWithRegisteredContacts(List<String> registeredContacts) {
        runOnUiThread(() -> {
            if (registeredContacts.isEmpty()) {
                noContactsTextView.setVisibility(View.VISIBLE);
                contactsRecyclerView.setVisibility(View.GONE);
            } else {
                noContactsTextView.setVisibility(View.GONE);
                contactsRecyclerView.setVisibility(View.VISIBLE);
                contactsList.clear();
                contactsList.addAll(registeredContacts);
                contactsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppNamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userPhoneNumber");
        editor.apply();
        navigateToVerificationScreen();
    }

    private void navigateToVerificationScreen() {
        Intent intent = new Intent(this, EnterPhoneNumber.class); // Replace EnterPhoneNumber with your login activity class
        startActivity(intent);
        finish();
    }
}


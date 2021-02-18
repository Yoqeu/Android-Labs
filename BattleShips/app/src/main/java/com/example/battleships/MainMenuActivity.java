package com.example.battleships;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {

    Button btnSignOut, btnCreate, btnFind, btnProfile;
    GoogleSignInClient mGoogleSignInClient;
    EditText editFind;
    TextView textViewLobbyId;
    ImageView imageViewCopy;
    String roomIdTemp;
    ArrayList<Room> rooms;
    ArrayList<DatabaseReference> roomReferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add fade transitions
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main_menu);

        rooms = new ArrayList<>();
        roomReferences = new ArrayList<>();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Room");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                rooms.add(snapshot.getValue(Room.class));
                roomReferences.add(snapshot.getRef());
                Log.i("added", "yes");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Lobby lobby = (lobbies.get(lobbies.size() - 1));

                Log.e("inDataChanged", "here");
                String opponentId = snapshot.getValue(String.class);
                String creatorId = snapshot.getRef().getParent().child("idCreator").toString();
                String lobbyId = snapshot.getRef().getParent().getKey();
                //Log.e("lobby != null", "lobby!=null");
                if (opponentId != null) {
                    Log.e("oppoentId!=null", opponentId);
                    Intent intent = new Intent(MainMenuActivity.this, DeploymentActivity.class);
                    intent.putExtra("opponentId", opponentId);
                    intent.putExtra("creatorId", creatorId);
                    intent.putExtra("lobbyId", lobbyId);

                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        editFind = findViewById(R.id.editTextFind);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Room").push();
                String roomId = myRef.getKey();
                roomIdTemp = roomId;
                Room room = new Room(roomId, user.getUid());
                myRef.setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myRef.child("idOpponent").addValueEventListener(valueEventListener);
                    }
                });
                editFind.setText(roomId);
                for (int i = 0; i < rooms.size(); i++) {
                    Log.i("roomId", rooms.get(i).getId());
                    if (rooms.get(i).getId().equals(editFind.getText().toString())) {

                        roomReferences.get(i).setValue(rooms.get(i));
                        Intent intent = new Intent(MainMenuActivity.this, DeploymentActivity.class);
                        intent.putExtra("creatorId", rooms.get(i).getIdCreator());
                        intent.putExtra("roomId", rooms.get(i).getId());
                        startActivity(intent);
                        break;
                    }
                }

            }
        });
        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(intent);
            }
        });
        btnSignOut = findViewById(R.id.btnSingOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        btnFind = findViewById(R.id.btnFind);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("edittext", editFind.getText().toString());
                Log.i("size", String.valueOf(rooms.size()));
                for (int i = 0; i < rooms.size(); i++) {
                    Log.i("roomId", rooms.get(i).getId());
                    if (rooms.get(i).getId().equals(editFind.getText().toString())) {

                        rooms.get(i).setIdOpponent(user.getUid());
                        roomReferences.get(i).setValue(rooms.get(i));
                        Intent intent = new Intent(MainMenuActivity.this, DeploymentActivity.class);
                        intent.putExtra("opponentId", rooms.get(i).getIdOpponent());
                        intent.putExtra("opponentId", rooms.get(i).getIdOpponent());
                        intent.putExtra("lobbyId", rooms.get(i).getId());
                        startActivity(intent);
                        break;
                    }
                }

            }
        });
    }

    private void signOut()
    {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainMenuActivity.this, "Sign out successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}


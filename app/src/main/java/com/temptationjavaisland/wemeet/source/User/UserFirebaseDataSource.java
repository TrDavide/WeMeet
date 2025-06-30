package com.temptationjavaisland.wemeet.source.User;

import static com.temptationjavaisland.wemeet.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Class that gets the user information using Firebase Realtime Database.
 */
public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public UserFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();

    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void getUserPreferedEvents(String idToken) {
        //databaseReference = firebaseDatabase.getReference().getRef();
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_EVENTS_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<Event> eventsList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            Event event = ds.getValue(Event.class);
                            eventsList.add(event);
                        }

                        userResponseCallback.onSuccessFromRemoteDatabase(eventsList);
                    }
                });
    }

    @Override
    public void saveUserPreferedEvent(String idToken, Event event) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_FAVORITE_EVENTS_COLLECTION)
                .child(event.getId()) // usa un ID univoco per evitare duplicati
                .setValue(event);
                /*.addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Evento preferito salvato con successo.");
                    //userResponseCallback.onSuccessFromRemoteDatabase(event);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Errore salvataggio evento preferito", e);
                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                });*/
    }

    @Override
    public void removeUserPreferedEvent(String idToken, String eventId) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_FAVORITE_EVENTS_COLLECTION)
                .child(eventId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    //userResponseCallback.onSuccessRemoveFavoriteEvent();
                })
                .addOnFailureListener(e -> {
                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                });
    }

    public void deleteUserData(String idToken, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .removeValue()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

}

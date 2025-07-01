package com.temptationjavaisland.wemeet.source.User;

import static com.temptationjavaisland.wemeet.util.Constants.*;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public UserFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }

    //salva i dati dell'utente nel database locale
    @Override
    public void saveUserData(User user) {
        //verifica se l'utente è già presente nel nodo "users"
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //utente già presente, si notifica il successo
                            Log.d(TAG, "User already present in Firebase Realtime Database");
                            userResponseCallback.onSuccessFromRemoteDatabase(user);
                        } else {
                            //utente non presente, si salva nel database
                            Log.d(TAG, "User not present in Firebase Realtime Database");
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        //dati salvati correttamente
                                        userResponseCallback.onSuccessFromRemoteDatabase(user);
                                    })
                                    .addOnFailureListener(e -> {
                                        //errore nel salvataggio, notifica fallimento
                                        userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //errore durante la lettura dal database
                        userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
                    }
                });
    }

    //recupera la lista di eventi preferiti di un utente tramite il suo idToken
    @Override
    public void getUserPreferedEvents(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_FAVORITE_EVENTS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //errore durante la lettura degli eventi preferiti
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    } else {
                        //lettura corretta, costruisce lista di eventi
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());
                        List<Event> eventsList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            Event event = ds.getValue(Event.class);
                            eventsList.add(event);
                        }

                        //invia la lista di eventi al callback
                        userResponseCallback.onSuccessFromRemoteDatabase(eventsList);
                    }
                });
    }


    //salva un evento preferito per un utente nel database
    @Override
    public void saveUserPreferedEvent(String idToken, Event event) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_FAVORITE_EVENTS_COLLECTION)
                .child(event.getId()) // utilizza id evento come chiave
                .setValue(event);
    }


    //rimuove un evento preferito di un utente tramite l'id dell'evento
    @Override
    public void removeUserPreferedEvent(String idToken, String eventId) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_FAVORITE_EVENTS_COLLECTION)
                .child(eventId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Rimozione avvenuta con successo, non fa nulla
                })
                .addOnFailureListener(e -> {
                    // Errore nella rimozione, notifica il fallimento
                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                });
    }


    //elimina i dati di un utente dal database
    public void deleteUserData(String idToken, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .removeValue()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

}

package com.temptationjavaisland.wemeet;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.shape.ShapeAppearanceModel;


public class HomePageActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu); // carica il menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.filter_icon){

            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_home).setIcon(R.drawable.home_active);

        bottomNavigationView.setItemActiveIndicatorEnabled(false);
        bottomNavigationView.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                resetIcons(); // Reimposta le icone allo stato normale
            if(item.getItemId() == R.id.nav_home){
                item.setIcon(R.drawable.home_active); // Cambia icona
                return true;
            }else if(item.getItemId() == R.id.nav_search){
                item.setIcon(R.drawable.place_active);
                return true;
            }else if(item.getItemId() == R.id.nav_save){
                item.setIcon(R.drawable.save_active);
                return true;
            }else if(item.getItemId() == R.id.nav_profile){
                item.setIcon(R.drawable.profile_active);
                return true;
            }
            return false;
        }
        });
    }

    // Metodo per ripristinare le icone normali
    private void resetIcons() {
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setIcon(R.drawable.home);
        bottomNavigationView.getMenu().findItem(R.id.nav_search).setIcon(R.drawable.place);
        bottomNavigationView.getMenu().findItem(R.id.nav_save).setIcon(R.drawable.save);
        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setIcon(R.drawable.profile);
    }
}
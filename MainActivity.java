package com.tp.gestiondepenses;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tp.gestiondepenses.ui.DashboardFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Afficher le Dashboard au démarrage
        loadFragment(new DashboardFragment());

        // Gérer les clics sur la barre de navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_depenses) {
                // fragment = new DepensesFragment(); // à créer plus tard
            } else if (itemId == R.id.nav_revenus) {
                // fragment = new RevenusFragment(); // à créer plus tard
            } else if (itemId == R.id.nav_budgets) {
                // fragment = new BudgetsFragment(); // à créer plus tard
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });
    }

    // Méthode pour charger un Fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
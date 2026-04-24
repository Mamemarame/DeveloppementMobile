package sn.esmt.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sn.esmt.myapplication.ui.RevenusFragment;

/**
 * MainActivity - Activité principale de l'application
 *
 * Elle sert de conteneur pour les différents Fragments.
 * La navigation entre les sections se fait via la BottomNavigationView.
 *
 * Pour l'instant, seul le Fragment Revenus est implémenté.
 * Tes collègues ajouteront leurs propres Fragments ici.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupère la BottomNavigationView du layout
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Affiche le Fragment Revenus au démarrage
        chargerFragment(new RevenusFragment());

        // Gère les clics sur la barre de navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_revenus) {
                // Onglet Revenus → notre Fragment
                fragment = new RevenusFragment();
            }
            // Les autres onglets seront ajoutés par tes collègues :
            // nav_accueil → DashboardFragment
            // nav_depenses → DepensesFragment
            // nav_budgets → BudgetsFragment

            if (fragment != null) {
                chargerFragment(fragment);
            }
            return true;
        });
    }

    /**
     * Charge un Fragment dans le conteneur principal
     * @param fragment : le Fragment à afficher
     */
    private void chargerFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
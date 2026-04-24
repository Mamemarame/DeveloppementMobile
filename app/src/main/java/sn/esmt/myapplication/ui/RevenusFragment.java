package sn.esmt.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import sn.esmt.myapplication.R;
import sn.esmt.myapplication.adapter.RevenuAdapter;
import sn.esmt.myapplication.model.Revenu;
import sn.esmt.myapplication.viewmodel.RevenuViewModel;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * RevenusFragment - Écran principal de la gestion des revenus
 *
 * Ce Fragment affiche :
 * - La carte avec le total des revenus du mois
 * - La liste de tous les revenus (RecyclerView)
 * - Un bouton FAB pour ajouter un nouveau revenu
 *
 * Il observe le ViewModel et se met à jour automatiquement
 * quand les données changent en base de données.
 */
public class RevenusFragment extends Fragment {

    // ─────────────────────────────────────────────────────────
    // COMPOSANTS DE L'ÉCRAN
    // ─────────────────────────────────────────────────────────

    /** ViewModel : source unique des données */
    private RevenuViewModel revenuViewModel;

    /** Adapter : fait le lien entre les données et le RecyclerView */
    private RevenuAdapter adapter;

    /** Affiche le total des revenus du mois */
    private TextView tvTotalMois;

    /** Affiché quand la liste est vide */
    private TextView tvListeVide;

    // ─────────────────────────────────────────────────────────
    // CRÉATION DE LA VUE
    // ─────────────────────────────────────────────────────────

    /**
     * Charge le layout XML du Fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revenus, container, false);
    }

    /**
     * Initialise les composants après la création de la vue
     * C'est ici qu'on connecte les données à l'interface
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── Récupération des vues ──
        tvTotalMois = view.findViewById(R.id.tvTotalMois);
        tvListeVide = view.findViewById(R.id.tvListeVide);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRevenus);
        FloatingActionButton fab  = view.findViewById(R.id.fabAjouterRevenu);

        // ── Configuration du RecyclerView ──
        // LinearLayoutManager : affiche les items en liste verticale
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crée l'adapter avec les actions sur les clics
        adapter = new RevenuAdapter(new RevenuAdapter.OnRevenuClickListener() {

            /**
             * Clic simple : ouvre le formulaire en mode modification
             */
            @Override
            public void onRevenuClick(Revenu revenu) {
                Intent intent = new Intent(getActivity(), FormRevenuActivity.class);
                // Passe l'ID du revenu pour le charger dans le formulaire
                intent.putExtra("REVENU_ID", revenu.getId());
                startActivity(intent);
            }

            /**
             * Clic long : demande confirmation avant suppression
             */
            @Override
            public void onRevenuLongClick(Revenu revenu) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Supprimer ce revenu ?")
                        .setMessage("Voulez-vous vraiment supprimer ce revenu ?\n"
                                + "Cette action est irréversible.")
                        .setPositiveButton("Supprimer", (dialog, which) -> {
                            // Supprime via le ViewModel
                            revenuViewModel.delete(revenu);
                            Snackbar.make(view,
                                    "Revenu supprimé",
                                    Snackbar.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        // ── Initialisation du ViewModel ──
        revenuViewModel = new ViewModelProvider(this).get(RevenuViewModel.class);

        // ── Observation des données (LiveData) ──

        /**
         * Observe la liste complète des revenus.
         * Chaque fois que la base de données change,
         * cette méthode est appelée automatiquement.
         */
        revenuViewModel.getAllRevenus().observe(getViewLifecycleOwner(), revenus -> {
            // Met à jour la liste affichée
            adapter.setRevenus(revenus);

            // Affiche ou cache le message "liste vide"
            if (revenus.isEmpty()) {
                tvListeVide.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvListeVide.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        /**
         * Observe le total des revenus du mois courant.
         * Mis à jour automatiquement quand un revenu est ajouté/supprimé.
         */
        revenuViewModel.getTotalRevenusMoisCourant().observe(
                getViewLifecycleOwner(), total -> {
                    if (total == null) total = 0.0;
                    // Formate le montant avec séparateurs de milliers
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
                    tvTotalMois.setText(nf.format(total) + " FCFA");
                });

        // ── Bouton FAB : ouvre le formulaire en mode création ──
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FormRevenuActivity.class);
            startActivity(intent);
        });
    }
}
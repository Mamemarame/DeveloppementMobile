package com.tp.gestiondepenses.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tp.gestiondepenses.R;
import com.tp.gestiondepenses.viewmodel.DashboardViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private TextView tvSolde;
    private TextView tvTotalDepenses;
    private RecyclerView rvLastTransactions;
    private FloatingActionButton fabAddDepense;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser les vues
        tvSolde = view.findViewById(R.id.tv_solde);
        tvTotalDepenses = view.findViewById(R.id.tv_total_depenses);
        rvLastTransactions = view.findViewById(R.id.rv_last_transactions);
        fabAddDepense = view.findViewById(R.id.fab_add_depense);

        // Configurer le RecyclerView
        rvLastTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Observer le solde
        viewModel.getSolde().observe(getViewLifecycleOwner(), solde -> {
            String montantFormate = formaterMontant(solde);
            tvSolde.setText(montantFormate + " FCFA");

            // Couleur selon solde positif ou négatif
            if (solde >= 0) {
                tvSolde.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvSolde.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        });

        // Observer le total des dépenses
        viewModel.getTotalDepenses().observe(getViewLifecycleOwner(), total -> {
            tvTotalDepenses.setText("Total dépenses : " + formaterMontant(total) + " FCFA");
        });

        // Observer les 5 dernières transactions
        viewModel.getLastFiveDepenses().observe(getViewLifecycleOwner(), depenses -> {
            // L'adapter sera créé dans la prochaine étape
        });

        // Bouton ajout rapide
        fabAddDepense.setOnClickListener(v -> {
            // Navigation vers le formulaire d'ajout de dépense
            // À compléter quand on aura la navigation
        });

        // Rafraîchir les données
        viewModel.refresh();
    }

    // Formater le montant en format lisible
    private String formaterMontant(double montant) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
        nf.setMaximumFractionDigits(0);
        return nf.format(montant);
    }
}
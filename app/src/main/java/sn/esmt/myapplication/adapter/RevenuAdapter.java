package sn.esmt.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sn.esmt.myapplication.R;
import sn.esmt.myapplication.model.Revenu;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RevenuAdapter - Adaptateur pour la liste des revenus
 *
 * Le RecyclerView ne peut pas afficher directement une liste d'objets Revenu.
 * L'Adapter fait le lien entre la liste de données et les vues (item_revenu.xml).
 *
 * Fonctionnement :
 * 1. onCreateViewHolder : crée une vue pour chaque item
 * 2. onBindViewHolder : remplit la vue avec les données du revenu
 * 3. getItemCount : indique le nombre d'éléments dans la liste
 */
public class RevenuAdapter extends RecyclerView.Adapter<RevenuAdapter.RevenuViewHolder> {

    /**
     * Liste des revenus à afficher
     * Initialisée vide, mise à jour via setRevenus()
     */
    private List<Revenu> revenus = new ArrayList<>();

    /**
     * Listener pour gérer les clics sur les items
     * Permet au Fragment de réagir quand l'utilisateur clique
     */
    private final OnRevenuClickListener listener;

    /**
     * Format de date : JJ/MM/AAAA (ex: 24/04/2025)
     */
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

    // ─────────────────────────────────────────────────────────
    // INTERFACE pour les clics sur les items
    // ─────────────────────────────────────────────────────────

    /**
     * Interface que le Fragment doit implémenter
     * pour réagir aux clics sur les revenus
     */
    public interface OnRevenuClickListener {
        /**
         * Clic simple : ouvrir le formulaire de modification
         */
        void onRevenuClick(Revenu revenu);

        /**
         * Clic long : afficher le dialog de suppression
         */
        void onRevenuLongClick(Revenu revenu);
    }

    /**
     * Constructeur de l'Adapter
     * @param listener : le Fragment qui gère les clics
     */
    public RevenuAdapter(OnRevenuClickListener listener) {
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────
    // MÉTHODES DU RECYCLERVIEW
    // ─────────────────────────────────────────────────────────

    /**
     * Crée une nouvelle vue pour un item de la liste
     * Appelé par le RecyclerView quand il a besoin d'une nouvelle vue
     */
    @NonNull
    @Override
    public RevenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Charge le layout item_revenu.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_revenu, parent, false);
        return new RevenuViewHolder(view);
    }

    /**
     * Remplit une vue avec les données d'un revenu
     * Appelé pour chaque item visible à l'écran
     * @param position : position de l'item dans la liste
     */
    @Override
    public void onBindViewHolder(@NonNull RevenuViewHolder holder, int position) {
        // Récupère le revenu à cette position
        Revenu revenu = revenus.get(position);

        // Affiche la source et l'emoji correspondant
        holder.tvSource.setText(revenu.getSource());
        holder.tvIconeSource.setText(getEmojiSource(revenu.getSource()));

        // Affiche la description si elle existe
        String desc = revenu.getDescription();
        if (desc != null && !desc.isEmpty()) {
            holder.tvDescription.setText(desc);
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Affiche la date au format JJ/MM/AAAA
        holder.tvDate.setText(SDF.format(new Date(revenu.getDate())));

        // Affiche le montant formaté en FCFA
        // NumberFormat ajoute les séparateurs de milliers
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
        holder.tvMontant.setText("+" + nf.format(revenu.getMontant()) + " FCFA");

        // Clic simple → modification
        holder.itemView.setOnClickListener(v -> listener.onRevenuClick(revenu));

        // Clic long → suppression
        holder.itemView.setOnLongClickListener(v -> {
            listener.onRevenuLongClick(revenu);
            return true;
        });
    }

    /**
     * Retourne le nombre total d'items dans la liste
     */
    @Override
    public int getItemCount() {
        return revenus.size();
    }

    /**
     * Met à jour la liste des revenus et rafraîchit l'affichage
     * Appelé par le Fragment quand le LiveData change
     */
    public void setRevenus(List<Revenu> revenus) {
        this.revenus = revenus;
        // Notifie le RecyclerView que les données ont changé
        notifyDataSetChanged();
    }

    /**
     * Retourne un emoji selon la source du revenu
     * Pour rendre l'interface plus visuelle
     */
    private String getEmojiSource(String source) {
        switch (source) {
            case "Salaire":   return "💼";
            case "Commerce":  return "🏪";
            case "Freelance": return "💻";
            case "Don":       return "🎁";
            default:          return "💰";
        }
    }

    // ─────────────────────────────────────────────────────────
    // VIEWHOLDER - référence les vues d'un item
    // ─────────────────────────────────────────────────────────

    /**
     * ViewHolder : contient les références aux vues d'un item
     * Évite de chercher les vues à chaque affichage (optimisation)
     */
    static class RevenuViewHolder extends RecyclerView.ViewHolder {

        TextView tvIconeSource; // Emoji de la source
        TextView tvSource;      // Nom de la source (Salaire, etc.)
        TextView tvDescription; // Description optionnelle
        TextView tvDate;        // Date du revenu
        TextView tvMontant;     // Montant en FCFA

        RevenuViewHolder(@NonNull View itemView) {
            super(itemView);
            // Lie chaque variable à son ID dans item_revenu.xml
            tvIconeSource  = itemView.findViewById(R.id.tvIconeSource);
            tvSource       = itemView.findViewById(R.id.tvSource);
            tvDescription  = itemView.findViewById(R.id.tvDescription);
            tvDate         = itemView.findViewById(R.id.tvDate);
            tvMontant      = itemView.findViewById(R.id.tvMontant);
        }
    }
}
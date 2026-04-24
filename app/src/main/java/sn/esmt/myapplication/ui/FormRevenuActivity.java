package sn.esmt.myapplication.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import sn.esmt.myapplication.R;
import sn.esmt.myapplication.model.Revenu;
import sn.esmt.myapplication.viewmodel.RevenuViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * FormRevenuActivity - Formulaire d'ajout et de modification d'un revenu
 *
 * Cette Activity gère deux modes :
 * - Mode création : quand on appuie sur le FAB (+)
 * - Mode modification : quand on clique sur un revenu existant
 *
 * Elle utilise le RevenuViewModel pour sauvegarder les données
 * sans accéder directement à la base de données.
 */
public class FormRevenuActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // VUES - références aux composants du layout
    // ─────────────────────────────────────────────────────────
    private TextInputEditText etMontant;    // Champ montant
    private TextInputEditText etDate;       // Champ date (lecture seule)
    private TextInputEditText etDescription;// Champ description
    private Spinner spinnerSource;          // Menu déroulant source
    private MaterialButton btnEnregistrer;  // Bouton de sauvegarde

    // ─────────────────────────────────────────────────────────
    // DONNÉES
    // ─────────────────────────────────────────────────────────

    /** ViewModel pour accéder aux opérations sur les revenus */
    private RevenuViewModel revenuViewModel;

    /** Calendrier pour stocker la date sélectionnée par l'utilisateur */
    private Calendar dateSelectionnee = Calendar.getInstance();

    /** Revenu en cours de modification (null si mode création) */
    private Revenu revenuAModifier = null;

    /** Format d'affichage de la date : JJ/MM/AAAA */
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_revenu);

        // Initialisation dans l'ordre
        initVues();
        initSpinnerSource();
        initViewModel();
        initDatePicker();

        // Date par défaut = aujourd'hui
        etDate.setText(SDF.format(dateSelectionnee.getTime()));

        // Vérifie si on est en mode modification
        int revenuId = getIntent().getIntExtra("REVENU_ID", -1);
        if (revenuId != -1) {
            // Mode modification : change le titre
            setTitle("Modifier le revenu");
        } else {
            // Mode création
            setTitle("Ajouter un revenu");
        }

        // Clic sur le bouton Enregistrer
        btnEnregistrer.setOnClickListener(v -> enregistrerRevenu());
    }

    // ─────────────────────────────────────────────────────────
    // INITIALISATION DES COMPOSANTS
    // ─────────────────────────────────────────────────────────

    /**
     * Lie les variables aux vues du layout XML
     */
    private void initVues() {
        etMontant      = findViewById(R.id.etMontant);
        etDate         = findViewById(R.id.etDate);
        etDescription  = findViewById(R.id.etDescription);
        spinnerSource  = findViewById(R.id.spinnerSource);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);
    }

    /**
     * Configure le Spinner avec les sources de revenus disponibles
     */
    private void initSpinnerSource() {
        // Liste des sources possibles selon le cahier des charges
        String[] sources = {"Salaire", "Commerce", "Freelance", "Don", "Autre"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sources
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(adapter);
    }

    /**
     * Initialise le ViewModel
     */
    private void initViewModel() {
        revenuViewModel = new ViewModelProvider(this).get(RevenuViewModel.class);
    }

    /**
     * Configure le champ date pour ouvrir un calendrier au clic
     * Bloque la sélection de dates futures (règle métier du CDC)
     */
    private void initDatePicker() {
        etDate.setOnClickListener(v -> {
            // Récupère la date actuellement sélectionnée
            int annee = dateSelectionnee.get(Calendar.YEAR);
            int mois  = dateSelectionnee.get(Calendar.MONTH);
            int jour  = dateSelectionnee.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, y, m, d) -> {
                        // Met à jour la date sélectionnée
                        dateSelectionnee.set(y, m, d);
                        // Affiche la date au format JJ/MM/AAAA
                        etDate.setText(SDF.format(dateSelectionnee.getTime()));
                    },
                    annee, mois, jour
            );
            // Bloque les dates futures (CDC : date ne peut pas être dans le futur)
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });
    }

    // ─────────────────────────────────────────────────────────
    // VALIDATION ET ENREGISTREMENT
    // ─────────────────────────────────────────────────────────

    /**
     * Valide les champs et enregistre le revenu en base de données
     * Règles de validation selon le cahier des charges :
     * - Montant obligatoire et > 0
     * - Date obligatoire et non future
     */
    private void enregistrerRevenu() {
        // Récupère les valeurs saisies
        String montantStr  = etMontant.getText() != null
                ? etMontant.getText().toString().trim() : "";
        String dateStr     = etDate.getText() != null
                ? etDate.getText().toString().trim() : "";
        String source      = spinnerSource.getSelectedItem().toString();
        String description = etDescription.getText() != null
                ? etDescription.getText().toString().trim() : "";

        // ── Validation du montant ──
        if (montantStr.isEmpty()) {
            etMontant.setError("Le montant est obligatoire");
            etMontant.requestFocus();
            return;
        }

        double montant = Double.parseDouble(montantStr);

        // Le montant doit être strictement supérieur à 0 (règle métier CDC)
        if (montant <= 0) {
            etMontant.setError("Le montant doit être supérieur à 0");
            etMontant.requestFocus();
            return;
        }

        // ── Validation de la date ──
        if (dateStr.isEmpty()) {
            etDate.setError("La date est obligatoire");
            etDate.requestFocus();
            return;
        }

        // ── Création ou mise à jour ──
        long timestamp = dateSelectionnee.getTimeInMillis();
        long now       = System.currentTimeMillis();

        if (revenuAModifier == null) {
            // Mode création : crée un nouvel objet Revenu
            Revenu nouveau = new Revenu(montant, source, timestamp, description, now);
            revenuViewModel.insert(nouveau);
            Snackbar.make(btnEnregistrer,
                    "Revenu enregistré avec succès ✓",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            // Mode modification : met à jour l'objet existant
            revenuAModifier.setMontant(montant);
            revenuAModifier.setSource(source);
            revenuAModifier.setDate(timestamp);
            revenuAModifier.setDescription(description);
            revenuViewModel.update(revenuAModifier);
            Snackbar.make(btnEnregistrer,
                    "Revenu modifié avec succès ✓",
                    Snackbar.LENGTH_SHORT).show();
        }

        // Retourne à l'écran précédent (liste des revenus)
        finish();
    }
}
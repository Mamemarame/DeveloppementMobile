package sn.esmt.myapplication.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import sn.esmt.myapplication.model.Revenu;
import sn.esmt.myapplication.repository.RevenuRepository;

import java.util.Calendar;
import java.util.List;

/**
 * RevenuViewModel - Gestion des données liées à l'interface
 *
 * Le ViewModel fait le lien entre l'interface utilisateur (Fragment/Activity)
 * et le Repository (base de données).
 *
 * Avantages du ViewModel :
 * - Les données survivent aux rotations d'écran
 * - L'interface n'accède jamais directement à la base de données
 * - Séparation claire entre logique métier et affichage
 *
 * AndroidViewModel : version du ViewModel qui a accès au contexte
 * de l'application (nécessaire pour le Repository)
 */
public class RevenuViewModel extends AndroidViewModel {

    /**
     * Repository : seul point d'accès aux données
     */
    private final RevenuRepository repository;

    /**
     * LiveData contenant tous les revenus
     * L'interface s'abonne à cette donnée et se met à jour automatiquement
     */
    private final LiveData<List<Revenu>> allRevenus;

    /**
     * Mois et année en cours pour les filtres par défaut
     * Calculés une seule fois à la création du ViewModel
     */
    private final int moisCourant;
    private final String anneeCourante;

    /**
     * Constructeur appelé automatiquement par Android
     * @param application : contexte transmis au Repository
     */
    public RevenuViewModel(@NonNull Application application) {
        super(application);

        // Initialise le Repository
        repository = new RevenuRepository(application);

        // Récupère le mois et l'année actuels
        // Calendar.MONTH est 0-indexé : janvier=0, donc on ajoute 1
        Calendar cal = Calendar.getInstance();
        moisCourant = cal.get(Calendar.MONTH) + 1;
        anneeCourante = String.valueOf(cal.get(Calendar.YEAR));

        // Charge tous les revenus dès la création du ViewModel
        allRevenus = repository.getAllRevenus();
    }

    // ─────────────────────────────────────────────────────────
    // OPÉRATIONS CRUD
    // L'interface appelle ces méthodes, le ViewModel délègue au Repository
    // ─────────────────────────────────────────────────────────

    /**
     * Ajouter un nouveau revenu
     */
    public void insert(Revenu revenu) {
        repository.insert(revenu);
    }

    /**
     * Modifier un revenu existant
     */
    public void update(Revenu revenu) {
        repository.update(revenu);
    }

    /**
     * Supprimer un revenu
     */
    public void delete(Revenu revenu) {
        repository.delete(revenu);
    }

    // ─────────────────────────────────────────────────────────
    // DONNÉES OBSERVABLES
    // Le Fragment observe ces LiveData pour mettre à jour l'affichage
    // ─────────────────────────────────────────────────────────

    /**
     * Retourne tous les revenus (LiveData)
     * Le Fragment s'abonne et reçoit les mises à jour automatiquement
     */
    public LiveData<List<Revenu>> getAllRevenus() {
        return allRevenus;
    }

    /**
     * Retourne les revenus du mois en cours (LiveData)
     */
    public LiveData<List<Revenu>> getRevenusDuMoisCourant() {
        return repository.getRevenusByMois(moisCourant, anneeCourante);
    }

    /**
     * Retourne le total des revenus du mois en cours (LiveData)
     * Utilisé pour afficher le solde sur le Dashboard
     */
    public LiveData<Double> getTotalRevenusMoisCourant() {
        return repository.getTotalRevenusParMois(moisCourant, anneeCourante);
    }
}
package sn.esmt.myapplication.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import sn.esmt.myapplication.database.AppDatabase;
import sn.esmt.myapplication.database.RevenuDao;
import sn.esmt.myapplication.model.Revenu;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RevenuRepository - Couche d'accès aux données
 *
 * Le Repository fait le lien entre le ViewModel et la base de données.
 * Il isole la logique d'accès aux données du reste de l'application.
 *
 * Pourquoi ? Le ViewModel ne doit pas connaître les détails de
 * la base de données. Le Repository s'en occupe à sa place.
 *
 * Les opérations d'écriture (insert, update, delete) doivent
 * être faites dans un thread séparé (pas le thread principal UI)
 * car elles peuvent être longues.
 */
public class RevenuRepository {

    /**
     * DAO pour accéder aux opérations SQL sur les revenus
     */
    private final RevenuDao revenuDao;

    /**
     * ExecutorService : gère un thread séparé pour les opérations
     * d'écriture dans la base de données
     * newSingleThreadExecutor = un seul thread à la fois
     */
    private final ExecutorService executor;

    /**
     * Constructeur : initialise la base de données et le DAO
     * @param application : contexte nécessaire pour accéder à la base
     */
    public RevenuRepository(Application application) {
        // Récupère l'instance unique de la base de données
        AppDatabase db = AppDatabase.getInstance(application);
        // Récupère le DAO des revenus
        revenuDao = db.revenuDao();
        // Crée un thread séparé pour les opérations d'écriture
        executor = Executors.newSingleThreadExecutor();
    }

    // ─────────────────────────────────────────────────────────
    // OPÉRATIONS D'ÉCRITURE (exécutées dans un thread séparé)
    // ─────────────────────────────────────────────────────────

    /**
     * Insérer un nouveau revenu en base de données
     * Lambda () -> : exécute l'insertion dans le thread séparé
     */
    public void insert(Revenu revenu) {
        executor.execute(() -> revenuDao.insertRevenu(revenu));
    }

    /**
     * Modifier un revenu existant en base de données
     */
    public void update(Revenu revenu) {
        executor.execute(() -> revenuDao.updateRevenu(revenu));
    }

    /**
     * Supprimer un revenu de la base de données
     */
    public void delete(Revenu revenu) {
        executor.execute(() -> revenuDao.deleteRevenu(revenu));
    }

    // ─────────────────────────────────────────────────────────
    // OPÉRATIONS DE LECTURE (LiveData - thread automatique)
    // ─────────────────────────────────────────────────────────

    /**
     * Récupérer tous les revenus
     * LiveData : Room gère automatiquement le thread pour les lectures
     */
    public LiveData<List<Revenu>> getAllRevenus() {
        return revenuDao.getAllRevenus();
    }

    /**
     * Récupérer les revenus d'un mois précis
     * @param mois : numéro du mois (1-12)
     * @param annee : année ex: "2025"
     */
    public LiveData<List<Revenu>> getRevenusByMois(int mois, String annee) {
        return revenuDao.getRevenusByMois(mois, annee);
    }

    /**
     * Calculer le total des revenus d'un mois
     * Utilisé pour le calcul du solde sur le Dashboard
     */
    public LiveData<Double> getTotalRevenusParMois(int mois, String annee) {
        return revenuDao.getTotalRevenusParMois(mois, annee);
    }
}
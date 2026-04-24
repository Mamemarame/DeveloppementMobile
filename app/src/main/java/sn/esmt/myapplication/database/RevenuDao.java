package sn.esmt.myapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import sn.esmt.myapplication.model.Revenu;
import java.util.List;

/**
 * RevenuDao - Data Access Object
 * Interface qui définit toutes les opérations SQL
 * sur la table "revenus".
 * Room génère automatiquement le code d'implémentation.
 */
@Dao
public interface RevenuDao {

    /**
     * Insérer un nouveau revenu dans la base de données
     */
    @Insert
    void insertRevenu(Revenu revenu);

    /**
     * Modifier un revenu existant
     * Room identifie le revenu à modifier grâce à son ID
     */
    @Update
    void updateRevenu(Revenu revenu);

    /**
     * Supprimer un revenu de la base de données
     */
    @Delete
    void deleteRevenu(Revenu revenu);

    /**
     * Récupérer tous les revenus, triés par date décroissante
     * LiveData : la liste se met à jour automatiquement dans l'interface
     */
    @Query("SELECT * FROM revenus ORDER BY date DESC")
    LiveData<List<Revenu>> getAllRevenus();

    /**
     * Récupérer les revenus d'un mois et d'une année donnés
     * Ex: getRevenusByMois(4, "2025") → revenus d'avril 2025
     */
    @Query("SELECT * FROM revenus WHERE " +
            "strftime('%m', date/1000, 'unixepoch') = printf('%02d', :mois) AND " +
            "strftime('%Y', date/1000, 'unixepoch') = :annee " +
            "ORDER BY date DESC")
    LiveData<List<Revenu>> getRevenusByMois(int mois, String annee);

    /**
     * Calculer le total des revenus d'un mois donné
     * Utilisé pour afficher le solde sur le Dashboard
     * COALESCE retourne 0 si aucun revenu n'existe ce mois-là
     */
    @Query("SELECT COALESCE(SUM(montant), 0) FROM revenus WHERE " +
            "strftime('%m', date/1000, 'unixepoch') = printf('%02d', :mois) AND " +
            "strftime('%Y', date/1000, 'unixepoch') = :annee")
    LiveData<Double> getTotalRevenusParMois(int mois, String annee);
}
package sn.esmt.myapplication.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sn.esmt.myapplication.model.Revenu;

/**
 * AppDatabase - Base de données principale de l'application
 *
 * C'est le point d'entrée principal pour accéder à la base de données Room.
 * On utilise le pattern Singleton pour avoir une seule instance
 * de la base de données dans toute l'application.
 *
 * @Database : indique à Room que c'est la classe de base de données
 * entities : liste des tables (ici uniquement Revenu)
 * version : numéro de version de la base (à incrémenter si on modifie la structure)
 */
@Database(entities = {Revenu.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Instance unique de la base de données (Singleton)
     * "volatile" garantit que l'instance est visible par tous les threads
     */
    private static volatile AppDatabase instance;

    /**
     * Méthode abstraite qui retourne le DAO des revenus
     * Room génère automatiquement l'implémentation
     */
    public abstract RevenuDao revenuDao();

    /**
     * Méthode Singleton pour obtenir l'instance de la base de données
     * Si elle n'existe pas encore, on la crée
     * "synchronized" évite les problèmes si plusieurs threads appellent
     * cette méthode en même temps
     *
     * @param context : contexte Android nécessaire pour créer la base
     * @return l'instance unique de AppDatabase
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            // Créer la base de données une seule fois
            instance = Room.databaseBuilder(
                            context.getApplicationContext(), // contexte de l'app
                            AppDatabase.class,               // classe de la base
                            "gestion_depenses_db"            // nom du fichier SQLite
                    )
                    // Si la structure change, détruit et recrée la base
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
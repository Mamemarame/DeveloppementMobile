package sn.esmt.myapplication.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Classe Revenu - Entité Room
 * Représente la table "revenus" dans la base de données SQLite.
 * Chaque objet Revenu correspond à une ligne dans cette table.
 */
@Entity(tableName = "revenus") // Indique à Room que cette classe est une table
public class Revenu {

    /**
     * Identifiant unique du revenu.
     * autoGenerate = true : Room génère automatiquement l'ID (1, 2, 3...)
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Montant du revenu (ex: 150000.0 FCFA)
     * Stocké en double pour supporter les décimales
     */
    @ColumnInfo(name = "montant")
    private double montant;

    /**
     * Source du revenu : Salaire, Commerce, Freelance, Don, Autre
     */
    @ColumnInfo(name = "source")
    private String source;

    /**
     * Date du revenu stockée en timestamp (millisecondes depuis 1970)
     * Ex: System.currentTimeMillis() → 1714000000000
     */
    @ColumnInfo(name = "date")
    private long date;

    /**
     * Description optionnelle du revenu
     * Ex: "Salaire du mois d'avril"
     */
    @ColumnInfo(name = "description")
    private String description;

    /**
     * Date de création de l'enregistrement (timestamp)
     * Permet de savoir quand la donnée a été saisie
     */
    @ColumnInfo(name = "created_at")
    private long createdAt;

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTEUR
    // Room utilise ce constructeur pour créer les objets
    // ─────────────────────────────────────────────────────────
    public Revenu(double montant, String source, long date,
                  String description, long createdAt) {
        this.montant = montant;
        this.source = source;
        this.date = date;
        this.description = description;
        this.createdAt = createdAt;
    }

    // ─────────────────────────────────────────────────────────
    // GETTERS - permettent de lire les valeurs des attributs
    // ─────────────────────────────────────────────────────────
    public int getId() { return id; }
    public double getMontant() { return montant; }
    public String getSource() { return source; }
    public long getDate() { return date; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }

    // ─────────────────────────────────────────────────────────
    // SETTERS - permettent de modifier les valeurs des attributs
    // ─────────────────────────────────────────────────────────
    public void setId(int id) { this.id = id; }
    public void setMontant(double montant) { this.montant = montant; }
    public void setSource(String source) { this.source = source; }
    public void setDate(long date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
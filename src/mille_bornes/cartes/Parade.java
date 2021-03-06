package mille_bornes.cartes;

import mille_bornes.EtatJoueur;
import mille_bornes.Jeu;

public abstract class Parade extends Bataille {
    private static final long serialVersionUID = -1307910562516914625L;

    public Parade(String nom) {
        super("\u001B[92m" + nom + "\u001B[0m", Categorie.PARADE);
    }

    @Override
    public void appliqueEffet(Jeu jeu, EtatJoueur joueur) {
        Bataille bataille = joueur.getBataille();

        // Si le joueur a bien une attaque
        if (bataille instanceof Attaque) {
            if (contre((Attaque) bataille)) {
                joueur.defausseBataille(jeu);
            } else {
                // Sinon on ne peut pas la parer
                throw new IllegalStateException("Cette carte ne peut pas être jouée maintenant");
            }
        } else {
            // On a rien à parer
            throw new IllegalStateException("Cette carte ne peut pas être jouée maintenant");
        }
        joueur.setBataille(this);
    }
}

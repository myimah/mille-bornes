package mille_bornes.cartes;

import mille_bornes.EtatJoueur;
import mille_bornes.Jeu;

public abstract class Botte extends Carte {
    private static final long serialVersionUID = -3439676551653430543L;

    public Botte(String nom) {
        super( "\u001B[32m" + nom + "\u001B[0m", Categorie.BOTTE);
    }

    public abstract boolean contre(Attaque carte);

    @Override
    public void appliqueEffet(Jeu jeu, EtatJoueur joueur) throws IllegalStateException {
        joueur.addBotte(this);
    }
}

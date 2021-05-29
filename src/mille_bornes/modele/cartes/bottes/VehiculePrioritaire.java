package mille_bornes.modele.cartes.bottes;

import mille_bornes.modele.EtatJoueur;
import mille_bornes.modele.Jeu;
import mille_bornes.modele.cartes.Attaque;
import mille_bornes.modele.cartes.Botte;

public class VehiculePrioritaire extends Botte {
    private static final long serialVersionUID = 7199288912826231138L;

    public VehiculePrioritaire() {
        super("Vehicule Prioritaire", "assets/cartes/Prioritaire.jpg");
    }

    @Override
    public boolean contre(Attaque carte) {
        return carte.estContreeParVehiculePrioritaire();
    }

    @Override
    public void appliqueEffet(Jeu jeu, EtatJoueur joueur) throws IllegalStateException {
        super.appliqueEffet(jeu, joueur);
        joueur.setLimiteVitesse(false);
    }
}
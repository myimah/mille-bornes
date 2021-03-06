package mille_bornes.cartes.attaques;

import mille_bornes.cartes.Attaque;

public class PanneEssence extends Attaque {
    private static final long serialVersionUID = 1840603587728231063L;

    public PanneEssence() {
        super("Panne d'essence");
    }

    @Override
    public boolean estContreeParEssence() {
        return true;
    }

    @Override
    public boolean estContreeParCiterne() {
        return true;
    }
}

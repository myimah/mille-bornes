package mille_bornes.vue;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import mille_bornes.controleur.BarreMenu;
import mille_bornes.modele.CoupFourreException;
import mille_bornes.modele.Jeu;
import mille_bornes.modele.Joueur;
import mille_bornes.modele.cartes.Attaque;
import mille_bornes.modele.cartes.Carte;
import mille_bornes.vue.jeu.Sabot;
import mille_bornes.vue.joueur.HJoueurMain;
import mille_bornes.vue.joueur.JoueurMain;
import mille_bornes.vue.joueur.VJoueurMain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MilleBornes {

    private final BorderPane contenu;
    private final VBox vBox;
    private final Sabot sabot;
    private final JoueurMain[] mains = new JoueurMain[4];
    private Jeu jeu;

    public MilleBornes(double width, double height) throws IOException {
        contenu = new BorderPane();
        sabot = new Sabot(null, this);

        FXMLLoader loader = new FXMLLoader(MilleBornes.class.getResource("/fxml/barre-menu.fxml"));
        MenuBar barreMenu = loader.load();
        Object controller = loader.getController();

        if (controller instanceof BarreMenu) {
            ((BarreMenu) controller).setGui(this);
        }

        vBox = new VBox();

        contenu.setBackground(new Background(new BackgroundFill(Color.rgb(158, 251, 144), CornerRadii.EMPTY, Insets.EMPTY)));
        vBox.getChildren().addAll(barreMenu, contenu);

        contenu.setPrefWidth(width);
        contenu.setPrefHeight(height);
        contenu.setPadding(new Insets(5, 5, 5, 5));
        contenu.setCenter(sabot);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public void setJeu(Jeu jeu) {
        setJeu(jeu, false);
    }

    public void setJeu(Jeu jeu, boolean partieChargee) {
        this.jeu = jeu;
        if (!partieChargee) this.jeu.prepareJeu();
        sabot.setJeu(jeu);
        Arrays.fill(mains, null);
        mains[0] = new HJoueurMain(this, jeu.getJoueurs().get(0), true);

        // Selection manuelle des emplacements des joueurs selon le nombre total
        switch (jeu.getNbJoueurs()) {
            case 2:
                mains[2] = new HJoueurMain(this, jeu.getJoueurs().get(1), false, true);
                break;
            case 3:
                mains[1] = new VJoueurMain(this, jeu.getJoueurs().get(1), false, true);
                mains[3] = new VJoueurMain(this, jeu.getJoueurs().get(2), false);
                break;
            case 4:
                mains[1] = new VJoueurMain(this, jeu.getJoueurs().get(1), false, true);
                mains[2] = new HJoueurMain(this, jeu.getJoueurs().get(2), false, true);
                mains[3] = new VJoueurMain(this, jeu.getJoueurs().get(3), false);
                break;
        }

        for (int i = 1; i < mains.length; i++) {
            if (mains[i] != null) mains[i].cacher();
        }

        contenu.setBottom(mains[0]);
        contenu.setRight(mains[1]);
        contenu.setTop(mains[2]);
        contenu.setLeft(mains[3]);
        jeu.activeProchainJoueurEtTireCarte();
        tournerJoueurs();
    }

    public void tournerJoueurs() {
        Joueur joueur = jeu.getJoueurActif();

        mains[0].setJoueur(joueur);

        for (int i = 1; i < mains.length; i++) {
            if (mains[i] == null) continue;
            joueur = joueur.getProchainJoueur();
            mains[i].setJoueur(joueur);
        }
    }

    public Sabot getSabot() {
        return sabot;
    }

    public BorderPane getContenu() {
        return contenu;
    }

    public VBox getHolder() {
        return vBox;
    }

    public void defausseCarte(Carte carte) {
        jeu.getJoueurActif().defausseCarte(jeu, carte);
    }

    public void joueCarte(Carte carte) {
        try {
            if (carte instanceof Attaque) {
                // afficher alert
                Joueur cible = new ChoisitDestination(jeu, (Attaque) carte).getCible();

                if (cible == null) return;

                jeu.getJoueurActif().joueCarte(jeu, carte, cible);
            } else {
                jeu.getJoueurActif().joueCarte(jeu, carte);
            }
            if (jeu.estPartieFinie()) {
                Alert victoire = new Alert(Alert.AlertType.INFORMATION);
                victoire.setTitle("Fin de partie");
                victoire.setHeaderText("Victoire !");
                List<Joueur> gagnants = jeu.getGagnant();
                if (gagnants.size() == 1) {
                    victoire.setContentText(gagnants.get(0).nom + " remporte la partie !");
                } else {
                    victoire.setContentText("Le gagnant de cette partie sont : \n- " + gagnants.stream().map(Joueur::getNom).collect(Collectors.joining("\n- ")));
                }
                victoire.showAndWait();
                return;
            }

            jeu.activeProchainJoueurEtTireCarte();

            Alert changementJoueur = new Alert(Alert.AlertType.INFORMATION);
            changementJoueur.setTitle("Changement de joueur");
            changementJoueur.setHeaderText("");
            changementJoueur.setContentText("C'est au tour de " + jeu.getJoueurActif().nom);
            changementJoueur.showAndWait();
            sabot.update();
            tournerJoueurs();
        } catch (IllegalStateException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText("Vous ne pouvez pas faire cette action");
            error.setContentText(e.getMessage());
            error.showAndWait();
        } catch (CoupFourreException e) {
            Alert coupFourre = new Alert(Alert.AlertType.INFORMATION);
            coupFourre.setTitle("Coup Fourré !");
            coupFourre.setHeaderText("Votre adversaire sort un coup-fourré!");
            coupFourre.setContentText("Votre attaque n'a aucun effet et il récupère la main.");
            coupFourre.showAndWait();
            jeu.activeProchainJoueurEtTireCarte();
            sabot.update();
            tournerJoueurs();
        }
    }
}

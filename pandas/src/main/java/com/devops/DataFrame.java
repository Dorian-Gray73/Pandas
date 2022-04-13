package com.devops;

import java.util.ArrayList;

public class DataFrame {
    ArrayList<String> labels;
    ArrayList<ArrayList<?>> dataframe;
    private Integer nbLigne = 0;



    /**
     * 
     * Génére un DataFrame en prenant une table de String et une table de données
     * 
     * @param label : tableau de String contenant les labels
     * @param table : tableau de tableau contenant les données
     * @throws Exception
     */
    public DataFrame(ArrayList<String> label, ArrayList<ArrayList<?>> tableau) throws Exception{
        if (label.size() != tableau.size()) 
            throw new Exception("Nombre de label incompatible avec le nombre de colonne du tableau");
        labels = new ArrayList<String>(label);

        dataframe = new ArrayList<ArrayList<?>>();
        // Remplir dataFrame en conservant les types d'entrée
        for (int i = 0; i < tableau.size(); i++) {
            ArrayList<?> ligne = (ArrayList<?>) tableau.get(i).clone();
            if (ligne.size() == 1 || ligne.size() == nbLigne || nbLigne == 0) {
                dataframe.add(ligne);
                if (nbLigne != 1){
                    nbLigne = ligne.size();
                }
            } else {
                throw new Exception("Nombre de ligne incoherent (non constant et different de 1)");
            }
        }
    }
    
    /**
    * Fonction qui retourne le nombre de ligne du DataFrame
    * @return le nombre de ligne du DataFrame
    */
	public int nbLigne() {
        return dataframe.get(0).size();
    }

    /**
    * Fonction qui retourne le nombre de colonne du DataFrame
    * @return le nombre de colonne du DataFrame
    */
	public int nbColonne() {
        return labels.size();
    }

    /**
    * Fonction qui retourne le nom de la colonne à l'indice i
    * @param i : indice de la colonne
    * @exception IllegalArgumentException si i est hors de la plage de valeurs
    * @return le nom de la colonne à l'indice i  
     */
	public ArrayList<?> getColumn(String string) throws IllegalArgumentException {
        int i = labels.indexOf(string);
        if(i == -1){
            throw new IllegalArgumentException("La colonne n'existe pas");
        }
        return dataframe.get(labels.indexOf(string));
    }
    /**
    * Fonction qui retourne les 10 premières lignes du DataFrame
    * @return les 10 premières lignes du DataFrame
    */
    public String head() {
        return head(10);
    }

    /**
    * Fonction qui retourne les n premières lignes du DataFrame
    * @param n : nombre de ligne à retourner
    * @return les n premières lignes du DataFrame
    */
    public String head(int nbl) {
        String s = "";
        for (int i = 0; i < labels.size(); i++) {
            s += labels.get(i) + "\t";
        }
        s += "\n";
        int mini = nbLigne < nbl ? nbLigne : nbl; 
        for (int i = 0; i < mini; i++) {
            for (int j = 0; j < dataframe.size(); j++) {
                try{
                s += dataframe.get(j).get(i) + "\t";
                }
                catch(Exception e){
                    s += dataframe.get(j).get(0) + "\t";
                }
            }
            s += "\n";
        }
        return s;
    }

    /**
    * Fonction qui retourne les lignes du DataFrame
    * @return les lignes composant le DataFrame
    */
    public String toString() {
        return head(Integer.MAX_VALUE);
    }

}
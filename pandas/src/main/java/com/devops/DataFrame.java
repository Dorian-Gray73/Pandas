package com.devops;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.TableView.TableRow;

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
    public DataFrame(ArrayList<String> label, ArrayList<ArrayList<?>> tableau) throws Exception {
        if (label.size() != tableau.size())
            throw new Exception("Nombre de label (" + label.size() + ") incompatible avec le nombre de colonne du tableau ( " + tableau.size() + ")");
        labels = new ArrayList<String>(label);

        dataframe = new ArrayList<ArrayList<?>>();
        // Remplir dataFrame en conservant les types d'entrée
        for (int i = 0; i < tableau.size(); i++) {
            ArrayList<?> ligne = (ArrayList<?>) tableau.get(i).clone();
            if (ligne.size() == 1 || ligne.size() == nbLigne || nbLigne == 0) {
                dataframe.add(ligne);
                if (nbLigne != 1) {
                    nbLigne = ligne.size();
                }
            } else {
                throw new Exception("Nombre de ligne incoherent (non constant et different de 1)");
            }
        }
    }

    /**
     * 
     * Génére un DataFrame en utilisant un fichier CSV contenant sur la première
     * ligne les labels
     * des colonnes et les données sur les lignes suivantes
     * 
     * @param fileName : nom du fichier CSV contenant les données à utiliser pour
     *                 générer le DataFrame
     */

    public DataFrame(String fileName) {
        try {
            labels = new ArrayList<String>();
            dataframe = new ArrayList<ArrayList<?>>();
            int nbLabel = 0;
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            String[] line = scanner.nextLine().split(",");
            nbLabel = line.length;
            for (String label : line) {
                labels.add(label);
            }

            // On determine le type sur la première ligne
            line = scanner.nextLine().split(",");
            if (line.length != nbLabel) {
                throw new Exception("la première ligne ne contient pas le bon nombre d'élément");
            }

            for (String input : line) {
                switch (multiType.getType(input)) {
                    case 0:
                        ArrayList<String> stringColonne = new ArrayList<String>();
                        stringColonne.add(input);
                        dataframe.add(stringColonne);
                        break;
                    case 1:
                        ArrayList<Integer> intColonne = new ArrayList<Integer>();
                        intColonne.add(Integer.parseInt(input));
                        dataframe.add(intColonne);
                        break;
                    case 2:
                        ArrayList<Float> floatColonne = new ArrayList<Float>();
                        floatColonne.add(Float.parseFloat(input));
                        dataframe.add(floatColonne);
                        break;
                    case 3:
                        Date date = DateFormat.getDateInstance().parse(input);
                        ArrayList<Date> dateColonne = new ArrayList<Date>();
                        dataframe.add(dateColonne);
                        break;
                    default:
                        break;
                }

            }
            // TODO -----------------------------use multitype to
            // simplify-----------------------
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().split(",");
                if (line.length != nbLabel) {
                    throw new Exception("Une ligne ne contient pas le bon nombre d'élément");
                }
                for (int i = 0; i < line.length; i++) {
                    switch (multiType.getType(line[i])) {
                        case 0:
                            dataframe.get(i).add(line[i]);
                            break;
                        case 1:
                            dataframe.get(i).add(Integer.parseInt(line[i]));
                            break;
                        case 2:
                            dataframe.get(i).add(Float.parseFloat(line[i]));
                            break;
                        case 3:
                            dataframe.get(i).add(DateFormat.getDateInstance().parse(line[i]));
                            break;
                        default:
                            break;
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui retourne le nombre de ligne du DataFrame
     * 
     * @return le nombre de ligne du DataFrame
     */
    public int nbLigne() {
        return dataframe.get(0).size();
    }

    /**
     * Fonction qui retourne le nombre de colonne du DataFrame
     * 
     * @return le nombre de colonne du DataFrame
     */
    public int nbColonne() {
        return labels.size();
    }

    /**
     * Fonction qui retourne le nom de la colonne à l'indice i
     * 
     * @param i : indice de la colonne
     * @exception IllegalArgumentException si i est hors de la plage de valeurs
     * @return le nom de la colonne à l'indice i
     */
    public ArrayList<?> getColumn(String string) throws IllegalArgumentException {
        int i = labels.indexOf(string);
        if (i == -1) {
            throw new IllegalArgumentException("La colonne n'existe pas");
        }
        return dataframe.get(labels.indexOf(string));
    }

    /**
     * Fonction qui retourne les 10 premières lignes du DataFrame
     * 
     * @return les 10 premières lignes du DataFrame
     */
    public String head() {
        return head(10);
    }

    /**
     * Fonction qui retourne les n premières lignes du DataFrame
     * 
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
                try {
                    s += dataframe.get(j).get(i) + "\t";
                } catch (Exception e) {
                    s += dataframe.get(j).get(0) + "\t";
                }
            }
            s += "\n";
        }
        return s;
    }

    /**
     * Fonction qui retourne les lignes du DataFrame
     * 
     * @return les lignes composant le DataFrame
     */
    public String toString() {
        return head(Integer.MAX_VALUE);
    }

    /**
     * Fonction qui retourne la moyenne d'une colonne d'un dataframe
    * @param string : nom de la colonne
    * @exception IllegalArgumentException si la colonne n'existe pas
    * @exception IllegalArgumentException si la colonne n'est pas un nombre
    * @return la moyenne d'une colonne d'un dataframe
    */
    public int moyenne(String string) throws IllegalArgumentException {
        int i = labels.indexOf(string);
        if (i == -1) {
            throw new IllegalArgumentException("La colonne n'existe pas");
        }
        
        if (!(dataframe.get(i).get(0) instanceof Integer || dataframe.get(i).get(0) instanceof Float) || dataframe.get(i).get(0) instanceof Double) {
            throw new IllegalArgumentException("La colonne n'est pas de type numérique");
        } 

        int sum = 0;
        int nb = 0;
        for (int j = 0; j < dataframe.get(i).size(); j++) {
            sum += (int) dataframe.get(i).get(j);
            nb++;
        }
        return sum / nb;
    }
    /**
    * Fonction qui retourne la moyenne d'une colonne d'un dataframe
    * @param string : nom de la colonne
    * @exception IllegalArgumentException si la colonne n'existe pas
    * @exception IllegalArgumentException si la colonne n'est pas de type numérique
    * @return la moyenne d'une colonne d'un dataframe
    */
    public int max(String string) throws IllegalArgumentException {
        int i = labels.indexOf(string);
        if (i == -1) {
            throw new IllegalArgumentException("La colonne n'existe pas");
        }
        
        if (!(dataframe.get(i).get(0) instanceof Integer || dataframe.get(i).get(0) instanceof Float) || dataframe.get(i).get(0) instanceof Double) {
            throw new IllegalArgumentException("La colonne n'est pas de type numérique");
        }

        int max = (int) dataframe.get(i).get(0);
        for (int j = 0; j < dataframe.get(i).size(); j++) {
            if ((int) dataframe.get(i).get(j) > max) {
                max = (int) dataframe.get(i).get(j);
            }
        }
        return max;
    }







































    
    static public class multiType {
        private int type;
        private String strValue;
        private int intValue;
        private float floatValue;
        private Date dateValue;

        public multiType(String input) {
            type = getType(input);
            switch (type) {
                case 0:
                    strValue = input;
                    break;
                case 1:
                    intValue = Integer.parseInt(input);
                    break;
                case 2:
                    floatValue = Float.parseFloat(input);
                    break;
                case 3:
                    dateValue = DateFormat.getDateInstance().parse(input);
                    break;
                default:
                    break;
            }
        }

        static private int getType(String input) {
            String intRegex = "\\d+";
            String floatRegex = "[+-]?(\\d+([.]\\d*)?|[.]\\d+)";
            String dateRegex = "\\d\\d/\\d\\d/\\d\\d\\d\\d";
            Pattern intPattern = Pattern.compile(intRegex);
            Pattern floatPattern = Pattern.compile(floatRegex);
            Pattern datePattern = Pattern.compile(dateRegex);
            Matcher intMatcher = intPattern.matcher(input);
            Matcher floatMatcher = floatPattern.matcher(input);
            Matcher dateMatcher = datePattern.matcher(input);
            if (dateMatcher.matches()) {
                return 3;
            } else {
                if (floatMatcher.matches()) {
                    return 2;
                } else {
                    if (intMatcher.matches()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }

        public int getMultiType() {
            return type;
        }

        public String getStr() {
            return strValue;
        }

        public int getInt() {
            return intValue;
        }

        public float getFloat() {
            return floatValue;
        }

        public Date getDate() {
            return dateValue;
        }
    }
}
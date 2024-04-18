import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static Perceptron[] perceptronLayer;
    private static String[] languageNames;

    public static void main(String[] args) {

        File[] folders = new File("dane").listFiles();
        int languageCount = folders.length;

        ArrayList<Double[]>[] charProportions = new ArrayList[languageCount];
        languageNames = new String[languageCount];
        int nameIndex = 0;

        int[] v = null;
        double[] proportionV = null;
        int totalCharCount;

        ArrayList<Double[]> languageProportions = null;
        Double[] proportions = null;
        for(File folder : folders) {
            languageProportions = new ArrayList<>();
            languageNames[nameIndex] = folder.getName();
            for (File file : folder.listFiles()) {
                v = new int[26];
                totalCharCount = 0;
                try (FileInputStream fis = new FileInputStream(file)) {
                    int tmp;
                    while ((tmp = fis.read()) > 0) {
                        totalCharCount++;
                        if (tmp >= 'A' && tmp <= 'Z')
                            tmp += 32;
                        if (tmp >= 'a' && tmp <= 'z')
                            v[tmp - 97] += 1;
                    }
                    proportions = new Double[v.length];
                    for(int i = 0; i < v.length; i++)
                        proportions[i] = (double) v[i] / totalCharCount;
                    languageProportions.add(proportions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            charProportions[nameIndex++] = languageProportions;
        }

        perceptronLayer = new Perceptron[languageCount];
        for(int i = 0; i < perceptronLayer.length; i++)
            perceptronLayer[i] = new Perceptron(0, new double[26]);

        boolean trainingDone = false;

        while (!trainingDone) {
            trainingDone = true;
            for (int i = 0; i < languageNames.length; i++) {
                for (Double[] p : charProportions[i])
                    for (int j = 0; j < perceptronLayer.length; j++)
                        if(!perceptronLayer[j].train(p, (i == j ? 1 : 0), 1))
                           trainingDone = false;
            }
        }

        File languageTestFolder;
        int total = 0, correct = 0;
        boolean correctLanguageHasActivated, wrongLanguageHasActivated, classificationResult;
        for (int i = 0; i < languageNames.length; i++) {
            languageTestFolder = new File("daneTestowe/" + languageNames[i]);
            System.out.println("Test języka: " + languageNames[i]);
            for(File testFile : languageTestFolder.listFiles()) {
                if (testFile != null) {
                    total++;
                    proportions = getCharProportions(testFile);
                    correctLanguageHasActivated = false;
                    wrongLanguageHasActivated = false;
                    System.out.println("\tPrzypadek #" + total + " plik: " + testFile.getName());
                    for (int j = 0; j < perceptronLayer.length; j++) { //aktywacja więcej niż jednego języka to błędna klasyfikacja -
                        classificationResult = perceptronLayer[j].classify(proportions);
                        if (classificationResult && i == j) {
                            System.out.println("\t\tPoprawna aktywacja perceptronu " + languageNames[j]);
                            correctLanguageHasActivated = true;
                        } else if (classificationResult) {
                            System.out.println("\t\tNiepoprawna aktywacja perceptronu " + languageNames[j]);
                            wrongLanguageHasActivated = true;
                        }
                    }
                    if (correctLanguageHasActivated && !wrongLanguageHasActivated)
                        correct++;
                    else if (!correctLanguageHasActivated && !wrongLanguageHasActivated)
                        System.out.println("\t\tŻaden perceptron nie został aktywowany");
                }
            }
        }

        System.out.println("Wszystkich: " + total + " Poprawnych: " + correct);
        System.out.println("Poprawność dla zbioru testowego: " + Math.round((double) correct / total * 10000) / 100d + "%");

        SwingUtilities.invokeLater(new Window());
    }

    private static Double[] getCharProportions(File file){
        int[] v = new int[26];
        int totalCharCount = 0;
        Double[] proportions = new Double[v.length];
        try (FileInputStream fis = new FileInputStream(file)) {
            int tmp;
            while ((tmp = fis.read()) > 0) {
                totalCharCount++;
                if (tmp >= 'A' && tmp <= 'Z')
                    tmp += 32;
                if (tmp >= 'a' && tmp <= 'z')
                    v[tmp - 97] += 1;
            }
            for (int i = 0; i < v.length; i++) {
                proportions[i] = (double) v[i] / totalCharCount;
            }
            return proportions;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void classify(String text) {
        int[] charCount = new int[26];
        int totalCharCount = 0;
        for(char c : text.toCharArray()) {
            totalCharCount++;
            if (c >= 'A' && c <= 'Z')
                c += 32;
            if (c >= 'a' && c <= 'z')
                charCount[c - 97] += 1;
        }
        Double[] proportions = new Double[charCount.length];
        for (int i = 0; i < charCount.length; i++)
            proportions[i] = (double) charCount[i] / totalCharCount;

        int count = 0;
        System.out.print("Klasyfikacja: ");
        for(int i = 0; i < perceptronLayer.length; i++)
            if(perceptronLayer[i].classify(proportions)) {
                System.out.print(languageNames[i] + " ");
                count++;
            }
        if(count == 0 || count > 1)
            System.out.print("- brak jednoznacznego wyniku");
        System.out.println();
    }
}
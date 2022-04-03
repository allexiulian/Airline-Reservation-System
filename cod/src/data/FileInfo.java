package data;

import java.io.*;

public class FileInfo {
    private static final String RESOURCE_FOLDER = "resources";
    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";

    public static BufferedWriter createWriter(){
        File outputFile = createOutputFile();
        try {
            return new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "Nu s-a putut realiza legatura cu outputfile";
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private static File createOutputFile(){
        File resourceFolder = new File(RESOURCE_FOLDER);
        if(!resourceFolder.exists()){
            resourceFolder.mkdir();
        }
        File outputFile = new File(resourceFolder, OUTPUT_FILE);
        if(!outputFile.exists()){
            System.out.println("Se creeaza fisierul output.txt......");
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputFile;
    }

    public static BufferedReader createReader(){
        File inputFile = createInputFile();
        try {
            return new BufferedReader(new FileReader(inputFile));
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "Nu s-a putut realiza legatura cu inputfile";
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private static File createInputFile(){
        File resourceFolder = new File(RESOURCE_FOLDER);
        if(!resourceFolder.exists()){
            resourceFolder.mkdir();
        }
        File inputFile = new File(resourceFolder, INPUT_FILE);
        if(!inputFile.exists()){
            System.out.println("Se creeaza fisierul input.txt......");
            try {
                inputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputFile;
    }
}

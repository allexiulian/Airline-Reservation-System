package logic;

import data.FileInfo;

import java.io.BufferedReader;
import java.io.IOException;

public class ReaderManager {
    private final BufferedReader bufferedReader;

    public ReaderManager() {
        bufferedReader = FileInfo.createReader();
    }

    public String readLine(){
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "Nu s-a putut face citirea";
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}

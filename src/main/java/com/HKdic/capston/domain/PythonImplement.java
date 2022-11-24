package com.HKdic.capston.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.HKdic.capston.controller.SpringUploadController.carInformations;
import static com.HKdic.capston.domain.DIR.*;

/* Class For Implement Python File */

public class PythonImplement {

    public static String nameOfCar = "";
    public static ArrayList<String> nameOfCars = new ArrayList<>();

    public Process makeProcess(String command, String pythonFile, String arg1) throws IOException {
        return (new ProcessBuilder(command, pythonFile, arg1)).start();
    }

    public Process makeProcess(String command, String pythonFile, String arg1, String arg2) throws IOException {
        return (new ProcessBuilder(command, pythonFile, arg1, arg2)).start();
    }

    /**
     * Obtain Car's name from image by Machine Learning
     * In python
     * parameter : (command, python File, Image File)
     * return : String (name of Car from image)
     */

    public void implementML() throws Exception{
        Process process = makeProcess(PYTHON_DIR.getVal(), PYTHON_ML_DIR.getVal(), UPLOADED_IMG_DIR.getVal());
        getCarName(process);
    }

    public void getCarName(Process process) throws Exception {
        int exitVal = process.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "euc-kr"));

        String result;
        int i=0;
        while((result=br.readLine()) != null){
            System.out.println("result = " + result);
            nameOfCars.add(result);
            if(i==3) break;
            i++;
        }

        System.out.println("끝남.");

    }

    /**
     * Obtain car information by Crawling
     * In python
     * parameter : (command, python File, nameOfCarFromImage)
     * return : Car's Basic Information
     */

    public void implementCrawling() throws Exception{
        if(nameOfCars.size() == 0) {
            return;
        }
        for (String carName : nameOfCars) {
            Process process = makeProcess(PYTHON_DIR.getVal(), PYTHON_CRAWLING_DIR.getVal(), carName, PYTHON_IMAGE_DIR.getVal());
            carInformations.add(getCarInformation(process));
        }
    }

    public CarInformation getCarInformation(Process process) throws Exception{
        int exitVal = process.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "euc-kr"));

        String result;
        ArrayList<String> infos = new ArrayList<>();
        int i=0;
        while((result=br.readLine()) != null){
            System.out.println("result = " + result);
            infos.add(result);
            if(i==6) break;
            i++;
        }

        System.out.println("infos = " + infos);

        if(exitVal != 0) return null; //비정상 종료
        return new CarInformation(infos);
    }

}

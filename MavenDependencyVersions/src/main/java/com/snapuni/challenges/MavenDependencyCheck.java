package com.snapuni.challenges;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MavenDependencyCheck {
    public static void main(String[] args) {
        FileWriter csvWriter = null;
        try {

            StringBuffer output = new StringBuffer();
            Scanner s = new Scanner(System.in);
            System.out.println("Enter the location of the project to check");
            String command = s.next();
            Runtime rt = Runtime.getRuntime();
            List<String> list = getDependencyList(rt, command);
            getDependencyLatestVersions(rt, command, list, csvWriter);
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<String> getDependencyList(Runtime rt , String projectLoc) throws IOException {
        Process pr = rt.exec("/usr/bin/mvn -f "+projectLoc+
                "/pom.xml dependency:list -DexcludeTransitive");
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        boolean foundDependency = false;
        List<String> list = new ArrayList<String>();
        while ((line = reader1.readLine()) != null) {
            if (foundDependency) {
                String[] arr = line.split(":");
                if (arr.length > 1) {
                    String[] a1 = arr[0].split(" ");
                    list.add(a1[a1.length-1] + ":" + arr[1]);
                }

            }
            if (line.contains("The following files have been resolved")){
                foundDependency = true;
            }
        }
        return list;
    }

    private static void getDependencyLatestVersions(Runtime rt, String projectLoc, List<String> dependencyList, FileWriter csvWriter) throws IOException {
        String line = "";
        boolean foundDependency = false;
        boolean readNextLine = false;
        boolean ignoreNextLine = false;
        Process pr = rt.exec("/usr/bin/mvn -f "+projectLoc+
                "/pom.xml org.codehaus.mojo:versions-maven-plugin:2.7:display-dependency-updates");
        String[] st = projectLoc.split("/");
        String fileName = st[st.length-1];

        BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((line = reader.readLine()) != null) {
            System.out.println("Command Output: " + line);
            if (foundDependency && csvWriter != null) {
                String[] arr = line.split(" ");
                if (readNextLine) {
                    arr = line.split("->");
                    String[] oldVersArr = arr[0].split(" ");
                    csvWriter.append(oldVersArr[oldVersArr.length - 1] + "," + arr[1]);
                    csvWriter.append("\n");
                    readNextLine = false;
                } else {
                    if (arr.length > 4 && !readNextLine && dependencyList.contains(arr[3])) {
                        csvWriter.append(arr[3] + ",");
                        if (arr.length > 5) {
                            csvWriter.append(arr[5] + "," + arr[7]);
                            csvWriter.append("\n");
                        } else {
                            readNextLine = true;
                        }
                    }
                }
            }
            if (line.contains("Dependency Management have newer versions")){
                foundDependency = true;
                csvWriter = new FileWriter(fileName+"_dependencyUpdate.csv");
                csvWriter.append("Dependency Name");
                csvWriter.append(",");
                csvWriter.append("Existing Version");
                csvWriter.append(",");
                csvWriter.append("New Version");
                csvWriter.append("\n");
            }
        }
        csvWriter.close();
    }
}

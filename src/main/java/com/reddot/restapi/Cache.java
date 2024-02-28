package com.reddot.restapi;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.time.LocalDate;
import java.util.Scanner;

public class Cache {
  public boolean isExist(String functionName, String param) {
    LocalDate date = LocalDate.now();
    File dir = new File("src/main/java/com/reddot/restapi/cache/");
    String filename = functionName + "_" + param + "_" + date + ".txt";
    File[] matches = dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.equals(filename);
      }
    });

    if (matches.length > 0)
      return true;

    return false;
  }

  public void write(String functionName, String param, String content) {
    String path = "src/main/java/com/reddot/restapi/cache/";

    LocalDate today = LocalDate.now();
    try {
      FileWriter myWriter = new FileWriter(path + functionName + "_" + param + "_" + today + ".txt");
      myWriter.write(content);
      myWriter.close();

      System.out.println("Successfully wrote to the file.");
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println(e.getMessage());
    }
  }

  public String read(String functionName, String param) {
    String path = "src/main/java/com/reddot/restapi/cache/";
    LocalDate date = LocalDate.now();
    String filename = functionName + "_" + param + "_" + date + ".txt";
    File myObjReader = new File(path + filename);

    String result = "";
    try {
      Scanner myReader = new Scanner(myObjReader);
      while (myReader.hasNextLine()) {
        result = result + myReader.nextLine();
        // System.out.println(data);
      }
      myReader.close();
    } catch (Exception e) {
      result = null;
      System.out.println(e.getMessage());
    }

    return result;
  }
}

package com.form;

// Requires Gson — add to your build file:
// Maven:  <dependency><groupId>com.google.code.gson</groupId><artifactId>gson</artifactId><version>2.10.1</version></dependency>
// Gradle: implementation 'com.google.code.gson:gson:2.10.1'

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResumeManager {

    // Pretty-print JSON so saved files are human-readable
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Saves a ResumeData object to a JSON file
    public static void saveToJson(ResumeData data, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            System.out.println("Failed to save resume: " + e.getMessage());
        }
    }

    // Loads a ResumeData object from a JSON file
    // Returns null if the file cannot be read or parsed
    public static ResumeData loadFromJson(File file) {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, ResumeData.class);
        } catch (Exception e) {
            System.out.println("Failed to load resume: " + e.getMessage());
            return null;
        }
    }

}
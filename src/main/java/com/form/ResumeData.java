package com.form;

import java.util.ArrayList;
import java.util.List;

// Plain data model for a full resume — holds all section data
// Used for JSON save/load via ResumeManager
public class ResumeData {

    String name = "";
    String contact = "";
    List<String> skills = new ArrayList<>();
    List<JobEntry> workExperience = new ArrayList<>();
    List<JobEntry> volunteerExperience = new ArrayList<>();
    List<String> certifications = new ArrayList<>();
    List<String> awards = new ArrayList<>();
    List<String> education = new ArrayList<>();

}
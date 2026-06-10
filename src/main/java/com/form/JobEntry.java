package com.form;

import java.util.ArrayList;
import java.util.List;

// Data model for a single job or volunteer entry
// Dates are stored as ISO strings (yyyy-MM-dd) from LocalDate.toString()
public class JobEntry {

    String jobTitle = "";
    String employer = "";
    String startDate = "";  // ISO date string, e.g. "2022-06-01"
    String endDate = "";    // ISO date string, empty if isPresent is true
    boolean isPresent = false;
    List<String> bullets = new ArrayList<>();

}
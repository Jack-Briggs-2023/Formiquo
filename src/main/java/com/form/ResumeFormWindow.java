package com.form;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class ResumeFormWindow implements StartWindow {

    private File currentFile; // tracks where the resume was last saved; null = never saved

    // --- Instance references to form nodes (needed by collectFormData and populateForm) ---
    private TextField nameField;
    private TextField contactField;
    private VBox skillsBulletList;
    private VBox workEntriesList;
    private VBox volunteerEntriesList;
    private VBox certsBulletList;
    private VBox awardsBulletList;
    private VBox educationBulletList;

    public ResumeFormWindow(File resumeFile) {
        this.currentFile = resumeFile; // may be null (new resume) or a file to load from
    }

    @Override
    public Scene buildScene() {
        BorderPane root = new BorderPane();
        Assets.mainWindowRoot = root; // Update shared root reference
        Assets.setFont(root, "/com/form/RobotoFonts/Roboto_Condensed-medium.ttf", 14);

        // --- Top: navigation bar ---
        HBox navBar = new HBox(12);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(12, 16, 12, 16));

        Button backButton = new Button("← Back");
        backButton.setOnAction(event -> {
            WindowManager.switchTo(new MainMenuWindow()); // Return to main menu
        });

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS); // Pushes right-side buttons to the far right

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            if (currentFile == null) {
                // No save location yet — ask the user where to save
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Resume");
                fileChooser.setInitialFileName("resume.json");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Resume Files (*.json)", "*.json")
                );
                File chosenFile = fileChooser.showSaveDialog(WindowManager.getStage());
                if (chosenFile != null) {
                    currentFile = chosenFile; // Remember location for future saves
                }
            }
            if (currentFile != null) {
                ResumeManager.saveToJson(collectFormData(), currentFile); // Collect and save
            }
        });

        Button exportButton = new Button("Export PDF");
        exportButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Resume as PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
            );
            // Pre-fill the file name with the user's name if available
            String resumeName = nameField.getText().trim();
            fileChooser.setInitialFileName(resumeName.isEmpty() ? "resume.pdf" : resumeName + " - Resume.pdf");
            File pdfFile = fileChooser.showSaveDialog(WindowManager.getStage());
            if (pdfFile != null) {
                PdfExporter.export(collectFormData(), pdfFile); // Collect form data and export
            }
        });

        navBar.getChildren().addAll(backButton, navSpacer, saveButton, exportButton);
        root.setTop(navBar);

        // --- Initialize instance VBoxes for bullet lists and entry lists ---
        skillsBulletList = new VBox(6);
        workEntriesList = new VBox(16);
        volunteerEntriesList = new VBox(16);
        certsBulletList = new VBox(6);
        awardsBulletList = new VBox(6);
        educationBulletList = new VBox(6);

        // --- Center: scrollable form with all resume sections ---
        VBox formContent = new VBox(24); // 24px gap between sections
        formContent.setPadding(new Insets(20, 40, 40, 40));

        formContent.getChildren().addAll(
            buildHeaderSection(),
            new Separator(),
            buildBulletSection("SKILLS", skillsBulletList),
            new Separator(),
            buildEntrySection("WORK EXPERIENCE", workEntriesList),
            new Separator(),
            buildEntrySection("VOLUNTEER EXPERIENCE", volunteerEntriesList),
            new Separator(),
            buildBulletSection("TRAINING / CERTIFICATIONS", certsBulletList),
            new Separator(),
            buildBulletSection("AWARDS", awardsBulletList),
            new Separator(),
            buildBulletSection("EDUCATION", educationBulletList)
        );

        ScrollPane scrollPane = new ScrollPane(formContent);
        scrollPane.setFitToWidth(true); // Stretches form content to fill scroll pane width
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // No horizontal scrollbar
        root.setCenter(scrollPane);

        // If a file was passed in, load its data into the form
        if (currentFile != null) {
            ResumeData loadedData = ResumeManager.loadFromJson(currentFile);
            if (loadedData != null) {
                populateForm(loadedData);
            }
        }

        return new Scene(root, Assets.windowWidth, Assets.windowHeight);
    }

    // Builds the resume header section (name and contact info, centred)
    private VBox buildHeaderSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setAlignment(Pos.CENTER);
        nameField.setMaxWidth(320);

        contactField = new TextField();
        contactField.setPromptText("Phone Number / Email");
        contactField.setAlignment(Pos.CENTER);
        contactField.setMaxWidth(320);

        section.getChildren().addAll(nameField, contactField);
        return section;
    }

    // Builds a titled section around a pre-created bullet list VBox
    // The bulletList VBox is stored as an instance variable so collectFormData can read it
    private VBox buildBulletSection(String sectionTitle, VBox bulletList) {
        VBox section = new VBox(8);

        Text titleText = new Text(sectionTitle);

        bulletList.getChildren().add(buildBulletRow(bulletList)); // Start with one empty row

        Button addItemButton = new Button("+ Add Item");
        addItemButton.setOnAction(event -> {
            bulletList.getChildren().add(buildBulletRow(bulletList)); // Append a new bullet row
        });

        section.getChildren().addAll(titleText, bulletList, addItemButton);
        return section;
    }

    // Builds a single bullet row — a text field with a remove button beside it
    private HBox buildBulletRow(VBox parentList) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        TextField bulletField = new TextField();
        bulletField.setPromptText("Enter item...");
        HBox.setHgrow(bulletField, Priority.ALWAYS); // Field stretches to fill available width

        Button removeButton = new Button("✕");
        removeButton.setOnAction(event -> {
            parentList.getChildren().remove(row); // Remove this row from the bullet list
        });

        row.getChildren().addAll(bulletField, removeButton);
        return row;
    }

    // Builds a titled section around a pre-created entries list VBox
    // The entriesList VBox is stored as an instance variable so collectFormData can read it
    private VBox buildEntrySection(String sectionTitle, VBox entriesList) {
        VBox section = new VBox(12);

        Text titleText = new Text(sectionTitle);

        entriesList.getChildren().add(buildEntryBlock(entriesList)); // Start with one empty entry

        Button addEntryButton = new Button("+ Add Entry");
        addEntryButton.setOnAction(event -> {
            entriesList.getChildren().add(buildEntryBlock(entriesList)); // Append a new entry block
        });

        section.getChildren().addAll(titleText, entriesList, addEntryButton);
        return section;
    }

    // Builds a single job/volunteer entry block with title, employer, dates, and bullet points
    // Child order is fixed: [0] titleRow, [1] dateRow, [2] bulletList, [3] addBulletButton, [4] removeEntryButton
    // collectEntries and populateEntries rely on this order
    private VBox buildEntryBlock(VBox parentList) {
        VBox entry = new VBox(8);
        entry.setPadding(new Insets(12));
        entry.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 4;");

        // -- [0] Job title and employer row --
        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");
        HBox.setHgrow(jobTitleField, Priority.ALWAYS);

        Text dashText = new Text("—");

        TextField employerField = new TextField();
        employerField.setPromptText("Employer");
        HBox.setHgrow(employerField, Priority.ALWAYS);

        titleRow.getChildren().addAll(jobTitleField, dashText, employerField); // [0]=jobTitle [1]=dash [2]=employer

        // -- [1] Date range row --
        HBox dateRow = new HBox(8);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        Text dateDashText = new Text("—");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        CheckBox presentCheckBox = new CheckBox("Present");
        presentCheckBox.setOnAction(event -> {
            endDatePicker.setDisable(presentCheckBox.isSelected()); // Disable end date when "Present" is checked
        });

        dateRow.getChildren().addAll(startDatePicker, dateDashText, endDatePicker, presentCheckBox); // [0]=start [1]=dash [2]=end [3]=present

        // -- [2] Bullet points for this entry --
        VBox bulletList = new VBox(6);
        bulletList.getChildren().add(buildBulletRow(bulletList)); // Start with one empty bullet

        // -- [3] Add bullet button --
        Button addBulletButton = new Button("+ Add Bullet");
        addBulletButton.setOnAction(event -> {
            bulletList.getChildren().add(buildBulletRow(bulletList)); // Append a new bullet row
        });

        // -- [4] Remove this entire entry --
        Button removeEntryButton = new Button("Remove Entry");
        removeEntryButton.setOnAction(event -> {
            parentList.getChildren().remove(entry); // Remove this block from the entries list
        });

        entry.getChildren().addAll(titleRow, dateRow, bulletList, addBulletButton, removeEntryButton);
        return entry;
    }

    // --- Data collection ---

    // Reads all form fields and returns a ResumeData object
    private ResumeData collectFormData() {
        ResumeData data = new ResumeData();
        data.name = nameField.getText().trim();
        data.contact = contactField.getText().trim();
        data.skills = collectBullets(skillsBulletList);
        data.workExperience = collectEntries(workEntriesList);
        data.volunteerExperience = collectEntries(volunteerEntriesList);
        data.certifications = collectBullets(certsBulletList);
        data.awards = collectBullets(awardsBulletList);
        data.education = collectBullets(educationBulletList);
        return data;
    }

    // Reads all non-empty text fields from a bullet list VBox
    private List<String> collectBullets(VBox bulletList) {
        List<String> items = new ArrayList<>();
        for (var node : bulletList.getChildren()) {
            if (node instanceof HBox) {
                TextField field = (TextField) ((HBox) node).getChildren().get(0); // TextField is always index 0
                String value = field.getText().trim();
                if (!value.isEmpty()) {
                    items.add(value);
                }
            }
        }
        return items;
    }

    // Reads all entry blocks from an entries list VBox
    private List<JobEntry> collectEntries(VBox entriesList) {
        List<JobEntry> entries = new ArrayList<>();
        for (var node : entriesList.getChildren()) {
            if (node instanceof VBox) {
                VBox block = (VBox) node;
                JobEntry entry = new JobEntry();

                // Title row: [0]=jobTitle [1]=dash [2]=employer
                HBox titleRow = (HBox) block.getChildren().get(0);
                entry.jobTitle = ((TextField) titleRow.getChildren().get(0)).getText().trim();
                entry.employer = ((TextField) titleRow.getChildren().get(2)).getText().trim();

                // Date row: [0]=startDate [1]=dash [2]=endDate [3]=presentCheckBox
                HBox dateRow = (HBox) block.getChildren().get(1);
                DatePicker startPicker = (DatePicker) dateRow.getChildren().get(0);
                DatePicker endPicker = (DatePicker) dateRow.getChildren().get(2);
                CheckBox presentBox = (CheckBox) dateRow.getChildren().get(3);

                entry.startDate = startPicker.getValue() != null ? startPicker.getValue().toString() : "";
                entry.isPresent = presentBox.isSelected();
                entry.endDate = entry.isPresent ? "" : (endPicker.getValue() != null ? endPicker.getValue().toString() : "");

                // Bullet list is at index 2
                VBox bulletList = (VBox) block.getChildren().get(2);
                entry.bullets = collectBullets(bulletList);

                entries.add(entry);
            }
        }
        return entries;
    }

    // --- Form population (used when loading a saved resume) ---

    // Fills all form fields with data from a loaded ResumeData object
    private void populateForm(ResumeData data) {
        nameField.setText(data.name);
        contactField.setText(data.contact);
        populateBullets(skillsBulletList, data.skills);
        populateEntries(workEntriesList, data.workExperience);
        populateEntries(volunteerEntriesList, data.volunteerExperience);
        populateBullets(certsBulletList, data.certifications);
        populateBullets(awardsBulletList, data.awards);
        populateBullets(educationBulletList, data.education);
    }

    // Clears a bullet list VBox and fills it with the given string values
    private void populateBullets(VBox bulletList, List<String> items) {
        bulletList.getChildren().clear();
        for (String item : items) {
            HBox row = buildBulletRow(bulletList);
            ((TextField) row.getChildren().get(0)).setText(item); // TextField is always index 0
            bulletList.getChildren().add(row);
        }
        if (bulletList.getChildren().isEmpty()) {
            bulletList.getChildren().add(buildBulletRow(bulletList)); // Always leave at least one empty row
        }
    }

    // Clears an entries list VBox and fills it with the given JobEntry values
    private void populateEntries(VBox entriesList, List<JobEntry> entries) {
        entriesList.getChildren().clear();
        for (JobEntry entry : entries) {
            VBox block = buildEntryBlock(entriesList);

            // Title row: [0]=jobTitle [1]=dash [2]=employer
            HBox titleRow = (HBox) block.getChildren().get(0);
            ((TextField) titleRow.getChildren().get(0)).setText(entry.jobTitle);
            ((TextField) titleRow.getChildren().get(2)).setText(entry.employer);

            // Date row: [0]=startDate [1]=dash [2]=endDate [3]=presentCheckBox
            HBox dateRow = (HBox) block.getChildren().get(1);
            DatePicker startPicker = (DatePicker) dateRow.getChildren().get(0);
            DatePicker endPicker = (DatePicker) dateRow.getChildren().get(2);
            CheckBox presentBox = (CheckBox) dateRow.getChildren().get(3);

            if (entry.startDate != null && !entry.startDate.isEmpty()) {
                startPicker.setValue(LocalDate.parse(entry.startDate));
            }
            presentBox.setSelected(entry.isPresent);
            endPicker.setDisable(entry.isPresent);
            if (!entry.isPresent && entry.endDate != null && !entry.endDate.isEmpty()) {
                endPicker.setValue(LocalDate.parse(entry.endDate));
            }

            // Bullet list is at index 2
            VBox bulletList = (VBox) block.getChildren().get(2);
            populateBullets(bulletList, entry.bullets);

            entriesList.getChildren().add(block);
        }
        if (entriesList.getChildren().isEmpty()) {
            entriesList.getChildren().add(buildEntryBlock(entriesList)); // Always leave at least one empty entry
        }
    }

    @Override
    public void onShow() {
        WindowManager.getStage().setTitle("Formiquo - Resume Editor");
    }

}
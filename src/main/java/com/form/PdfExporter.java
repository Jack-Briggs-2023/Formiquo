package com.form;

// Requires Apache PDFBox — add to your build file:
// Maven:  <dependency><groupId>org.apache.pdfbox</groupId><artifactId>pdfbox</artifactId><version>2.0.29</version></dependency>
// Gradle: implementation 'org.apache.pdfbox:pdfbox:2.0.29'

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PdfExporter {

    // --- Page setup (Letter: 8.5" x 11" = 612 x 792 points) ---
    private static final float PAGE_WIDTH    = 612;
    private static final float PAGE_HEIGHT   = 792;
    private static final float MARGIN_LEFT   = 60;
    private static final float MARGIN_RIGHT  = 60;
    private static final float MARGIN_TOP    = 50;
    private static final float MARGIN_BOTTOM = 50;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;

    // --- Font sizes ---
    private static final float NAME_SIZE           = 22;
    private static final float CONTACT_SIZE        = 11;
    private static final float SECTION_HEADER_SIZE = 12;
    private static final float ENTRY_TITLE_SIZE    = 11;
    private static final float DATE_SIZE           = 10;
    private static final float BODY_SIZE           = 11;

    // --- Spacing ---
    private static final float LINE_HEIGHT   = 16;
    private static final float SECTION_GAP   = 14; // space after each section
    private static final float ENTRY_GAP     = 10; // space after each job/volunteer entry
    private static final float BULLET_INDENT = 16; // indent for bullet items

    // --- Fonts (PDFBox 2.x Type1 constants) ---
    private static final PDFont BOLD        = PDType1Font.HELVETICA_BOLD;
    private static final PDFont REGULAR     = PDType1Font.HELVETICA;
    private static final PDFont ITALIC      = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDFont BOLD_ITALIC = PDType1Font.HELVETICA_BOLD_OBLIQUE;

    // --- Per-export instance state ---
    private PDDocument doc;
    private PDPageContentStream cs;  // content stream for the current page
    private float currentY;          // tracks vertical position on the page (decreases as we draw downward)

    // Entry point — creates a new PdfExporter instance and runs the export
    public static void export(ResumeData data, File outputFile) {
        new PdfExporter().generate(data, outputFile);
    }

    private void generate(ResumeData data, File outputFile) {
        try {
            doc = new PDDocument();
            startNewPage();

            drawTopRule();
            drawHeader(data.name, data.contact);

            if (!data.skills.isEmpty())             drawBulletSection("SKILLS", data.skills);
            if (!data.workExperience.isEmpty())      drawEntrySection("WORK EXPERIENCE", data.workExperience);
            if (!data.volunteerExperience.isEmpty()) drawEntrySection("VOLUNTEER EXPERIENCE", data.volunteerExperience);
            if (!data.certifications.isEmpty())      drawBulletSection("TRAINING / CERTIFICATIONS", data.certifications);
            if (!data.awards.isEmpty())              drawBulletSection("AWARDS", data.awards);
            if (!data.education.isEmpty())           drawBulletSection("EDUCATION", data.education);

            cs.close();
            doc.save(outputFile);
            doc.close();
            System.out.println("PDF exported: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("PDF export failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Page management
    // -------------------------------------------------------------------------

    // Closes the current content stream and starts a fresh page
    private void startNewPage() throws IOException {
        if (cs != null) cs.close();
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);
        cs = new PDPageContentStream(doc, page);
        currentY = PAGE_HEIGHT - MARGIN_TOP;
    }

    // Starts a new page if the next block won't fit in the remaining space
    // This is the core of the no-split guarantee — called before every block is drawn
    private void checkPageBreak(float blockHeight) throws IOException {
        if (currentY - blockHeight < MARGIN_BOTTOM) {
            startNewPage();
        }
    }

    private void moveDown(float amount) {
        currentY -= amount;
    }

    // -------------------------------------------------------------------------
    // Text helpers
    // -------------------------------------------------------------------------

    // Base method — draws text at a specific (x, y) position
    private void drawTextAt(String text, PDFont font, float size, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(clean(text));
        cs.endText();
    }

    // Draws text at (x, currentY)
    private void drawText(String text, PDFont font, float size, float x) throws IOException {
        drawTextAt(text, font, size, x, currentY);
    }

    // Draws text centred horizontally on the page at currentY
    private void drawCentered(String text, PDFont font, float size) throws IOException {
        float textWidth = font.getStringWidth(clean(text)) / 1000f * size;
        drawText(text, font, size, (PAGE_WIDTH - textWidth) / 2f);
    }

    // Draws text with a thin underline at currentY (used for section headers)
    private void drawUnderlined(String text, PDFont font, float size, float x) throws IOException {
        drawText(text, font, size, x);
        float textWidth = font.getStringWidth(clean(text)) / 1000f * size;
        cs.setLineWidth(0.75f);
        cs.moveTo(x, currentY - 2);
        cs.lineTo(x + textWidth, currentY - 2);
        cs.stroke();
    }

    // Draws text with word-wrapping if it exceeds maxWidth
    // Calls moveDown internally for each wrapped line except the last; caller handles the final moveDown
    private void drawWrapped(String text, PDFont font, float size, float x, float maxWidth) throws IOException {
        String[] words = clean(text).split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            float candidateWidth = font.getStringWidth(candidate) / 1000f * size;
            if (candidateWidth > maxWidth && line.length() > 0) {
                drawText(line.toString(), font, size, x);
                moveDown(LINE_HEIGHT);
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            drawText(line.toString(), font, size, x); // last (or only) line — caller moves down
        }
    }

    // Strips characters outside the WinAnsiEncoding range used by PDType1Font
    private String clean(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c < 0x20) continue; // skip control characters
            sb.append(c <= 0xFF ? c : '?'); // replace non-Latin-1 characters
        }
        return sb.toString();
    }

    // Formats an ISO date string (yyyy-MM-dd) as "MMM yyyy" (e.g. "Jun 2022")
    private String formatDate(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            return LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("MMM yyyy"));
        } catch (Exception e) {
            return iso; // return as-is if parsing fails
        }
    }

    // -------------------------------------------------------------------------
    // Height estimation — runs before drawing to decide if a new page is needed
    // -------------------------------------------------------------------------

    private float estimateBulletSectionHeight(List<String> items) {
        return (LINE_HEIGHT * 1.4f)        // section title line
             + 4                           // gap after title
             + (items.size() * LINE_HEIGHT) // one line per item
             + SECTION_GAP;
    }

    private float estimateEntryBlockHeight(JobEntry entry) {
        return LINE_HEIGHT                         // job title / employer line
             + LINE_HEIGHT                         // date line
             + (entry.bullets.size() * LINE_HEIGHT) // one line per bullet
             + ENTRY_GAP;
    }

    // -------------------------------------------------------------------------
    // Section drawing
    // -------------------------------------------------------------------------

    // Draws the thick horizontal rule that appears at the top of every page in the template
    private void drawTopRule() throws IOException {
        cs.setLineWidth(3f);
        cs.moveTo(MARGIN_LEFT, currentY);
        cs.lineTo(PAGE_WIDTH - MARGIN_RIGHT, currentY);
        cs.stroke();
        moveDown(10); // space between rule and content below it
    }

    // Draws the resume header: large bold name + italic contact line, both centred
    private void drawHeader(String name, String contact) throws IOException {
        float headerHeight = (LINE_HEIGHT * 0.5f) + (LINE_HEIGHT * 1.6f) + LINE_HEIGHT + SECTION_GAP;
        checkPageBreak(headerHeight);

        moveDown(LINE_HEIGHT * 0.5f); // breathing room below the rule

        drawCentered(name, BOLD, NAME_SIZE);
        moveDown(LINE_HEIGHT * 1.6f);

        drawCentered(contact, ITALIC, CONTACT_SIZE);
        moveDown(LINE_HEIGHT + SECTION_GAP);
    }

    // Draws a bullet-list section (Skills, Certifications, Awards, Education)
    // The entire section is kept on one page — no splitting
    private void drawBulletSection(String title, List<String> items) throws IOException {
        checkPageBreak(estimateBulletSectionHeight(items)); // keep whole section together

        drawUnderlined(title, BOLD, SECTION_HEADER_SIZE, MARGIN_LEFT);
        moveDown(LINE_HEIGHT * 1.4f);

        for (String item : items) {
            drawWrapped("- " + item, REGULAR, BODY_SIZE, MARGIN_LEFT + BULLET_INDENT, CONTENT_WIDTH - BULLET_INDENT);
            moveDown(LINE_HEIGHT);
        }

        moveDown(SECTION_GAP);
    }

    // Draws a job-entry section (Work Experience, Volunteer Experience)
    // Section title is kept with the first entry; each subsequent entry is kept whole on one page
    private void drawEntrySection(String title, List<JobEntry> entries) throws IOException {
        float firstBlockHeight = entries.isEmpty() ? 0 : estimateEntryBlockHeight(entries.get(0));
        checkPageBreak((LINE_HEIGHT * 1.4f) + firstBlockHeight); // keep title with first entry

        drawUnderlined(title, BOLD, SECTION_HEADER_SIZE, MARGIN_LEFT);
        moveDown(LINE_HEIGHT * 1.4f);

        for (JobEntry entry : entries) {
            checkPageBreak(estimateEntryBlockHeight(entry)); // keep each entry block on one page
            drawEntryBlock(entry);
        }

        moveDown(SECTION_GAP);
    }

    // Draws a single job or volunteer entry block:
    // "JOB TITLE (bold) - EMPLOYER (bold italic)" on one line, then date range, then bullets
    private void drawEntryBlock(JobEntry entry) throws IOException {
        // -- Title line: job title (bold) + " - " + employer (bold italic) --
        float titleX = MARGIN_LEFT;
        float titleWidth = BOLD.getStringWidth(clean(entry.jobTitle)) / 1000f * ENTRY_TITLE_SIZE;
        drawTextAt(entry.jobTitle, BOLD, ENTRY_TITLE_SIZE, titleX, currentY);

        String dash = " - ";
        float dashX = titleX + titleWidth;
        float dashWidth = BOLD.getStringWidth(dash) / 1000f * ENTRY_TITLE_SIZE;
        drawTextAt(dash, BOLD, ENTRY_TITLE_SIZE, dashX, currentY);

        drawTextAt(entry.employer, BOLD_ITALIC, ENTRY_TITLE_SIZE, dashX + dashWidth, currentY);
        moveDown(LINE_HEIGHT);

        // -- Date range line --
        String startStr = formatDate(entry.startDate);
        String endStr   = entry.isPresent ? "Present" : formatDate(entry.endDate);
        String dateStr  = startStr.isEmpty() ? endStr : startStr + " - " + endStr;
        drawText(dateStr, REGULAR, DATE_SIZE, MARGIN_LEFT);
        moveDown(LINE_HEIGHT);

        // -- Bullet points --
        for (String bullet : entry.bullets) {
            drawWrapped("- " + bullet, REGULAR, BODY_SIZE, MARGIN_LEFT + BULLET_INDENT, CONTENT_WIDTH - BULLET_INDENT);
            moveDown(LINE_HEIGHT);
        }

        moveDown(ENTRY_GAP);
    }

}
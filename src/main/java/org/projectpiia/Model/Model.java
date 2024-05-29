package org.projectpiia.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

/**
 * Model class of the application
 */
public class Model {
    private String text1;
    private String text2;
    private String path1;
    private String path2;
    private final AtomicBoolean isText1Loaded = new AtomicBoolean(false);
    private final AtomicBoolean isText2Loaded = new AtomicBoolean(false);

    // Load the first file
    public void loadFile1() throws IOException {
        if (!path1.equals("")) {
            text1 = new String(Files.readAllBytes(Paths.get(path1)));
            if (text1.length() > 10000) {
                throw new IllegalStateException("File size exceeds 10,000 characters");
            }
            isText1Loaded.set(true);
        }
    }

    // Load the second file
    public void loadFile2() throws IOException {
        if (!path2.equals("")) {
            text2 = new String(Files.readAllBytes(Paths.get(path2)));
            isText2Loaded.set(true);
        }
    }

    // Generate diff text
    public String diffText() throws IOException {
        List<String> lines1 = Arrays.asList(text1.split("\\R"));
        List<String> lines2 = Arrays.asList(text2.split("\\R"));

        List<DiffRow> diffRows = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "")
                .newTag(f -> "")
                .build()
                .generateDiffRows(lines1, lines2);

        StringBuilder diffText = new StringBuilder();
        for (DiffRow row : diffRows) {
            if (row.getTag() == DiffRow.Tag.CHANGE) {
                diffText.append("🔄").append(row.getNewLine()).append("\n");
            } else if (row.getTag() == DiffRow.Tag.DELETE) {
                diffText.append("➖").append(row.getOldLine()).append("\n");
            } else if (row.getTag() == DiffRow.Tag.INSERT) {
                diffText.append("➕").append(row.getNewLine()).append("\n");
            } else {
                diffText.append(row.getOldLine()).append("\n");
            }
        }
        return diffText.toString();
    }

    // Generate modifications for the diff
    public List<Modification> generateModifications() throws IOException {
        List<String> lines1 = Arrays.asList(text1.split("\\R"));
        List<String> lines2 = Arrays.asList(text2.split("\\R"));

        List<DiffRow> diffRows = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "")
                .newTag(f -> "")
                .build()
                .generateDiffRows(lines1, lines2);

        List<Modification> modifications = new ArrayList<>();
        for (int i = 0; i < diffRows.size(); i++) {
            DiffRow row = diffRows.get(i);
            if (row.getTag() == DiffRow.Tag.DELETE) {
                modifications.add(new Modification(row.getOldLine(), "", i, Modification.Type.DELETE));
            } else if (row.getTag() == DiffRow.Tag.INSERT) {
                modifications.add(new Modification("", row.getNewLine(), i, Modification.Type.INSERT));
            } else if (row.getTag() == DiffRow.Tag.CHANGE) {
                modifications.add(new Modification(row.getOldLine(), row.getNewLine(), i, Modification.Type.REPLACE));
            }
        }
        return modifications;
    }

    // Getters

    public String getText1() {
        return text1;
    }

    public boolean isText1Loaded() {
        return isText1Loaded.get();
    }

    public boolean isText2Loaded() {
        return isText2Loaded.get();
    }

    // Setters

    public void setPath1(String path1) {
        this.path1 = path1;
    }

    public void setPath2(String path2) {
        this.path2 = path2;
    }
}

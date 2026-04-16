import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Reads the contest results CSV and splits it into two normalized files:
 * one for institutions and one for teams.
 *
 * <p>The program assigns each distinct institution a numeric ID and then uses
 * that ID as the link between the two output files.</p>
 */
public class Split2015 {

    /**
     * Program entry point.
     *
     * @param args command-line arguments, not used
     * @throws IOException if the input file cannot be read or the output files
     *                     cannot be written
     */
    static void main(String[] args) throws IOException {
        System.out.println("Starting CSV split...");

        // Run this from ChallengeProblem5 folder so the paths work.
        String inputFile = "files/2015.csv";
        String institutionFile = "files/Institutions.csv";
        String teamFile = "files/Teams.csv";

        // Open the input file and also set up the two output files.
        BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), StandardCharsets.UTF_8);
        BufferedWriter instWriter = Files.newBufferedWriter(Paths.get(institutionFile), StandardCharsets.UTF_8);
        BufferedWriter teamWriter = Files.newBufferedWriter(Paths.get(teamFile), StandardCharsets.UTF_8);

        // Read the first line so we can deal with the header.
        String line = reader.readLine();
        if (line == null) {
            reader.close();
            instWriter.close();
            teamWriter.close();
            return;
        }

        // The source file has weird text in the first header, so clean that up.
        String[] header = parseLine(line);
        if (header.length > 0) {
            header[0] = header[0].replace("\uFEFF", "").replace("ï»¿", "");
        }

        // These are the column positions in the CSV file.
        int instIndex = 0;
        int teamIndex = 1;
        int cityIndex = 2;
        int stateIndex = 3;
        int countryIndex = 4;
        int advisorIndex = 5;
        int problemIndex = 6;
        int rankingIndex = 7;

        ArrayList<String[]> institutionRows = new ArrayList<String[]>();
        ArrayList<String[]> teamRows = new ArrayList<String[]>();
        HashMap<String, Integer> institutionIds = new HashMap<String, Integer>();

        // Go through every row and separate the institution data from the team data.
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }

            // Pull out the row values and give each new institution a fresh ID.
            String[] parts = parseLine(line);
            if (parts.length < 8) {
                continue;
            }

            // Grab the columns we need for the two new files.
            String institution = parts[instIndex].trim();
            String city = parts[cityIndex].trim();
            String state = parts[stateIndex].trim();
            String country = parts[countryIndex].trim();

            // If this institution is new, add it to the list with the next ID.
            Integer id = institutionIds.get(institution);
            if (id == null) {
                id = institutionIds.size() + 1;
                institutionIds.put(institution, id);
                institutionRows.add(new String[]{
                        String.valueOf(id),
                        institution,
                        city,
                        state,
                        country
                });
            }

            // Every team row points back to the institution ID we just found.
            teamRows.add(new String[]{
                    parts[teamIndex].trim(),
                    parts[advisorIndex].trim(),
                    parts[problemIndex].trim(),
                    parts[rankingIndex].trim(),
                    String.valueOf(id)
            });
        }

        // Write the institution file first.
        instWriter.write("Institution ID,Institution Name,City,State/Province,Country");
        instWriter.newLine();
        for (String[] row : institutionRows) {
            writeRow(instWriter, row);
        }

        // Then write the team file using the institution IDs.
        teamWriter.write("Team Number,Advisor,Problem,Ranking,Institution ID");
        teamWriter.newLine();
        for (String[] row : teamRows) {
            writeRow(teamWriter, row);
        }

        reader.close();
        instWriter.close();
        teamWriter.close();

        System.out.println("Done. Created " + institutionFile + " and " + teamFile + ".");
    }

    /**
     * Splits one CSV line into fields.
     *
     * <p>This is a small parser that handles quoted values, including commas
     * inside quotes and doubled quote characters.</p>
     *
     * @param line one line from the CSV file
     * @return the parsed fields for that line
     */
    private static String[] parseLine(String line) {
        ArrayList<String> values = new ArrayList<String>();
        String current = "";
        boolean inQuotes = false;

        // Walk through the line one character at a time and build each field.
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                // If we see double quotes inside a quoted field, keep one quote.
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current += '"';
                    i++;
                } else {
                    // Otherwise this quote just switches us in or out of quotes.
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Commas only split fields when we are not inside quotes.
                values.add(current);
                current = "";
            } else {
                // Normal character, just add it to the current field.
                current += c;
            }
        }

        // Add the last field after the loop ends.
        values.add(current);
        return values.toArray(new String[0]);
    }

    /**
     * Writes a single CSV row to the given writer.
     *
     * @param writer the output writer
     * @param row    the fields to write
     * @throws IOException if the row cannot be written
     */
    private static void writeRow(BufferedWriter writer, String[] row) throws IOException {
        // Write each field and put commas between them.
        for (int i = 0; i < row.length; i++) {
            if (i > 0) {
                writer.write(",");
            }
            writer.write(escape(row[i]));
        }
        writer.newLine();
    }

    /**
     * Escapes a field so it is safe to write as CSV.
     *
     * @param value the raw field value
     * @return the escaped CSV field
     */
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        // Quote fields when they have commas or quotes in them.
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

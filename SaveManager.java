/*
 * Authors: Rahsun and Mohammed
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class SaveManager {
    private static final Path SAVES_DIR = Paths.get("saves");

    public static void ensureSavesDir() throws IOException {
        if (Files.notExists(SAVES_DIR)) {
            Files.createDirectories(SAVES_DIR);
        }
    }

    public static void save(String filename, Map<String, String> state) throws IOException {
        ensureSavesDir();
        if (!filename.endsWith(".txt")) filename += ".txt";
        Path file = SAVES_DIR.resolve(filename);

        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write("# Dead End save file");
            w.newLine();
            for (Map.Entry<String, String> e : state.entrySet()) {
                w.write(escape(e.getKey()));
                w.write("=");
                w.write(escape(e.getValue()));
                w.newLine();
            }
        }
    }

    public static Map<String, String> load(String filename) throws IOException {
        ensureSavesDir();
        if (!filename.endsWith(".txt")) filename += ".txt";
        Path file = SAVES_DIR.resolve(filename);

        if (Files.notExists(file)) {
            throw new FileNotFoundException("Save file not found: " + file.toString());
        }

        Map<String, String> result = new LinkedHashMap<>();
        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = indexOfUnescapedEquals(line);
                if (idx < 0) continue; // ignore malformed lines
                String rawKey = line.substring(0, idx);
                String rawVal = line.substring(idx + 1);
                String key = unescape(rawKey);
                String val = unescape(rawVal);
                result.put(key, val);
            }
        }
        return result;
    }

    public static List<String> listSaves() throws IOException {
        ensureSavesDir();
        List<String> out = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(SAVES_DIR, "*.txt")) {
            for (Path p : ds) {
                out.add(p.getFileName().toString().replaceAll("\\.txt$", ""));
            }
        }
        return out;
    }

    public static boolean exists(String filename) throws IOException {
        ensureSavesDir();
        if (!filename.endsWith(".txt")) filename += ".txt";
        return Files.exists(SAVES_DIR.resolve(filename));
    }

    public static boolean delete(String filename) throws IOException {
        ensureSavesDir();
        if (!filename.endsWith(".txt")) filename += ".txt";
        Path file = SAVES_DIR.resolve(filename);
        return Files.deleteIfExists(file);
    }

    private static int indexOfUnescapedEquals(String s) {
        boolean escaped = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && !escaped) {
                escaped = true;
                continue;
            }
            if (c == '=' && !escaped) return i;
            escaped = false;
        }
        return -1;
    }

    // Basic escaping for \, =, newline, carriage return
    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(Math.max(16, s.length()));
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '=': sb.append("\\="); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(Math.max(16, s.length()));
        boolean escaped = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (escaped) {
                switch (c) {
                    case '\\': sb.append('\\'); break;
                    case '=': sb.append('='); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    default: sb.append(c);
                }
                escaped = false;
            } else {
                if (c == '\\') {
                    escaped = true;
                } else {
                    sb.append(c);
                }
            }
        }
        if (escaped) sb.append('\\');
        return sb.toString();
    }
}
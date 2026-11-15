package Patterns.Facade;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import Patterns.Observer.ProgressSnapshot;
import Patterns.Observer.WordProgress;


public final class FileProgressRepository implements ProgressRepository {
    private final Path file;
    public FileProgressRepository(Path file) { this.file = file.toAbsolutePath(); }
    public Path file() { return file; }

    public ProgressSnapshot load() throws IOException {
        if (!Files.exists(file)) return ProgressSnapshot.empty();
        var properties = new Properties();
        try (var input = Files.newInputStream(file)) { properties.load(input); }
        catch (IllegalArgumentException invalid) { throw new IOException("Invalid progress format at " + file, invalid); }
        try {
            if (!"1".equals(properties.getProperty("version"))) {
                throw new IllegalArgumentException("Unsupported progress format.");
            }
            int xp = Integer.parseInt(properties.getProperty("xp", "0"));
            int lessons = Integer.parseInt(properties.getProperty("lessons", "0"));
            if (xp < 0 || lessons < 0) throw new IllegalArgumentException("Negative totals.");
            var words = new HashMap<String, WordProgress>();
            var days = new HashSet<LocalDate>();
            for (String key : properties.stringPropertyNames()) {
                if (key.startsWith("word.")) {
                    String[] fields = properties.getProperty(key).split(",", -1);
                    if (fields.length != 6) throw new IllegalArgumentException("Invalid word entry.");
                    words.put(key.substring(5), new WordProgress(Integer.parseInt(fields[0]),
                            Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]),
                            LocalDate.parse(fields[4]), fields[5].isEmpty() ? null : LocalDate.parse(fields[5])));
                } else if (key.startsWith("day.")) days.add(LocalDate.parse(key.substring(4)));
            }
            return new ProgressSnapshot(words, xp, lessons, days);
        } catch (RuntimeException failure) {
            throw new IOException("Cannot read progress at " + file + ". The file was kept unchanged.", failure);
        }
    }

    public void save(ProgressSnapshot snapshot) throws IOException {
        Files.createDirectories(file.getParent());
        var properties = new Properties();
        properties.setProperty("version", "1");
        properties.setProperty("xp", Integer.toString(snapshot.xp()));
        properties.setProperty("lessons", Integer.toString(snapshot.completedLessons()));
        snapshot.studyDays().forEach(day -> properties.setProperty("day." + day, "1"));
        snapshot.words().forEach((id, p) -> properties.setProperty("word." + id,
                p.attempts() + "," + p.correct() + "," + p.streak() + "," + p.intervalDays()
                        + "," + p.due() + "," + (p.lastReviewed() == null ? "" : p.lastReviewed())));
        Path temporary = Files.createTempFile(file.getParent(), "progress-", ".tmp");
        try {
            try (var output = Files.newOutputStream(temporary)) {
                properties.store(output, "LinguaLab progress / format 1");
            }
            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporary); }
    }
}

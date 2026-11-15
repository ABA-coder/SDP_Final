package Patterns.Facade;

import java.io.IOException;
import java.nio.file.Path;
import Patterns.Observer.ProgressSnapshot;


public interface ProgressRepository {
    ProgressSnapshot load() throws IOException;
    void save(ProgressSnapshot snapshot) throws IOException;
    Path file();
}

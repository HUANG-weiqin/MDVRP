package vnbfs_optimizer.model;

import java.util.List;

public interface Voisinable {
    <T> List<T> getAllVoisin(int type);

    int distance(Voisinable other);
}

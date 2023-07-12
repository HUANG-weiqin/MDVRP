package vnbfs_optimizer.model;

import java.util.List;

public interface Voisinable {
    List<? extends  Voisinable> getAllVoisin();
    List<? extends  Voisinable> getRandomVoisin(int nb);

    int distance(Voisinable other);
}

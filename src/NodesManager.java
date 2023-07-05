import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NodesManager {
    public String fileName;
    public List<ClientNode> clients= new ArrayList<>();
    public List<DepotNode> depots= new ArrayList<>();

    public int nbClients;
    public int nbDepots;

    public Set<Integer> carCapacity = new HashSet<>();

    private String[] readLine(Scanner scanner){
        return scanner.nextLine().trim().split("\\s+");
    }

    public NodesManager(String fileName) {
        this.fileName = fileName;
        File file = new File(fileName);

        try {
            Scanner scanner = new Scanner(file);

            String[] head = readLine(scanner);
            nbClients = Integer.parseInt(head[2]);
            nbDepots = Integer.parseInt(head[3]);
            for(int i=0;i<nbDepots;++i){
                String[] numbers = readLine(scanner);
                int capacity = Integer.parseInt(numbers[1]);
                carCapacity.add(capacity);
            }

            for(int i=0;i<nbClients;++i){
                String[] numbers = readLine(scanner);
                int id = Integer.parseInt(numbers[0]);
                float x = Float.parseFloat(numbers[1]);
                float y = Float.parseFloat(numbers[2]);
                int demand = Integer.parseInt(numbers[4]);
                clients.add(new ClientNode(id,x,y,demand));
            }

            for(int i=0;i<nbDepots;++i){
                String[] numbers = readLine(scanner);
                int id = Integer.parseInt(numbers[0]);
                float x = Float.parseFloat(numbers[1]);
                float y = Float.parseFloat(numbers[2]);
                depots.add(new DepotNode(id,x,y));
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String res = "";
        res += "nb-clients: " + clients.size() + "\n";
        res += "nb-depots: " + depots.size() + "\n";
        res += "car-capacity: " + carCapacity.toString() + "\n";

        return res;
    }
}

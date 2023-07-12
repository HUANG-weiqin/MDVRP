import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NodesManager {
    public String fileName;
    public List<ClientNode> clients= new ArrayList<>();
    public List<DepotNode> depots= new ArrayList<>();

    public Set<ClientNode> boderlineClients = new HashSet<>();
    public Map<ClientNode,DepotNode> nonBoderlineClientsInitDepot = new Hashtable<>();

    public int nbClients;
    public int nbDepots;

    public List<Integer> carCapacity = new ArrayList<>();

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
                if(! carCapacity.contains(capacity))
                    carCapacity.add(capacity);
            }

            Collections.sort(carCapacity);

            for(int i=0;i<nbClients;++i){
                String[] numbers = readLine(scanner);
                int id = Integer.parseInt(numbers[0]);
                double x = Double.parseDouble(numbers[1]);
                double y = Double.parseDouble(numbers[2]);
                int demand = Integer.parseInt(numbers[4]);
                clients.add(new ClientNode(id,x,y,demand));
            }

            for(int i=0;i<nbDepots;++i){
                String[] numbers = readLine(scanner);
                int id = Integer.parseInt(numbers[0]);
                double x = Double.parseDouble(numbers[1]);
                double y = Double.parseDouble(numbers[2]);
                depots.add(new DepotNode(id,x,y));
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        updateBoderlineClients();
    }

    private void updateBoderlineClients(){
        for (ClientNode client: clients) {
            DepotNode depot = Algo.BorderLineTest(client,depots);
            if (depot == null){
                boderlineClients.add(client);
            }else {
                nonBoderlineClientsInitDepot.put(client,depot);
            }
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

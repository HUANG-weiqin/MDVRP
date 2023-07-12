public class ClientNode extends Point {
    int demand;
    public ClientNode(int id, double x, double y, int demand) {
        super(id, x, y);
        this.demand = demand;
    }
}

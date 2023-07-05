public class ClientNode extends Point{
    int demand;
    public ClientNode(int id, float x, float y, int demand) {
        super(id, x, y);
        this.demand = demand;
    }
}

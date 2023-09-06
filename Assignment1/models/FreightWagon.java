package models;
// TODO
public class FreightWagon extends Wagon {

    public int maxWeight;

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public FreightWagon(int wagonId, int maxWeight) {
        // TODO
        super(wagonId);
        this.maxWeight = maxWeight;
    }

    public int getMaxWeight() {
        // TODO
        return 0;
    }
}

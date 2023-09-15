package models;
// TODO
public class PassengerWagon extends Wagon{

    public int numberOfSeats;

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public PassengerWagon(int wagonId, int numberOfSeats) {
        // TODO
        super(wagonId);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {

        return numberOfSeats;
    }
}

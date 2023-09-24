package models;

public class Train {
    public final String origin;
    public final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Indicates whether the train has at least one connected Wagon
     *
     * @return
     */
    public boolean hasWagons() {
        return this.getFirstWagon() != null; // If it has a first wagon so if not null it will return true, else it will return false.
    }

    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     *
     * @return
     */
    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon; // If first wagon is an instance of a passenger wagon it will return true, else it will return false.
    }

    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     *
     * @return
     */
    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon; // If first wagon is an instance of a freight wagon it will return true, else it will return false.
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon; // First wagon will be set to given wagon
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        Wagon currentWagon = firstWagon; // Initializes a variable as first wagon
        int counter = 0;
        while (currentWagon != null) { // Loop until there are no more wagons
            currentWagon = currentWagon.getNextWagon(); // Sets current wagon as its follow-up
            counter++; // Adds 1 to the counter
        }
        return counter; // Returns the number of wagons
    }


    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = firstWagon; // Initializes a variable as first wagon
        while (currentWagon != null && currentWagon.hasNextWagon()) { // Loops till current wagon has no next wagon (so till it reaches last wagon)
            currentWagon = currentWagon.getNextWagon(); // Sets current wagon as its follow up
        }
        return currentWagon; // Returns the wagon where the loop breaks
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int numberOfSeats = 0;
        if (isPassengerTrain()) {
            PassengerWagon currentWagon = (PassengerWagon) firstWagon; // Initializes a variable as first wagon
            while (currentWagon != null) { // Loops till current wagon has no next wagon (so till it reaches last wagon)
                numberOfSeats = numberOfSeats + currentWagon.getNumberOfSeats(); // Adds the number of seats of the current wagon to the total
                currentWagon = (PassengerWagon) currentWagon.getNextWagon(); // Sets current wagon as its follow up
            }
            return numberOfSeats;
        }
        return 0;   // returns 0 if train is a freight wagon
    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        int weight = 0;
        if (isFreightTrain()) {
            FreightWagon currentWagon = (FreightWagon) firstWagon; // Initializes a variable as first wagon
            while (currentWagon != null) { // Loops till current wagon has no next wagon (so till it reaches last wagon)
                weight = weight + currentWagon.getMaxWeight(); // Adds the max weight of the current wagon to the total
                currentWagon = (FreightWagon) currentWagon.getNextWagon(); // Sets current wagon as its follow up
            }
            return weight;
        }
        return 0;   // returns 0 if train is a passenger wagon
    }

    /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     *
     * @param position
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon currentWagon = firstWagon;
        int currentPosition = 0;

        // checks if the given position is within the valid range,
        // meaning it's non-negative and less than or equal to the total number of wagons.
        // If the position is valid, it proceeds to find the wagon.
        if (currentWagon != null) {
            if (position >= 0 && position <= this.getNumberOfWagons()) {
                while (currentPosition != position) {

                    currentWagon = currentWagon.getNextWagon();
                    currentPosition++;
                }
                return currentWagon;
            }
        }
        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon currentWagon = firstWagon;
        int currentPosition = 0;

        while (currentWagon != null && currentPosition < this.getNumberOfWagons()) {
            if (currentWagon.id == wagonId) {
                return currentWagon;
            }
            currentWagon = currentWagon.getNextWagon();
            currentPosition++;
        }
        return null;
    }


    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     *
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {

        Wagon currentWagon = firstWagon;

        while (currentWagon != null) {
            if (currentWagon.equals(wagon)) {
                return false;
            }
            currentWagon = currentWagon.getNextWagon();
        }

        if (isFreightTrain() && !(wagon instanceof FreightWagon)) {

            return false;
        } else if (isPassengerTrain() && !(wagon instanceof PassengerWagon)) {

            return false;
        }

        int totalWagons = getNumberOfWagons() + wagon.getSequenceLength();
        int maxAllowedWagons = engine.getMaxWagons();

        if (totalWagons > maxAllowedWagons) {
            return false;
        }

        return true;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {

        Wagon lastWagon = getLastWagonAttached();

        int totalWagons = getNumberOfWagons() + wagon.getSequenceLength();
        int maxAllowedWagons = engine.getMaxWagons();

        //
        if (totalWagons > maxAllowedWagons) {
            return false;
        }

        if (wagon.hasPreviousWagon()) {
            wagon.detachFront();
        }

        // If there are existing wagons in the train:
        // Find the last wagon in the current sequence.
        // Connect the new wagon to the last wagon.
        // If there are no existing wagons, set the new wagon as the first wagon.
        if (lastWagon != null) {
            while (lastWagon.hasNextWagon()) {
                lastWagon = lastWagon.getNextWagon();
            }
            lastWagon.setNextWagon(wagon);
            wagon.setPreviousWagon(lastWagon);
        } else {
            firstWagon = wagon;
        }


        return true;

    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {


        int totalWagons = getNumberOfWagons() + wagon.getSequenceLength();
        int maxAllowedWagons = engine.getMaxWagons();

        //
        if (totalWagons > maxAllowedWagons) {
            return false;
        }


        wagon.detachFront();

        if (firstWagon != null) {
            if (firstWagon.equals(wagon)) {
                return false;
            }
            firstWagon.setPreviousWagon(wagon.getLastWagonAttached());
        }


        wagon.getLastWagonAttached().setNextWagon(firstWagon);

        firstWagon = wagon;


        return true;

    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     * after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     * or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     *
     * @param position the position where the head wagon and its successors shall be inserted
     *                 0 <= position <= numWagons
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon    the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {

        int totalWagons = getNumberOfWagons() + wagon.getSequenceLength();
        int maxAllowedWagons = engine.getMaxWagons();

        //
        if (totalWagons > maxAllowedWagons || position < 0 || position > totalWagons) {
            return false;
        }

        Wagon currentWagon = findWagonAtPosition(position);



        wagon.detachFront();

        if (position == 0) {
            if (firstWagon != null) {
                currentWagon.detachFront();
                wagon.attachTail(currentWagon);

            }
            firstWagon = wagon;
        } else {

            if (currentWagon == null) {
                currentWagon = wagon;
                firstWagon.getLastWagonAttached().attachTail(currentWagon);
            } else {
                Wagon prevWagon = currentWagon.getPreviousWagon();

                currentWagon.detachFront();
                prevWagon.attachTail(wagon);
                wagon.getLastWagonAttached().attachTail(currentWagon);
            }



        }



        return true;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param wagonId the id of the wagon to be removed
     * @param toTrain the train to which the wagon shall be attached
     *                toTrain shall be different from this train
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        Wagon currentWagon = findWagonById(wagonId);
        Wagon nextWagon = currentWagon.getNextWagon();
        Wagon prevWagon = currentWagon.getPreviousWagon();

        int totalWagons = currentWagon.getSequenceLength();
        int maxTotalWagons = engine.getMaxWagons();


        if (totalWagons > maxTotalWagons || findWagonById(wagonId) == null) {
            return false;
        }

        if (toTrain.isPassengerTrain() && (currentWagon instanceof FreightWagon)) {
            return false;
        } else if (toTrain.isFreightTrain() && (currentWagon instanceof PassengerWagon)) {
            return false;
        }


        currentWagon.detachFront();
        currentWagon.detachTail();

        if (firstWagon.equals(currentWagon)) {
            firstWagon = nextWagon;
        } else {
            if (nextWagon == null) {
                firstWagon.getLastWagonAttached().setId(prevWagon.getId());


            } else {
                firstWagon.setNextWagon(nextWagon);
                firstWagon.getNextWagon().setPreviousWagon(prevWagon);
            }

        }


        if (toTrain.firstWagon == null) {
            toTrain.firstWagon = currentWagon;
        } else {
            toTrain.getLastWagonAttached().setNextWagon(currentWagon);
        }


        return true;
    }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param position 0 <= position < numWagons
     * @param toTrain  the train to which the split sequence shall be attached
     *                 toTrain shall be different from this train
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {

        Wagon wagonAtPosition = findWagonAtPosition(position);


        if (firstWagon == null) {
            return false;
        }

        int totalWagons =  firstWagon.getSequenceLength();
        int maxTotalWagons = toTrain.engine.getMaxWagons();


        if (totalWagons > maxTotalWagons || position < 0 || position  >= totalWagons) {
            return false;
        }
        int toTrainMaxWagons = wagonAtPosition.getSequenceLength() + toTrain.getNumberOfWagons();

        if (toTrainMaxWagons > maxTotalWagons) {
            return false;
        }



        if (toTrain.isPassengerTrain() && (wagonAtPosition instanceof FreightWagon)) {
            return false;
        } else if (toTrain.isFreightTrain() && (wagonAtPosition instanceof PassengerWagon)) {
            return false;
        }

        if (wagonAtPosition.getPreviousWagon() == null) {
            firstWagon = null;
        }
        wagonAtPosition.detachFront();



        if (toTrain.firstWagon == null) {
            toTrain.firstWagon = wagonAtPosition;
        } else {
            Wagon currentWagon = toTrain.getLastWagonAttached();
            currentWagon.attachTail(wagonAtPosition);


        }




        return true;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        if (firstWagon == null || firstWagon.getNextWagon() == null) {

        } else {
            firstWagon = firstWagon.reverseSequence();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[" + engine + "]");
        Wagon currentWagon = firstWagon;
        while (currentWagon != null) {
            result.append(" ").append(currentWagon);
            currentWagon = currentWagon.getNextWagon();
        }
        return result + " with " + getNumberOfWagons() + " wagons from " + origin + " to " + destination;
    }
}

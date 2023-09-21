package models;

public class Wagon {
    public void setId(int id) {
        this.id = id;
    }

    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    public int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon (int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    /**
     * @return  whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        if(this.nextWagon != null){ // If there is a next wagon
            return true;
        } else{
            return false;
        }
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        if(this.previousWagon != null){ // If there is a predecessor
            return true;
        } else{
            return false;
        }
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     * @return  the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = this;
        while(currentWagon.hasNextWagon()){ // Loop until there is no follow up
            currentWagon = currentWagon.getNextWagon(); // Sets current wagon as its followup
        }

        return currentWagon;
    }

    /**
     * @return  the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        Wagon currentWagon = this; // Initializes a variable as last wagon
        int counter = 1;
        while(currentWagon.hasNextWagon()){ // Loops till current wagon has no previous wagon (so till it reaches head wagon)
            currentWagon = currentWagon.getNextWagon(); // Sets current wagon as its predecessor
            counter++; // Adds 1 to the counter
        }
        return counter; // Returns integer of how many wagons there are.
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *          The exception should include a message that reports the conflicting connection,
     *          e.g.: "%s is already pulling %s"
     *          or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {
        if (tail.getPreviousWagon() != null){
            throw new IllegalStateException("[" + tail + "] has already been attached to [" + tail.getPreviousWagon() + "]");
        }
        if(this.getNextWagon() == null){ // If wagon has no tail attach another wagon, if it has a tail then throw an error message containing both train id's.
            this.setNextWagon(tail);
            tail.setPreviousWagon(this);
        } else{
            throw new IllegalStateException("[" + this + "] is already pulling [" + this.getNextWagon() + "]");
        }
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     * @return the first wagon of the tail that has been detached
     *          or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if(this.hasNextWagon()){
            Wagon headWagon = getNextWagon(); // Saves follow up of this wagon
            nextWagon.setPreviousWagon(null); // Removes itself from his followup
            this.setNextWagon(null);  // Removes his followup
            return headWagon;
        } else{
            return null;
        }
    }

    public Wagon detachFront() {
        if (previousWagon != null) {
            Wagon detachedWagon = previousWagon;
            previousWagon = null; // Detach the current wagon from the previous one
            detachedWagon.nextWagon = null;
            return detachedWagon; // Return the detached wagon
        } else {
            return null; // There was no previous wagon to detach from
        }
    }


    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        // Remove connection with predecessor
        previousWagon.nextWagon = null;
        this.previousWagon = null;

        // Remove tail from front
        if(front.hasNextWagon()){
            front.nextWagon.previousWagon = null;
        }

        // Set this as the next wagon of first wagon
        front.nextWagon = this;
        this.previousWagon = front;
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        Wagon wagonToBeAttached = nextWagon;
        Wagon wagonToBeAttachedTo = previousWagon;
        if(this.hasPreviousWagon()){
            previousWagon.nextWagon = null; // Removes this wagon from its predecessor
            this.previousWagon = null;
        }
        if(this.hasNextWagon()){
            nextWagon.previousWagon = null; // Removes this wagon from his next wagon
            this.nextWagon = null;
        }

        if(wagonToBeAttached != null) {
            wagonToBeAttached.previousWagon = wagonToBeAttachedTo; // Sets predecessors tail as tail of this wagon
        }
        if(wagonToBeAttachedTo != null) {
            wagonToBeAttachedTo.nextWagon = wagonToBeAttached; // Sets predecessors tail as tail of this wagon
        }
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // Check if there is a succeeding next wagon attached
        Wagon currentWagon = this;
        Wagon front = this.previousWagon;
        Wagon newFirstWagon = getLastWagonAttached();

        getLastWagonAttached().detachFront();

        for (int i = 0; i < getSequenceLength(); i++) {
            Wagon reversing = getLastWagonAttached();
            getLastWagonAttached().detachFront();
            newFirstWagon.getLastWagonAttached().attachTail(reversing);
        }
        currentWagon.detachFront();
        newFirstWagon.getLastWagonAttached().attachTail(this);
        if (front != null) {
            front.attachTail(newFirstWagon);
        }
        return newFirstWagon;
    }




    @Override
    public String toString() {
        return "[Wagon-" + id + "]";
    }
}

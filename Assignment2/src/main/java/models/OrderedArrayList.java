package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> sortOrder;   // the comparator that has been used with the latest sort
    protected int nSorted;                       // the number of sorted items in the first section of the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given sortOrder comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> sortOrder) {
        super();
        this.sortOrder = sortOrder;
        this.nSorted = 0;
    }

    public Comparator<? super E> getSortOrder() {
        return this.sortOrder;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.sortOrder = c;
        this.nSorted = this.size();
    }

    // TODO override the ArrayList.add(index, item), ArrayList.remove(index) and Collection.remove(object) methods
    //  such that they both meet the ArrayList contract of these methods (see ArrayList JavaDoc)
    //  and sustain the representation invariant of OrderedArrayList
    //  (hint: only change nSorted as required to guarantee the representation invariant,
    //   do not invoke a sort or reorder items otherwise differently than is specified by the ArrayList contract)

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        if (index < nSorted) {
            // If an element was added within the sorted section, the order might be disrupted.
            nSorted = index;
        }
    }

    @Override
    public E remove(int index) {
        E removedItem = super.remove(index); // Call the super class method to remove the item
        if (index < nSorted) {
            nSorted--; // Decrement nSorted if the removed item was in the sorted section
        }
        return removedItem;
    }

    @Override
    public boolean remove(Object obj) {
        int index = super.indexOf(obj); // Find the index of the object using the super class method
        if (index >= 0) {
            super.remove(index); // Remove the object
            if (index < nSorted) {
                nSorted--; // Decrement nSorted if the removed object was in the sorted section
            }
            return true;
        }
        return false; // Object not found
    }




    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.sortOrder);
        }
    }

    @Override
    public int indexOf(Object item) {
        // efficient search can be done only if you have provided an sortOrder for the list
        if (this.getSortOrder() != null) {
            return indexOfByIterativeBinarySearch((E)item);
        } else {
            return super.indexOf(item);
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.sortOrder
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {
        // Perform binary search in the sorted section
        int left = 0;
        int right = nSorted - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            E midItem = this.get(mid);

            int compare = this.sortOrder.compare(midItem, searchItem);

            if (compare == 0) {
                // Match found in the sorted section
                return mid;
            } else if (compare < 0) {
                // If midItem is less than searchItem, search the right half
                left = mid + 1;
            } else {
                // If midItem is greater than searchItem, search the left half
                right = mid - 1;
            }
        }

        // If no match was found in the sorted section, perform linear search in the unsorted section
        for (int i = nSorted; i < size(); i++) {
            E current = this.get(i);

            if (this.sortOrder.compare(current, searchItem) == 0) {
                // Match found in the unsorted section
                return i;
            }
        }

        // If no match was found in either section, return -1
        return -1;
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.sortOrder
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        int left = 0;
        int right = nSorted - 1;

        return indexOfByRecursiveBinarySearch(searchItem, left, right);
    }

    private int indexOfByRecursiveBinarySearch(E searchItem, int left, int right) {
        if (left <= right) {
            int mid = left + (right - left) / 2;
            int comparisonResult = this.sortOrder.compare(this.get(mid), searchItem);

            if (comparisonResult == 0) {
                // Found the item, return its position
                return mid;
            } else if (comparisonResult < 0) {
                // Search in the right half
                left = mid + 1;
                return indexOfByRecursiveBinarySearch(searchItem, left, right);
            } else {
                // Search in the left half
                right = mid - 1;
                return indexOfByRecursiveBinarySearch(searchItem, left, right);
            }
        }

        // If no match was found in the sorted section, attempt a linear search in the unsorted section
        for (int i = nSorted; i < size(); i++) {
            if (this.sortOrder.compare(this.get(i), searchItem) == 0) {
                return i;
            }
        }

        // Item not found
        return -1;
    }



    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     * @param newItem
     * @param merger    a function that takes two items and returns an item that contains the merged content of
     *                  the two items according to some merging rule.
     *                  e.g. a merger could add the value of attribute X of the second item
     *                  to attribute X of the first item and then return the first item
     * @return  whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            // TODO retrieve the matched item and
            //  replace the matched item in the list with the merger of the matched item and the newItem
            E matchedItem = this.get(matchedItemIndex);
            E mergedItem = merger.apply(matchedItem, newItem);
            this.set(matchedItemIndex, mergedItem);
            return false;
        }
    }

    /**
     * calculates the total sum of contributions of all items in the list
     * @param mapper    a function that calculates the contribution of a single item
     * @return          the total sum of all contributions
     */
    @Override
    public double aggregate(Function<E,Double> mapper) {
        double sum = 0.0;
        for (int i = 0; i < size(); i++) {
            E current = this.get(i);
            sum += mapper.apply(current);
        }
        return sum;
    }
}

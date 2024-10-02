package src.pepse.util;

/**
 * A generic class for holding a pair of objects.
 * This class is useful for cases where you need to return or handle two values as a single entity.
 * The types of the two objects can be independently specified.
 *
 * @param <T1> the type of the first element in the pair
 * @param <T2> the type of the second element in the pair
 */
public class Pair<T1, T2> {
    /**
     * The first element of the pair. This field can store any object of type T1.
     */
    private T1 first;
    /**
     * The second element of the pair. This field can store any object of type T2.
     */
    private T2 second;


    /**
     * Constructs a new Pair with the specified values for its elements.
     *
     * @param first  the first element of the pair
     * @param second the second element of the pair
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Retrieves the first element of the pair.
     *
     * @return the first element of the pair
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Retrieves the second element of the pair.
     *
     * @return the second element of the pair
     */
    public T2 getSecond() {
        return second;
    }
}

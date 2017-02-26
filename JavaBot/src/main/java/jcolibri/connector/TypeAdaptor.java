package jcolibri.connector;

/**
 * Interface that must implement the classes which want to be used in the
 * connectors. By default, connectors manage basic java types: integers,
 * booleans, ... If developers want to use complex types, they must obey this
 * interface.
 * <p>
 * IMPORTANT: You must define the equals() method to avoid problems with the
 * data base connector. If you continue having problems try returning always
 * "true".
 *
 * @author Juan Antonio Recio Garc�a
 * @version 1.0
 */
public interface TypeAdaptor {

    /**
     * You must define this method to avoid problems with the data base
     * connector (Hibernate)
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * Reads the type from a string.
     *
     * @param content
     */
    public abstract void fromString(String content) throws Exception;

    /**
     * Returns a string representation of the type.
     */
    @Override
    public abstract String toString();
}

/**
 * ProgressController.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/01/2007
 */
package jcolibri.util;

import java.util.*;

/**
 * This class allows methods to indicate their progress in a long task. This is
 * a modification of the Observer pattern where the notifier of the tasks keeps
 * a list of observers and notifies them its progress. Here we centralize the
 * managing of the observers in this class simplifing the code of the notifiers.
 * <br>
 * If a method wants to notify its progress it only has to call to:
 * <ul>
 * <li>init(myclass, some info, estimated steps)
 * <li>step(myclass)
 * <li>...
 * <li>step(myclass)
 * <li>finish(myclass)
 * </ul>
 * On the other side observers must implement the ProgressListener interface and
 * register to recieve the events from a concrete class or all the classes.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 * @see jcolibri.util.ProgressController
 */
public class ProgressController {

    /** Defines unknown number of steps for a progress */
    public static final int UNKNOWN_STEPS = -1;

    static Hashtable<Class<?>, Collection<ProgressListener>> listeners = new Hashtable<>();
    static Collection<ProgressListener> listenersEverything = new ArrayList<>();

    /**
     * Removes all listeners
     */
    public static void clear() {
        listeners.clear();
        listenersEverything.clear();
    }

    /**
     * Deregisters a listener to recieve the progress of all classes.
     */
    public static void deregister(ProgressListener pl) {
        listenersEverything.remove(pl);
    }

    /**
     * Deregisters a listener to recieve the progress of a concrete class.
     */
    public static void deregister(ProgressListener pl, Class<?> c) {
        Collection<ProgressListener> l = listeners.get(c);
        if (l != null) l.remove(pl);
    }

    /**
     * Finishes the progress of a task.
     */
    public static void finish(Class<?> c) {
        Collection<ProgressListener> ls = listeners.get(c);
        if (ls != null) for (ProgressListener pl : ls)
            pl.finish();
        for (ProgressListener pl : listenersEverything)
            pl.finish();

    }

    /**
     * Notifies to all the listeners of a class that the progress is begining.
     *
     * @param c
     *            Class that notifies the progress.
     * @param info
     *            Some info about the progress.
     * @param numberOfSteps
     *            Number of steps of the task. If unknown it must be -1.
     */
    public static void init(Class<?> c, String info, int numberOfSteps) {
        Collection<ProgressListener> ls = listeners.get(c);
        if (ls != null) for (ProgressListener pl : ls)
            pl.init(info, numberOfSteps);
        for (ProgressListener pl : listenersEverything)
            pl.init(info, numberOfSteps);
    }

    /**
     * Registers a listener to recieve the progress of all classes.
     */
    public static void register(ProgressListener pl) {
        listenersEverything.add(pl);
    }

    /**
     * Registers a listener to recieve the progress of a concrete class.
     */
    public static void register(ProgressListener pl, Class<?> c) {
        Collection<ProgressListener> l = listeners.get(c);
        if (l == null) {
            l = new ArrayList<>();
            listeners.put(c, l);
        }
        l.add(pl);
    }

    /**
     * Notifies a new step in the task for a concrete class.
     */
    public static void step(Class<?> c) {
        Collection<ProgressListener> ls = listeners.get(c);
        if (ls != null) for (ProgressListener pl : ls)
            pl.step();
        for (ProgressListener pl : listenersEverything)
            pl.step();

    }

}

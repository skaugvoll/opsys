package round_robin;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class implements functionality associated with
 * the I/O device of the simulated system.
 */
public class Io {
    private Process activeProcess = null;

    private LinkedList<Process> ioQueue;
    private long avgIoTime;
    private Statistics statistics;


    /**
     * Creates a new I/O device with the given parameters.
     *
     * @param ioQueue    The I/O queue to be used.
     * @param avgIoTime  The average duration of an I/O operation.
     * @param statistics A reference to the statistics collector.
     */
    public Io(LinkedList<Process> ioQueue, long avgIoTime, Statistics statistics) {
        // Incomplete
        this.ioQueue = ioQueue;
        this.avgIoTime = avgIoTime;
        this.statistics = statistics;
    }

    /**
     * Adds a process to the I/O queue, and initiates an I/O operation
     * if the device is free.
     *
     * @param requestingProcess The process to be added to the I/O queue.
     * @param clock             The time of the request.
     * @return The event ending the I/O operation, or null
     * if no operation was initiated.
     */
    public Event addIoRequest(Process requestingProcess, long clock) {
        // Incomplete
        this.ioQueue.add(requestingProcess);
        if (this.getActiveProcess() == null) {
            return this.startIoOperation(clock);
        }
        return null;
    }

    /**
     * Starts a new I/O operation if the I/O device is free and there are
     * processes waiting to perform I/O.
     *
     * @param clock The global time.
     * @return An event describing the end of the I/O operation that was started,
     * or null	if no operation was initiated.
     */
    public Event startIoOperation(long clock) {
        if(statistics.ioQueueLargestLength < ioQueue.size()){
            statistics.ioQueueLargestLength = ioQueue.size();
        }

        if (getActiveProcess() == null && !ioQueue.isEmpty()) {
            try {
                this.activeProcess = this.ioQueue.pop();
                return new Event(Event.END_IO, clock + activeProcess.getAverageIOtime());
            } catch (NoSuchElementException e) {
                return null;
            }
        }
        return null;

    }

    /**
     * This method is called when a discrete amount of time has passed.
     *
     * @param timePassed The amount of time that has passed since the last call to this method.
     */
    public void timePassed(long timePassed) {
        // Incomplete
        if (this.activeProcess != null) {
            this.activeProcess.updateTimeNeeded(timePassed);
        }
    }

    /**
     * Removes the process currently doing I/O from the I/O device.
     *
     * @return The process that was doing I/O, or null if no process was doing I/O.
     */
    public Process removeActiveProcess() {
        // Incomplete
        statistics.nofProcessedIoOperations++;
        System.out.println("removing active IO process");
        Process process = this.getActiveProcess();
        this.activeProcess = null;
        return process;
    }

    public Process getActiveProcess() {
        return activeProcess;
    }
}

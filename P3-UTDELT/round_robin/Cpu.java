package round_robin;

import java.net.PortUnreachableException;
import java.util.LinkedList;

/**
 * This class implements functionality associated with
 * the CPU unit of the simulated system.
 */
public class Cpu {
    /**
     * Creates a new CPU with the given parameters.
     * @param cpuQueue		The CPU queue to be used.
     * @param maxCpuTime	The Round Robin time quant to be used.
     * @param statistics	A reference to the statistics collector.
     */

    private LinkedList<Process> cpuQueue;
    private long maxCpuTime; // burst time
    private Statistics statistics;
    private Process activeProcess = null;
    private long activeProcessStart;
    private long timePassed;


    public Cpu(LinkedList<Process> cpuQueue, long maxCpuTime, Statistics statistics) {
        this.cpuQueue = cpuQueue;
        this. maxCpuTime = maxCpuTime;
        this.statistics = statistics;
    }

    /**
     * Adds a process to the CPU queue, and activates (switches in) the first process
     * in the CPU queue, if the CPU is idle.
     * @param p		The process to be added to the CPU queue.
     * @param clock	The global time.
     * @return		The event causing the process that was activated to leave the CPU,
     *				or null	if no process was activated.
     */
    public Event insertProcess(Process p, long clock) {

        // add to queue
        this.cpuQueue.add(p);

        // clock = arrival time for this process ?  The time at which the event will occur.
        if(this.activeProcess == null){
            System.out.println("test");
            return switchProcess(clock);
        }
        System.out.println("asdadsasd");
        // return event to make active process leave the cpu
        return null;
    }

    /**
     * Activates (switches in) the first process in the CPU queue, if the queue is non-empty.
     * The process that was using the CPU, if any, is switched out and added to the back of
     * the CPU queue, in accordance with the Round Robin algorithm.
     * @param clock	The global time.
     * @return		The event causing the process that was activated to leave the CPU,
     *				or null	if no process was activated.
     */
    public Event switchProcess(long clock) {
        // Incomplete

        // round robin, hvor lang tid er det igjen av processen? hvis den ikke er ferdig, så legg den tilbake
        // bakerst i køen, sammen med gjennværende eksivkeringstid. og la en ny process starte.
        if (this.cpuQueue.isEmpty() && this.activeProcess == null){
            System.out.println("hei");
            return null;
        }
        else if(this.activeProcess != null){
            //sjekk prosessen som allerede er i cpu
            long burstTime = this.activeProcess.getProcessTimeNeeded();

            if(this.activeProcess.getTimeToNextIoOperation() - maxCpuTime <= 0){
                // send inn i io ko
                return new Event(Event.IO_REQUEST, clock);
            }

            else if(burstTime > maxCpuTime) {
                this.cpuQueue.add(this.activeProcess); // pushes the pre active process to back of cpu queue
                this.activeProcess.updateTimeNeeded(maxCpuTime);

                return new Event(Event.NEXT_PROCESS, clock + this.maxCpuTime);
            }


        }

        // if CPU queue has processes and there is not an active process
        Process newProcess = this.cpuQueue.pop(); // gets first process in cpuQueue
        long burstTime = newProcess.getProcessTimeNeeded();

        if(burstTime > maxCpuTime) {
            this.activeProcess = newProcess; // activates the new process
            this.cpuQueue.add(newProcess); // pushes the pre active process to back of cpu queue
            newProcess.updateTimeNeeded(maxCpuTime);
            return new Event(Event.SWITCH_PROCESS, clock + this.maxCpuTime);
            }
            // også sjekk om tiden er innefor tid tilgjengelig i CPU
        else if(newProcess.getTimeToNextIoOperation() == 0 ){
            return new Event(Event.IO_REQUEST, clock + newProcess.getProcessTimeNeeded());
        }
        else {
            return new Event(Event.END_PROCESS, clock + newProcess.getProcessTimeNeeded());

        }

    }

    /**
     * Called when the active process left the CPU (for example to perform I/O),
     * and a new process needs to be switched in.
     * @return	The event generated by the process switch, or null if no new
     *			process was switched in.
     */
    public Event activeProcessLeft(long clock) {
        // Incomplete
        return null;
    }

    /**
     * Returns the process currently using the CPU.
     * @return	The process currently using the CPU.
     */
    public Process getActiveProcess() {
        return this.activeProcess;
    }

    /**
     * This method is called when a discrete amount of time has passed.
     * @param timePassed	The amount of time that has passed since the last call to this method.
     */
    public void timePassed(long timePassed) {
        // Incomplete
        this.timePassed = timePassed;



    }

}

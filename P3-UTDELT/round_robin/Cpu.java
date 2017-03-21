package round_robin;

import java.net.PortUnreachableException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

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
        statistics.totalNofTimesInReadyQueue++;

        // clock = arrival time for this process ?  The time at which the event will occur.
        if(this.activeProcess == null){
            System.out.println("Legger til i CPU kø, og switcher");
            return switchProcess(clock);
        }
        System.out.println("Legger til i CPU ko, men ikke noe switch, siden det er en aktiv prosess");
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
        // round robin, hvor lang tid er det igjen av processen? hvis den ikke er ferdig, så legg den tilbake
        // bakerst i køen, sammen med gjennværende eksivkeringstid. og la en ny process starte.
        if(statistics.cpuQueueLargestLength < cpuQueue.size()){
            statistics.cpuQueueLargestLength = cpuQueue.size();
        }

        // hvis ikke aktiv
        if(this.activeProcess == null){
            // Er det kø?
            // Hvis ikke kø
            if(cpuQueue.isEmpty()){
                return null;
            }
            // Hvis kø
            else{
                // ut av kø
                // inn i cpu (aktiv)
                this.activeProcess = cpuQueue.pop();

                // Sjekk event
                Event nextEvent = getNextEvent(clock);
                // oppdater tid
                statistics.totalBusyCpuTime += nextEvent.getTime() - clock;
                statistics.totalTimeSpentInCpu += nextEvent.getTime() - clock;

                return nextEvent;
            }
            // hvis aktive
        }else{
            Event nextEvent = getNextEvent(clock);
            // Sjekk om next event er switch, skal CPU-kø oppdateres
            // hvis ikke skal bare event returneres
            if (nextEvent.getType() == Event.SWITCH_PROCESS) {
                // sendes aktiv bakerst i kø
                Process currentP = this.activeProcess;
                cpuQueue.add(currentP);
                this.activeProcess = cpuQueue.pop();
            }
            statistics.totalBusyCpuTime += nextEvent.getTime() - clock;
            statistics.totalTimeSpentInCpu += nextEvent.getTime() - clock;
            return nextEvent;


        }
    }

    private Event getNextEvent(long clock) {
        if(activeProcess.getProcessTimeNeeded() <= maxCpuTime){
            if(activeProcess.getProcessTimeNeeded() <= activeProcess.getTimeToNextIoOperation()){
                activeProcess.updateTimeNeeded(activeProcess.getProcessTimeNeeded());
                return new Event(Event.END_PROCESS, clock + activeProcess.getProcessTimeNeeded());

            }else if(activeProcess.getTimeToNextIoOperation() <= activeProcess.getProcessTimeNeeded()){
                activeProcess.updateTimeNeeded(activeProcess.getTimeToNextIoOperation());
                statistics.nofProcessedIoOperations++;

                return new Event(Event.IO_REQUEST, clock + activeProcess.getTimeToNextIoOperation());
            }

        }
        else if(activeProcess.getTimeToNextIoOperation() <= maxCpuTime && activeProcess.getTimeToNextIoOperation() > 0){
            activeProcess.updateTimeNeeded(activeProcess.getTimeToNextIoOperation());
            statistics.nofProcessedIoOperations++;
            return new Event(Event.IO_REQUEST, clock + activeProcess.getTimeToNextIoOperation());
        }
        else {
            activeProcess.updateTimeNeeded(maxCpuTime);
            statistics.nofProcessSwitches++;
            statistics.totalNofTimesInReadyQueue++;
            return new Event(Event.SWITCH_PROCESS, clock + maxCpuTime);
        }
        return null;
    }

    /**
     * Called when the active process left the CPU (for example to perform I/O),
     * and a new process needs to be switched in.
     * @return	The event generated by the process switch, or null if no new
     *			process was switched in.
     */
    public Event activeProcessLeft(long clock) {
        // Incomplete
        this.activeProcess = null;
        return this.switchProcess(clock);
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
        System.out.println("Time passed: " + timePassed);
        // Incomplete
        // oppdaterer hvor lenge prossessen har vært i cpu. (blir bre brukt her, ellers statestikk)
        // korrigerings metode, for å fortelle aktiv prosess om tiden såm har gått og den har vært i CPU men IO request event har happend.
        if(this.activeProcess != null) {
            this.activeProcess.updateTimeNeeded(timePassed);
            statistics.totalTimeSpentInCpu += timePassed;
            statistics.totalBusyCpuTime += timePassed;
            // sjekk om den tiden som har gått gjor at faenskapet ble ferdig eller skal til IO ?

        }

    }


}

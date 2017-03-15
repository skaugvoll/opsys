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
        // Incomplete

        // round robin, hvor lang tid er det igjen av processen? hvis den ikke er ferdig, så legg den tilbake
        // bakerst i køen, sammen med gjennværende eksivkeringstid. og la en ny process starte.

        // hvis ingen koer og ingen aktiv prosess
        if (this.cpuQueue.isEmpty() && this.activeProcess == null){
            System.out.println("hvis ingen koer og ingen aktiv prosess");
            return null;
        }
        // hvis aktiv og ko
        else if(this.activeProcess != null && !this.cpuQueue.isEmpty()){
            //sjekk prosessen som allerede er i cpu
            long burstTime = this.activeProcess.getProcessTimeNeeded();

            // hvis det skal utfores en io request innen tiden igjen i cpu.
            if(this.activeProcess.getTimeToNextIoOperation() - maxCpuTime <= 0){
                // send inn i io ko, men si at vi har brukt tiden fram til IO request skal utføres, av total CPU Tid
                System.out.println("hvis det skal utfores en io request innen tiden igjen i cpu.");
                return new Event(Event.IO_REQUEST, clock + this.activeProcess.getTimeToNextIoOperation());
            }
            //
            else if(burstTime > maxCpuTime) {
                this.cpuQueue.add(this.activeProcess); // pushes the pre active process to back of cpu queue
                this.activeProcess.updateTimeNeeded(maxCpuTime);
                System.out.println("hvis det ikke skal utføres IO innen denne cpu tiden, og prosessen blir ikke ferdig");
                return new Event(Event.NEXT_PROCESS, clock + this.maxCpuTime);
            }
        }
        // hvis ko men ingen aktiv prosess
        System.out.println("hvis ko men ingen aktiv prosess");
        Process newProcess = null;
        try {
            newProcess = this.cpuQueue.pop(); // gets first process in cpuQueue
        }
        catch (NoSuchElementException e){
            System.out.println("No processes in CPU queue, therefor returning null");
            return null; // kan ikke gjøre noe av det vi ønsker hvis vi ikke har en prosess.
        }
        long burstTime = newProcess.getProcessTimeNeeded();
        this.activeProcess = newProcess; // activates the new process

        // hvis trenger mer tid enn tilgjengelig i cpu, og ikke IO innenfor cpu-tid
        if(burstTime > maxCpuTime && newProcess.getTimeToNextIoOperation() - maxCpuTime > 0) {
            this.cpuQueue.add(newProcess); // pushes the pre active process to back of cpu queue
            newProcess.updateTimeNeeded(maxCpuTime);
            System.out.println("hvis trenger mer tid enn tilgjengelig i cpu, og ikke IO innenfor cpu-tid");
            return new Event(Event.SWITCH_PROCESS, clock + this.maxCpuTime);
            }
        // hvis tiden til IO request skal skje innenfor denne cpu-kjøringen
        else if(newProcess.getTimeToNextIoOperation() < maxCpuTime ){
            // oppdater prosessen med at den kjører fram til IO request og at den utfører det
            newProcess.updateTimeNeeded(newProcess.getTimeToNextIoOperation());
            //må overføre faenskapet til IO kø
            System.out.println("hvis tiden til IO request skal skje innenfor denne cpu-kjøringen");
            return new Event(Event.IO_REQUEST, clock + newProcess.getProcessTimeNeeded());
        }
        // hvis nok kjøretid denne gangen og ingen IO request
        else {
            newProcess.updateTimeNeeded(burstTime);
            System.out.println("hvis nok kjøretid denne gangen og ingen IO request");
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
        // Incomplete
        // oppdaterer hvor lenge prossessen har vært i cpu. (blir bre brukt her, ellers statestikk)
        // korrigerings metode, for å fortelle aktiv prosess om tiden såm har gått og den har vært i CPU men IO request event har happend.
//       if(this.activeProcess != null) {
//            this.activeProcess.updateTimeNeeded(timePassed);
//        }




    }


}

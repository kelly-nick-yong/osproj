
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class os {
	public static int currentTime;
	public static int timeBefore;
	public static IOScheduler IO;
	public static CPUscheduler cpu;
	public static MemoryManager mm;
	public static Swapper swapper;
	public static JobTable jobTable;
	
	public static void startup(){
		sos.ontrace();
		mm = new MemoryManager();
		cpu = new CPUscheduler();
		swapper = new Swapper();
		jobTable = new JobTable();
	}
	
	/**
	 * Indicates the arrival of a new job on the drum.
       At call : p [1] = job number
       p [2] = priority
       p [3] = job size, K bytes
       p [4] = max CPU time allowed for job
       p [5] = current time
	 */
	//interrupt when job comes in, store the job in jobtable
	public static void Crint(int [] a, int [] p){
		System.out.println("INSIDE CRINT!!!");
		timeUpdate(p);
		cpu.bookkeep();
		
		jobTable.add(p);
		int jobNum = p[1];
		mm.addJob(jobNum);
		swapper.swapIn(jobNum);
		swapper.swap();
		//cpu.scheduler(a,p);
	}

	// Interrupt when job finishes doing IO.
	public static void Dskint(int [] a, int [] p){
		System.out.println("Inside DSKINT");
	}
	
	//interrupt after swapping
	public static void Drmint(int [] a, int [] p){
		System.out.println("INSIDE DRMINT!!!");
		timeUpdate(p);
		cpu.bookkeep();
		//return index after done swapping
		int jobNum = swapper.swapDone();
		
		int dir = jobTable.getDirection(jobNum);
		if(dir == 0){ //drum to mem
			mm.addToCore(jobNum);
			cpu.ready(jobNum);
		}
		else if(dir == 1){ //mem to drum
			mm.removeJob(jobNum);
		}
		
	}
	
	//interrupt to serve the job's need
	public static void Svc(int [] a, int [] p){
		System.out.println("Inside Svc");
		//If *a=5: the job is requesting termination.
        //If *a=6: the job is requesting another disk I/O operation.
        //If *a=7: the job is requesting to be blocked until all pending I/O requests are completed.
	}
	
	//terminate the job
	public static void Tro(int [] a, int [] p){
		System.out.println("Inside TRO");
	}
	
	public static void timeUpdate(int [] p){
		timeBefore = currentTime;
		currentTime = p[5];
	}
	
	public static void recheck(int [] a, int [] p){
		//exceedTime[0] = exceeded maxCPUtime, free the memory by terminating
		//exceedTime[1] = exceeded maximum time in memory (1000)
		int [] cpuMemExceed = cpu.scheduler(a, p);
		mm.addTerminated(cpuMemExceed[0]);
	}
	
	/**
	 *  a[0]: gives the state of the CPU:
  
       a[1]: no jobs to run, CPU will wait until interrupt (extremely rare situation, but it can come up!)
                 Ignore p values.
  
       a[2]: set CPU to run mode, must set p values as below.
  
           - p[0], p[1], and p[5]: ignored
           - p[2]: the base address of job to be run.
           - p[3]: the size (in K) of job to be run.
           - p[4]: time slice (time quantum)
	 * @param a
	 * @param p
	 */
	
}







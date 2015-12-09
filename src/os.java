
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class os {
	public static int currentTime; // current time use for bookkeep and other
	public static int timeBefore; // last time 
	public static IOScheduler IO;
	public static CPUscheduler cpu;
	public static MemoryManager mm;
	public static Swapper swapper;
	public static JobTable jobTable;
	
	public static void startup(){
		//sos.ontrace();
		currentTime = 0;
		timeBefore = 0;
		mm = new MemoryManager();
		cpu = new CPUscheduler();
		swapper = new Swapper();
		jobTable = new JobTable();
		IO = new IOScheduler();
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
		System.out.println("\nINSIDE CRINT!!!");
		timeUpdate(p);
		cpu.bookkeep();
		
		jobTable.add(p);
		int jobNum = p[1];
		mm.addJob(jobNum);
		swapper.swapIn(jobNum);
		swapper.swap();
		recheck(a,p);
	}

	// Interrupt when job finishes doing IO.
	public static void Dskint(int [] a, int [] p){
		System.out.println("\nInside DSKINT");
	}
	
	//interrupt after swapping
	public static void Drmint(int [] a, int [] p){
		System.out.println("\nINSIDE DRMINT!!!");
		timeUpdate(p);
		cpu.bookkeep();
		//return index after done swapping
		int jobNum = swapper.swapDone();
		
		int dir = jobTable.getDirection(jobNum);
		if(dir == 0){ //drum to mem
			System.out.println("drum to mem");
			mm.addToCore(jobNum);
			cpu.ready(jobNum);
		}
		else if(dir == 1){ //mem to drum
			System.out.println("mem to drum");
			mm.removeJob(jobNum);
		}
		IO.processIO();
		
		recheck(a,p);
	}
	
	//interrupt to serve the job's need
	public static void Svc(int [] a, int [] p){
		System.out.println("\nInside Svc");
		timeUpdate(p);
		cpu.bookkeep();
		//If *a=5: the job is requesting termination.
        //If *a=6: the job is requesting another disk I/O operation.
        //If *a=7: the job is requesting to be blocked until all pending I/O requests are completed.
	

		// The job is requesting termination
		if (a[0] == 5) {
			// System.out.println("Requesting termination");
			int jobNum = cpu.terminateJob();
			jobTable.setDirection(jobNum, -1);
			mm.addTerminated(jobNum);
			// Moves IO to terminated queue
			IO.IOplacement(jobNum);
		}
		// The job is requesting another I/O operation
		else if (a[0] == 6) {
			// System.out.println("Requesting another i/o operation");
			int jobNum = cpu.currentJobInd;
			IO.newIOjob(jobNum);
		}
		// The job is requesting to be blocked until all pending
		// I/O requests are completed
		else if (a[0] == 7) {
			// System.out.println(
			// 	"Block until all pending I/O requests are completed");
			int jobNum = cpu.currentJobInd;
			// If job is using I/O, block, but don't free
			if (IO.isProcessingIO(jobNum)) {
				// System.out.println("-I/O: Job is doing I/O");
				cpu.block();
				IO.IOplacement(jobNum);
			}
			// If jobs are pending, block and free
			else if (jobTable.getPendingIO(jobNum) > 0) {
				// System.out.println("-I/O: Job has pending I/O");
				cpu.block();
				/*if (mm.smartSwap()) {
					swapper.swapOut(jobNum);
				}*/
				IO.IOplacement(jobNum);
			}
			// If job not using I/O and no pending I/O, ignore
			else {
				System.out.println("-I/O: Job has no pending I/O");
			}
		}
	}
	
	//terminate the job
	public static void Tro(int [] a, int [] p){
		System.out.println("\nInside TRO");
		timeUpdate(p);
		cpu.bookkeep();
		
		recheck(a,p);
	}
	
	
	//update current time
	public static void timeUpdate(int [] p){
		timeBefore = currentTime;
		currentTime = p[5];
		System.out.println("TIME BEFORE: " + timeBefore);			
		System.out.println("CURRENT TIME: " + currentTime);
		
		mm.removeTerminated();
	}
	
	public static void recheck(int [] a, int [] p){
		//exceedTime[0] = exceeded maxCPUtime, free the memory by terminating
		//exceedTime[1] = exceeded maximum time in memory (1000)
		System.out.println("recheck..");
		int [] cpuMemExceed = cpu.scheduler(a, p);
		int jobNum = -1;
		//terminate job exceeded max cpu time
		mm.addTerminated(cpuMemExceed[0]);
		//check blocked job wants to swap out from mem
		swapper.swapOut(IO.swapOutReady());
		//if blocked job wants to swap into mem
		jobNum = IO.swapInReady();
		mm.addJob(jobNum);
		swapper.swapIn(jobNum);
		//swap out job exceeded max mem time
		swapper.swapOut(cpuMemExceed[1]);
		//next job in queues to swap in
		jobNum = mm.nextInQueues();
		swapper.swapIn(jobNum);
		//if there is any to swap
		jobNum =swapper.swap();
		IO.IOplacement(jobNum);
		//assign job to do IO
		IO.processIO();
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








import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class os {
	public static int currentTime; // current time use for bookkeep and other
	public static int timeBefore; // last updated time 
	public static IOmanager IO; // scheduling for IOs
	public static CPU cpu; // cpu scheduler
	public static MemoryManager mm; //keep track of memory table
	public static Swapper swapper; //swapping jobs drum mem
	public static JobTable jobTable; // job list, and manipulating jobs
	
	public static void startup(){
		sos.ontrace();
		currentTime = 0;
		timeBefore = 0;
		mm = new MemoryManager();
		cpu = new CPU();
		swapper = new Swapper();
		jobTable = new JobTable();
		IO = new IOmanager();
	}
	
	/**
	 * Indicates the arrival of a new job on the drum.
       At call : p [1] = job number
       p [2] = priority
       p [3] = job size, K bytes
       p [4] = max CPU time needed for job
       p [5] = current time
	 */
	//interrupt when job comes in, store the job in jobtable
	public static void Crint(int [] a, int [] p){
		System.out.println("\nINSIDE CRINT!!!");
		timeUpdate(p); //update the current time and save the last time
		cpu.bookkeep(); //tracking remaining max cpu time
		
		jobTable.add(p); //add the job to jobtable
		int jobNum = p[1];
		//filling the memory table, and swap into mem
		jobNum = mm.addJob(jobNum); //able to add or not (-1)
		swapper.swapIn(jobNum); //set dir, and put in queue to swap to mem
		swapper.swap(); //actual swap happens
		
		check(a,p);
	}

	// Interrupt when job finishes doing IO.
	public static void Dskint(int [] a, int [] p){
		System.out.println("\nInside DSKINT");
		timeUpdate(p);
		cpu.bookkeep();
		//job finished IO
		int jobNum = IO.finishIO(); //-1 means failed
		cpu.readyToRun(jobNum); //put in readyQueue to run on cpu 
		IO.IOplacement(jobNum); //queues in IO
		
		check(a,p); //cpu scheduling next job and other checking
	}
	
	//interrupt after swapping
	public static void Drmint(int [] a, int [] p){
		System.out.println("\nINSIDE DRMINT!!!");
		timeUpdate(p);
		cpu.bookkeep();
		//return index after done swapping
		int jobNum = swapper.swapDone();
		int dir = jobTable.getDirection(jobNum);
		System.out.println("swap done: " + jobNum + " dir: " + dir);
		if(dir == 0){ //drum to mem
			System.out.println("drum to mem");
			mm.addToCore(jobNum);
			cpu.readyToRun(jobNum);
		}
		else if(dir == 1){ //mem to drum
			System.out.println("mem to drum");
			mm.removeJob(jobNum);
		}
		IO.processIO();
		
		check(a,p);
	}
	
	//interrupt to serve the job's need
	public static void Svc(int [] a, int [] p){
		System.out.println("\nInside Svc");
		timeUpdate(p);
		cpu.bookkeep();
		//If *a=5: the job is requesting termination.
        //If *a=6: the job is requesting another disk I/O operation.
        //If *a=7: the job is requesting to be blocked until all pending I/O requests are completed.
	
		System.out.println("a[0] = " + a[0]);
		//requesting termination
		if (a[0] == 5) {
			System.out.println("Requesting termination: a = 5");
			int jobNum = cpu.terminateJob();
			jobTable.setDirection(jobNum, -1);
			mm.addTerminated(jobNum);
			// Moves IO to terminated queue
			IO.IOplacement(jobNum);
		}
		//requesting IO 
		else if (a[0] == 6) {
			System.out.println("Requesting IO: a = 6");
			int jobNum = cpu.currentJobInd;
			System.out.println("cpu current running job: " + jobNum);
			IO.newIOjob(jobNum);
		}
		// request blocking the job (when IO, and pending IO requests are done)
		else if (a[0] == 7) {
			System.out.println("if job has pendingIO or Doing IO, block job: a = 7");
			int jobNum = cpu.currentJobInd;
			System.out.println("cpu current running job: " + jobNum);
			// If job is using IO or If jobs are pending, block and free
			if (IO.isProcessingIO(jobNum) 
					|| jobTable.getIOrequests(jobNum) > 0) {
				cpu.blockCurrentJob();
				IO.IOplacement(jobNum);
			}
		}
		
		check(a,p);
	}
	
	//quantum time ended
	public static void Tro(int [] a, int [] p){
		System.out.println("\nInside TRO");
		timeUpdate(p);
		cpu.bookkeep();
		
		check(a,p);
	}
	
	
	//update current time
	public static void timeUpdate(int [] p){
		timeBefore = currentTime;
		currentTime = p[5];
		System.out.println("TIME BEFORE: " + timeBefore);			
		System.out.println("CURRENT TIME: " + currentTime);
		
		mm.removeTerminated();
	}
	
	public static void check(int [] a, int [] p){
		//exceedTime[0] = exceeded maxCPUtime, free the memory by terminating
		//exceedTime[1] = exceeded maximum time in memory (1000)
		System.out.println("CHECK..");
		int [] cpuMemExceed = cpu.scheduler(a, p);
		int jobNum = -1;
		//terminate job exceeded max cpu time
		mm.addTerminated(cpuMemExceed[0]);
		//check blocked job wants to swap out from mem
		/*
		swapper.swapOut(IO.swapOutReady());
		//if blocked job wants to swap into mem
		jobNum = IO.swapInReady();
		jobNum = mm.addJob(jobNum); //able to add, return added index
		swapper.swapIn(jobNum);
		*/
		//swap out job exceeded max mem time
		swapper.swapOut(cpuMemExceed[1]);
		//next job in queues to swap in
		jobNum = mm.nextInQueues();
		jobNum = mm.addJob(jobNum); //able to add, return added index
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







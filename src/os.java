
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class os {
	static List<Job> jobTable;
	static MemoryManager mm;
	static int currentJobInd;
	static Queue<Integer> IOQueue;
	static Queue<Integer> readyQueue;
	static Queue<Integer> diskQueue;
	static boolean doingIO;
	
	public static void startup(){
		sos.ontrace();
		jobTable = new ArrayList<Job>();
		mm = new MemoryManager();
		currentJobInd = 0;
		IOQueue = new PriorityQueue<Integer>();
		readyQueue = new PriorityQueue<Integer>();
		diskQueue = new PriorityQueue<Integer>();
		doingIO = false;
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
		//bookkeep(p[5]);
		
		jobTable.add(new Job(p[1], p[2], p[3], p[4], p[5]));
		swapper();
		//CPUscheduler(a,p);
	}

	// Interrupt when job finishes doing IO.
	public static void Dskint(int [] a, int [] p){
		System.out.println("Inside DSKINT");
		Job job = jobTable.get(currentJobInd);
		bookkeep(p[5]);
		
		doingIO = false;
		job.setBlocked(false);
		
		swapper();
		CPUscheduler(a,p);
	}
	
	//interrupt after swapping
	public static void Drmint(int [] a, int [] p){
		System.out.println("INSIDE DRMINT!!!");
		bookkeep(p[5]);
		
		Job job = jobTable.get(currentJobInd);
		if(job.isSwapping()){
			job.setSwapping(false);
			job.setInMemory(true);
		}
		
		swapper();
		CPUscheduler(a,p);
	}
	
	//interrupt to serve the job's need
	public static void Svc(int [] a, int [] p){
		System.out.println("Inside Svc");
		bookkeep(p[5]);
		//If *a=5: the job is requesting termination.
        //If *a=6: the job is requesting another disk I/O operation.
        //If *a=7: the job is requesting to be blocked until all pending I/O requests are completed.
		Job job = jobTable.get(currentJobInd);
		if(a[0] == 5){
			//job is terminated, kick job out of memory
			if(!job.isTerminated()){
				System.out.println(currentJobInd + " Terminating");
				jobTermination();
			}
			else
				System.out.println(currentJobInd + ": Terminated");
		}
		else if(a[0] == 6){
			//request IO
			System.out.println(currentJobInd +": processing IO");
			IOQueue.add(job.getJobNum());
			processIO();
		}
		else if(a[0] ==7){
			//block the job
			System.out.println(currentJobInd +": blocking");
			job.setBlocked(true);
		}
		swapper();
		CPUscheduler(a,p);
	}
	
	//terminate the job
	public static void Tro(int [] a, int [] p){
		System.out.println("Inside TRO");
		bookkeep(p[5]);
		
		Job job = jobTable.get(currentJobInd);
		int timeLeft = job.getMaxCPUtime() - job.getEnterTime();
		if(timeLeft == 0){
			jobTermination();
		}
		
		
		swapper();
		CPUscheduler(a,p);
	}
	
	//Keeps track of remaining Max CPU Time
	private static void bookkeep(int currentTime){
		System.out.println("Inside BOOKKEEP");
		
		if(currentJobInd != -1){
			Job job = jobTable.get(currentJobInd);
			int timeSpend = currentTime - job.getEnterTime();
			int timeLeft = job.getMaxCPUtime() - timeSpend;
			job.setMaxCPUtime(timeLeft);
			if(job.getMaxCPUtime() < 0)
				job.setMaxCPUtime(0);
			System.out.println("job maxCPUTime: " + job.getMaxCPUtime());
		}
	}
	
	private static void swapper() {
		System.out.println("INSIDE SWAPPER!!!");
		assignJob();
		if(currentJobInd == -1){
			System.out.println("jobInd: " + -1);
			return;
		}
		Job job = jobTable.get(currentJobInd);
		if(!job.isSwapping()){
			if(!job.isInMemory()){
				mm.addJob(job);
				sos.siodrum(job.getJobNum(), job.getSize(), job.getAddress(), 0);
				job.setSwapping(true);
			}
		}
	}
	
	private static void processIO(){
		System.out.println("INSIDE IOprocess");
		Job job = jobTable.get(currentJobInd);
		if(!doingIO){
			//place on diskqueue
			if(job.isInMemory()){
				//poll() retrieve and remove head jobnum
				sos.siodisk(IOQueue.poll());
				doingIO = true;
			}
		}
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
	
	private static void CPUscheduler(int [] a, int [] p){
		System.out.println("INSIDE CPUscheduler!!!");
		assignJob();
		
		int quantum = 5;
		if(currentJobInd == -1){
			a[0] = 1;
			return;
		}
		Job job = jobTable.get(currentJobInd);
		if(job.isInMemory()){
			if(job.isBlocked()){
				System.out.println("blocked!");
				a[0] = 1;
			}
			else{
				/*if(job.getMaxCPUtime() < quantum){
					p[4] = job.getMaxCPUtime();
					p[2] = job.getAddress();
					p[3] = job.getSize();
					a[0] = 2;
				}*/
				//else{
					p[4] = quantum;
					p[2] = job.getAddress();
					p[3] = job.getSize();
					a[0] = 2;
				//}
				System.out.println("address: " + p[2] + " size: " + p[3] + 
						" maxCpu: " + p[4]);
			}
		}
	}// end of scheduler
	
	private static void assignJob(){
		System.out.println("Running: " + currentJobInd);
		if(jobTable.isEmpty())
			currentJobInd = -1;
		else
			currentJobInd = 0;
		System.out.println("Now Running: " + currentJobInd);
	}
	
	private static void jobTermination(){
		System.out.println("job termination!");
		Job job = jobTable.get(currentJobInd);
		
		mm.eraseJob(job);
		job.setTerminated(true);
		jobTable.remove(currentJobInd);
		job.setInMemory(false);
		
	}
}







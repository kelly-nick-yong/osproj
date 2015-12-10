import java.util.LinkedList;

public class IOScheduler {
	LinkedList<Integer> unblocked;	 // IO for unblocked jobs	
	LinkedList<Integer> blockedInCore; // IO for blocked, in memory jobs
	LinkedList<Integer> blockedInDrum;// IO for blocked, in drum jobs
	LinkedList<Integer> terminated;// IO for terminated jobs
	//LinkedList<Integer> requestedSwap; //job requested to swap
	int jobInIO;		//current job in IO
	
	IOScheduler(){
		unblocked = new LinkedList<Integer>();
		blockedInCore = new LinkedList<Integer>();
		blockedInDrum = new LinkedList<Integer>();
		terminated = new LinkedList<Integer>();
		//requestedSwap =  new LinkedList<Integer>();
		jobInIO = -1;
	}
	
	//add new job to do IO
	public void newIOjob(int jobNum){
		System.out.println("Inside IO, newIOjob");
		//increments pendingIO request
		int pendingIO = JobTable.getPendingIO(jobNum);
		JobTable.setPendingIO(jobNum, pendingIO +1);
		
		unblocked.add(jobNum);
		processIO();
		
	}
	
	//check blocked InCore jobs to swap out
	public int swapOutReady(){
		System.out.println("Inside IO, swapOutReady");
		int jobToSwap = -1;
		
		//if there is blocked job in core
		if(!blockedInCore.isEmpty()){
			// check if last element is swapping
			if(blockedInCore.getLast() != jobInIO){ // not already swapping
				jobToSwap = blockedInCore.getLast(); //swap out
			}
			//if blocked job exceeded mem time
			else if(JobTable.getEnterTime(blockedInCore.getFirst())
					> 1000){
				jobToSwap = blockedInCore.getFirst(); //swap out
			}
			/*
			if (requestedSwap.contains(jobToSwap)) { // job have requested to swap out
				jobToSwap = -1;
			}
			else { // job have not requested to swap out
				requestedSwap.add(jobToSwap);
			}*/
		}
		
		return jobToSwap;
	}
	
	//blocked job to swap into mem
	public int swapInReady(){
		System.out.println("Inside IO, swapInReady");
		int jobToSwap = -1;
		//core has no blocked job to swap
		if(blockedInCore.isEmpty() && !blockedInDrum.isEmpty()){
			jobToSwap = blockedInDrum.getFirst();
			/*
			if (requestedSwap.contains(jobToSwap)) { // job have requested to swap out
				jobToSwap = -1;
			}
			else { // job have not requested to swap out
				requestedSwap.add(jobToSwap);
			}*/
		}
		return jobToSwap;
	}
	
	//any job io for job in queues
	public void processIO(){
		System.out.println("Inside IO, processIO");
		if(jobInIO == -1){
			System.out.println("jobInIO: -1");
			 printQueues();
			if (!terminated.isEmpty()){
				System.out.println("assign from terminated..");
				jobInIO = terminated.poll();
			}
			else if(!blockedInCore.isEmpty()){
				System.out.println("assign from blockedInCore..");
				jobInIO = blockedInCore.poll();
			}
			else if(!unblocked.isEmpty()){
				System.out.println("assign from unblocked..");
				jobInIO = unblocked.poll();
			}
			System.out.println("IO after queue: " + jobInIO);
			if(jobInIO != -1){
				System.out.println("assign to do IO: " + jobInIO);
				JobTable.setDoingIO(jobInIO, true);
				sos.siodisk(jobInIO);
			}
		}
	}
	
	//current job finished doing io, remove and start next job
	public int finishIO(){
		int jobNum = jobInIO;
		jobInIO = -1;
		if(jobNum != -1){
			//one less pending io request
			JobTable.setPendingIO(jobNum, JobTable.getPendingIO(jobNum) -1);
			JobTable.setDoingIO(jobNum, false);
			
			if(JobTable.getPendingIO(jobNum) == 0 
					&& JobTable.isBlocked(jobNum)){
				JobTable.setBlocked(jobNum, false);
			}
		}
		
		processIO();
		
		return jobNum;
	}
	
	public void printQueues(){
		System.out.println("terminated num: "+ terminated.size());
		System.out.println("blockedInCore num: "+ blockedInCore.size());
		System.out.println("unblocked num: "+ unblocked.size());
	}
	
	//state of the job changed, place it in right queue
	public void IOplacement(int jobNum){
		System.out.println("Inside IO, IOplacement");
		if(jobNum != -1){
			int pendingIOs = JobTable.getPendingIO(jobNum); //pending IO requests
			System.out.println("Job that has IO pending: " + pendingIOs);
			
			if(pendingIOs > 0){ //there is io request
				/*
				if(requestedSwap.contains(jobNum)){
					requestedSwap.remove((Integer)jobNum);
				}*/
				
				//move into terminated
				if(JobTable.isTerminated(jobNum)){
					System.out.println("Moving to terminated");
					while(unblocked.contains((Integer)jobNum)){
						unblocked.remove((Integer)jobNum);
						terminated.add(jobNum);
					}
				}
			}
			
		}//-1
	}// IOplacement
	
	public boolean isProcessingIO(int jobNum){
		System.out.println("Inside IO, isProcessingIO");
		if(jobNum == jobInIO){
			System.out.println(jobNum + " is processing IO");
			return true;
		}
		else{
			System.out.println(jobNum + " is NOT processing IO");
			return false;
		}
	}
	
}















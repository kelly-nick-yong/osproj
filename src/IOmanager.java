import java.util.LinkedList;

public class IOmanager {
	LinkedList<Integer> waitingIOQueue;	 // waiting jobs	
	LinkedList<Integer> terminated;// terminated jobs
	int jobInIO;		//current job doing IO
	
	IOmanager(){
		waitingIOQueue = new LinkedList<Integer>();
		terminated = new LinkedList<Integer>();
		jobInIO = -1;
	}
	
	//add new job to do IO
	public void newIOjob(int jobNum){
		System.out.println("Inside IO, newIOjob");
		//increments pendingIO request
		int pendingIO = JobTable.getIOrequests(jobNum);
		JobTable.setIOrequests(jobNum, pendingIO +1);
		
		waitingIOQueue.add(jobNum);
		processIO();
		
	}
	
	//any job io for job in queues
	public void processIO(){
		System.out.println("Inside IO, processIO");
		if(jobInIO == -1){
			System.out.println("jobInIO: -1");
			 printQueues();
			 //get job from a queue to do io
			 //terminated job first
			if (!terminated.isEmpty()){
				System.out.println("assign from terminated..");
				jobInIO = terminated.poll();
			}
			
			//lastly waiting jobs, may be not enough memory to run before
			else if(!waitingIOQueue.isEmpty()){
				System.out.println("assign from waitingQueue..");
				jobInIO = waitingIOQueue.poll();
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
			JobTable.setIOrequests(jobNum, JobTable.getIOrequests(jobNum) -1);
			JobTable.setDoingIO(jobNum, false);
			//if no more IO pending requests, unblock the job
			if(JobTable.getIOrequests(jobNum) == 0 
					&& JobTable.isBlocked(jobNum)){
				JobTable.setBlocked(jobNum, false);
			}
		}
		
		processIO();
		
		return jobNum;
	}
	
	public void printQueues(){
		System.out.println("terminated num: "+ terminated.size());
		//System.out.println("blockedInCore num: "+ blockedInCore.size());
		System.out.println("waitingIOQueue num: "+ waitingIOQueue.size());
	}
	
	//state of the job changed, place it in right queue
	public void IOplacement(int jobNum){
		System.out.println("Inside IO, IOplacement");
		if(jobNum != -1){
			int pendingIOs = JobTable.getIOrequests(jobNum); //pending IO requests
			System.out.println("Job that has IO pending: " + pendingIOs);
			
			if(pendingIOs > 0){ //there is io request
				
				//move into terminated
				if(JobTable.isTerminated(jobNum)){
					System.out.println("Moving to terminated");
					while(waitingIOQueue.contains((Integer)jobNum)){
						waitingIOQueue.remove((Integer)jobNum);
						terminated.add(jobNum);
					}
				}
			}
			if(JobTable.isSwappedOut(jobNum)){
				System.out.println("jobNum: " + jobNum + " is swappedOut");
				waitingIOQueue.remove((Integer)jobNum);
			}
			
		}//not -1
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















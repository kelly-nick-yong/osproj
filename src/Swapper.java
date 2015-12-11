import java.util.LinkedList;

public class Swapper {
	// swapin = 0, swapout = 1
	int jobInDrum; //current job in drum index
	//LinkedList<Integer> blockedMemToDrum;	// jobNum is blocked, out
	//LinkedList<Integer> blockedDrumToMem;		// jobNum is blocked, in
	LinkedList<Integer> memToDrum;	// To be swapped out
	LinkedList<Integer> drumToMem;		// To be swapped in
	
	Swapper(){
		jobInDrum = -1;
		
		//blockedMemToDrum = new LinkedList<Integer>();
		memToDrum = new LinkedList<Integer>();
		//blockedDrumToMem = new LinkedList<Integer>();
		drumToMem = new LinkedList<Integer>();
	}
	
	// job from drum to mem
	public void swapIn(int jobNum){
		System.out.println("inside swapper, swapIn");
		if(jobNum != -1){
			//set the dir and add it to queues
			System.out.println("Swapper swap in of Job: " + jobNum);
			System.out.println("Job that's in drum: " + jobInDrum);
			JobTable.setDirection(jobNum, 0);
			JobTable.setInMemory(jobNum, true);
			addToQueues(jobNum);
		}
	}
	// job from mem to drum
	public void swapOut (int jobNum) {
		System.out.println("inside swapper, swapOut");
		if(jobNum != -1) {
			//set the dir and add it to queues
			System.out.println("-Swapper swap out of Job: " + jobNum);
			System.out.println("--Added Job " + jobNum + " to memToDrum queue");
			JobTable.setDirection(jobNum, 1);
			JobTable.setInMemory(jobNum, false);
			JobTable.setSwappedOut(jobNum, true);
			addToQueues(jobNum);
		}
	}
	
	//finish swapping
	public int swapDone(){
		
		System.out.println("Inside swapper, done swapping");
		int jobNum = jobInDrum;
		jobInDrum = -1;
		// done swapping
		JobTable.setSwapping(jobNum, false);
		//JobTable.setCurrentTime(jobNum);
		
		if(JobTable.getDirection(jobNum) == 0 ){ //get into mem
			JobTable.setInMemory(jobNum, true);
		}
		else if(JobTable.getDirection(jobNum) == 1 ){  //get out of mem
			JobTable.setInMemory(jobNum, false);
			JobTable.setSwappedOut(jobNum, true);
		}
		
		return jobNum;
	}
	
	private void addToQueues(int jobNum) {
		// TODO Auto-generated method stub
		System.out.println("inside swapper, addToQueues");
		if(jobNum != jobInDrum){
			removeFromQueues(jobNum);
			//in queues
			if(JobTable.getDirection(jobNum) == 0){//drum to mem
				/*
				if(JobTable.isBlocked(jobNum)){ //is blocked
					blockedDrumToMem.add(jobNum);
				}
				else { //unblocked */
					drumToMem.add(jobNum);
					
				//}
			}
			else if(JobTable.getDirection(jobNum) == 1) { //mem to drum
				/*if(JobTable.isBlocked(jobNum)){ //is blocked
					blockedMemToDrum.add(jobNum);
					blockedDrumToMem.remove((Integer)jobNum);
				}
				else { //unblocked*/
					memToDrum.add(jobNum);
					
				//}
			}
			
			
		}
		
	}
	private void removeFromQueues(int jobNum) {
		System.out.println("inside swapper, removeFromQueues");
		System.out.println("jobNum to remove from queue: " + jobNum);
		// remove object not index position, so cast as obj
		//blockedMemToDrum.remove((Integer)jobNum); 
		//blockedDrumToMem.remove((Integer)jobNum);
		memToDrum.remove((Integer)jobNum);
		drumToMem.remove((Integer)jobNum);
	}

	//doing the actual swapping
	public int swap(){
		System.out.println("inside swapper, swap");
		System.out.println("-Swap Queues:");
		//System.out.println("--blockedDrumToMem has " + blockedDrumToMem.size());
		//System.out.println("--blockedMemToDrum has " + blockedMemToDrum.size());
		System.out.println("--memToDrum has " + memToDrum.size());
		System.out.println("--drumToMem has " + drumToMem.size());
		
		if (jobInDrum == -1) { //choose next job in queue needs to swap
			System.out.println("jobInDrum: " + jobInDrum);
			if (!drumToMem.isEmpty()) {
				jobInDrum = drumToMem.poll();
			}
			else if (!memToDrum.isEmpty()) {

				jobInDrum = memToDrum.poll();
			}
			/*
			else if (!blockedMemToDrum.isEmpty()) {
				jobInDrum = blockedMemToDrum.poll();
			}
			else if (!blockedDrumToMem.isEmpty()) {
				jobInDrum = blockedDrumToMem.poll();
			}*/
			if (jobInDrum != -1) { //if there is a job waiting in queue
				//if it is doing io added back to queue
				if (JobTable.isDoingIO(jobInDrum)) {
					addToQueues(jobInDrum);
					jobInDrum = -1;
				}
				else {
					JobTable.setSwapping(jobInDrum, true);
					Job job = JobTable.getJob(jobInDrum);
					System.out.println("jobInDrum: " + jobInDrum);
					System.out.println("--Begin swapping Job " +
							job.jobNum +" with size " + job.size +
							" address:"+ job.address);
					
					sos.siodrum (job.jobNum, job.size,
						job.address, job.direction);
					
				}
			}
		}
		return jobInDrum;
	}//swap
	
	
	
}

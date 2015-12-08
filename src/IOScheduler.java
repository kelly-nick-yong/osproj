import java.util.LinkedList;

public class IOScheduler {
	LinkedList<Integer> unblocked;	 // I/O for non-blocked jobs	
	LinkedList<Integer> blockedInCore; // I/O for blocked, in-memory jobs
	LinkedList<Integer> blockedInDrum;// I/O for blocked, in drum jobs
	LinkedList<Integer> terminated;// I/O for terminated jobs
	LinkedList<Integer> reportedToSwap;
	int jobInIO;		//current job in IO
	
	IOScheduler(){
		unblocked = new LinkedList<Integer>();
		blockedInCore = new LinkedList<Integer>();
		blockedInDrum = new LinkedList<Integer>();
		terminated = new LinkedList<Integer>();
		reportedToSwap =  new LinkedList<Integer>();
		jobInIO = -1;
	}
	
	//check blocked InCore for jobs to swap out
	public int swapOutReady(){
		int jobToSwap = -1;
		
		//if there is blocked job in core
		if(!blockedInCore.isEmpty()){
			
		}
		
		return jobToSwap;
	}
}

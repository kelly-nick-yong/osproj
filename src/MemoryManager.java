import java.util.LinkedList;
import java.util.List;

public class MemoryManager {
	static final int MAX = 100;
	List<Integer> memoryTable;
	LinkedList<Integer> jobInCore; //jobs in memory
	LinkedList<Integer> blocked; //blocked jobs
	LinkedList<Integer> swapped; //swapped jobs
	LinkedList<Integer> unswapped; //not swapped jobs
	LinkedList<Integer> terminated; //terminated jobs
	static boolean haveSpace;
	
	public MemoryManager(){
		blocked = new LinkedList<Integer>();
		swapped = new LinkedList<Integer>();
		unswapped = new LinkedList<Integer>();
		terminated = new LinkedList<Integer>();
		
		memoryTable = new LinkedList<Integer>();
		jobInCore = new LinkedList<Integer>();
		// 0 means free space, 1 means occupied
		for(int i= 0; i< MAX; i++){
			memoryTable.add(0);
		}
		haveSpace= true;
	}
	
	public void addToQueues (int jobNum) {
		System.out.println("Inside Mem, addToQueues");
		if (JobTable.getAddress(jobNum) == -1) {
			blocked.remove((Integer)jobNum);
			unswapped.remove((Integer)jobNum);
			swapped.remove((Integer)jobNum);
			
			if (JobTable.isBlocked(jobNum)) {
				blocked.add(jobNum);
			}
			else if (JobTable.isSwapped(jobNum)) {
				swapped.add(jobNum);
			}
			else {
				unswapped.add(jobNum);
			}
		}
	}
	
	public int nextInQueues(){
		System.out.println("Inside Mem, nextInQueues");
		int jobNum =-1;
		//unswapped job first
		if(!unswapped.isEmpty()){
			findSpace(unswapped.getFirst());
			if(haveSpace)
				jobNum = unswapped.getFirst();
		}
		//swapped job next
		else if(!swapped.isEmpty()){
			findSpace(swapped.getFirst());
			if(haveSpace)
				jobNum = swapped.getFirst();
		}
		//then blocked job
		else if(!blocked.isEmpty()){
			findSpace(blocked.getFirst());
			if(haveSpace)
				jobNum = blocked.getFirst();
		}
		return jobNum;
	}
	
	public void addJob(int jobNum){
		System.out.println("Inside Mem, addJob!!");
		if(jobNum == -1){
			System.out.println("job is -1");
			return;
		}
		int [] begEnd = findSpace(jobNum); //check and find if there is space
		if(!haveSpace){
			System.out.println("Not enough Space!!!");
		}
		else{
			fillAddress(jobNum, begEnd);
			//addToCore(jobNum);
		}
	}
	
	public void removeJob(int jobNum){
		System.out.println("Inside Mem, removeJob!!");
		if(jobNum == -1){
			System.out.println("job is -1");
			return;
		}
		eraseAddress(jobNum);
		removeFromCore(jobNum);
	}
	
	//fill in memory table
	public void fillAddress(int jobNum, int [] begEnd){
		System.out.println("Inside Mem, filling address");
	
		JobTable.setAddress(jobNum, begEnd[0]);
		//JobTable.setDirection(jobNum, 0); //into memory
		for(int i= begEnd[0]; i<= begEnd[1]; i++){
			memoryTable.set(i, 1);
		}
		printMemory();
	}
	
	//best fit
	public int [] findSpace(int jobNum){
		System.out.println("Inside Mem, Finding Space!!");
		// beginning index and ending index
		int beg = -1, end = -1;
		int tmpBeg =0, tmpEnd=0;
		int sizeNeeded = JobTable.getSize(jobNum);
		int bestFitLen = MAX; //shortest length fit the job
		boolean first0 = true;
		int tempLen = 0;
		
		for(int i=0; i < MAX; i++){
			if(memoryTable.get(i) == 0){
				if(first0){  //if it is the beginning of a freespace
					tmpBeg = i;
					first0 = false;
				}
				tempLen++; //counting length of this freespace
				if(tempLen == sizeNeeded)
					tmpEnd = i;
			}
			//when freespace count ends
			if(memoryTable.get(i) == 1 || i == 99){
				first0 = true;
				//see if the space is big enough and
				//if it is smaller than the last bestfitlen
				if(tempLen >= sizeNeeded && tempLen <= bestFitLen){
					//new best fit space found
					bestFitLen = tempLen;
					beg = tmpBeg;
					end = tmpEnd;
					tempLen = 0;
				}
			}
			
		}
		if(end == -1) // have space to for job
			haveSpace = false;
		
		return new int[] {beg, end};
	}
	
	//remove from job table space
	public void eraseAddress(int jobNum){
		System.out.println("Inside Mem, eraseAddress!!");
		int beg = JobTable.getAddress(jobNum);
		int end = beg + JobTable.getSize(jobNum) -1;
		//set 1s into 0s where this job allocated
		for(int i=beg; i<= end; i++){
			memoryTable.set(i, 0);
		}
		JobTable.setAddress(jobNum, -1);
	}
	
	//record job is in memory
	public void addToCore(int jobNum){
		System.out.println("Inside Mem, addToCore");
		if(jobNum != -1)
			jobInCore.add(jobNum);
	}
	
	public void removeFromCore(int jobNum){
		System.out.println("Inside Mem, removeFromCore");
		if(jobNum != -1)
			jobInCore.remove((Integer)jobNum);
	}
	
	public void printMemory(){
		for(int i = 0; i< memoryTable.size(); i++){
			System.out.print(i + ": " + memoryTable.get(i) + ";  ");
			if(i%10 == 0 && i != 0)
				System.out.println();
		}
		System.out.println();
	}
	
	//add to jobs terminated, that needs to free the memory
	public void addTerminated(int jobNum){
		System.out.println("Inside Mem, addTerminated");
		if(jobNum != -1){
			if(JobTable.getPendingIO(jobNum) > 0 || JobTable.isDoingIO(jobNum)){
				terminated.add(jobNum);
				System.out.println("The process to be terminated is still doingIO or still have pending IO requests");

			}
			else{
				removeJob(jobNum);
				System.out.println("Freeing the memory space that job "
				+ jobNum + " was taking up.");
			}
		}
	}
	
	//remove terminated
	public void removeTerminated(){
		System.out.println("Inside Mem, addTerminated");
		for(int i = 0; i < terminated.size(); i++){
			int jobNum = terminated.get(i);
			if(JobTable.getPendingIO(jobNum) == 0){
				System.out.println("remove terminated: " + jobNum);
				removeJob(jobNum);
			}
		}
	}
	
	
	
}

import java.util.LinkedList;
import java.util.List;

public class MemoryManager {
	static final int MAX = 100;
	List<Integer> memoryTable;
	LinkedList<Integer> jobInCore; //jobs in memory
	LinkedList<Integer> blocked; //blocked jobs
	LinkedList<Integer> unswapped; //not swapped jobs
	LinkedList<Integer> terminated; //terminated jobs
	static int [] spaceBegEnd = {-1, -1};
	
	public MemoryManager(){
		blocked = new LinkedList<Integer>();
		unswapped = new LinkedList<Integer>();
		terminated = new LinkedList<Integer>();
		
		memoryTable = new LinkedList<Integer>();
		jobInCore = new LinkedList<Integer>();
		// 0 means free space, 1 means occupied
		for(int i= 0; i< MAX; i++){
			memoryTable.add(0);
		}
	}
	
	public void printQueues(){
		System.out.println("terminated num: "+ terminated.size());
		System.out.println("blocked num: "+ blocked.size());
		System.out.println("unswapped num: "+ unswapped.size());
	}
	
	public void addToQueues (int jobNum) {
		System.out.println("Inside Mem, addToQueues");
		if (JobTable.getAddress(jobNum) == -1) {
			
			if (JobTable.isBlocked(jobNum)) {
				System.out.println("add job to blocked..");
				blocked.add(jobNum);
			}
			else {
				System.out.println("add job to unswapped..");
				unswapped.add(jobNum);
			}
		}
	}
	
	public int nextInQueues(){
		System.out.println("Inside Mem, nextInQueues");
		int jobNum =-1;
		printQueues();
		//unswapped job first
		if(!unswapped.isEmpty()){
			if( findSpace(unswapped.poll()) ){
				jobNum = unswapped.poll();
				System.out.println("add job from unswapped: " 
						+ unswapped.poll());
			}
			else
				System.out.println("Not Enough Space for job: "
						+ unswapped.poll());
		}
		
		//then blocked job
		else if(!blocked.isEmpty()){
			if( findSpace(blocked.poll()) ){
				jobNum = blocked.poll();
				System.out.println("add job from blocked: " 
						+ blocked.poll());
			}
			else
				System.out.println("Not Enough Space for job: "
						+ blocked.poll());
			
		}
		return jobNum;
	}
	
	public int addJob(int jobNum){
		System.out.println("Inside Mem, addJob!!");
		if(jobNum == -1){
			System.out.println("job is -1");
			return -1;
		}
		//check and find if there is space
		if(!findSpace(jobNum)){
			System.out.println("Not enough Space!!!");
			addToQueues(jobNum);
			return -1;
		}
		else{
			fillAddress(jobNum, spaceBegEnd);
			//addToCore(jobNum);
			//assign back to -1 after filling the address
			spaceBegEnd[0] = -1;
			spaceBegEnd[1] = -1;
			return jobNum;
		}
	}
	
	//fill in memory table
	public void fillAddress(int jobNum, int [] begEnd){
		System.out.println("Inside Mem, filling address");
	
		JobTable.setAddress(jobNum, begEnd[0]);
		System.out.println("job: "+ jobNum + " with size: "+ JobTable.getSize(jobNum)
			+ " address from "+ begEnd[0]+ " to " + begEnd[1]);
		for(int i= begEnd[0]; i<= begEnd[1]; i++){
			memoryTable.set(i, 1);
		}
		printMemory();
	}

	public void removeJob(int jobNum){
		System.out.println("Inside Mem, removeJob!!");
		if(jobNum == -1){
			System.out.println("job is -1");
			return;
		}
		if(JobTable.getAddress(jobNum) != -1){
			eraseAddress(jobNum);
			removeFromCore(jobNum);
		}
		else{
			System.out.println("job: " + jobNum +" already removed address");
		}
	}
	
	//best fit //change it to boolean afterward
	public boolean findSpace(int jobNum){
		System.out.println("Inside Mem, Finding Space!!");
		// beginning index and ending index
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
				//System.out.println("tempLen: " + tempLen 
				//		+ " index: " + i);
				if(tempLen == sizeNeeded){
					tmpEnd = i;
				}
			}
			//when freespace count ends
			if(memoryTable.get(i) == 1 || i >= 99){
				first0 = true;
				//see if the space is big enough and
				//if it is smaller than the last bestfitlen
				if(tempLen >= sizeNeeded && tempLen <= bestFitLen){
					//new best fit space found
					System.out.println("Address Before: "+ spaceBegEnd[0] + " to " 
							+ spaceBegEnd[1]);
					bestFitLen = tempLen;
					spaceBegEnd[0] = tmpBeg;
					spaceBegEnd[1] = tmpEnd;
					System.out.println("size needed: " + sizeNeeded
							+ " bestFitLen: " + bestFitLen);
					System.out.println("Address finding: "+ spaceBegEnd[0] + " to " 
							+ spaceBegEnd[1]);
						
				}
				tempLen = 0;
			}
			
		}
		
		if(spaceBegEnd[0] != -1)
			return true;
		else
			return false;
	}
	
	//remove from job table space
	public void eraseAddress(int jobNum){
		System.out.println("Inside Mem, eraseAddress!!");
		int beg = JobTable.getAddress(jobNum);
		int end = beg + JobTable.getSize(jobNum) -1;
		System.out.println(jobNum + ": address " + beg + " to " + end);
		//set 1s into 0s where this job allocated
		for(int i=beg; i<= end; i++){
			memoryTable.set(i, 0);
		}
		JobTable.setAddress(jobNum, -1);
		printMemory();
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
			if(JobTable.getIOrequests(jobNum) > 0 || JobTable.isDoingIO(jobNum)){
				terminated.add(jobNum);
				System.out.println("The job to be terminated "
						+ "is doingIO or have pending IO requests");

			}
			else{
				removeJob(jobNum);
				System.out.println("erasing the memory space for job: "
						+ jobNum);
			}
		}
	}
	
	//remove terminated
	public void removeTerminated(){
		System.out.println("Inside Mem, addTerminated");
		for(int i = 0; i < terminated.size(); i++){
			int jobNum = terminated.get(i);
			if(JobTable.getIOrequests(jobNum) == 0){
				System.out.println("remove terminated: " + jobNum);
				terminated.remove(i);
				removeJob(jobNum);
			}
			
		}
	}
	
	
	
}

import java.util.LinkedList;

public class MemoryManager {
	private final int MAX = 100;
	private LinkedList<Integer> memoryTable;
	private boolean haveSpace;
	
	public MemoryManager(){
		memoryTable = new LinkedList<Integer>();
		for(int i= 0; i< MAX; i++){
			memoryTable.add(0);
		}
		setHaveSpace(true);
	}
	
	public void addJob(Job job){
		System.out.println("adding job!!");
		int [] begEnd = findSpace(job);
		if(!isHaveSpace())
			System.out.println("Not enough Space!!!");
		else{
			job.setAddress(begEnd[0]);
			for(int i= begEnd[0]; i<= begEnd[1]; i++){
				memoryTable.set(i, 1);
			}
		}
	}
	
	public int [] findSpace(Job job){
		System.out.println("Finding Space!!");
		int beg = -1, end = -1;
		int tmpBeg =0, tmpEnd=0;
		int sizeNeeded = job.getSize();
		int bestFitLen = MAX;
		boolean isFirst = true, first1= true;
		int tempLen = 0;
		
		for(int i=0; i < MAX; i++){
			if(memoryTable.get(i) == 0){
				if(isFirst){
					tmpBeg = i;
					isFirst = false;
				}
				if(tempLen == sizeNeeded)
					tmpEnd = i;
				tempLen++;
			}
			else if(memoryTable.get(i) == 1 && first1){
				isFirst = true;
				first1 = false;
				
				if(tempLen >= sizeNeeded && tempLen < bestFitLen){
					bestFitLen = tempLen;
					beg = tmpBeg;
					end = tmpEnd;
				}
			}
		}
		if(end == -1)
			setHaveSpace(false);
		
		return new int[] {beg, end};
	}
	
	public void eraseJob(Job job){
		int beg = job.getAddress();
		int end = beg + job.getSize() -1;
		for(int i=beg; i<= end; i++){
			memoryTable.set(i, 0);
		}
	}

	public boolean isHaveSpace() {
		return haveSpace;
	}

	public void setHaveSpace(boolean haveSpace) {
		this.haveSpace = haveSpace;
	}
	
}

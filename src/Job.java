
public class Job {
	int jobNum; //job number, 1-460
	int priority;
	int size;
	int maxCPUtime; //job have this much time on cpu, then terminates
	int enterTime;
	int timeInCPU; //job's time spended in cpu
	int IOrequests; //number of I/O request pending
	int address;
	int direction; // 0: Drum-to-Memory, 1: Memory-to-Drum, -1: No Swap
	boolean inMemory;
	boolean blocked;
	boolean swappedOut; //swapped out of memory
	boolean swapping; //is swapping
	boolean terminated;
	boolean doingIO;
	boolean ready; //in ready queue

	public Job(){
		
	}
	
	public Job(int [] p){
		this.jobNum = p[1];
		this.priority = p[2];
		this.size = p[3];
		this.maxCPUtime = p[4];
		this.enterTime = p[5];
		timeInCPU = 0;
		address = -1;
		IOrequests = 0;
		direction = -1;
		inMemory = false;
		blocked = false;
		swapping = false;
		terminated = false;
		doingIO = false;
		ready = false;
	}
}

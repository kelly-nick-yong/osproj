
public class Job {
	int jobNum;
	int priority;
	int size;
	int maxCPUtime;
	int enterTime;
	int currentTime;
	int timeInCPU;
	int pendingIO; //number of I/O request pending
	int address;
	int direction; // 0: Drum-to-Memory, 1: Memory-to-Drum, -1: No Swap
	boolean inMemory;
	boolean blocked;
	boolean swapped; //swapped at least once
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
		currentTime = 0;
		timeInCPU = 0;
		address = -1;
		pendingIO = 0;
		direction = -1;
		inMemory = false;
		blocked = false;
		swapped = false;
		swapping = false;
		terminated = false;
		doingIO = false;
		ready = false;
	}
}

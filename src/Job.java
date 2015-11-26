
public class Job {
	private int jobNum;
	private int priority;
	private int size;
	private int maxCPUtime;
	private int enterTime;
	private int address;
	private boolean inMemory;
	private boolean blocked;
	private boolean swapping;
	private boolean terminated;

	public Job(){
		jobNum = 0;
		priority = -1;
		size = 0;
		maxCPUtime = 0;
		address = -1;
		setInMemory(false);
		setBlocked(false);
		setSwapping(false);
		setTerminated(false);
	}
	
	public Job(int jobNum, int priority ,int size,int maxCPUtime, int currentTime){
		this.jobNum = jobNum;
		this.priority = priority;
		this.size = size;
		this.maxCPUtime = maxCPUtime;
		this.enterTime = currentTime;
		address = -1;
		setInMemory(false);
		setBlocked(false);
		setSwapping(false);
		setTerminated(false);
	}

	public int getJobNum() {
		return jobNum;
	}
	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getMaxCPUtime() {
		return maxCPUtime;
	}
	public void setMaxCPUtime(int maxCPUtime) {
		this.maxCPUtime = maxCPUtime;
	}
	public int getEnterTime() {
		return enterTime;
	}
	public void setEnterTime(int enterTime) {
		this.enterTime = enterTime;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public boolean isInMemory() {
		return inMemory;
	}

	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
	}
	
	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	public boolean isSwapping() {
		return swapping;
	}

	public void setSwapping(boolean swapping) {
		this.swapping = swapping;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}
}

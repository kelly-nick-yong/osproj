
public class Job {
	private int jobNum;
	private int priority;
	private int size;
	private int maxCPUtime;
	private int enterTime;
	private int address;
	private boolean inMemory;
	
	public Job(){
		jobNum = -1;
		priority = -1;
		size = 0;
		maxCPUtime = -1;
		address = -1;
		inMemory = false;
	}
	
	public Job(int jobNum, int priority ,int size,int maxCPUtime, int currentTime){
		this.jobNum = jobNum;
		this.priority = priority;
		this.size = size;
		this.maxCPUtime = maxCPUtime;
		this.enterTime = currentTime;
		inMemory = false;
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
}

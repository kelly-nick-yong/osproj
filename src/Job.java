
public class Job {
	private int jobNum;
	private int priority;
	private int size;
	private int maxCPUtime;
	private int currentTime;
	
	public Job(){
		
	}
	
	public Job(int jobNum, int priority ,int size,int maxCPUtime, int currentTime){
		this.jobNum = jobNum;
		this.priority = priority;
		this.size = size;
		this.maxCPUtime = maxCPUtime;
		this.currentTime = currentTime;
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
	public int getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
}

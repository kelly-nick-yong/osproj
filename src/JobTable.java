import java.util.LinkedList;

public class JobTable {
	static LinkedList<Job> jobTable;

	public JobTable() {
		jobTable = new LinkedList<Job>();
	}

	public static void add(int[] p) {
		Job job = new Job(p);
		jobTable.add(job);
	}

	public static Job getJob(int jobNum) {
		if (jobTable.get(jobNum-1) != null) {
			return jobTable.get(jobNum-1);
		}
		return null;
	}

	public static int getPriority(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).priority;
	}

	public static int getSize(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).size;
	}

	public static int getMaxCPUTime(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).maxCPUtime;
	}

	public static int getEnterTime(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).enterTime;
	}

	public static int getAddress(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).address;
	}

	public static void setAddress(int jobNum, int address) {
		int jobInd = jobNum -1;
		jobTable.get(jobInd).address = address;
	}
	
	public static int getIOrequests(int jobNum){
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).IOrequests;
	}
	
	public static void setIOrequests(int jobNum, int pendingIO){
		int jobInd = jobNum -1;
		jobTable.get(jobInd).IOrequests = pendingIO;
	}

	public static int getTimeLeft(int jobNum) {
		int jobInd = jobNum -1;
		Job job = jobTable.get(jobInd);
		int timeLeft = job.maxCPUtime - job.timeInCPU;
		return timeLeft;
	}

	public static void setTimeInCPU(int jobNum, int timeSpend) {
		int jobInd = jobNum -1;
		jobTable.get(jobInd).timeInCPU = jobTable.get(jobInd).timeInCPU + timeSpend;
	}

	public static int getDirection(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).direction;
	}

	public static void setDirection(int jobNum, int direction) {
		int jobInd = jobNum -1;
		System.out.println("Setting the direction from " + jobTable.get(jobInd).direction + " to " + direction);
		jobTable.get(jobInd).direction = direction;
	}

	public static boolean isInMemory(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).inMemory;
	}

	public static void setInMemory(int jobNum, boolean inMemory) {
		int jobInd = jobNum -1;
		jobTable.get(jobInd).inMemory = inMemory;
	}

	public static boolean isTerminated(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).terminated;
	}

	public static void setTerminated(int jobNum, boolean terminated) {
		int jobInd = jobNum -1;
		jobTable.get(jobInd).terminated = terminated;
	}

	public static boolean isBlocked(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).blocked;
	}

	public static void setBlocked(int jobNum, boolean blocked) {
		int jobInd = jobNum -1;
		System.out.println("Setting blocked to " + blocked);
		jobTable.get(jobInd).blocked = blocked;
	}

	public static boolean isSwapping(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).swapping;
	}

	public static void setSwapping(int jobNum, boolean swapping) {
		int jobInd = jobNum -1;
		jobTable.get(jobInd).swapping = swapping;
	}
	
	public static boolean isSwappedOut(int jobNum) {
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).swappedOut;
	}

	public static void setSwappedOut(int jobNum, boolean swappedOut) {
		int jobInd = jobNum -1;
		jobTable.get(jobInd).swappedOut = swappedOut;
	}
	
	public static boolean isDoingIO(int jobNum){
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).doingIO;
	}
	
	public static void setDoingIO(int jobNum, boolean doingIO){
		int jobInd = jobNum -1;
		jobTable.get(jobInd).doingIO = doingIO;
	}
	
	public static boolean isReady(int jobNum){
		int jobInd = jobNum -1;
		return jobTable.get(jobInd).ready;
	}
	
	public static void setReady(int jobNum, boolean ready){
		int jobInd = jobNum -1;
		jobTable.get(jobInd).ready = ready;
	}
}

















import java.util.LinkedList;
import java.util.List;

public class os {
	static LinkedList<Job> jobTable;
	static MemoryManager mm;
	
	public static void startup(){
		sos.ontrace();
		jobTable = new LinkedList<Job>();
		mm = new MemoryManager();
	}
	
	/**
	 * Indicates the arrival of a new job on the drum.
       At call : p [1] = job number
       p [2] = priority
       p [3] = job size, K bytes
       p [4] = max CPU time allowed for job
       p [5] = current time
	 */
	public static void Crint(int [] a, int [] p){
		System.out.println("INSIDE CRINT!!!");
		bookkeep(p[5]);
		
		jobTable.add(new Job(p[1], p[2], p[3], p[4], p[5]));
		swapper(p[1]);
	}

	public static void Dskint(int [] a, int [] p){
		
	}
	
	public static void Drmint(int [] a, int [] p){
		
	}
	
	public static void Svc(int [] a, int [] p){
		
	}
	
	public static void Tro(int [] a, int [] p){
	}
	
	//Keeps track of remaining Max CPU Time
	private static void bookkeep(int currentTime){
		
	}
	
	private static void swapper(int jobNum) {
		// TODO Auto-generated method stub
		
	}
}


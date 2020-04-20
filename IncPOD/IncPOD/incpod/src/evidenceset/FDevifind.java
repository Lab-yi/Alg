package evidenceset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import algorithms.hybrid.FDcase;
import chains.Builder;
import evidenceset.build.LinePair;
import pod.pointOD.PointOD;
import pod.pointOD.PointODSet;

public class FDevifind {
	private long time;
	private CountDownLatch countDownLatch;
	public List<Set<String>> evidence=new ArrayList<Set<String>>();
	public FDevifind(List<FDcase> FDs,int[][] add, int[][] data_all) throws Exception{
		List<Thread> t1 = new ArrayList<Thread>();
		for(FDcase fd:FDs) {
			Set<String> evi=new HashSet<String>();
			Thread t = new Thread (new RunnableDemo(fd, data_all, add,evi));
			t1.add(t);
		}
		countDownLatch = new CountDownLatch(FDs.size());
		long start = System.currentTimeMillis();
		for(int i=0;i<FDs.size();i++) 
			t1.get(i).start();
	    countDownLatch.await();
		long end = System.currentTimeMillis();
		time=end-start;
	}
	public long getTime() {
		// TODO Auto-generated method stub
		return time;
	}
	
	class RunnableDemo implements Runnable {
		   private Thread t;
		   private FDcase fd;
		   private int[][] data;
		   private int[][] add;
		   public Set<String> evi=new ConcurrentSkipListSet<String>();
		   RunnableDemo(FDcase fd, int[][] data, int[][] add, Set<String> evi) {
			   this.fd=fd;
			   this.data=data;
			   this.add = add;
			   this.evi=evi;
		   }
		   
		   public void run() {
			   evi.addAll(fd.getevidence(data, add));
			   evidence.add(evi);
			   countDownLatch.countDown();
		   }

		public void start () {
		    	  t.start();
		   }
		}
}   
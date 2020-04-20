package evidenceset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import algorithms.hybrid.UCCcase;
import chains.Builder;
import evidenceset.build.LinePair;
import pod.pointOD.PointOD;
import pod.pointOD.PointODSet;

public class UCCevifind {
	private long time;
	private CountDownLatch countDownLatch;
	public List<Set<String>> evidence=new ArrayList<Set<String>>();
	public UCCevifind(List<UCCcase> UCCs, int[][] add, int[][] data_all) throws Exception{
		List<Thread> t1 = new ArrayList<Thread>();
		for(UCCcase ucc:UCCs) {
			Set<String> evi=new HashSet<String>();
			Thread t = new Thread (new RunnableDemo(ucc, data_all, add,evi));
			t1.add(t);
		}
		countDownLatch = new CountDownLatch(UCCs.size());
		long start = System.currentTimeMillis();
		for(int i=0;i<UCCs.size();i++) 
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
		   private UCCcase ucc;
		   private int[][] data;
		   private int[][] add;
		   public Set<String> evi=new ConcurrentSkipListSet<String>();
		   RunnableDemo(UCCcase ucc, int[][] data, int[][] add, Set<String> evi) {
			   this.ucc=ucc;
			   this.data=data;
			   this.add = add;
			   this.evi=evi;
		   }
		   
		   public void run() {
			   evi.addAll(ucc.getevidence(data, add));
			   evidence.add(evi);
			   countDownLatch.countDown();
		   }

		public void start () {
		    	  t.start();
		   }
		}
}   
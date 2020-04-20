package evidenceset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import chains.Builder;
import chains.Chains;
import evidenceset.build.LinePair;

public class Repair {
	private List<Builder> chains;
	public Map<Integer , String> column= new HashMap<Integer, String>();
	
	public  Repair(List<Builder> chains,Map<Integer , String> column) {
		this.chains=chains;
		this.column=column;
	}
	
	public Set<String> getevidence(int[][] data,int[][] add_data) throws Exception {
		berfind bf=new berfind(chains, data, add_data);
		return bf.evidence;
	}

}

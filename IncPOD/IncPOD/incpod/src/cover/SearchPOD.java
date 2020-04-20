package cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ch.javasoft.bitset.IBitSet;
import ch.javasoft.bitset.LongBitSet;
import ch.javasoft.bitset.search.ITreeSearch;
import ch.javasoft.bitset.search.TranslatingTreeSearch;
import ch.javasoft.bitset.search.TreeSearch;
import chains.Builder;
import evidenceset.IEvidenceSet;
import evidenceset.Repair;
import evidenceset.berfind;
import evidenceset.build.LinePair;
import evidenceset.build.Operator;
import input.ParsedColumn;
import pod.pointOD.PointOD;
import pod.pointOD.PointODSet;
import predicates.Predicate;
import predicates.PredicateBuilder;
import predicates.sets.PredicateBitSet;
import predicates.sets.PredicateSetFactory;

public class SearchPOD {
	private PredicateBuilder predicates;
	private PointODSet total;
	
	public SearchPOD(PredicateBuilder predicates, PointODSet total) {
		// TODO Auto-generated constructor stub
		this.predicates=predicates;
		this.total=total;
	}

	public PointODSet getPointODs(Set<String> invalid,int[][] data_all,Map<Integer, String> column,ParsedColumn[] cols) {
		PointODSet result=new PointODSet();
		Queue<PointOD> set = new LinkedList<PointOD>();
		for(PointOD od:total) set.add(od);
			while(!set.isEmpty()) {
				PointODSet refine = new PointODSet();
				refine.add(set.poll());
				for(String lp:invalid) {
					refine.rev(lp,column,cols);
					if(refine.size()==0) break;
				}
				if(refine.size()==0) break;
				for(PointOD pod:refine) {result.add(pod);}
				if(result.size()>10000) {
					result.minimize();
				} 
			}
			return result;
	}

}

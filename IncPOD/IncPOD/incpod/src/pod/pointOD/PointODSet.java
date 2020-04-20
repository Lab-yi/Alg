package pod.pointOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.javasoft.bitset.IBitSet;
import ch.javasoft.bitset.search.NTreeSearch;
import chains.Builder;
import input.ParsedColumn;
import predicates.Predicate;
import predicates.sets.Closure;
import predicates.sets.PredicateBitSet;
import predicates.sets.PredicateSetFactory;

import java.util.Set;

public class PointODSet implements Iterable<PointOD> {

	private Set<PointOD> constraints = new HashSet<>();

	public boolean contains(PointOD dc) {
		return constraints.contains(dc);
	}

	private static class MinimalDCCandidate {
		PointOD dc;
		IBitSet bitset;

		public MinimalDCCandidate(PointOD dc) {
			this.dc = dc;
			this.bitset = PredicateSetFactory.create(dc.getPredicateSet()).getBitset();
		}

		public boolean shouldReplace(MinimalDCCandidate prior) {
			if (prior == null)
				return true;

			if (dc.getPredicateCount() < prior.dc.getPredicateCount())
				return true;

			if (dc.getPredicateCount() > prior.dc.getPredicateCount())
				return false;

			return bitset.compareTo(prior.bitset) <= 0;
		}
	}

	public void minimize() {
		Map<PredicateBitSet, MinimalDCCandidate> constraintsClosureMap = new HashMap<>();
		for (PointOD dc : constraints) {
			PredicateBitSet predicateSet = dc.getPredicateSet();
			Closure c = new Closure(predicateSet);
			if (c.construct()) {
				MinimalDCCandidate candidate = new MinimalDCCandidate(dc);
				PredicateBitSet closure = c.getClosure();
				MinimalDCCandidate prior = constraintsClosureMap.get(closure);
				if (candidate.shouldReplace(prior))
					constraintsClosureMap.put(closure, candidate);
			}
		}

		List<Entry<PredicateBitSet, MinimalDCCandidate>> constraints2 = new ArrayList<>(constraintsClosureMap.entrySet());
	//	log.info("Sym size created " + constraints2.size());

		constraints2.sort((entry1, entry2) -> {
			int res = Integer.compare(entry1.getKey().size(), entry2.getKey().size());
			if (res != 0)
				return res;
			res = Integer.compare(entry1.getValue().dc.getPredicateCount(), entry2.getValue().dc.getPredicateCount());
			if (res != 0)
				return res;
			return entry1.getValue().bitset.compareTo(entry2.getValue().bitset);
		});

		constraints = new HashSet<>();
		NTreeSearch tree = new NTreeSearch();
		for (Entry<PredicateBitSet, MinimalDCCandidate> entry : constraints2) {
			if (tree.containsSubset(PredicateSetFactory.create(entry.getKey()).getBitset()))
				continue;

			PointOD inv = entry.getValue().dc.getInvT1T2DC();
			if (inv != null) {
				Closure c = new Closure(inv.getPredicateSet());
				if (!c.construct())
					continue;
				 if
				 (tree.containsSubset(PredicateSetFactory.create(c.getClosure()).getBitset()))
				 continue;
			}

			constraints.add(entry.getValue().dc);
			tree.add(entry.getValue().bitset);
			 if(inv != null)
				 tree.add(PredicateSetFactory.create(inv.getPredicateSet()).getBitset());
		}
		// etmMonitor.render(new SimpleTextRenderer());
	}

	public void add(PointOD dc) {
		constraints.add(dc);
	}


	@Override
	public Iterator<PointOD> iterator() {
		return constraints.iterator();
	}

	public int size() {
		return constraints.size();
	}

	public List<PointOD> getpod() {
		// TODO Auto-generated method stub
		List<PointOD> ods =new ArrayList<PointOD>();
		for(PointOD od:constraints) ods.add(od);
		return ods;
	}

	public boolean rev(String lp,Map<Integer, String> column,ParsedColumn[] cols) {
		// TODO Auto-generated method stub
		boolean flag=false;
		Set<PointOD> ref = new HashSet<>();
		for(PointOD od:constraints) {
			if(od.invalid(lp,column)) {
				flag=true;
				for(PointOD newod:od.rev(lp, column, cols)) ref.add(newod);
			}
			else ref.add(od);
		}
		if(flag) this.constraints=ref;
		return flag;
	}

	private boolean canfind(Builder ber, PointOD od) {
		// TODO Auto-generated method stub
		int count = 0;
		int count1=0;
		for(Predicate p:od.predicates) {
			if(p.getOperand1().getColumn().getName().equals(ber.pre1.getOperand1().getColumn().getName())&&
					type(p.getopindex(),ber.index21)==1) 
				count++;
			if(p.getOperand1().getColumn().getName().equals(ber.pre2.getOperand1().getColumn().getName())&&
					type(p.getopindex(),ber.index4)==1)
				count++;
			if(p.getOperand1().getColumn().getName().equals(ber.pre1.getOperand1().getColumn().getName())&&
					type(4-p.getopindex(),ber.index21)==1) 
				count1++;
			if(p.getOperand1().getColumn().getName().equals(ber.pre2.getOperand1().getColumn().getName())&&
					type(4-p.getopindex(),ber.index4)==1)
				count1++;
			
		}
		if(count==2||count1==2)
			return true;
		return false;
	}
	public int type(int i,int j) {
		if(i==-1||j==-1){
			if(i!=2||j!=2) 
				return 1;
			return 0;
		}
		if(i==j) return 1;
		switch(10*i+j) {
			case 10:
			case 20:
			case 24:
			case 34:
			return 1;
		}
		return 0;
}

}

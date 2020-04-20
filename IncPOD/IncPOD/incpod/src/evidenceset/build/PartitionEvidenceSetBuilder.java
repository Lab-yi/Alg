package evidenceset.build;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import evidenceset.IEvidenceSet;
import evidenceset.TroveEvidenceSet;
import input.ColumnPair;
import input.Input;
import predicates.PredicateBuilder;
import predicates.sets.PredicateBitSet;
import predicates.sets.PredicateSetFactory;

public class PartitionEvidenceSetBuilder extends EvidenceSetBuilder {
	private Collection<ColumnPair> pairs;
	private int[][] input2s;

	public PartitionEvidenceSetBuilder(PredicateBuilder predicates2, Input input, int[][] values) {
		super(predicates2);
		pairs = predicates.getColumnPairs();
		createSets(pairs);
		this.input2s = values;
	}

	public IEvidenceSet buildEvidenceSet(Input input, Set<LinePair> invalid) {
		int[][] input2s = input.getInts();

		Collection<ColumnPair> pairs = predicates.getColumnPairs();
		createSets(pairs);

		IEvidenceSet evidenceSet = new TroveEvidenceSet();
		for (LinePair lp:invalid) {
			int[] row1 = input2s[lp.index1];
			PredicateBitSet staticSet = getStatic(pairs, row1);
			int[] row2 = input2s[lp.index2];
			PredicateBitSet set = getPredicateSet(staticSet, pairs, row1, row2);
			evidenceSet.add(set);
		}
		return evidenceSet;
	}

	protected PredicateBitSet getPredicateSet(PredicateBitSet staticSet, Collection<ColumnPair> pairs, int[] row1,
			int[] row2) {
		PredicateBitSet set = PredicateSetFactory.create(staticSet);
		// which predicates are satisfied by these two lines?
		for (ColumnPair p : pairs) {
			PredicateBitSet[] list = map.get(p);
			if (p.isJoinable()) {
				if (equals(row1, row2, p))
					set.addAll(list[0]);
				else
					set.addAll(list[1]);
			}
			if (p.isComparable()) {
				int compare = compare(row1, row2, p);
				if (compare < 0) {
					set.addAll(list[4]);
				} else if (compare == 0) {
					set.addAll(list[5]);
				} else {
					set.addAll(list[6]);
				}

			}

		}
		return set;
	}

	protected PredicateBitSet getStatic(Collection<ColumnPair> pairs, int[] row1) {
		PredicateBitSet set = PredicateSetFactory.create();
		// which predicates are satisfied by these two lines?
		for (ColumnPair p : pairs) {
			if (p.getC1().equals(p.getC2()))
				continue;

			PredicateBitSet[] list = map.get(p);
			if (p.isJoinable()) {
				if (equals(row1, row1, p))
					set.addAll(list[2]);
				else
					set.addAll(list[3]);
			}
			if (p.isComparable()) {
				int compare2 = compare(row1, row1, p);
				if (compare2 < 0) {
					set.addAll(list[7]);
				} else if (compare2 == 0) {
					set.addAll(list[8]);
				} else {
					set.addAll(list[9]);
				}
			}

		}
		return set;
	}

	private int compare(int[] row1, int[] row2, ColumnPair p) {
		return Integer.compare(row1[p.getC1().getIndex()], row2[p.getC2().getIndex()]);
	}

	private boolean equals(int[] row1, int[] row2, ColumnPair p) {
		return row1[p.getC1().getIndex()] == row2[p.getC2().getIndex()];
	}
	
}

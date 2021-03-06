package algorithms.hybrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chains.Skiplist;
import chains.skiplistnode;
import evidenceset.build.LinePair;
import evidenceset.build.Operator;
import pod.pointOD.PointOD;
import predicates.Predicate;
import predicates.operands.ColumnOperand;

public class FDcase {

	private Skiplist list=new Skiplist();
	int[] LHS;
	int RHS;
	boolean needindex=true;
	
	Map<Integer, String> column = new HashMap<Integer,String>();
	public FDcase(PointOD od, Map<Integer, String> column, int[][] input_data,int[][] diff, List<FDcase> FDs) {
		// TODO Auto-generated constructor stub
		this.column=column;
		LHS=new int[od.predicates.length-1];
		int[] LHS1=new int[2];
		LHS1[0]=0;
		LHS1[1]=0;
		RHS=-1;
		int index=0;
		int ind=0;
		for(Predicate pre:od.predicates) {
			if(pre.getOperator()==Operator.UNEQUAL) RHS=getIndex(pre.getOperand1());
			else{
				LHS[index++]=getIndex(pre.getOperand1());
			}
		}
		sort(LHS,diff);
		LHS1[0]=LHS[0];
		if(LHS.length>1) LHS1[1]=LHS[1];
		for(FDcase fd:FDs) {
			if(fd.LHS[0]==this.LHS[0]) {
				if(fd.LHS.length>=2&&(this.LHS.length<2||(this.LHS.length>=2&&fd.LHS[1]==this.LHS[1])))
				{this.list=fd.list;needindex=false;return;}
				if(this.LHS.length<2) {this.list=fd.list;needindex=false;return;}
			}
		}
		
		sort(input_data,LHS1);
		for(int i=0;i<input_data.length;i++) {
			int key1=getKey(input_data[i],LHS);
			int key2=input_data[i][RHS+1];
			list.insert(key1, key2, input_data[i][0], 1, 0);
		}
	}

	private void sort(int[] LHS, int[][] diff) {
		// TODO Auto-generated method stub
		for (int i = 0; i < LHS.length; i++) {
			for (int m = i; m < LHS.length; m++) {
				if (diff[LHS[i]+1][LHS[i]+1] < diff[LHS[m]+1][LHS[m]+1]) {
					int temp = LHS[i];
					LHS[i] = LHS[m];
					LHS[m] = temp;
				}
			}
		}
	}

	private int getKey(int[] is, int[] order) {
		// TODO Auto-generated method stub
		int result=0;
		for(int i=0;i<Math.min(order.length,2);i++) 
			result=31*result+is[order[i]+1];
		return result;
	}

	public Set<String> getevidence(int[][] data_all, int[][] add_data) {
		// TODO Auto-generated method stub
		Set<String> evidence=new HashSet<String>();
		for(int k=0;k<add_data.length;k++) {
			skiplistnode p,q;
			int key1=getKey(add_data[k],LHS);
			int key2=add_data[k][RHS+1];
			boolean insert=true;
			
			p=list.find(key1);
			q=list.findkey2(key2);
			while(p.right!=null&&p.right.key1==key1) {
				if(invalid(add_data[k],data_all[p.right.value.get(0)])) {
					for(int i:p.right.value) {
						if(data_all[i][0]!=add_data[k][0]) {
							evidence.add(getev(data_all[i],add_data[k]));
							evidence.add(getev(add_data[k],data_all[i]));
						}
					}
				}
				else if(insert&&p.right.key2==key2) {
					p.right.value.add(add_data[k][0]);
					insert=false;
				}
				p=p.right;
			}
			if(insert&&(p.right.key1>key1||p.right.key2>key2)) {
			   	list.insert(key1,key2,add_data[k][0],1,0);
				insert=false;
			   }
		}
		return evidence;
	}
	
	private String getev(int[] is1, int[] is2) {
		// TODO Auto-generated method stub
		String s="";
		for(int i=1;i<is1.length;i++) {
			if(column.get(i-1).contains("String")) 
				if(is1[i]==is2[i]) s+=2;
				else s+=5;
			else if(is1[i]>is2[i]) s+=1;
			else if(is1[i]==is2[i]) s+=2;
			else if(is1[i]<is2[i]) s+=3;
		}
		return s;
	}
	
	private boolean invalid(int[] is, int[] is2) {
		// TODO Auto-generated method stub
		for(int i=0;i<LHS.length;i++) {
			if(is[LHS[i]+1]!=is2[LHS[i]+1])
				return false;
		}
		if(is[RHS+1]==is2[RHS+1]) return false;
		return true;
	}

	private void sort(int[][] input, int[] order) {
		// TODO Auto-generated method stub
	 Arrays.sort(input, new Comparator<Object>() {    
         public int compare(Object o1, Object o2) {    
             int[] one = (int[]) o1;    
             int[] two = (int[]) o2;    
             for (int i = 0; i < order.length; i++) {    
                 int k = order[i];    
                 if (one[k] > two[k]) {    
                     return 1;    
                 } else if (one[k] < two[k]) {    
                     return -1;    
                 } else {    
                     continue;  //如果按一条件比较结果相等，就使用第二个条件进行比较。  
                 }    
             }    
             return 0;    
         }    
     });
	}

	
	private int getIndex(ColumnOperand<?> operand1) {
		// TODO Auto-generated method stub
		for(Integer i:column.keySet()) {
			if(column.get(i).equals(operand1.getColumn().getName()))
			{
				return i;
			}
		}
		return -1;
	}

}

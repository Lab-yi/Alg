package algorithms.hybrid;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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

public class UCCcase {

	boolean needindex=true;
	private Map<Integer, String> column;
	private int[] order;
	private Skiplist list=new Skiplist();
	
	public UCCcase(PointOD od, Map<Integer, String> column, int[][] input_data,int[][] diff,List<UCCcase> UCCs) {
		// TODO Auto-generated constructor stub
		this.column=column;
		order=new int[od.predicates.length];
		int index=0;
		for(Predicate pre:od.predicates) {
			order[index++]=getIndex(pre.getOperand1());
		}
		int[] order1=new int[3];
		sort(order,diff);
		order1[0]=order[0];
		if(order.length>1) order1[1]=order[1];
		for(UCCcase ucc:UCCs) {
			if(ucc.order[0]==this.order[0]) {
				if(ucc.order.length>=2&&(this.order.length<2||(this.order.length>=2&&ucc.order[1]==this.order[1]))){this.list=ucc.list;needindex=false;return;}
				if(this.order.length<2) {this.list=ucc.list;needindex=false;return;}
				
			}
		}
		
		sort(input_data,order1);
		for(int i=0;i<input_data.length;i++) {
			int key1=getKey(input_data[i],order1);
			int key2=key1;
			list.insert(key1, key2, input_data[i][0], 1, 0);
		}
		
	}
	
	private void sort(int[] order, int[][] diff) {
		// TODO Auto-generated method stub
		for (int i = 0; i < order.length; i++) {
			for (int m = i; m < order.length; m++) {
				if (diff[order[i]+1][order[i]+1] > diff[order[m]+1][order[m]+1]) {
					int temp = order[i];
					order[i] = order[m];
					order[m] = temp;
				}
			}
		}
	}
	
	private int getKey(int[] is, int[] order) {
		// TODO Auto-generated method stub
		int result=0;
		for(int i=0;i<Math.min(order.length,2);i++) result=31*result+is[order[i]+1];
		return result;
	}

	private boolean invalid(int[] order2, int[] is, int[] is2) {
		// TODO Auto-generated method stub
		for(int i=0;i<order.length;i++) {
			if(is[order[i]+1]!=is2[order[i]+1])
				return false;
		}
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


	public Set<String> getevidence(int[][] data_all, int[][] add_data) {
		// TODO Auto-generated method stub
		Set<String> evidence=new HashSet<String>();
		for(int k=0;k<add_data.length;k++) {
			skiplistnode p,q;
			int key1=getKey(add_data[k],order);
			int key2=key1;
			boolean insert=true;
			
			p=list.find(key1);
			while(p.right!=null&&p.right.key1==key1) {
				for(int i:p.right.value) {
					if(data_all[i][0]!=add_data[k][0]&&invalid(order,data_all[i],add_data[k])){
						evidence.add(getev(data_all[i],add_data[k]));
			    		evidence.add(getev(add_data[k],data_all[i]));
					}
				}
				p.right.value.add(add_data[k][0]);
				p=p.right;
				insert=false;
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

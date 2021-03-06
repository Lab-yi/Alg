package chains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import evidenceset.build.Operator;
import input.ParsedColumn;
import pod.pointOD.PointOD;
import predicates.Predicate;
import predicates.operands.ColumnOperand;  

public class Chains {
	public List<Builder> indexes = new ArrayList();
	private List<PointOD> ods;
	private Map<String,Double> map = new TreeMap<String,Double>();
	public Map<Integer, String> column;
	public ParsedColumn[] cols;
	public int[][] diff;
	
	public Chains(List<PointOD> ods, int[][] input, double alpha, Map<Integer, String> column,ParsedColumn[] cols) {
		int[] contain=new int[ods.size()];
		this.column=column;
	    this.cols=cols;
		int columnsize=column.size()+1;
		Set<Integer> sig = new HashSet<Integer>();
		diff = new int[columnsize][columnsize];
		int count=Math.min(10000, (input.length-1)/10+2);
		HashSet<Integer> set1 = new HashSet<Integer>();
		randomSet(0,input.length,count,set1);
		int[][] sample = new int[count][columnsize];
		int index=0;
		for(int i:set1){
        	for(int j=0;j<columnsize;j++) 
        		sample[index][j]=input[i][j];
        	index++;
        }
		for(int i=0;i<columnsize;i++) {
			double[] cov=new double[input[0].length];
			for(int k=i;k<columnsize;k++) {
				int[] order=new int[]{i,k};
			    sort(sample,order);
				for(int j=0;j<count-1;j++) {
					if(sample[j][i]!=sample[j+1][i]||sample[j][k]!=sample[j+1][k]) {diff[i][k]++;diff[k][i]++;}	
				}		
			}
			diff[i][i]=diff[i][i]/2;
		}	
		
		double[] mean=new double[sample[0].length];
		for(int k=0;k<columnsize;k++) {
			for(int j=0;j<count;j++) 
				mean[k]+=(1.0*sample[j][k])/count;
		}
		double[] sigma=new double[sample[0].length];
		
		for(int k=0;k<sample.length;k++) {
			
			for(int j=0;j<columnsize;j++) {sigma[j]+=(sample[k][j]-mean[j])*(sample[k][j]-mean[j]);}
		}
		double[][] p=new double[columnsize][columnsize];
		for(int i=0;i<columnsize;i++) {
				int[] order=new int[]{i};
				sort(sample,order);
				double[] cov=new double[columnsize];
				for(int j=0;j<sample.length-1;j++) {
					if(sample[j][i]!=sample[j+1][i]) {diff[i][i]++;}	
					for(int k=i+1;k<columnsize;k++) {
						cov[k]+=(sample[j][i]-mean[i])*(sample[j][k]-mean[k]);
						if(sample[j][i]!=sample[j+1][i]||sample[j][k]!=sample[j+1][k]) {diff[i][k]++;diff[k][i]++;}	
					}
					
				}
				for(int k=i+1;k<columnsize;k++) {
					p[i][k]=cov[k]/(Math.sqrt(sigma[i])*Math.sqrt(sigma[k]));
				}
		}
		
		
		for(int i=0;i<ods.size();i++) {
			for(int j=0;j<ods.get(i).getPredicateCount();j++) {
				if(ods.get(i).predicates[j].getopindex()==-1) {
					if(ods.get(i).getPredicateCount()==2) {
						if(sig.contains(getIndex(ods.get(i).predicates[1-j].getOperand1()))){contain[i]=1;continue;}
						ColumnOperand a0=new ColumnOperand(ods.get(i).predicates[1-j].getOperand1().getColumn(),0);
						ColumnOperand a1=new ColumnOperand(ods.get(i).predicates[1-j].getOperand1().getColumn(),1);
						ColumnOperand b0=new ColumnOperand(ods.get(i).predicates[j].getOperand1().getColumn(),0);
						ColumnOperand b1=new ColumnOperand(ods.get(i).predicates[j].getOperand1().getColumn(),1);
						Predicate pre1=new Predicate(Operator.GREATER_EQUAL,a0,a1);
						Predicate pre2=new Predicate(Operator.GREATER_EQUAL,b0,b1);
						Builder ber=new Builder(input,pre1,pre2,2,-1,column);
						indexes.add(ber);
						contain[i]=1;
					}
				}
			}
		}
		
		
		for(int i=0;i<ods.size();i++) {
			if(contain[i]==1) continue;
			if(ods.get(i).getPredicateCount()==2) {
				ColumnOperand a0=new ColumnOperand(ods.get(i).predicates[1].getOperand1().getColumn(),0);
				ColumnOperand a1=new ColumnOperand(ods.get(i).predicates[1].getOperand1().getColumn(),1);
				ColumnOperand b0=new ColumnOperand(ods.get(i).predicates[0].getOperand1().getColumn(),0);
				ColumnOperand b1=new ColumnOperand(ods.get(i).predicates[0].getOperand1().getColumn(),1);
				Predicate pre1=new Predicate(Operator.GREATER_EQUAL,a0,a1);
				Predicate pre2=new Predicate(Operator.GREATER_EQUAL,b0,b1);
				Builder ber;
				if(diff[getIndex(pre1.getOperand1())+1][getIndex(pre1.getOperand1())+1]>diff[getIndex(pre2.getOperand1())+1][getIndex(pre2.getOperand1())+1])
				    ber=new Builder(input,pre1,pre2,ods.get(i).predicates[0].getopindex(),ods.get(i).predicates[1].getopindex(),column);
				else
					ber=new Builder(input,pre2,pre1,ods.get(i).predicates[1].getopindex(),ods.get(i).predicates[0].getopindex(),column);
				indexes.add(ber);
				contain[i]=1;
				continue;
			}
			for(int j=0;j<ods.get(i).getPredicateCount();j++) {
				if(ods.get(i).predicates[j].getopindex()==2) {
					if(sig.contains(getIndex(ods.get(i).predicates[j].getOperand1()))) {
						contain[i]=1;
						break;
					}
					if(contain[i]==0&diff[getIndex(ods.get(i).predicates[j].getOperand1())+1][getIndex(ods.get(i).predicates[j].getOperand1())+1]>alpha*count) {
						Predicate pre1=new Predicate(Operator.GREATER_EQUAL,ods.get(i).predicates[j].getOperand1(),ods.get(i).predicates[j].getOperand2());
						Builder ber=new Builder(input,pre1,pre1,2,2,column);
						indexes.add(ber);
						sig.add(getIndex(ods.get(i).predicates[j].getOperand1()));
						contain[i]=1;
					}
				}
					
			}
			if(contain[i]==0) {
				int in=0;
				for(int j=0;j<ods.get(i).getPredicateCount();j++) {
					if(ods.get(i).predicates[j].getOperator()==Operator.EQUAL) in++;
				}
				if(in==ods.get(i).getPredicateCount()||in==ods.get(i).getPredicateCount()-1){
					continue;
				}
				for(int j=0;j<ods.get(i).getPredicateCount()-1;j++) {
					for(int k=j+1;k<ods.get(i).getPredicateCount();k++) {
						if(ods.get(i).predicates[j].getopindex()>-1&&ods.get(i).predicates[k].getopindex()>-1) {
							String s=getIndex(ods.get(i).predicates[j].getOperand1())+" "+ods.get(i).predicates[j].getopindex()+" "+getIndex(ods.get(i).predicates[k].getOperand1())+" "+ods.get(i).predicates[k].getopindex();
							String s1=getIndex(ods.get(i).predicates[k].getOperand1())+" "+ods.get(i).predicates[k].getopindex()+" "+getIndex(ods.get(i).predicates[j].getOperand1())+" "+ods.get(i).predicates[j].getopindex();
							if(map.containsKey(s))
								map.put(s,map.get(s)+1);
							else if(map.containsKey(s1)) {map.put(s1,map.get(s1)+1);}
							else map.put(s,1.0);
						}
					}
				}
			}
		}//转为数组
		
		
		Set<String> set=new HashSet<String>();
		for(String s:map.keySet()) set.add(s);
		for(String s:set) {
			String sr=s;
			int index1=Integer.valueOf(sr.substring(0,sr.indexOf(" ")));
			sr=sr.substring(sr.indexOf(" ")+1);
			int index2=Integer.valueOf(sr.substring(0,sr.indexOf(" ")));
			sr=sr.substring(sr.indexOf(" ")+1);
			int index3=Integer.valueOf(sr.substring(0,sr.indexOf(" ")));
			sr=sr.substring(sr.indexOf(" ")+1);
			int index4=Integer.valueOf(sr);
			int ind=Math.max(index1, index3);
			int in=index1+index3-ind;
			double score =Math.abs(p[in+1][ind+1]);
			map.put(s,score*map.get(s));
		}
		
		List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
            //升序排序
            public int compare(Entry<String, Double> o1,
                    Entry<String, Double> o2) {
            	Double a = Math.abs(o1.getValue());
            	Double b=Math.abs(o2.getValue());
                return b.compareTo(a);
            }
            
        });
        
		for(Map.Entry<String,Double> sc:list) {
			String s = sc.getKey();
			String sr=s;
			int index1=Integer.valueOf(sr.substring(0,sr.indexOf(" ")));
			sr=sr.substring(sr.indexOf(" ")+1);
			int index2=Integer.valueOf(sr.substring(0,sr.indexOf(" ")));
			sr=sr.substring(sr.indexOf(" ")+1);
			int index3=Integer.valueOf(sr.substring(0,sr.indexOf(" ")));
			sr=sr.substring(sr.indexOf(" ")+1);
			int index4=Integer.valueOf(sr);
			int change=0;
			for(int i=0;i<ods.size();i++) {
				if(contain[i]==1) continue;
				int flag=0;
				int flag1=0;
				for(int j=0;j<ods.get(i).getPredicateCount();j++) {
					if((getIndex(ods.get(i).predicates[j].getOperand1())==index1&&type(ods.get(i).predicates[j].getopindex(),index2)==1)||(getIndex(ods.get(i).predicates[j].getOperand1())==index3&&type(ods.get(i).predicates[j].getopindex(),index4)==1)) 
					{
						flag+=1;
					}
					if(flag==2) {
						contain[i]=1;
						change+=1;
					}
					if((getIndex(ods.get(i).predicates[j].getOperand1())==index1&&type(4-ods.get(i).predicates[j].getopindex(),index2)==1)||(getIndex(ods.get(i).predicates[j].getOperand1())==index3&&type(4-ods.get(i).predicates[j].getopindex(),index4)==1)) 
					{
						flag1+=1;
					}
					if(flag1==2) {
						contain[i]=1;
						change+=1;
					}
					
				}
			}
			if(change>0){
			if(p[Math.min(index1,index3)+1][Math.max(index1,index3)+1]>=0) {
					if(diff[index1+1][index1+1]>diff[index3+1][index3+1]){
						int a=index3;
						index3=index1;
						index1=a;
						int b=index4;
						index4=index2;
						index2=b;
					}
					Predicate pre1=new Predicate(Operator.GREATER_EQUAL,new ColumnOperand(cols[index1],0),new ColumnOperand(cols[index1],1));
					Predicate pre2=new Predicate(Operator.GREATER_EQUAL,new ColumnOperand(cols[index3],0),new ColumnOperand(cols[index3],1));
					Builder ber=new Builder(input,pre1,pre2,index2,index4,column);
					boolean have=false;
					for(Builder b:indexes){ 
						if(b.toString().equals(ber.toString())||b.toString().equals(ber.tosymString()))
						have=true;
						}
					if(!have) indexes.add(ber);
				}
				else if(p[Math.min(index1,index3)+1][Math.max(index1,index3)+1]<0) {
					if(diff[index1+1][index1+1]>diff[index3+1][index3+1]){
						int a=index3;
						index3=index1;
						index1=a;
						int b=index4;
						index4=index2;
						index2=b;
					}
					Predicate pre1=new Predicate(Operator.GREATER_EQUAL,new ColumnOperand(cols[index1],0),new ColumnOperand(cols[index1],1));
					Predicate pre2=new Predicate(Operator.LESS_EQUAL,new ColumnOperand(cols[index3],0),new ColumnOperand(cols[index3],1));
					
					Builder ber=new Builder(input,pre1,pre2,index2,index4,column);
					boolean have=false;
					for(Builder b:indexes){ 
						if(b.toString().equals(ber.toString())||b.toString().equals(ber.tosymString()))
						have=true;
						}
					if(!have) indexes.add(ber);
				}
				
			}
		}
		for(int i=0;i<contain.length;i++) 
			if(contain[i]!=1) System.out.println(ods.get(i).toString());
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


	public List<Builder> getchains(){
		return this.indexes;
	}

	public List<PointOD> getods(){
		return this.ods;
	}

	public String tostring() {
		String str="";
		int count = 0;
		for(Builder ber: indexes) {
			str+= "this is the "+count+" -th chains "+ber.toString()+""+"\n";
		} 
		return str;
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

	public static void randomSet(int min, int max, int n, HashSet<Integer> set) {
        if (n > (max - min + 1) || max < min) {
            return;
        }
        for (int i = 0; i < n; i++) {
            int num = (int) (Math.random() * (max - min)) + min;
            if(!set.contains(num)) set.add(num);
        }
        int setSize = set.size();
        if (setSize < n) {
        	randomSet(min, max, n - setSize, set);
        }
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
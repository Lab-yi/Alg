package chains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import evidenceset.build.LinePair;
import evidenceset.build.Operator;
import input.Column;
import pod.pointOD.PointOD;
import predicates.Predicate;
import predicates.operands.ColumnOperand;

public class Builder {
	public Predicate pre1;
	public Predicate pre2;
	public int index21;
	public int index4;
	private int type=0;
	private int flag;
	public int sim;
	public Map<Integer , String> column;
	private List<Skiplist> chains=new ArrayList<Skiplist>();
	private List<PointOD> pods=new ArrayList<PointOD>();
	public Builder(Predicate index1, Predicate index2) {
		this.pre1=index1;
		this.pre2=index2;
	}
	public Builder(int[][] in, Predicate index1, Predicate index2,int index21,int index4, Map<Integer , String> column) {
		if(index21==2&&index4==2) {
			this.index21=index21;
			this.index4=index4;
			this.column=column;
			this.pre1=index1;
			this.pre2=index2;
			this.flag=3;
			this.type=3;
			sim=4;
			sort(in, new int[] {getIndex(pre1.getOperand1())+1,getIndex(pre2.getOperand1())+1});
			Skiplist now = new Skiplist();
			for(int i=0; i<in.length;i++) {
				int count=-1;
				int in1=0;
				int key1=in[i][getIndex(pre1.getOperand1())+1];
				int key2=in[i][getIndex(pre2.getOperand1())+1];
				now.insert(key1,key2,in[i][0],3,3); 
			}
			chains.add(now);
		}
		else if(index21==2&&index4==-1) {
			this.index21=index21;
			this.index4=index4;
			this.column=column;
			this.pre1=index1;
			this.pre2=index2;
			this.flag=3;
			this.type=3;
			sim=3;
			sort(in, new int[] {getIndex(pre1.getOperand1())+1,getIndex(pre2.getOperand1())+1});
			Skiplist now = new Skiplist();
			for(int i=0; i<in.length;i++) {
				int count=-1;
				int in1=0;
				int key1=in[i][getIndex(pre1.getOperand1())+1];
				int key2=in[i][getIndex(pre2.getOperand1())+1];
				now.insert(key1,key2,in[i][0],3,3); 
			}
			chains.add(now);
		}
		else {
			this.index21=index21;
			this.index4=index4;
			this.column=column;
			int[][] input=in.clone();
			if(index1.getopindex()>2) {
				index1=index1.getSymmetric();
				index2=index2.getSymmetric();
			}
			
			//System.out.println("index of "+index1.toString()+index1.getopindex()+" "+index2.toString()+index2.getopindex());
			this.pre1=index1;
			this.pre2=index2;
			if(index1.getopindex()==-1||index1.getopindex()==5){
				this.pre1=index2;
				this.pre2=index1;
			}
			if(this.pre2.getopindex()==-1||this.pre2.getopindex()==5){
				this.pre2=new Predicate(Operator.GREATER,pre2.getOperand1(),pre2.getOperand2());
				
			}

	
			switch(this.pre1.getopindex()*10+this.pre2.getopindex()) {//-1表示后面符号相反，1表示不需要
				case 0:
				case 2:
				case 22:
				case 20:this.flag=1;this.type=0;buildingchains(input,1,0);break;
				case 24:
				case 4:this.flag=-1;this.type=0;buildingchains(input,-1,0);break;
			
				case 1:
				case 21:this.flag=1;this.type=1;buildingchains(input,1,1);break;
				case 3:
				case 23:this.flag=-1;this.type=1;buildingchains(input,-1,1);break;
				case 14:this.flag=-1;this.type=1;this.pre1=index2.getSymmetric();int a=index21;index21=index4;index4=a;this.pre2=index1.getSymmetric();buildingchains(input,-1,1);break;//==
				case 12://
				case 10:this.flag=1;this.type=1;this.pre1=index2;this.pre2=index1;int a1=index21;index21=index4;index4=a1;buildingchains(input,1,1);break;
			
				case 11:this.flag=1;this.type=2;buildingchains(input,1,2);break;
				case 13:this.flag=-1;this.type=2;buildingchains(input,-1,2);break;
				
				default: System.out.println("没有初始化第一个index的符号，要是>，>=和=才可以");
			
			}
			if(samedir(this.pre1.getopindex(),index21)&&samedir(this.pre2.getopindex(),index4)) 
				sim=1;
			else sim=0;
		}
	}

	

	private void buildingchains(int[][] input,int flag,int type){//-1表示后面符号相反，1表示前面符号相反,0表示正常
		/*
		 * case0://>=;>=
		 * case 1://>=;>
		 * case 2://>,>
		 * 允许两个元素都有相同的情况
		 * */
		
		sort(input, new int[] {getIndex(pre1.getOperand1())+1,flag*(getIndex(pre2.getOperand1())+1)});  
		for(int i=0; i<input.length;i++) {
			int count=-1;
			Skiplist now = new Skiplist();
			int in1=0;
			int key1=input[i][getIndex(pre1.getOperand1())+1];
			int key2=input[i][getIndex(pre2.getOperand1())+1];
			for(int i1=0;i1<chains.size();i1++) {
				int dis=chains.get(i1).checkinsert(key1, key2, input[i][0],flag,type);
				if(dis==0) {
					count=0;
					break;
				}
				if(count ==-1&dis>0) {count = dis;in1=i1;}
				else if(count>dis&dis>0) {
						count=dis;
						in1=i1;
				}
				
			}
			
			if(count==-1) {now.insert(key1,key2,input[i][0],flag,type); chains.add(now);}
			else{ if(count>0) {chains.get(in1).insert(key1,key2,input[i][0],flag,type);}}
		}
	}
	
	public Predicate getindex1() {
		return pre1;
	}
	

	public Predicate getindex2() {
		return pre2;
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
	
	public List<Skiplist> getChains(){
		return chains;
	}
	
	public int getflag() {
		return flag;
	}
	
	public int gettype() {
		return type;
	}
	
	public String toString() {
		String s="";
		int count= 0;
	//	s+=pre1.toString()+" "+pre2.toString()+" "+index21+" "+index4+"\n";
		s+=getIndex(pre1.getOperand1())+" "+getIndex(pre2.getOperand1())+" "+index21+" "+index4+"\n";
		return s;
	}
	
	private static void sort(int[][] ob, final int[] order) {
		// TODO Auto-generated method stub 
		 Arrays.sort(ob, new Comparator<Object>() {    
             public int compare(Object o1, Object o2) {    
                 int[] one = (int[]) o1;    
                 int[] two = (int[]) o2;    
                 for (int i = 0; i < order.length; i++) {    
                     int k = order[i];
                     boolean flag=false;
                     if(k<0){flag=true;k=-k;}
                     if(flag){
                         if (one[k] < two[k]) {    
                             return 1;    
                         } else if (one[k] > two[k]) {    
                             return -1;    
                         } else {    
                             continue;  //如果按一条件比较结果相等，就使用第二个条件进行比较。  
                         } 
                     }
                     else{
	                     if (one[k] > two[k]) {    
	                         return 1;    
	                     } else if (one[k] < two[k]) {    
	                         return -1;    
	                     } else {    
	                         continue;  //如果按一条件比较结果相等，就使用第二个条件进行比较。  
	                     }   
                     }
                 }    
                 return 0;    
             }    
         });
		
	}
	
	public void findvio(int[][] data, int[][] add_data,Set<String> evidence) {
		for(int k=0;k<add_data.length;k++) {
			skiplistnode p,q;
			int key1=add_data[k][getIndex(pre1.getOperand1())+1];
			int key2=add_data[k][getIndex(pre2.getOperand1())+1];
			boolean insert=true;
			
			for(Skiplist l:chains) {
				p=l.find(key1);
				q=l.findkey2(key2);
				
				if(sim==4) {//special type
					while(p.right!=null&&p.right.key1==key1) {
						if(p.right.key2==key2) {
							for(int i:p.right.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
							p.right.value.add(add_data[k][0]);
							insert=false;
							break;
						}
						if(p.right.key2>key2) {break;}
						p=p.right;
					}
				    if(insert&&(p.right.key1>key1||p.right.key2>key2)) {
				    	l.insert(key1,key2,add_data[k][0],3,3);
						insert=false;
					}
				}
				
				if(sim==3) {//special type
					while(p.right!=null&&p.right.key1==key1) {
						if(p.right.key2!=key2) {
							for(int i:p.right.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
						}
						else{
							p.right.value.add(add_data[k][0]);
							insert=false;
						}
						p=p.right;
					}
					if(insert) {
					   	l.insert(key1,key2,add_data[k][0],3,3);
						insert=false;
					}
				}
				if(sim==1) {//fetch
					if(q.key1<=p.key1) {
						skiplistnode temp=p;
						p=q;
						q=temp;
					}
					if(p!=null) {
						int op = getop(key1,p.key1);
						int op0= getop(key2,p.key2);
						if(p.key1<Integer.MAX_VALUE&&p.key1>Integer.MIN_VALUE&&samedir(op,index21)&&samedir(op0,index4)) 
							for(int i:p.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
						p=p.right;
						while(p.key1<Integer.MAX_VALUE&&p.key1>Integer.MIN_VALUE&&samedir(op,index21)&&samedir(op0,index4)) {
							for(int i:p.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
							p=p.right;
						}
					}
					
					if(q!=null) {
						int op = getop(key1,q.key1);
						int op0= getop(key2,q.key2);
						if(q.key1<Integer.MAX_VALUE&&q.key1>Integer.MIN_VALUE&&samedir(op,index21)&&samedir(op0,index4)) 
							for(int i:q.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
						q=q.left;
						while(q.key1<Integer.MAX_VALUE&&q.key1>Integer.MIN_VALUE&&samedir(op,index21)&&samedir(op0,index4)) {
							for(int i:q.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
							q=q.left;
						}
					}
				}
					
				if(sim==0) {//revfetch
					
					if(p.left!=null) p=p.left;
					if(q.right!=null)q=q.right;
					if(q.key1<=p.key1) {
						skiplistnode temp=p;
						p=q;
						q=temp;
					}
					if(index21==2) {
						p=p.right;
						if(p.right!=null) q=p.right;
						else q=p;
					}
					else if(index4==2) {
						p=q.left;
					}
					while(q.right!=null&&p!=q.right) {
						int op01 = getop(key1,p.key1);
						int op02= getop(key2,p.key2);
						if(p.key1>Integer.MIN_VALUE&&p.key1<Integer.MAX_VALUE&&samedir(op01,index21)&&samedir(op02,index4)) {
							for(int i:p.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
						}
						else if(p.key1>Integer.MIN_VALUE&&p.key1<Integer.MAX_VALUE&&samedir(4-op01,index21)&&samedir(4-op02,index4)) {
							for(int i:p.value) {
								evidence.add(getev(data[i],add_data[k]));
								evidence.add(getev(add_data[k],data[i]));
							}
						
						}
						p=p.right;
					}
				}
					
				int canin =l.checkinsert(key1, key2, add_data[k][0], flag, type);
				if(canin>=0&&insert) {
					l.insert(key1, key2,add_data[k][0],flag,type);
					insert=false;
				}
			}
			if(insert) {
				Skiplist l=new Skiplist();
				l.insert(key1, key2,add_data[k][0], flag, type);
				chains.add(l);
			}
		}
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
	
	private int getop(int key1, int key12) {
		// TODO Auto-generated method stub

		if(key1<key12) return 3;
		if(key1==key12) return 2;
		if(key1>key12) return 1;
		return 0;
	}


	private boolean samedir(int op1, int opindex) {
		// TODO Auto-generated method stub
		if(opindex==-1) {
			if(op1!=2)return true;
			else return false;
		}
		if(op1==-1) {
			if(opindex!=2) return true;
			else return false;
		}
		switch(10*op1+opindex) {
			case 10:
			case 11:
			case 20:
	     	case 24:
			case 22:
			case 33:
			case 34:return true;	
		}
		return false;
	}
	public String tosymString() {
		// TODO Auto-generated method stub
		String s="";
		int count= 0;
		int a=4-index21;
		int b=4-index4;
		s+=pre1.toString()+" "+pre2.toString()+" "+a+" "+b+"\n";
		return s;
	}
}

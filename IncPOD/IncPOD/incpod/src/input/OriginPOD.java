package input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import evidenceset.build.Operator;
import pod.pointOD.PointOD;
import pod.pointOD.PointODSet;
import predicates.Predicate;
import predicates.operands.ColumnOperand;

import java.io.*;

public class OriginPOD {
	public PointODSet total=new PointODSet();
	private PointODSet od=new PointODSet();
	private PointODSet fds=new PointODSet();
	private PointODSet uccs=new PointODSet();
	
	public OriginPOD(File hydra,ParsedColumn<?> [] col) throws IOException{
        int columncount=col.length;
        String list = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(hydra));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行\
            	if(s.length()==0) continue;
                list=s;
                list = list.substring(list.indexOf("this is")+8);
                String str;
                if(list.contains("this is ")) s=list.substring(list.indexOf("["),list.indexOf("this is"));
                else s=list.substring(list.indexOf("["));
	            int countune=0;
	    		int countin=0;
	    		int eq=0;
	    		List<Predicate> ls = new ArrayList<Predicate>();;
	    		while(s.indexOf("]")!=-1) {
	    			String col1 = s.substring(1,s.indexOf("]"));
	    			s=s.substring(s.indexOf("]")+1);
	    			String op;
	    			if(s.indexOf(",")!=-1) op=s.substring(0,s.indexOf(","));
	    			else op=s;
	    			s=s.substring(s.indexOf(",")+1);
	    			boolean flag=false;
	    			for(int i=0;i<columncount;i++) {
	    				Operator oper=getoperator(op);
	    				if(col[i].getName().equals(col1)) {
	    					if(oper==Operator.UNEQUAL) {
	    						countune++;
		    					}
		    				if(oper==Operator.EQUAL) eq++;
		    				ColumnOperand operand1=new ColumnOperand(col[i],0);
		    				ColumnOperand operand2=new ColumnOperand(col[i],1);
		    				Predicate e = new Predicate(oper,operand1,operand2);
							ls.add(e);
							flag=true;
						}
	    			}
	    			if(!flag) System.out.println(col1);
	    			if(countune==2) 
	    				break;
				 }
	    		if(countune<2&&eq<ls.size()-1) {
					od.add(new PointOD(ls));
					total.add(new PointOD(ls));
				}
	    		else if(countune<2&&eq==ls.size()-1) {fds.add(new PointOD(ls));	total.add(new PointOD(ls));}
	    		else if(countune<2&&eq==ls.size()) {uccs.add(new PointOD(ls));	total.add(new PointOD(ls));}
            }
	     }catch(IOException e){
		      e.printStackTrace();
		     }
    }
    
	public Operator getoperator(String s) {
		switch(s) {
		case "GREATER_EQUAL" : return Operator.GREATER_EQUAL;
		case "GREATER":return Operator.GREATER;
		case "EQUAL":return Operator.EQUAL;
		case "LESS": return Operator.LESS;
		case "LESS_EQUAL":return Operator.LESS_EQUAL;
		case "UNEQUAL": return Operator.UNEQUAL;
		}
		return null;
	}
	
	
    public PointODSet getod() {
    	return od;
    }
    
    public String toString() {
        return total.toString();
      }

	public PointODSet getfd() {
		// TODO Auto-generated method stub
		return fds;
	}

	public PointODSet getucc() {
		// TODO Auto-generated method stub
		return uccs;
	}
}
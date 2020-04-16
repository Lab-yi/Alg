import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import chains.Builder;
import chains.Chains;
import evidenceset.Repair;
import input.Input;
import input.ParsedColumn;
import input.inputod;
import input.newod;
import pod.pod;
import pod.podset;
import pod.predicate;
import pod.predicatespace;

public class main {
	public static int[][] input;
	public static Map<Integer , String> column = new HashMap<Integer, String>();
	
	private static String getev(int[] is1, int[] is2) {
		// TODO Auto-generated method stub
		String s="";
		for(int i=1;i<is1.length;i++) {
			if(column.get(i-1).equals("STRING")) 
				if(is1[i]==is2[i]) s+=2;
				else s+=1;
			else if(is1[i]>is2[i]) s+=1;
			else if(is1[i]==is2[i]) s+=2;
			else if(is1[i]<is2[i]) s+=3;
		}
		return s;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File data = new File("claimsnew.csv");
		int n = Integer.valueOf("52000");
		int m = Integer.valueOf("2000");
		double alpha=Double.valueOf("0.4");
		//这上面是5个参数，需要变动的
		Input data1 = new Input(data,n+m);
		ParsedColumn<?> [] col = data1.getColumns();
		int columncount = col.length;
		int[][] input=data1.getInts();
		for(int i=0; i<columncount;i++) {
			column.put(i, col[i].getType());
		}
		long used=0;
		long st=  System.currentTimeMillis();
		long end=  System.currentTimeMillis();
		used=end-st;
		int[] s1= {50447, 8286, 6776, 8305, 564, 771, 2029, 1223, 775, 2166, 1, 1182};
		int[] s2= {50413, 8285, 6776, 1818, 564, 771, 2103, 1896, 775, 2166, 1441, 86};
		String s=getev(s1,s2);
		
		System.out.println(s);
		
		
		
		
		
		for(int i=0;i<input.length;i++) {
			for(int j=0;j<input.length;j++) {
				if(i==j) continue;
				if(input[i][3]==input[j][3]&&input[i][9]>=input[j][9]&&
						input[i][0]>input[j][0]&&input[i][8]==input[j][8]&&
						input[i][10]!=input[j][10]&&input[i][1]==input[j][1])
					System.out.println(i+" "+j);
			}
			
		}
	}
}
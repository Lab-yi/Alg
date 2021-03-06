import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import algorithms.hybrid.Incpod;
import input.Input;
import input.InputIterationException;
import input.OriginPOD;
import input.ParsedColumn;
import input.RelationalInput;
import pod.pointOD.PointOD;
import pod.pointOD.PointODSet;
import predicates.PredicateBuilder;


public class Main{

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String line =args[0];    
		String incline =args[1];    
		String odline =args[2];
		
		int n=Integer.valueOf(args[3]);
		int m=Integer.valueOf(args[4]);
		int size=m;
		double alpha = 0.4;
		if(args.length>=6) {
			if(args[5].contains("size=")) {String s=args[5].replaceAll("size=","");size=Integer.valueOf(s);}
			if(args[5].contains("l=")) {String s=args[5].replaceAll("l=","");alpha=1-Double.valueOf(s);}
		}
		
		if(args.length>=7) {
			if(args[6].contains("size=")) {String s=args[6].replaceAll("size=","");size=Integer.valueOf(s);}
			if(args[6].contains("l=")) {String s=args[6].replaceAll("l=","");alpha=1-Double.valueOf(s);}
		}
		File file = new File(line);
		File incfile = new File(incline);
		File od = new File(odline);
		RelationalInput data = new RelationalInput(file);
		RelationalInput incdata = new RelationalInput(incfile);
		Input input = new Input(data,incdata,n,m);
		System.out.println("data name : claim");
		System.out.println("origin data size : "+n);
		System.out.println("incremental data size : "+m);
		System.out.println("tuples in a single round : "+size);
		PredicateBuilder predicates = new PredicateBuilder(input);
		OriginPOD origin=new OriginPOD(od,input.getColumns());
		 
		System.out.println("total constraints size : "+origin.total.size());

		Incpod incpod = new Incpod();
		System.out.println("------------------ now is Incpod for od -------------------");
		PointODSet pods = incpod.run(input,predicates,origin,alpha,n,m,size);
		System.out.println("total used time(excluding time for building indexes) : "+ incpod.time+ " ms"); 
		System.out.println("minimal PODs size : "+pods.size());
		
		String miniod="In order to compare Batch pod result, those PODs are in DC form"+"\n";
		int count=-1; 
		for(Iterator<PointOD> iter = pods.iterator();iter.hasNext();){
			count++;
		    miniod+="this is "+count+" "+iter.next().toString()+"\n";
		}
		
		try{
			String miniodline =line.replaceAll(".csv", "_")+"inc_PODs.txt";

			System.out.println("Writting # of pods to file : "+miniodline);
			File miniodfile =new File(miniodline); 
			FileWriter fileWritter = new FileWriter(miniodfile);
			fileWritter.write(miniod);
			if(!miniodfile.exists()){
				miniodfile.createNewFile();
			}
			fileWritter.close();
			System.out.println("Write pod Done");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

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
		if(args.length>4) m=Integer.valueOf(args[4]);
		double alpha = 0.4;
		if(args.length>5) alpha=Double.valueOf(args[5]);
		
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
		PointODSet dcs = incpod.run(input,predicates,origin,alpha,n,m,size);
		System.out.println("total used time(excluding time for building indexes) : "+ incpod.time+ " ms"); 
		System.out.println("minimal PODs size : "+dcs.size());
	}
}

package owp.nwm;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NWISGaugesNotUsed {
	String NWMVersion = null;
	Instant startDateTime = null;
	Instant currentDateTime = Instant.now();
	Long executionCount = null;
	Map<String, Long> gaugeIdAndCount = new HashMap<>();
	private Scanner scanner;
	
	public NWISGaugesNotUsed( String filename )
	{
		Path path = Paths.get(filename);
		if ( ! Files.exists( path ) )
		{
			executionCount = new Long(0);
			NWMVersion = "NWM v1.2.4";
			startDateTime = Instant.now();
			
			System.out.println("file not found!");
			return;
		}
		try {
		   scanner = new Scanner( path );
		   Scanner scan = scanner.useDelimiter("\\n");
		   scan.findInLine("(NWM v\\d+(\\.\\d+)+)");
		   NWMVersion = scan.match().group(1);
		   scan.nextLine();
		   scan.findInLine("start date and time: (\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z)");
		   
//		   startDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:SS").parse( scan.match().group(1) ).toInstant();
	       startDateTime = Instant.parse(scan.match().group(1));	   
		   
		   System.out.println(NWMVersion);
		   System.out.println(startDateTime);
		   scan.nextLine();
		   scan.findInLine("current date and time: (\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z)");
		   System.out.println(scan.match().group(1));
		   
		   scan.nextLine();
		   scan.findInLine("Count of script executions: (\\d+)");
		   executionCount = Long.parseLong(scan.match().group(1));
		   
		   scan.nextLine();
		   scan.findInLine("Gage ID                 Count");
		   scan.match();
		   
		   scan.nextLine();
		   scan.findInLine("(\\w+)\\s+(\\d+)");
		   gaugeIdAndCount.put(scan.match().group(1), Long.parseLong(scan.match().group(2)) );
		   
		   while( scan.hasNextLine())
		   {
			   scan.nextLine();
 		       scan.findInLine("(\\w+)\\s+(\\d+)");
			   gaugeIdAndCount.put(scan.match().group(1), Long.parseLong(scan.match().group(2)) );
		   }
		   System.out.println( gaugeIdAndCount);
		}catch (IOException e)
		{
		   e.printStackTrace();	
		}
		finally{
			scanner.close();
		}
		
	}
	
	public void update( String NWIS_gages_not_in_RLandParams_filename )
	{
			
		executionCount ++;
		
		Scanner scanner = null;
		List<String> gages = new ArrayList<>();
		
		
		try {
			scanner = new Scanner( new File(NWIS_gages_not_in_RLandParams_filename ) );
		    while( scanner.hasNext() )
		    {
		    	gages.add(scanner.next());
   	
		    }

		    for( String g : gages)
		    {
               	if ( gaugeIdAndCount.containsKey( g ) )
               	{
               		Long count = gaugeIdAndCount.get(g) + 1;
               		
               		//gaugeIdAndCount.computeIfPresent( g, (k,v)-> v + 1)
               		gaugeIdAndCount.put(g, count);
               		
               	}
               	else
               	{
               		gaugeIdAndCount.put(g, new Long(1));
               	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

		scanner.close();
	}
	
	public void toFile( String filename)
	{
		try{
	        FileWriter fw = new FileWriter( filename);
	        BufferedWriter bw = new BufferedWriter( fw );
	        bw.write(NWMVersion);
	        bw.newLine();
	        bw.write("start date and time: " + startDateTime.toString() );
	        bw.newLine();
	        bw.write("current date and time: " + currentDateTime.toString() );
	        bw.newLine();
	        bw.write("Count of script executions: " + executionCount.toString());
	        bw.newLine();
	        bw.write("Gage ID                 Count" );
	        
	        gaugeIdAndCount.forEach((k,v)->{ try{ bw.newLine();bw.write(String.format("%20s",k) + "   " + String.format("%20d", v.longValue()) );}catch(IOException e){ e.printStackTrace();}});
            bw.close();		
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
	

}

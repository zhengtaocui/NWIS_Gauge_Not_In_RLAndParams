
package oata;
import owp.nwm.*;

public class HellowWorld {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println("Hello World");
        if ( args.length != 2 )
        {
        	System.out.println("Usage: java -jar HellowWorld.jar <model_output_gages_file> <accumulated_gage_file>");
        	return;
        }
        
        NWISGaugesNotUsed gauges = new NWISGaugesNotUsed( args[1] );
        gauges.update(args[0]);
        gauges.toFile(args[1]);
        return;
	}

}

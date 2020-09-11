package testips;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import testips.Main.Ips;

public class Tools {

	public Date convertStringToDate(String sDate)
    {
    	try {
			return Main.formatter.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
    }
	
	public static boolean exportLineToCSVFile(String ip, String date, String ports)
    {
    	try
    	{
    		// File does not exist? -> create and write header
    		BufferedWriter writer;
    		if(!Main.file.exists())
    		{
    			Main.file.createNewFile();
    			writer = new BufferedWriter(new OutputStreamWriter(
    					new FileOutputStream(Main.pathToResultFile, true), "UTF-8"));
    			writer.append("ip,test_on,ports");
    			writer.newLine();
    			writer.close();
    		}
	    		writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(Main.pathToResultFile, true), "UTF-8"));
				writer.append(ip+","+date+","+ports);
				writer.newLine();
				writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
	
	public static int getDifferenceTimeinHoursFromElementDateToNow(String dateOfLastTest)
    {
    	try {
    		if(dateOfLastTest.startsWith("["))
    			dateOfLastTest = dateOfLastTest.replace("[", "").replace("]", "");
			Date d_dateOfLastTest = Main.formatter.parse(dateOfLastTest);
			//Timestamp ts_dateOfLastTest = new Timestamp(d_dateOfLastTest);
			Date now = new Date(System.currentTimeMillis());
			long seconds = (now.getTime() - d_dateOfLastTest.getTime()) / 1000;
			return (int)seconds / 3600;
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return 0;
    }
	
	public static double getMaxMemory()
    {
    	//System.out.println("max memory: "+(float) Runtime.getRuntime().maxMemory() / 1024/1024);
    	return (double) Runtime.getRuntime().maxMemory() / 1024/1024;
    }
    
	public static double getUsedMemory()
    {
    	//System.out.println("used memory: "+(float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() / 1024/1024));
		MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    	//return (double) (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1024/1024;
    	return (double) (heapMemoryUsage.getUsed()) / 1024/1024;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getElementsInRandomSequence(List <String> ips)
    {
    	Set allIpsInRandomSequence_ = new HashSet();
        int random;
        for (Object temp: ips)
        {
            random = (int) (Math.random() * ips.size()) + 0;
            allIpsInRandomSequence_.add(ips.get(random).toString());
        }
        return new ArrayList(allIpsInRandomSequence_);
    }

    @SuppressWarnings({ "rawtypes", "unused" })
    public static ArrayList <Ips> getElementsInRandomSequenceObj(List <Ips> ips)
    {
    	Set allIpsInRandomSequence_ = new HashSet();
    	int random;
    	for (Ips temp: ips)
    	{
    		random = (int) (Math.random() * ips.size()) + 0;
    		allIpsInRandomSequence_.add(ips.get(random));
    	}
    	return new ArrayList<Ips>(allIpsInRandomSequence_);
    }
    
    public static String listToString(List<String> ports)
    {
    	String s_ports = "";
    	for(String temp:ports)
    	{
    		if(ports.indexOf(temp) < ports.size()-1)
    			s_ports += temp + ":";
    		else
    			s_ports += temp;
    	}
    	return s_ports;
    }
}

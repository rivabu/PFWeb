package org.rients.com.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public final class FileUtils
{
    public static String newline = "\n";

	public static String addNumberToFile(String fileName, int number)
	{
		String prefix = fileName.substring(0, fileName.indexOf("."));
        String suffix = fileName.substring(fileName.indexOf("."));
        
        String fileNameNew = prefix+number+suffix;
        //System.out.println("filename = "+fileNameNew);
      	return fileNameNew;
	}

	
	

	public static void writeToFile(String fileName, String line)
	{
		if (fileName != null)
			{
			try
				{
				PrintWriter file = new PrintWriter(new FileOutputStream(fileName, false), false);


				synchronized (file)
				{
					file.println(line);
					file.close();
				}
			} catch (IOException e)
				{
				System.err.println("Exception opening file " + fileName + ": " + e);
			}
		}
	}
	
	public static void writeToFile(String fileName, List<String> lines, String header)
	{
		if (fileName != null)
			{
			try
				{
				PrintWriter file = new PrintWriter(new FileOutputStream(fileName, false), false);


				synchronized (file)
				{
					if(header != null)
					    file.println(header);
				    for(int i=0; i<lines.size(); i++)
					    file.println(lines.get(i));
					file.close();
				}
			} catch (IOException e)
				{
				System.err.println("Exception opening file " + fileName + ": " + e);
			}
		}
	}
	
	public static <E> void writeToFile(String fileName, List<String> lines)
	{
		writeToFile(fileName, lines, null);
	}

	   
	public static String readerToString(String filename)
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			Reader is = new FileReader(filename);
			char[] b = new char[100];
			int n;
			while((n = is.read(b)) > 0)
			{
				sb.append(b, 0, n);
			}
			
		}
		catch(IOException e)
		{
			System.out.println("error reading file: "+e.toString());
		}
			return sb.toString();
	}
	
	

    public static void cleanDirectory(String s)
    {
        try
        {
        	File file = new File(s);
        	String as[] = file.list();
        for(int i = 0; i < as.length; i++)
        {
            File file1 = new File(s + "\\" + as[i]);
            if(file1.isFile())
                file1.delete();
        }
        }
        catch(Exception e)
        {
        	System.err.println("something wrong with cleaning up directory: "+s);
        	e.printStackTrace();
        	System.exit(-1);
        }

    }
    
    public static long getNumberOfFiles(String dir)
    {
        try
        {
        	File file = new File(dir);
        	return file.list().length;
        }
        catch(Exception e)
        {
        	System.err.println("something wrong with counting files in directory: "+dir);
        	e.printStackTrace();
        	System.exit(-1);
        }
        return 0;
    }
    
    public static List<String> getFiles(String dir, String extension, boolean withExtension)
    {
        List<String> arraylist = new ArrayList<String>();
        try
        {
            File file = new File(dir);
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(dir + "\\" + as[i]);
                if(file1.isFile() && file1.getName().endsWith("." + extension)) {
                    String filename = file1.getName();
                     if (!withExtension) {
                        filename = filename.substring(0, filename.lastIndexOf("."));
                    }
                    arraylist.add(filename);
                }
            }
        }
        catch(Exception e)
        {
            System.err.println("something wrong with reading in directory: "+dir);
            e.printStackTrace();
            System.exit(-1);
        }
        return arraylist;
    }
    
        
    public static List<String> getSubdirs(String dir)
    {
        List<String> arraylist = new ArrayList<String>();
        try
        {
        	File file = new File(dir);
        	String as[] = file.list();
        	for(int i = 0; i < as.length; i++)
        	{
            	File file1 = new File(dir + "\\" + as[i]);
            	if(file1.isDirectory() )
                	arraylist.add(file1.getName());
        	}
        }
        catch(Exception e)
        {
        	System.err.println("something wrong with reading in directory: "+dir);
        	e.printStackTrace();
        	System.exit(-1);
        }
        return arraylist;
    }

    public static BufferedReader openInputFile(String s)
    {
        BufferedReader bufferedreader = null;
        try
        {
            FileReader filereader = new FileReader(s);
            bufferedreader = new BufferedReader(filereader);
        }
        catch(Exception exception)
        {
            //System.out.println("error in lezen file: " + exception);
            //exception.printStackTrace();
            return null;
        }
        return bufferedreader;
    }

    public static BufferedWriter openOutputFile(String s)
    {
        BufferedWriter bufferedwriter = null;
        try
        {
            FileWriter filewriter = new FileWriter(s);
            bufferedwriter = new BufferedWriter(filewriter);
        }
        catch(IOException ioexception)
        {
            System.err.println("error in schrijven file: " + ioexception);
            return null;
        }
        return bufferedwriter;
    }


    public static boolean fileExists(String s)
    {
        File f = new File(s);
        return f.exists();
    }


    public static BufferedWriter openOutputAppendFile(String Filename, String data)
    {
        BufferedWriter bufferedwriter = null;
        try
        {
            FileWriter filewriter = new FileWriter(Filename, true);
            bufferedwriter = new BufferedWriter(filewriter);
            bufferedwriter.write(data+"\n");
            bufferedwriter.close();
        }
        catch(IOException ioexception)
        {
            System.err.println("error in schrijven file: " + ioexception);
            return null;
        }
        return bufferedwriter;
    }


    public static void removeFile(String s)
    {
        try
        {
            File file = new File(s);
            file.delete();
        }
        catch(Exception exception)
        {
            System.err.println("error in deleten file: " + exception);
        }
       
    }
    
    public static int copyFile(String sourceName, String copyName)
    {
         
         InputStream source;  // Stream for reading from the source file.
         OutputStream copy;   // Stream for writing the copy.
         
         /* Get file names from the command line and check for the 
            presence of the -f option.  If the command line is not one
            of the two possible legal forms, print an error message and 
            end this program. */
      
         
         try {
            source = new FileInputStream(sourceName);
         }
         catch (FileNotFoundException e) {
            System.err.println("dbaccess.DBaccess.copyFile Can't find file \"" + sourceName + "\".");
            return -1;
         }
         
         
         /* Create the output stream.  If an error occurs, 
            end the program. */
   
         try {
            copy = new FileOutputStream(copyName);
         }
         catch (IOException e) {
            System.err.println("Can't open output file \"" 
                                                    + copyName + "\".");
            return -1;
         }
         
         /* Copy one byte at a time from the input stream to the output
            stream, ending when the read() method returns -1 (which is 
            the signal that the end of the stream has been reached).  If any 
            error occurs, print an error message.  Also print a message if 
            the file has been copied successfully.  */
         
         
         try {
            while (true) {
               int data = source.read();
               if (data < 0)
                  break;
               copy.write(data);
            }
            source.close();
            copy.close();
         }
         catch (Exception e) {
            System.err.println("Error occurred while copying.  ");
            System.err.println(e.toString());
         }
         return 0;
         
      
   } // end class CopyFile


}


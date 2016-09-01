/* File: Loader.java
 * Description: Loader is used for loading training data by apropriate loader, which is specified in config files.
 * Author: xorman00 <xorman00@stud.fit.vutbr.cz>
 */

package cz.vutbr.mefw.plugins.SA.loaders;

import java.util.*;
import java.util.Scanner;
import cz.vutbr.mefw.plugins.SA.src.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.regex.*;
import java.util.*;

// Class is used for founding datasets, and using apropriate loaders for loading these datasets
public class Loader
{
	// List of sub-loaders, that can be used by this loader
	private List<String> available_loaders = new ArrayList<String>();
	// Directories with training data
	private List<String> dataset_storage = new ArrayList<String>();
	private List<String> dataset_loader = new ArrayList<String>();

	// Constructor
	// We need to know, which sub-loaders this processor can use
	// and this information is stored in config file
	public Loader(String processor_name,String config_file_path) throws IOException
	{
		// Because Java ...
		FileInputStream fstream = new FileInputStream(config_file_path+"config.ini");
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)
		{
			String[] parts = strLine.split("\t");

			// line contains list compatible sub-loaders setting
			if(parts[0].equals(processor_name) && parts.length==2)
			{
				// String is splited into substrings, each containing name of compatible loader
				String[] subparts = (parts[1].replaceAll("\\s","")).split(",");
				for(int i=0;i<subparts.length;i++)
				{
					//System.out.println(subparts[i]);
					available_loaders.add(subparts[i]);
				}
			}
			// line contains list path to directory with training data
			else if(parts[0].equals("training_data_path") && parts.length == 3 )
			{
				dataset_storage.add(config_file_path+parts[1]);
				dataset_loader.add(parts[2]);
			}
		}
		fstream.close();
	}

	// We are searching thru directories with training data
	// and loading data, that can be accesed by available sub-loaders
	public void load(Dataset training_data) throws IOException
	{
		// We need to know, which files can we load with available loaders/tools
		for(int i=0;i<dataset_storage.size();i++)
		{
			// If file can be loaded by one of available loaders
			if (available_loaders.contains(dataset_loader.get(i)))
			{
				try
				{
					training_data.load(Class.forName("cz.vutbr.mefw.plugins.SA.loaders."+dataset_loader.get(i)),dataset_storage.get(i));
				}
				catch(IncompatibleClassChangeError e0)
				{
					System.err.println(e0);
					;
				}
				// sub-loader in not available
				catch(ClassNotFoundException e1)
				{
					System.err.println(e1);
					;
				}
				// something wrong with data file
				catch(IOException e2)
				{
					System.err.println(e2);
					;
				}
			}
		}
	}
}

/*
    try
    {
      DocumentBuilder builder = (DocumentBuilderFactory.newInstance()).newDocumentBuilder();
      Document doc = builder.parse(file);
      Element root = doc.getDocumentElement();

      NodeList posReviews = root.getElementsByTagName("positive_summary");
      for(int r = 0; r < posReviews.getLength(); r++)
        set.add(new Sentiment(posReviews.item(r).getTextContent(), "positive"));

      NodeList negReviews = root.getElementsByTagName("negative_summary");
      for(int r = 0; r < negReviews.getLength(); r++)
        set.add(new Sentiment(negReviews.item(r).getTextContent(), "negative"));
    }
    catch(SAXException|ParserConfigurationException e)
    {
      e.printStackTrace();
    }

    return set;
  }
  */


/*
	private static void lingpipe_sanders(Dataset training_data) throws Exception
	{
		System.out.println("Sanders tweets");
		training_data.load(SandersLoader.class, "corpora/sanders/full-corpus.csv");
		training_data.removePolarity("irrelevant");
	}
*/
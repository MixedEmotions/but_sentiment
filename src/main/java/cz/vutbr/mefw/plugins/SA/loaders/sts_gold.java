/* File: sts_gold.java
 * Description: Loader for sts_gold_v3 dataset
 * Author: xorman00 <xorman00@stud.fit.vutbr.cz>
 */

package cz.vutbr.mefw.plugins.SA.loaders;

import java.util.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.regex.*;

public class sts_gold implements LoaderInterface
{
	private final Pattern polarity = Pattern.compile("^[^\t]+");
	private final Pattern sentence = Pattern.compile("[^\t]+$");
	
	private boolean extract(String line, List<Sentiment> set)
	{
		Matcher m = polarity.matcher(line);
		Matcher n = sentence.matcher(line);
		if(m.find() && n.find() )
		{	
			if(m.group(0).equals("0"))
			{
				set.add(new Sentiment(n.group(0), "negative", "unknown"));
			}
			else if (m.group(0).equals("4")) 
			{
				set.add(new Sentiment(n.group(0), "positive", "unknown"));
			}
			else
			{
				return false;
			}
			return true;
		}
		return false;
	}

	public List<Sentiment> load(String filename) throws IOException
	{
		List<Sentiment> set = new ArrayList<Sentiment>();

		BufferedReader corpus = new BufferedReader(new FileReader(filename));

		String line;
		while((line = corpus.readLine()) != null)
		{
			this.extract(line, set);
		}
		corpus.close();
		return set;
	}

	// Main for testing purposes
	public static void main(String[] args)
	{
		sts_gold kolotoc = new sts_gold();
		try
		{
			kolotoc.load("./corpora/sts_gold_v03/raw_data.txt");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}          

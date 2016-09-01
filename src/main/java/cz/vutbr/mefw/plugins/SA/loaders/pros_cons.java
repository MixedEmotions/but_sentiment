/* File: pros_cons.java
 * Description: Loader for pros-cons dataset
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

public class pros_cons implements LoaderInterface
{
	private final Pattern polarity = Pattern.compile("<[^>]+>");
	private final Pattern sentence = Pattern.compile("(?<=>)[^<]+");
	
	private boolean extract(String line, List<Sentiment> set)
	{
		Matcher m = polarity.matcher(line);
		Matcher n = sentence.matcher(line);
		if(m.find() && n.find() )
		{	
			if(m.group(0).equals("<Cons>"))
			{
				set.add(new Sentiment(n.group(0), "negative", "unknown"));
			}
			else if (m.group(0).equals("<Pros>")) 
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
		pros_cons kolotoc = new pros_cons();
		try
		{
			kolotoc.load("./corpora/pros-cons/rt-polaritydata/.txt");
			kolotoc.load("./corpora/pros-cons/rt-polaritydata/.txt");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}          
 
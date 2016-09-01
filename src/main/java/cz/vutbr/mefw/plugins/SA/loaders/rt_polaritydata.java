/* File: rt_polaritydata.java
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

public class rt_polaritydata implements LoaderInterface
{
	public List<Sentiment> load(String filename) throws IOException
	{
		List<Sentiment> set = new ArrayList<Sentiment>();
		String line;

		// negative stuff
		BufferedReader corpus = new BufferedReader(new FileReader(filename+"rt-polarity.neg"));
		while((line = corpus.readLine()) != null)
		{
			set.add(new Sentiment(line, "negative", "unknown"));
		}
		corpus.close();

		// positive
		corpus = new BufferedReader(new FileReader(filename+"rt-polarity.pos"));
		while((line = corpus.readLine()) != null)
		{
			set.add(new Sentiment(line, "positive", "unknown"));
		}
		corpus.close();

		return set;
	}

	// Main for testing purposes
	public static void main(String[] args)
	{
		rt_polaritydata kolotoc = new rt_polaritydata();
		try
		{
			kolotoc.load("./corpora/rt-polaritydata/");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
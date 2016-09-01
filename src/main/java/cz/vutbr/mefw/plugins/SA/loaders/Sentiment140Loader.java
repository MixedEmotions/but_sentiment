package cz.vutbr.mefw.plugins.SA.loaders;

import java.util.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.regex.*;

public class Sentiment140Loader implements LoaderInterface
{
  public List<Sentiment> load(String filename) throws IOException
  {
    List<Sentiment> set = new ArrayList<Sentiment>();

    BufferedReader corpus = new BufferedReader(new FileReader(filename));
    corpus.readLine();

    String line;
    int wrong = 0;
    while((line = corpus.readLine()) != null)
    {
      Pattern p = Pattern.compile("\"([0-4])\",\"[0-9]+\",\"[^\"]+\",\"(.+)\",\"[^\"]+\",\"(.+)\"");

      Matcher m = p.matcher(line);

      if(m.find())
      {
        String polarity;
        int pol = m.group(1).charAt(0) - '0';
        if(pol == 0)
          polarity = "negative";
        else if(pol == 2)
          polarity = "neutral";
        else
          polarity = "positive";
        String topic = m.group(2);
        String sentence = m.group(3);

        set.add(new Sentiment(sentence, polarity, topic));
      }
      else
      {
        wrong++;
        System.err.printf(line);
      }
    }

    if(wrong > 0)
      System.err.println("There are "+String.valueOf(wrong)+" invalid lines.");

    corpus.close();

    return set;
  }
}

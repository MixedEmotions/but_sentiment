package cz.vutbr.mefw.plugins.SA.loaders;

import java.util.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.regex.*;

public class SandersLoader implements LoaderInterface
{
  private final Pattern patternFull = Pattern.compile("^\"([a-z]+)\",\"([a-z]+)\",\"[0-9]+\",\"[^\"]+\",\"(.+)\"$");

  private final Pattern patternPartial = Pattern.compile("^\"[a-z]+\",\"[a-z]+\",\"[0-9]+\",\"[^\"]+\",\".*$");

  private boolean extract(String line, List<Sentiment> set)
  {
    Matcher m = patternFull.matcher(line);
    if(m.find())
    {
      String topic = m.group(1);
      String polarity = m.group(2);
      String sentence = m.group(3);
      if(polarity=="irrelevant")
        return false;
      set.add(new Sentiment(sentence, polarity, topic));
      return true;
    }
    return false;
  }

  public List<Sentiment> load(String filename) throws IOException
  {
    List<Sentiment> set = new ArrayList<Sentiment>();

    BufferedReader corpus = new BufferedReader(new FileReader(filename));
    corpus.readLine();

    String line;
    while((line = corpus.readLine()) != null)
    {
      this.extract(line, set);
    }

    corpus.close();
    return set;
  }
}

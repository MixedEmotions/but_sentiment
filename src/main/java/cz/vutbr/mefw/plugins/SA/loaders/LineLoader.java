package cz.vutbr.mefw.plugins.SA.loaders;

import cz.vutbr.mefw.plugins.SA.src.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;

public class LineLoader implements LoaderInterface
{
  private final Pattern pattern = Pattern.compile("^([a-z]+)?[ \t]*(.+)$");

  public List<Sentiment> load(String filename) throws IOException
  {
    List<Sentiment> set = new ArrayList<Sentiment>();

    BufferedReader corpus = new BufferedReader(new FileReader(filename));
    int linenum = 0;
    String line;
    while((line = corpus.readLine()) != null)
    {
      ++linenum;
      Matcher m = pattern.matcher(line);
      if(m.find())
        set.add(new Sentiment(m.group(2), m.group(1)));
      else
        throw new WrongFormatException(filename, linenum);
    }

    corpus.close();
    return set;
  }
}

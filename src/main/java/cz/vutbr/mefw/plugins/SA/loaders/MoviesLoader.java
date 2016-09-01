package cz.vutbr.mefw.plugins.SA.loaders;

import java.util.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;

public class MoviesLoader implements LoaderInterface
{
  public List<Sentiment> load(String dirpath) throws IOException
  {
    List<Sentiment> set = new ArrayList<Sentiment>();

    File dir = new File(dirpath);
    for(final File polarityDir : dir.listFiles())
    {
      String polarity = polarityDir.getName();

      for(final File file : polarityDir.listFiles())
      {
        BufferedReader fr = new BufferedReader(new FileReader(file));

        String sentiment;
        while((sentiment = fr.readLine()) != null)
          if(polarity=="pos")
            polarity="positive";
          else if(polarity=="neg")
            polarity="negative";
          set.add(new Sentiment(sentiment, polarity, "movie"));
      }
    }
    return set;
  }
}

package cz.vutbr.mefw.plugins.SA.loaders;

import cz.vutbr.mefw.plugins.SA.src.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;

public class CzechReviewsLoader implements LoaderInterface
{
  public List<Sentiment> load(String file) throws IOException
  {
    List<Sentiment> set = new ArrayList<Sentiment>();

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
}

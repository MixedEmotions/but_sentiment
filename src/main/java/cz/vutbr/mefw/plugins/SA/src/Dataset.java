package cz.vutbr.mefw.plugins.SA.src;

//import cz.vutbr.mefw.plugins.SA.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * A class representing sentiment data set.
 */
public class Dataset
{
  private ArrayList<Sentiment> set = new ArrayList<Sentiment>();
  private String id = null;

  public Dataset()
  {

  }

  /**
   * @param id Identifier of the set
   */
  public Dataset(String id)
  {
    this.id = id;
  }

  /**
   * This constructor creates a set from given file containing sentiments.
   *
   * @param loader Loader class to use to extract sentimens from given file
   * @param  file A file containing sentiments
   * @param id Identifier of the set
   * @throws IOException when an I/O error occurs
   */
  public Dataset(Class<?> loader, String file, String id) throws IOException
  {
    this.id = id;
    this.load(loader, file);
  }

  /**
   * Returns identifier of the set.
   *
   * @return The id
   */
  public String id()
  {
    return this.id;
  }

  /**
   * Adds a sentiment to the set.
   *
   * @param sentiment The sentiment to add
   */
  public void add(Sentiment sentiment)
  {
    set.add(sentiment);
  }

  /**
   * Adds all sentiments of given set to this set.
   *
   * @param joinSet A set whose sentiments will be added
   */
  public void add(Dataset joinSet)
  {
    for(int i = 0; i < joinSet.size(); i++)
      set.add(joinSet.get(i));
  }

  /**
   * Returns the number of sentiments in this set.
   *
   * @return The size
   */
  public int size()
  {
    return set.size();
  }

  /**
   * Rename a sentiment polarity in this set. This will rename the polarity in all sentiments the set contains.
   *
   * @param from The initial polarity name
   * @param to The replacement polarity name
   */
  public void renamePolarity(String from, String to)
  {
    for(Sentiment sentiment : set)
    {
      String polarity = sentiment.getPolarity();
      if(polarity != null && polarity.equals(from))
        sentiment.setPolarity(to);
    }
  }

  /**
   * Removes all sentiments from the set that are marked with given polarity.
   *
   * @param polarity The polarity to remove
   */
  public void removePolarity(String polarity)
  {
    for(int i = 0; i < set.size(); i++)
    {
      String pol = set.get(i).getPolarity();
      if(pol != null && pol.equals(polarity))
      {
        set.remove(i);
        i--;
      }
    }
  }

  /**
   * Returns a sentiment by index.
   *
   * @param index The index
   * @return The sentiment
   */
  public Sentiment get(int index)
  {
    return set.get(index);
  }

  /**
   * Returns all sentiment polarities that the data set contains.
   *
   * @return A set of polarities
   */
  public Set<String> getPolarities()
  {
    Set<String> polarities = new HashSet<String>();
    for(Sentiment sentiment : set)
    {
      String polarity = sentiment.getPolarity();
      if(polarity != null)
        polarities.add(polarity);
    }
    return polarities;
  }

  /**
   * Trims the data set in the way that all sentiment polarities have the same count of sentiments assigned.
   */
  public void uniform()
  {
    Map<String, Integer> count = new HashMap<String, Integer>();
    for(Sentiment sentiment : set)
    {
      Integer c = count.get(sentiment.getPolarity());
      if(c == null)
        c = new Integer(0);
      count.put(sentiment.getPolarity(), c+1);
    }

    int newsize = Integer.MAX_VALUE;
    for (Map.Entry<String, Integer> e : count.entrySet())
      if(e.getValue() < newsize)
        newsize = e.getValue();

    Set<String> polarities = this.getPolarities();
    for(String pol : polarities)
      count.put(pol, new Integer(0));

    for(int i = 0; i < set.size(); i++)
    {
      String polarity = set.get(i).getPolarity();
      Integer c = count.get(polarity);
      if(c >= newsize)
      {
        set.remove(i);
        i--;
      }
      else
        count.put(polarity, c+1);
    }
  }

  /**
   * Shuffles the set.
   */
  public void shuffle()
  {
    Collections.shuffle(set);
  }

  /**
   * Returns a copy of this set divided into two pieces respecting given proportion.
   *
   * @param proportion A value between 0.0 and 1.0
   * @return Pieces of the set
   */
  public Dataset[] split(double proportion)
  {
    Dataset[] sets = new Dataset[2];
    sets[0] = new Dataset();
    sets[1] = new Dataset();

    Map<String, ArrayList<Sentiment>> sentimentsByPolarity = new HashMap<String, ArrayList<Sentiment>>();

    for(int i = 0; i < this.size(); i++)
    {
      Sentiment sentiment = this.get(i);
      ArrayList<Sentiment> list = sentimentsByPolarity.get(sentiment.getPolarity());
      if(list == null)
        sentimentsByPolarity.put(sentiment.getPolarity(), (list = new ArrayList<Sentiment>()));
      list.add(sentiment);
    }

    for (Map.Entry<String, ArrayList<Sentiment>> e : sentimentsByPolarity.entrySet())
    {
      ArrayList<Sentiment> list = e.getValue();
      int firstPartSize = (int)(proportion * (double)list.size());

      for(int i = 0; i < firstPartSize; i++)
        sets[0].add(list.get(i));
      for(int i = firstPartSize; i < list.size(); i++)
        sets[1].add(list.get(i));
    }

    return sets;
  }

  /**
   * Returns a copy of this set divided into n same-sized pieces.
   *
   * @param n Number of pieces
   * @return Pieces of the set
   */
  public Dataset[] splitn(int n)
  {
    Dataset[] sets = new Dataset[n];
    for(int i = 0; i < n; i++)
      sets[i] = new Dataset();
    int x = 0;
    for(int i = 0; i < set.size(); i++)
    {
      sets[x].add(set.get(i));
      x = (x == n-1) ? 0 : x+1;
    }
    return sets;
  }

  /**
   * Returns a copy of this set.
   *
   * @return Copy of the set
   */
  public Dataset copy()
  {
    Dataset copiedSet = new Dataset(this.id+"-copy"+String.valueOf(System.currentTimeMillis()));
    for(Sentiment sentiment : set)
      copiedSet.add(sentiment);
    return copiedSet;
  }

  /**
   * Adds sentiments extracted from a file into the set.
   *
   * @param loader Loader class to use to extract sentimens from given file
   * @param  file A file containing sentiments
   * @throws IOException when an I/O error occurs
   */
  public void load(Class<?> loader, String file) throws IOException
  {
    try
    {
      Method load = loader.getMethod("load", String.class);
      @SuppressWarnings("unchecked") //This should be safe as long as interface Loader is not corrupted
      ArrayList<Sentiment> list = (ArrayList<Sentiment>) load.invoke( loader.newInstance(), file);
      set.addAll(list);
    }
    catch(NoSuchMethodException e)
    {
      System.err.println("Class "+loader.getName()+" does not implement method ArrayList<Sentiment> load(String)");
    }
    catch(InvocationTargetException e)
    {
      Throwable cause = e.getCause();
      if(cause instanceof IOException)
        throw (IOException)cause;
      else
        e.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Shrinks the set into given number of sentiments. If the set contains less sentiments than that, nothing happens.
   *
   * @param size The desired size
   */
  public void shrink(int size)
  {
    if(this.size() <= size)
      return;
    ArrayList<Sentiment> shrinked = new ArrayList<Sentiment>();
    for(int i = 0; i < size; i++)
      shrinked.add(set.get(i));
    set = shrinked;
  }

  /**
   * Shrinks the set into given number of sentiments per each polarity. If less sentiments per particular polarity are present, nothing happens.
   *
   * @param size The desired number of sentiments per each polarity
   */
  public void shrinkEachPolarity(int size)
  {
    Map<String, Integer> examplesUsed = new HashMap<String, Integer>();
    for(int i = 0; i < set.size(); i++)
    {
      String polarity = set.get(i).getPolarity();
      Integer used = examplesUsed.get(polarity);
      if(used == null)
        used = new Integer(0);

      if(used >= size)
        set.remove(i--);
      else
        examplesUsed.put(polarity, used+1);
    }
  }

  /**
   * Returns all sentiments of the set.
   *
   * @return The sentimens
   */
  public List<Sentiment> getAll()
  {
    return set;
  }

  /**
   * Returns all aspect categories that the set contains.
   *
   * @return The categories
   */
  public Set<String> getCategories()
  {
    Set<String> categories = new HashSet<String>();
    for(Sentiment sent : set)
      for(Opinion op : sent.getOpinions())
        categories.add(op.category);
    return categories;
  }

  /**
   * Returns all entities that the set contains.
   *
   * @return The entities
   */
  public Set<String> getEntities()
  {
    Set<String> entities = new HashSet<String>();
    Set<String> categories = this.getCategories();
    for(String category : categories)
      entities.add(category.substring(0, category.indexOf("#")));
    return entities;
  }

  /**
   * Returns all attributes that the set contains.
   *
   * @return The attributes
   */
  public Set<String> getAttributes()
  {
    Set<String> attributes = new HashSet<String>();
    Set<String> categories = this.getCategories();
    for(String category : categories)
      attributes.add(category.substring(category.indexOf("#")+1));
    return attributes;
  }

  /**
   * Remove a sentiment from the set.
   *
   * @param sentiment The sentiment to remove
   */
  public void remove(Sentiment sentiment)
  {
    this.set.remove(sentiment);
  }
}

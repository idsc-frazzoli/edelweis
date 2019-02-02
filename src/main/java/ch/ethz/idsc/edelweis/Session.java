// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

public class Session {
  private final UserProperties userProperties;
  private final Map<String, BulkParser> map = new LinkedHashMap<>();
  public final String user;

  public Session(String user) {
    this.user = user;
    userProperties = new UserProperties(user);
    // ---
    Properties properties = userProperties.load("projects");
    Properties ignore = userProperties.load("ignore");
    for (String project : new TreeSet<>(properties.stringPropertyNames()))
      map.put(project, new BulkParser(new File(properties.getProperty(project)), project, ignore));
  }

  public List<BulkParserPair> testPairs() {
    List<BulkParserPair> list = new LinkedList<>();
    for (BulkParser bulkParser : map.values())
      if (bulkParser.nonTest()) {
        String test = bulkParser.name() + "-test";
        if (map.containsKey(test))
          list.add(new BulkParserPair(bulkParser, map.get(test)));
      }
    return list;
  }

  public Collection<BulkParser> bulkParsers() {
    return map.values();
  }

  public BulkParser bulkParser(String string) {
    return map.get(string);
  }

  public void showStats() {
    for (Entry<String, BulkParser> entry : map.entrySet()) {
      System.out.println(entry.getKey());
      BulkParser bulkParser = entry.getValue();
      System.out.println(" test=" + bulkParser.nonTest());
      System.out.println(" skip=" + bulkParser.unknownExtensions());
    }
  }
}

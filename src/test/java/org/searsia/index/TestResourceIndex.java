package org.searsia.index;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.searsia.index.ResourceIndex;
import org.searsia.engine.Resource;

public class TestResourceIndex {
	
    private static final Logger LOGGER = Logger.getLogger("org.searsia");
    private static final String PATH  = "target/index-test";
    private static final String INDEX = "test";
    private static ResourceIndex engines;
   
    @BeforeClass
    public static void setUp() throws Exception {
        LOGGER.removeAllAppenders();
        LOGGER.addAppender(new NullAppender());
    	engines = new ResourceIndex(PATH, INDEX);
    	Resource engine = searsia();
    	engines.putMother(engine);
    }
    
    @AfterClass
    public static void lastThing() throws IOException, XPathExpressionException, JSONException {
    	engines.put(newby());
    	checkFiles();
    }
     
    private static Resource utwente() throws XPathExpressionException, JSONException {
        JSONObject json = new JSONObject(
            "{\"apitemplate\":\"http://utwente.nl/search?q={q}\",\"id\":\"567\",\"name\":\"UT\"}"
        );
    	return new Resource(json);
    }
    
    private static Resource searsia() throws XPathExpressionException, JSONException {
        JSONObject json = new JSONObject(
            "{\"apitemplate\":\"http://searsia.com/?q={q}\",\"id\":\"1234\",\"privateparameters\":{\"api\":\"topsecret\"}}"
        );
    	return new Resource(json);
    }
    
    private static Resource newby() throws XPathExpressionException, JSONException {
        JSONObject json = new JSONObject(
            "{\"apitemplate\":\"http://new.com/?q={q}\",\"id\":\"new\",\"privateparameters\":{\"apikey\":\"secret\"}}"
        );
    	return new Resource(json);
    }
    
    private static Resource me() throws XPathExpressionException, JSONException {
        JSONObject json = new JSONObject(
            "{\"apitemplate\":\"http://me.org\",\"id\":\"me\",\"name\":\"Me\"}"
        );
        return new Resource(json);
    }
    
    
    public static void checkFiles() throws IOException, XPathExpressionException, JSONException {
    	Resource e1 = me();
    	Resource e2 = engines.getMyself();
    	Assert.assertTrue("Trying to retrieve me", e1.equals(e2));
    	Resource e3 = utwente();
    	Resource e4 = engines.get(e3.getId());
    	Assert.assertTrue("Trying to retrieve utwente", e3.equals(e4));
    	Assert.assertTrue("No private parameters expected", e4.getJsonPrivateParameters() == null);
    	Resource e6 = engines.get(newby().getId());
    	Assert.assertTrue("Private parameters expected", e6.getJsonPrivateParameters() != null);
    	Assert.assertTrue("Top 1", engines.topValues("anything", 1).size() == 1);
    	Assert.assertTrue("Top 2", engines.topValues(null, 2).size() == 2);
    }
	
    @Test
    public void addResource() throws XPathExpressionException, JSONException {
    	Resource e1 = utwente();
    	engines.put(e1);
    	Resource e2 = engines.get(e1.getId());
    	Assert.assertTrue("Add", e1.equals(e2));
    }
 
    @Test
    public void addMe() throws XPathExpressionException, JSONException {
    	Resource e1 = me();
    	engines.putMyself(e1);
    	Resource e2 = engines.getMyself();
    	Assert.assertTrue("Me", e1.equals(e2));
    }

    @Test
    public void getMother() throws XPathExpressionException, JSONException {
    	Resource e1 = searsia();
    	Resource e2 = engines.getMother();
    	Assert.assertTrue("Mother", e1.equals(e2));
    }


}

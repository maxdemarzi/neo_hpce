package com.hpce.unit;

import com.hpce.Labels;
import com.hpce.NeoService;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class GetUser {
    private NeoService service;
    private GraphDatabaseService db;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        service = new NeoService();
        populateDb(db);
    }

    public void populateDb(GraphDatabaseService db) {
        try ( Transaction tx = db.beginTx() ) {
            Node node = db.createNode(Labels.User);
            node.setProperty("username", "A");

            tx.success();
        }
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldGetUser() throws IOException {
        Response response = service.getUser("A", db);
        HashMap actual = objectMapper.readValue((String)response.getEntity(), HashMap.class);
        assertEquals(userA, actual);
    }

    static HashMap<String, Object> userA = new HashMap<String, Object>(){{
        put("username","A");
    }};
}

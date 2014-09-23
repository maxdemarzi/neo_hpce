package com.hpce.unit;

import com.hpce.NeoService;
import com.hpce.RelationshipTypes;
import com.hpce.TestHelper;
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
import java.util.ArrayList;

import static com.hpce.TestHelper.*;
import static org.junit.Assert.assertEquals;

public class GetUserFOFs {
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
            Node userA = TestHelper.createUsers(db, "A");
            Node userB = TestHelper.createUsers(db, "B");
            Node userC = TestHelper.createUsers(db, "C");
            Node userD = TestHelper.createUsers(db, "D");
            Node userE = TestHelper.createUsers(db, "E");
            Node userF = TestHelper.createUsers(db, "F");

            /*
              A-B-C
                B-D
                B-F
              A-E-C
              A-F
             */

            userA.createRelationshipTo(userB, RelationshipTypes.FRIENDS);
            userA.createRelationshipTo(userE, RelationshipTypes.FRIENDS);
            userA.createRelationshipTo(userF, RelationshipTypes.FRIENDS);
            userB.createRelationshipTo(userC, RelationshipTypes.FRIENDS);
            userE.createRelationshipTo(userC, RelationshipTypes.FRIENDS);
            userB.createRelationshipTo(userD, RelationshipTypes.FRIENDS);
            userB.createRelationshipTo(userF, RelationshipTypes.FRIENDS);

            tx.success();
        }
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldGetUserFOFs() throws IOException {
        Response response = service.getUserFOFs("A", db);
        ArrayList actual = objectMapper.readValue((String)response.getEntity(), ArrayList.class);
        assertEquals(userAFOFs, actual);
    }
}

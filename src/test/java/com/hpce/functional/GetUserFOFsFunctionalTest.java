package com.hpce.functional;

import com.hpce.RelationshipTypes;
import com.hpce.TestHelper;
import com.sun.jersey.api.client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.NeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.neo4j.server.rest.JaxRsResponse;
import org.neo4j.server.rest.RestRequest;

import java.io.IOException;
import java.util.ArrayList;

import static com.hpce.TestHelper.*;
import static org.junit.Assert.assertEquals;

public class GetUserFOFsFunctionalTest {
    private static final Client CLIENT = Client.create();
    private static NeoServer server;
    private static RestRequest request;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        server = CommunityServerBuilder.server()
                .withThirdPartyJaxRsPackage("com.hpce", "/v1")
                .onPort(8484)
                .build();
        server.start();
        request = new RestRequest(server.baseUri().resolve("/v1"), CLIENT);
        populate(server.getDatabase().getGraph());
    }

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
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
        server.stop();
    }

    @Test
    public void shouldGetUserFOFs() throws IOException {
        JaxRsResponse response = request.get("service/user/A/fofs");
        ArrayList actual = objectMapper.readValue(response.getEntity(), ArrayList.class);
        assertEquals(userAFOFs, actual);
    }

}

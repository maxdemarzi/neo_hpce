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

@Ignore
public class GetUserFriendsFunctionalTest {
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

            /*
              A-B
              A-C
              A-D
             */

            userA.createRelationshipTo(userB, RelationshipTypes.FRIENDS);
            userA.createRelationshipTo(userC, RelationshipTypes.FRIENDS);
            userA.createRelationshipTo(userD, RelationshipTypes.FRIENDS);

            tx.success();
        }
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldGetUserFriends() throws IOException {
        JaxRsResponse response = request.get("service/user/A/friends");
        ArrayList actual = objectMapper.readValue(response.getEntity(), ArrayList.class);
        assertEquals(userAFriends, actual);
    }

}

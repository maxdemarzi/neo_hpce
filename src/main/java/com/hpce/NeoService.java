package com.hpce;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Path("/service")
public class NeoService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final ReverseComparator REVERSE_COMPARATOR = new ReverseComparator();

    @GET
    @Path("/helloworld")
    public String helloWorld() {
        return "Hello World!";
    }

    @GET
    @Path("/warmup")
    public String warmUp(@Context GraphDatabaseService db) {
        try ( Transaction tx = db.beginTx()) {
            for ( Node n : GlobalGraphOperations.at(db).getAllNodes()) {
                n.getPropertyKeys();
                for ( Relationship relationship : n.getRelationships()) {
                    relationship.getPropertyKeys();
                    relationship.getStartNode();
                }
            }
        }
        return "Warmed up and ready to go!";
    }

    @GET
    @Path("/migrate")
    public String migrate(@Context GraphDatabaseService db) {
        boolean migrated;

        try (Transaction tx = db.beginTx()) {
            migrated = db.schema().getConstraints().iterator().hasNext();
        }

        if (migrated){
            return "Already Migrated!";
        } else {
            // Perform Migration
            try (Transaction tx = db.beginTx()) {
                Schema schema = db.schema();
                schema.constraintFor(Labels.User)
                        .assertPropertyIsUnique("username")
                        .create();
                tx.success();
            }
            // Wait for indexes to come online
            try (Transaction tx = db.beginTx()) {
                Schema schema = db.schema();
                schema.awaitIndexesOnline(1, TimeUnit.DAYS);
            }
            return "Migrated!";
        }
    }

    @GET
    @Path("/user/{username}")
    public Response getUser(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        Map<String, Object> results = new HashMap<>();

        try ( Transaction tx = db.beginTx() )
        {
            //Get the user and their properties
        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/user/{username}/friends")
    public Response getUserFriends(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<String> results = new ArrayList<>();

        try ( Transaction tx = db.beginTx() )
        {
           // Get the username of the user's friends
        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/user/{username}/fofs")
    public Response getUserFOFs(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        HashMap<Node, int[]> fofs = new HashMap<>();
        try ( Transaction tx = db.beginTx() )
        {
            final Node user = IteratorUtil.singleOrNull(db.findNodesByLabelAndProperty(Labels.User, "username", username));

            findFofs(fofs, user);
            List<Map.Entry<Node, int[]>> fofList = orderFofs(fofs);
            returnFofs(results, fofList.subList(0, Math.min(fofList.size(), 10)));
        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();

    }

    private void findFofs(HashMap<Node, int[]> fofs, Node user) {
        Set<Node> friends = new HashSet<>();

        if (user != null){
            getFirstLevelFriends(user, friends);
            getSecondLevelFriends(fofs, user, friends);
        }
    }

    private void getFirstLevelFriends(Node user, Set<Node> friends) {
        // Get their Friends
    }

    private void getSecondLevelFriends(HashMap<Node, int[]> fofs, Node user, Set<Node> friends) {
        // Get the friends of their friends and group count them

        // Remove the user and their Friends from Friends of Friends list
    }

    private void returnFofs(List<Map<String, Object>> results, List<Map.Entry<Node, int[]>> fofList) {
        Map<String, Object> resultsEntry;
        Map<String, Object> fofEntry;
        Node fof;
        for (Map.Entry<Node, int[]> entry : fofList) {
            resultsEntry = new HashMap<>();
            fofEntry = new HashMap<>();
            fof = entry.getKey();

            for (String prop : fof.getPropertyKeys()) {
                fofEntry.put(prop, fof.getProperty(prop));
            }

            resultsEntry.put("fof", fofEntry);
            resultsEntry.put("friend_count", entry.getValue()[0]);
            results.add(resultsEntry);
        }
    }


    private List<Map.Entry<Node, int[]>> orderFofs(HashMap<Node, int[]> fofs) {
        List<Map.Entry<Node, int[]>> fofList = new ArrayList<>(fofs.entrySet());
        Collections.sort(fofList, REVERSE_COMPARATOR);
        return fofList;
    }
}

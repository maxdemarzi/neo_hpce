package com.hpce;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class TestHelper {

    public static Node createUsers(GraphDatabaseService db, String name) {
        Node node = db.createNode(Labels.User);
        node.setProperty("username", name);
        return node;
    }

    public static HashMap<String, Object> userA = new HashMap<String, Object>(){{
        put("username","A");
    }};

    public static final ArrayList<String> userAFriends = new ArrayList<String>(2){{
        add("B");
        add("C");
        add("D");
    }};

    public static final ArrayList<HashMap<String, Object>> userAFOFs = new ArrayList<HashMap<String, Object>>(2){{

        HashMap<String, Object> entryOne = new HashMap<String, Object>(){{
            put("friend_count",2);
            put("fof", new HashMap<String, Object>(){{
                put("username", "C");
            }});
        }};

        HashMap<String, Object> entryTwo = new HashMap<String, Object>(){{
            put("friend_count",1);
            put("fof", new HashMap<String, Object>(){{
                put("username", "D");
            }});
        }};

        add(entryOne);
        add(entryTwo);

    }};
}

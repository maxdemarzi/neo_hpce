package com.hpce.unit;

import com.hpce.NeoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import static org.junit.Assert.assertEquals;

public class NeoServiceTest {
    private NeoService service;
    private GraphDatabaseService db;

    @Before
    public void setUp() {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        service = new NeoService();
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldRespondToHelloWorld() {
        assertEquals("Hello World!", service.helloWorld());
    }

    @Test
    public void shouldWarmUp() {
        assertEquals("Warmed up and ready to go!", service.warmUp(db));
    }

    @Test
    public void shouldMigrate() {
        assertEquals("Migrated!", service.migrate(db));
        assertEquals("Already Migrated!", service.migrate(db));
    }

}

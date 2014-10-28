neo_hpce
========

Neo4j High Performance Compressed Edition Code

1. Download Neo4j:

        curl -O http://dist.neo4j.org/neo4j-community-2.1.3-unix.tar.gz
or

http://dist.neo4j.org/neo4j-community-2.1.3-windows.zip

2. Uncompress it:


        tar -xvzf neo4j-community-2.1.3-unix.tar.gz

or unzip it.

        
3. Move it to just "neo4j":
        
        mv neo4j-community-2.1.3 neo4j
        
4. Build the extension:

        mvn clean package

5. Copy target/neo_hpce-1.0-SNAPSHOT.jar to the plugins/ directory of your Neo4j server.

        cp target/neo_hpce-1.0-SNAPSHOT.jar neo4j/plugins/.

6. Configure Neo4j to use the extension by adding this line to neo4j/conf/neo4j-server.properties:

        org.neo4j.server.thirdparty_jaxrs_classes= com.hpce=/v1

7. Start Neo4j server.

        neo4j/bin/neo4j start

8. Query it over HTTP:

        curl http://localhost:7474/v1/service/helloworld

9. Try it from the Neo4j UI:

        :GET /v1/service/helloworld
        
        
10. Make some Users:
        
        WITH ["Jennifer","Michelle","Tanya","Julie","Christie","Sophie","Amanda","Khloe","Sarah","Kaylee"] AS names 
        FOREACH (r IN range(0,100000) | CREATE (:User {username:names[r % size(names)]+r}))
        
11. Relate those Users:
        
        MATCH (u1:User),(u2:User)
        WITH u1,u2
        LIMIT 5000000
        WHERE rand() < 0.1
        MERGE (u1)-[:FRIENDS]->(u2);        

12. Migrate Schema:
        
        :GET /v1/service/migrate        
        
13. Remove @Ignore from test/java/com.hpce/unit/GetUser.class and test/java/com.hpce/functional/GetUser.class       

14. Make tests Pass.

15. Repeat 13 and 14 for GetUserFriends and GetUserFOFs

16. Rebuild extension, replace previous one, restart Neo4j:

        mvn clean package
        cp target/neo_hpce-1.0-SNAPSHOT.jar neo4j/plugins/.
        neo4j/bin/neo4j restart

17. Try it:
        
        :GET /v1/service/user/Jennifer0
        :GET /v1/service/user/Jennifer0/friends
        :GET /v1/service/user/Jennifer0/fofs
        
18. Enjoy.        

19. Extra Credit:

    Add LIMIT and Pagination to fofs.
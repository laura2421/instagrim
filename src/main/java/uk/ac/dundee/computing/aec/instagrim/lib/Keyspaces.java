package uk.ac.dundee.computing.aec.instagrim.lib;


import com.datastax.driver.core.*;

public final class Keyspaces {

    public Keyspaces() {

    }

    public static void SetUpKeySpaces(Cluster c) {
        try {
            //Add some keyspaces here
            String createkeyspace = "create keyspace if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String CreatePicTable = "CREATE TABLE if not exists instagrimYan.Pics ("
                    + " user varchar,"
                    + " picid uuid, "
                    + " interaction_time timestamp,"
                    + " title varchar,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + " processedlength int,"
                    + " type  varchar,"
                    + " name  varchar,"
                    + " thumbnum int,"
                    + " messagenum int,"
                    + " PRIMARY KEY (picid)"
                    + ")";
            String Createuserpiclist = "CREATE TABLE if not exists instagrimYan.userpiclist (\n"
                    + "picid uuid,\n"
                    + "user varchar,\n"
                    + "pic_added timestamp,\n"
                    + "PRIMARY KEY (user,pic_added)\n"
                    + ") WITH CLUSTERING ORDER BY (pic_added desc);";
            String CreateCommentTable = "CREATE TABLE if not exists instagrimYan.comments ("                    
                    + " comment varchar,"
                    + " commentator varchar,"
                    + " picid uuid, "
                    + " interaction_time timestamp,"                   
                    + " PRIMARY KEY (comment)"
                    + ")";
            String Createpiccommentlist = "CREATE TABLE if not exists instagrimYan.piccommentlist (\n"
                    + "comment varchar,\n"
                    + "picid uuid,\n"
                    + "comment_added timestamp,\n"
                    + "PRIMARY KEY (picid,comment_added)\n"
                    + ") WITH CLUSTERING ORDER BY (comment_added desc);";
            String Createfollowrecommendlist = "CREATE TABLE if not exists instagrimYan.followrecommendlist (\n"
                    + "user varchar,\n"
                    + "recommend varchar,\n"
                    + "reason varchar,\n"
                    + "recommend_added timestamp,\n"
                    + "PRIMARY KEY (user,recommend_added)\n"
                    + ") WITH CLUSTERING ORDER BY (recommend_added desc);";
            String CreateAddressType = "CREATE TYPE if not exists instagrimYan.address (\n"
                    + "      street text,\n"
                    + "      city text,\n"
                    + "      zip int\n"
                    + "  );";
            String CreateUserProfile = "CREATE TABLE if not exists instagrimYan.userprofiles (\n"
                    + "      login text PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      gender text,\n"
                    + "      first_name text,\n"
                    + "      last_name text,\n"
                    + "      email set<text>,\n"
                    + "      follower set<text>,\n"
                    + "      following map<text,int>,\n"
                    + "      userImgNum text,\n"
                    + "      addresses  map<text, frozen <address>>\n"
                    + "  );";
            String CreateUserPicTable = "CREATE TABLE if not exists instagrimYan.userpic ("
                    + " user varchar,"
                    + " picid uuid, "
                    + " interaction_time timestamp,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + " processedlength int,"
                    + " type  varchar,"
                    + " PRIMARY KEY (user)"
                    + ")";
            
            
            Session session = c.connect();
            try {
                PreparedStatement statement = session
                        .prepare(createkeyspace);
                BoundStatement boundStatement = new BoundStatement(
                        statement);
                ResultSet rs = session
                        .execute(boundStatement);
                System.out.println("created instagrimYan ");
            } catch (Exception et) {
                System.out.println("Can't create instagrimYan " + et);
            }

            //now add some column families 
            System.out.println("" + CreatePicTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreatePicTable);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create tweet table " + et);
            }
            System.out.println("" + Createuserpiclist);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(Createuserpiclist);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create user pic list table " + et);
            }
            System.out.println("" + CreateCommentTable);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateCommentTable);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create comment list table " + et);
            }
            System.out.println("" + Createpiccommentlist);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(Createpiccommentlist);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create pic comment list table " + et);
            }
            
            System.out.println("" + Createfollowrecommendlist);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(Createfollowrecommendlist);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create follow recommend list table " + et);
            }
            System.out.println("" + CreateAddressType);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateAddressType);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Address type " + et);
            }
            System.out.println("" + CreateUserProfile);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateUserProfile);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Address Profile " + et);
            }
            System.out.println("" + CreatePicTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateUserPicTable);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create tweet table " + et);
            }
            session.close();    

        } catch (Exception et) {
            System.out.println("Other keyspace or coulm definition error" + et);
        }

    }
}

package org.apache.zookeeper.server.createNode;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class TesTDataTreeCreateNodePitImprove {

    private DataTree dataTree;
    private String path;
    private byte[] data;;
    private List<ACL> acl;
    private long ephemeralOwner;
    private int parentCVersion;
    private long zxid;
    private long time;
    private Stat outputStat;
    private int length;
    private boolean ephemeralNode;
    private boolean parentCVersionTest;
    private boolean isExpectedException;

    public TesTDataTreeCreateNodePitImprove(
            String path,
            byte[] data,
            List<ACL> acl,
            long ephemeralOwner,
            int parentCVersion,
            long zxid,
            long time,
            Stat outputStat,
            boolean isExpectedException,
            boolean ephemeralNode,
            boolean parentCVersionTest){

        this.path = path;
        this.data = data;
        this.acl = acl;
        this.ephemeralOwner = ephemeralOwner;
        this.parentCVersion = parentCVersion;
        this.zxid = zxid;
        this.time = time;
        this.outputStat = outputStat;
        this.isExpectedException = isExpectedException;
        this.ephemeralNode = ephemeralNode;
        this.parentCVersionTest = parentCVersionTest;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //{PATH,                        DATA,               ACL,                            EPHEMERAL_OWNER,        PARENTCVERSION,     ZXID,       TIME,       OUTPUTSTAT,         EXCEPTION,      EPHEMERAL,     PARENT_VERSION}
                //After PIT reports
                {"/a",                          new byte[1],        ZooDefs.Ids.CREATOR_ALL_ACL,    0L,                     3,                  1L,         1L,         null,               false,          false,          false},         //case 23 <-- Path Test
                {"/abc",                        new byte[20000],    ZooDefs.Ids.OPEN_ACL_UNSAFE,    122L,                   0,                  1L,         1L,         null,               false,          false,          false},         //case 24 <-- Path Test
                {"/abcd",                       new byte[0],        ZooDefs.Ids.CREATOR_ALL_ACL,    0L,                     -2,                 1L,         1L,         null,               false,          false,          false},         //case 25 <-- Path Test
                {"/a@9m",                       null,               ZooDefs.Ids.READ_ACL_UNSAFE,    122L,                   0,                  1L,         1L,         null,               false,          false,          false},         //case 26 <-- Path Test & Special Character
                {"/node1/node2/node3",          new byte[1000],     ZooDefs.Ids.CREATOR_ALL_ACL,    122L,                   0,                  0L,         1L,         null,               false,          false,          false},         //case 27 <-- Path Test & Tree
                {"/n1/n2/n3/n4",                new byte[100000],   ZooDefs.Ids.READ_ACL_UNSAFE,    122L,                   0,                  0L,         1L,         null,               false,          false,          false},         //case 28 <-- Path Test & Tree
                {"/n1/n2/n3/n4/n5/n6/n7/n8",    new byte[100000],   ZooDefs.Ids.READ_ACL_UNSAFE,    122L,                   0,                  0L,         1L,         null,               false,          false,          false},         //case 29 <-- Path Test & Tree
                {"/a/b",                        new byte[4000],     ZooDefs.Ids.OPEN_ACL_UNSAFE,    0L,                     1,                  1000L,      1L,         null,               true,           true,           false},         //case 30 <-- Invalid Path
                {"/a/b",                        new byte[4000],     ZooDefs.Ids.READ_ACL_UNSAFE,    23L,                    1,                  1L,         1L,         null,               true,           true,           false},         //case 31 <-- Invalid Path
                {"/a",                          new byte[1],        ZooDefs.Ids.CREATOR_ALL_ACL,    0L,                     3,                  1L,         1L,         null,               false,          true,           true},          //case 32 <-- parentCVersion
                {"/abc",                        new byte[20000],    ZooDefs.Ids.OPEN_ACL_UNSAFE,    122L,                   0,                  1L,         1L,         null,               false,          true,           true},          //case 33 <-- parentCVersion
                {"/abcd",                       new byte[0],        ZooDefs.Ids.CREATOR_ALL_ACL,    0L,                     -2,                 1L,         1L,         null,               false,          true,           true},          //case 34 <-- parentCVersion
                {"/a@9m",                       null,               ZooDefs.Ids.READ_ACL_UNSAFE,    122L,                   0,                  1L,         1L,         null,               false,          true,           true},          //case 35 <-- parentCVersion
        });
    }

    // Create a tree full of not ephemeral nodes
    public void setUp() throws KeeperException.NoNodeException, KeeperException.NodeExistsException {

        this.dataTree = new DataTree();
        String[] elementsOfPath = path.split("/");
        int n = elementsOfPath.length;
        String newPat;
        String oldPath = "/";
        String parentPath = "/";
        int count = 1;


        if(!this.ephemeralNode) {

            for (String element : elementsOfPath) {

                newPat = oldPath + element;
                if (count == n)
                    break;

                this.dataTree.createNode(newPat, new byte[1000], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, this.dataTree.getNode(parentPath).stat.getCversion(), 0L, 1L);
                oldPath = newPat;
                parentPath = newPat;

                if (count != 1) {

                    oldPath += "/";
                    count++;
                    continue;

                }
                count++;
            }

            this.length = n;
        }

    }

    @Test
    public void createNodeTest() {

        Exception exception = null;
        if (!this.ephemeralNode && !this.parentCVersionTest) {

            try {

                setUp();
                this.dataTree.createNode(this.path, this.data, this.acl, this.ephemeralOwner, this.parentCVersion, this.zxid, this.time, this.outputStat);

            } catch (KeeperException.NoNodeException | KeeperException.NodeExistsException | NullPointerException | StringIndexOutOfBoundsException e) {
                exception = e;
            }
            if (exception != null) {
                Assert.assertTrue(this.isExpectedException);
            }

        } else if (this.ephemeralNode && !this.parentCVersionTest) {

            Exception error = null;
            int n = 0;
            try {

                setUp();
                this.dataTree.createNode(this.path, this.data, this.acl, this.ephemeralOwner, this.parentCVersion, this.zxid, this.time, this.outputStat);
                n = this.dataTree.getEphemerals().size();

            } catch (KeeperException.NoNodeException | KeeperException.NodeExistsException e) {
                error = e;
            }

            if (error == null && this.ephemeralOwner != 0L) {
                Assert.assertEquals(1, n);
            } else {
                Assert.assertEquals(0, n);
            }

        } else if (!this.ephemeralNode && this.parentCVersionTest) {

            int n = 0;
            Exception error = null;
            DataNode root = null;
            try {
                setUp();
                root = this.dataTree.getNode("/");
                n = root.stat.getCversion();
                this.dataTree.createNode(this.path, this.data, this.acl, this.ephemeralOwner, this.parentCVersion, this.zxid, this.time, this.outputStat);
                root = this.dataTree.getNode("/");

            } catch (KeeperException.NoNodeException | KeeperException.NodeExistsException e) {
                error = e;
            }

            if (error == null && root != null) {

                if (n <= this.parentCVersion)
                    Assert.assertEquals(this.parentCVersion, root.stat.getCversion());
                if (n > this.parentCVersion) {
                    Assert.assertNotEquals(this.parentCVersion, root.stat.getCversion());
                    Assert.assertEquals(n, root.stat.getCversion());
                }
            }

            if (error == null) {
                Assert.assertTrue(this.isExpectedException);
            }

        }
    }
}

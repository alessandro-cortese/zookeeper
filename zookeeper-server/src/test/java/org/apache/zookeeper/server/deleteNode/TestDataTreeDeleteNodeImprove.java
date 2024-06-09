package org.apache.zookeeper.server.deleteNode;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.DataTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestDataTreeDeleteNodeImprove {

    private DataTree dataTree;
    private String path;
    private long zxid;
    private boolean deleteParent;
    private long ephemeralOwner;
    private boolean zookeeperPath;
    private boolean isExpectedException;


    public TestDataTreeDeleteNodeImprove(

            String path,
            long zxid,
            boolean isExpectedException,
            boolean parent,
            long ephemeralOwner,
            boolean zookeeperPath) {

        this.path = path;
        this.zxid = zxid;
        this.isExpectedException = isExpectedException;
        this.deleteParent = parent;
        this.ephemeralOwner = ephemeralOwner;
        this.zookeeperPath = zookeeperPath;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{

                //{PATH_STATE,                          ZXID,       EXCEPTION,      DELETE_PARENT,      EPHEMERAL_OWNER,        ZOOKEEPER_PATH}

                //After Ba-Dua and JaCoCo reports

                {"/abab/cd",                            1L,         true,           true,               0L,                     false},                 //case 7    <-- Parent Null
                {"/abcd",                               1L,         true,           false,              0L,                     false},                 //case 8    <-- No node in path
                {"/abcb",                               1L,         false,          false,              1L,                     false},                 //case 9    <-- Ephemeral Owner case
                {"/abcc",                               1L,         false,          false,              Long.MIN_VALUE,         false},                 //case 10   <-- Ephemeral Owner case
                {"/abcc",                               1L,         false,          false,              1L,                     false},                 //case 11   <-- Ephemeral Owner case
                {"/zookeeper/quota/zookeeper_stats",    1L,         false,          false,              0,                      true}                   //case 12   <-- Zookeeper Path
        });

    }

    public void setUp() throws KeeperException.NoNodeException, KeeperException.NodeExistsException {

        this.dataTree = new DataTree();

        if(this.zookeeperPath)
            this.dataTree.createNode("/zookeeper/quota/zookeeper_stats", new byte[100], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, 2, 1L, 1L, new Stat());


        if(this.deleteParent) {

            this.dataTree.createNode("/abab", new byte[100], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, 1, 1L, 1L, null);
            this.dataTree.createNode(this.path, new byte[100], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, 1, 1L, 1L, null);
            this.dataTree.deleteNode("/abab", 0L);

        }

        if(this.ephemeralOwner != 0)
            this.dataTree.createNode(this.path, new byte[100], ZooDefs.Ids.CREATOR_ALL_ACL, this.ephemeralOwner, 1, 0L, 1L, null);

    }

    @Test
    public void deleteNodeTest(){

        Exception exception = null;

        try {
            setUp();
            this.dataTree.deleteNode(this.path, this.zxid);
        } catch (KeeperException.NoNodeException | KeeperException.NodeExistsException e) {
            exception = e;
        }

        if(exception != null){
            Assert.assertTrue(this.isExpectedException);
        }

    }

}

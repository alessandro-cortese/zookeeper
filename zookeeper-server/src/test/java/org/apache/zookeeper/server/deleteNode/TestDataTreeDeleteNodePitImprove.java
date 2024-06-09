package org.apache.zookeeper.server.deleteNode;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.server.DataNode;
import org.apache.zookeeper.server.DataTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestDataTreeDeleteNodePitImprove {

    private DataTree dataTree;
    private String path;
    private long zxid;
    private boolean deleteParent;
    private long ephemeralOwner;
    private boolean isExpectedException;


    public TestDataTreeDeleteNodePitImprove(

            String path,
            long zxid,
            boolean isExpectedException,
            boolean deleteParent,
            long ephemeralOwner) {

        this.path = path;
        this.zxid = zxid;
        this.isExpectedException = isExpectedException;
        this.deleteParent = deleteParent;
        this.ephemeralOwner = ephemeralOwner;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{

                //{PATH_STATE,                              ZXID,       EXCEPTION,      DELETE_PARENT,      EPHEMERAL_OWNER}

                //After Pit report

                {"/abab/cd",                                15L,        true,           true,               122L},                //case 13
                {"/abcd",                                   1L,         true,           false,              15L},               //case 14
                {"/abcb",                                   1L,         true,           false,              1L},                //case 15
                {"/ab.cc",                                  1L,         true,           false,              122L},              //case 16
                {"/abcc.",                                  1L,         true,           false,              23L},               //case 17
                {"/node1./node@2/node3",                    -1L,        true,           true,               0L},                //case 18
                {"/ab/ab/cd",                               5L,         true,           true,               23L},               //case 19
                {"/zookeeper/../ab",                        9L,         true,           true,               122L},              //case 20
        });

    }

    public void setUp() throws KeeperException.NoNodeException, KeeperException.NodeExistsException {

        this.dataTree = new DataTree();

        if(this.deleteParent) {

            String newPath;
            String oldPath = "/";
            String parent = "/";
            int count = 1;

            String [] pathElements = path.split("/");
            for(String pathElement: pathElements){
                newPath =  oldPath  + pathElement;
                this.dataTree.createNode(newPath, new byte[1000], ZooDefs.Ids.CREATOR_ALL_ACL, this.ephemeralOwner, this.dataTree.getNode(parent).stat.getCversion(), 0, 1);
                oldPath = newPath;
                parent = newPath;
                if(count!=1){
                    oldPath += "/";
                    count++;
                    continue;
                }
                count++;
            }
            int lastSlash = path.lastIndexOf('/');
            String parentName = path.substring(0, lastSlash);
            this.dataTree.deleteNode(parentName, 0);


        }else {

            this.dataTree.createNode("/abab", new byte[100], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, 1, 0L, 1L, null);
            this.dataTree.createNode(this.path, new byte[100], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, 3, 1L, 1L, null);
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

        if (exception != null) {
            Assert.assertTrue(this.isExpectedException);}

    }

}

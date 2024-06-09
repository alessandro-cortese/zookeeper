package org.apache.zookeeper.server.deleteNode;


import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.server.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestDataTreeDeleteNode {

    private final static String VALID_PATH = "/abab";
    private final static String INVALID_PATH = "abc";

    private DataTree dataTree;
    private String path;
    private long zxid;
    private PATH_STATE pathState;
    private boolean isExpectedException;

    public TestDataTreeDeleteNode(

            PATH_STATE pathState,
            long zxid,
            boolean isExpectedException) {

        this.pathState = pathState;
        this.zxid = zxid;
        this.isExpectedException = isExpectedException;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {

                //{PATH_STATE,          ZXID,       EXCEPTION}

                {PATH_STATE.EMPTY,      1L,         true},          //case 1
                {PATH_STATE.NULL,       0L,         true},          //case 2
                {PATH_STATE.VALID,      1L,         false},         //case 3
                {PATH_STATE.INVALID,    1L,         true},          //case 4
                {PATH_STATE.VALID,      1L,         false},         //case 5
                {PATH_STATE.VALID,      -1L,        true}           //case 6

        });

    }

    public void setUp() throws KeeperException.NoNodeException, KeeperException.NodeExistsException {

        switch (this.pathState) {
            case EMPTY:
                this.path = "";
                break;
            case NULL:
                this.path = null;
                break;
            case VALID:
                this.path = VALID_PATH;
                break;
            case INVALID:
                this.path = INVALID_PATH;
                break;
        }

        this.dataTree = new DataTree();
        this.dataTree.createNode(VALID_PATH, new byte [100], ZooDefs.Ids.CREATOR_ALL_ACL, 0L, 1, 0L, 1L);

    }

    @Test
    public void deleteNodeTest(){

        Exception exception = null;

        try{

            setUp();
            this.dataTree.deleteNode(this.path, this.zxid);

        } catch (KeeperException.NoNodeException | KeeperException.NodeExistsException | StringIndexOutOfBoundsException | NullPointerException e) {
            exception = e;
        }

        if(exception != null){
            Assert.assertTrue(this.isExpectedException);
        }

    }

}

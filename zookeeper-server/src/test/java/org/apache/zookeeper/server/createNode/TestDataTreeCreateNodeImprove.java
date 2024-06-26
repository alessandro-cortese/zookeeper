package org.apache.zookeeper.server.createNode;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@RunWith(Parameterized.class)
public class TestDataTreeCreateNodeImprove {

    private final static String VALID_PATH = "/abab";
    private final static String INVALID_PATH = "abc";
    private final static String LONG_PATH = "/This/is/a/long/path/to/see/if/it/is/managed/correctly";
    private final static String ZOOKEEPER_PATH = "/zookeeper/quota/zookeeper_stats";
    private final static String TTL_NODE_PATH = "/node.1/node.2/node.3";

    private DataTree dataTree;
    private String path;
    private byte[] data;;
    private List<ACL> acl;
    private long ephemeralOwner;
    private int parentCVersion;
    private long zxid;
    private long time;
    private Stat outputStat;

    private PATH_STATE pathState;
    private ACL_STATE aclState;
    private DATA_STATE dataState;
    private STAT_STATE statState;

    private boolean isExpectedException;
    private boolean duplicate;

    public TestDataTreeCreateNodeImprove(
            PATH_STATE pathState,
            DATA_STATE dataState,
            ACL_STATE aclState,
            long ephemeralOwner,
            int parentCVersion,
            long zxid,
            long time,
            STAT_STATE statState,
            boolean isExpectedException,
            boolean duplicate){

        this.pathState = pathState;
        this.dataState = dataState;
        this.aclState = aclState;
        this.ephemeralOwner = ephemeralOwner;
        this.parentCVersion = parentCVersion;
        this.zxid = zxid;
        this.time = time;
        this.statState = statState;
        this.isExpectedException = isExpectedException;
        this.duplicate = duplicate;

    }


    @Before
    public void setUp() throws Exception {

        //set path
        switch (this.pathState) {
            case NULL:
                this.path = null;
                break;
            case EMPTY:
                this.path = "";
                break;
            case VALID:
                this.path = VALID_PATH;
                break;
            case INVALID:
                this.path = INVALID_PATH;
                break;
            case LONG_PATH:
                this.path = LONG_PATH;
                break;
            case ZOOKEEPER_PATH:
                this.path = ZOOKEEPER_PATH;
                break;
            case TTL_PATH:
                this.path = TTL_NODE_PATH;
                break;
        }

        //set data
        switch (this.dataState) {
            case NULL:
                this.data = null;
                break;
            case EMPTY:
                this.data = new byte[100];
                break;
            case VALID:
                SecureRandom secureRandom = new SecureRandom();
                this.data = new byte[100];
                secureRandom.nextBytes(data);
                break;
            case INVALID:
                this.data = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 };
                break;
            case EXCEED:
                SecureRandom secureRandom2 = new SecureRandom();
                this.data = new byte[1048577];
                secureRandom2.nextBytes(data);
                break;
        }

        //set ACL
        List<ACL> validAcl = new ArrayList<>();
        List<ACL> invalidAcl = new ArrayList<>();
        //invalid ACL
        ACL aclRules1 = new ACL(-1, new Id("world", "anyone"));
        ACL aclRules2 = new ACL(ZooDefs.Perms.ALL, new Id("invalidschema", "identifier"));
        invalidAcl.add(aclRules1);
        invalidAcl.add(aclRules2);
        //valid ACL
        validAcl = ZooDefs.Ids.CREATOR_ALL_ACL;

        switch (this.aclState) {
            case VALID:
                this.acl = validAcl;
                break;
            case INVALID:
                this.acl = invalidAcl;
                break;
            case NULL:
                this.acl = null;
                break;
            case EMPTY:
                this.acl = new ArrayList<>();
                break;
        }

        //set outputStat
        switch (this.statState) {
            case NULL:
                this.outputStat = null;
                break;
            case VALID:
                this.outputStat = new Stat();
                break;
            case INVALID:
                this.outputStat = new Stat();
                this.outputStat.setCzxid(-1L);
                break;
        }

        this.dataTree = new DataTree();

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //{PATH_STATE,              DATA_STATE,         ACL_STATE,              EPHEMERAL_OWNER,        PARENTCVERSION,     ZXID,       TIME,       OUTPUTSTAT,         EXCEPTION,  DUPLICATE}

                //After ba-dua and JaCoCo reports

                {PATH_STATE.VALID,          DATA_STATE.VALID,   ACL_STATE.VALID,        Long.MIN_VALUE,         1,                  1L,         1L,         STAT_STATE.VALID,   true,       false},     //case 18
                {PATH_STATE.VALID,          DATA_STATE.VALID,   ACL_STATE.VALID,        2L,                     1,                  1L,         1L,         STAT_STATE.VALID,   true,       false},     //case 19
                {PATH_STATE.LONG_PATH,      DATA_STATE.VALID,   ACL_STATE.VALID,        1L,                     1,                  1L,         1L,         STAT_STATE.VALID,   true,       false},     //case 20
                {PATH_STATE.ZOOKEEPER_PATH, DATA_STATE.EMPTY,   ACL_STATE.VALID,        0L,                     2,                  1L,         1L,         STAT_STATE.VALID,   false,      false},     //case 21
                {PATH_STATE.VALID,          DATA_STATE.VALID,   ACL_STATE.VALID,        0L,                     0,                  1L,         1L,         STAT_STATE.VALID,   true,       true}       //case 22

        });
    }

    @Test
    public void createNodeTest(){

        Exception exception = null;
        try{
            this.dataTree.createNode(this.path, this.data, this.acl, this.ephemeralOwner, this.parentCVersion, this.zxid, this.time, this.outputStat);
            if(this.duplicate)
                this.dataTree.createNode(this.path, this.data, this.acl, this.ephemeralOwner, this.parentCVersion, this.zxid, this.time, this.outputStat);
        } catch (KeeperException.NoNodeException | KeeperException.NodeExistsException | NullPointerException | StringIndexOutOfBoundsException e) {
            exception = e;
        }
        if(exception != null){
            Assert.assertTrue(this.isExpectedException);
        }
    }



}

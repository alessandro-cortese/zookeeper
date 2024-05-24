package org.apache.zookeeper.recipes.leader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

public class LeaderElectionSupportTest {

    private LeaderElectionSupport electionSupport;
    private ZooKeeper mockZooKeeper;

    @Before
    public void setUp() {
        electionSupport = new LeaderElectionSupport();
        mockZooKeeper = mock(ZooKeeper.class);
        electionSupport.setZooKeeper(mockZooKeeper);
    }

    @Test(expected = IllegalStateException.class)
    public void startWithoutZooKeeperInstance() {
        electionSupport.start();
    }

    @Test(expected = IllegalStateException.class)
    public void startWithoutHostName() {
        electionSupport.setZooKeeper(mockZooKeeper);
        electionSupport.start();
    }


    @Test
    public void getLeaderHostNameWithNoLeaderOffers() throws KeeperException, InterruptedException {
        electionSupport.setRootNodeName("/test");
        when(mockZooKeeper.getChildren("/test", false)).thenReturn(java.util.Collections.emptyList());
        assertEquals(null, electionSupport.getLeaderHostName());
    }
}

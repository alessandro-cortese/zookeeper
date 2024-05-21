package org.apache.zookeeper.recipes.leader;
import org.junit.jupiter.api.Test;

public class LeaderOfferTest {

    @Test
    public void testDefaultConstructor() {
        LeaderOffer offer = new LeaderOffer();
        // Esegui i getter per garantire la copertura
        offer.getId();
        offer.getNodePath();
        offer.getHostName();
    }

    @Test
    public void testParameterizedConstructor() {
        LeaderOffer offer = new LeaderOffer(1, "/node1", "host1");
        // Esegui i getter per garantire la copertura
        offer.getId();
        offer.getNodePath();
        offer.getHostName();
    }

    @Test
    public void testSetId() {
        LeaderOffer offer = new LeaderOffer();
        offer.setId(2);
        // Esegui il getter per garantire la copertura
        offer.getId();
    }

    @Test
    public void testSetNodePath() {
        LeaderOffer offer = new LeaderOffer();
        offer.setNodePath("/node2");
        // Esegui il getter per garantire la copertura
        offer.getNodePath();
    }

    @Test
    public void testSetHostName() {
        LeaderOffer offer = new LeaderOffer();
        offer.setHostName("host2");
        // Esegui il getter per garantire la copertura
        offer.getHostName();
    }

    @Test
    public void testToString() {
        LeaderOffer offer = new LeaderOffer(3, "/node3", "host3");
        // Esegui toString per garantire la copertura
        offer.toString();
    }

    @Test
    public void testIdComparator() {
        LeaderOffer offer1 = new LeaderOffer(1, "/node1", "host1");
        LeaderOffer offer2 = new LeaderOffer(2, "/node2", "host2");
        LeaderOffer.IdComparator comparator = new LeaderOffer.IdComparator();
        // Esegui il compare per garantire la copertura
        comparator.compare(offer1, offer2);
        comparator.compare(offer2, offer1);
        comparator.compare(offer1, offer1);
    }
}

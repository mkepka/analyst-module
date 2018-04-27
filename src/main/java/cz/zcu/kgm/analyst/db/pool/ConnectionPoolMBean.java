package cz.zcu.kgm.analyst.db.pool;

/**
 * @author jezekjan
 *
 */
public interface ConnectionPoolMBean {

    public int getNumConnections();
    public int getNumUsedConnections() throws InterruptedException;
}
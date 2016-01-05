package com.patriotcoder.pihomesecurity.dataobjects;

/**
 * Data object representing a pi (or other microcomputer) node.
 *
 * @author jeffrey
 */
public class SecNode {

    /**
     * Ip of the Pi (or other microcomputer).
     */
    private String ip;
    
    /**
     * Name (or function) of this pi (or other microcomputer).
     */
    private String name;

    /**
     * Default constructor.
     */
    public SecNode() {
    }

    /**
     * Constructor.
     * @param ip Node ip (or DNS).
     * @param name Name of this node.
     */
    public SecNode(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SecNode{" + "ip=" + ip + ", name=" + name + '}';
    }

    
}

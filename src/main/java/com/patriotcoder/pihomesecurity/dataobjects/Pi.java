package com.patriotcoder.pihomesecurity.dataobjects;

/**
 * Data object representing a pi node.
 *
 * @author jeffrey
 */
public class Pi {

    /**
     * Ip of the Pi.
     */
    private String ip;
    
    /**
     * Name (or function) of this pi.
     */
    private String name;

    public Pi() {
    }

    public Pi(String ip, String name) {
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
        return "Pi{" + "ip=" + ip + ", name=" + name + '}';
    }

    
}

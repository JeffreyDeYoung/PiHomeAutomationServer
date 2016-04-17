package com.patriotcoder.pihomesecurity;

/**
 * Constants for this application.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class Constants
{

    /**
     * Name of the Docussandra database for this application.
     */
    public static final String DB = "pihomeautomation";

    /**
     * Name of the Docussandra sensor nodes table.
     */
    public static final String SENSOR_NODES_TABLE = "sensor_nodes";

    /**
     * Name of the Docussandra sensor nodes name index.
     */
    public static final String SENSOR_NODES_TABLE_NAME_INDEX = "name";

    /**
     * Name of the Docussandra sensor nodes index that indicates if a node is
     * running.
     */
    public static final String SENSOR_NODES_TABLE_RUNNING_INDEX = "running";
}

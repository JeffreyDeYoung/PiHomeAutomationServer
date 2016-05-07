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
     * Name of the Docussandra nodes table.
     */
    public static final String NODES_TABLE = "nodes";

    /**
     * Name of the Actor Ability Status table.
     */
    public static final String ACTOR_ABILITY_STATUS_TABLE = "actor_ability_status";

    /**
     * Name of the Docussandra actor ability name index.
     */
    public static final String ACTOR_ABILITY_STATUS_NAME_INDEX = "name";

    /**
     * Name of the Docussandra nodes name index.
     */
    public static final String NODES_TABLE_NAME_INDEX = "name";

    /**
     * Name of the Docussandra nodes index that indicates if a node is running.
     */
    public static final String NODES_TABLE_RUNNING_INDEX = "running";

    /**
     * Name of the Docussandra nodes index that indicates the type of node it
     * is.
     */
    public static final String NODE_TYPE_INDEX = "type";
}

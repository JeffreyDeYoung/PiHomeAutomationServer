package com.patriotcoder.automation.plugin.jar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Jeffrey DeYoung
 */
public class ServerSideAction {
  @JsonProperty
  private String name;
  @JsonProperty
  private String state;

  public ServerSideAction() {}


  public ServerSideAction(String name, String state) {
    this.name = name;
    this.state = state;
  }

  public String getAbilityName() {
    return this.getName().split("\\Q_\\E")[1];
  }

  public String getActorName() {
    return this.getName().split("\\Q_\\E")[0];
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

  /**
   * @return the state
   */
  public String getState() {
    return state;
  }

  /**
   * @param state the state to set
   */
  public void setState(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return "ServerSideAction{" + "name=" + name + ", state=" + state + '}';
  }

}

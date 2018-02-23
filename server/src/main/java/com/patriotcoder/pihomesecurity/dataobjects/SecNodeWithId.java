/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package com.patriotcoder.pihomesecurity.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

/**
 * Wrapper for SecNode that maps it to a Docussandra ID. Will not (de)serialize the id.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class SecNodeWithId extends SecNode {

  /**
   * Docussandra UUID for this node.
   */
  @JsonIgnore
  private UUID id;

  /**
   * Docussandra UUID for this node.
   *
   * @return the id
   */
  public UUID getId() {
    return id;
  }

  /**
   * Docussandra UUID for this node.
   *
   * @param id the id to set
   */
  public void setId(UUID id) {
    this.id = id;
  }

}

/*
 * Copyright 2013 8D Technologies, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of 8D Technologies, Inc.
 * Use is subject to license terms.
 */

package com.sunnyd.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Manager
{

  public static void main(String[] args)
  {
    final Logger logger = LoggerFactory.getLogger(Manager.class);
    logger.info("Hello world");
  }

  public HashMap<Object, Object> find(int id){
    HashMap<Object, Object> Bean = new HashMap<Object, Object>();
    switch (id) {
    case 1:
      Bean.put("firstName", "bitch");
      Bean.put("lastName", "please");
      break;
    case 2:
      Bean.put("firstName", "Mike");
      Bean.put("lastName", "Pham");
      break;
    case 3:
      return null;
    default:
      break;
    }

    return Bean;
  }
}

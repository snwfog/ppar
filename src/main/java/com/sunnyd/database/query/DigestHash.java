package com.sunnyd.database.query;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class DigestHash
{
  public static void main(String[] args)
  {
    String test = getShaHash("I LOVE PORC SANDWHICH");
  }

  public static String getShaHash(String toHash)
  {
    StringBuffer shab = new StringBuffer();

    try
    {
      shab = new StringBuffer();
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] charArray = digest.digest(toHash.getBytes("UTF-8"));
      for (int i = 0; i < charArray.length; i++)
      {
        shab.append(Integer.toString((charArray[i] & 0xff) + 0x100, 16).substring(1));
      }
    }
    catch (NoSuchAlgorithmException e)
    {

    }
    catch (UnsupportedEncodingException e)
    {

    }

    return shab.toString();
  }
}

/*****************************************************************************
 *
 * AreaType.java
 * -------------
 *
 * Copyright (C) 1999 Adasys AG, Kronenstr. 38, 8006 Zurich, Switzerland
 *
 * Revision 0.2  February 1999    Sascha Brawer <sb@adasys.ch>
 *
 *****************************************************************************/

package ch.interlis.ili2c.metamodel;


/** The type which is represented in INTERLIS using the keyword AREA.
*/
public class AreaType extends SurfaceOrAreaType
{
  /** Constructs a new AreaType.
  */
  public AreaType ()
  {
  }



  /** Returns <code>AREA</code>. */
  public String toString ()
  {
    return "AREA";
  }


}

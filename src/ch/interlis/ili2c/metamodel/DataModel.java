/*****************************************************************************
 *
 * DataModel.java
 * --------------
 *
 * Copyright (C) 1999 Adasys AG, Kronenstr. 38, 8006 Zurich, Switzerland
 *
 * Revision 0.2  February 1999    Sascha Brawer <sb@adasys.ch>
 *
 *****************************************************************************/

package ch.interlis.ili2c.metamodel;


/** An Interlis Model which is used for modelling data (and not,
    for instance, types).

    @author Sascha Brawer, sb@adasys.ch
*/
public class DataModel extends Model
{
  /** Constructs a new DataModel. */
  public DataModel()
  {
  }


  /** Returns a String consisting of <code>DATA MODEL</code> followed
              by the model's name.
  */
public String toString()
  {
    return "MODEL " + getScopedName(/*no scope */ null);
  }
}

/*****************************************************************************
 *
 * LocalAttribute.java
 * -------------------
 *
 * Copyright (C) 1999 Adasys AG, Kronenstr. 38, 8006 Zurich, Switzerland
 *
 * Revision 0.2  February 1999    Sascha Brawer <sb@adasys.ch>
 *
 *****************************************************************************/

package ch.interlis.ili2c.metamodel;


/** An attribute whose type is neither relational nor composed, but
    stored locally.

    @author    Sascha Brawer
*/
public class LocalAttribute extends AttributeDef
{
	protected Evaluable[] basePaths;
	private boolean subdivision=false;
	private boolean continuous=false;

  public LocalAttribute()
  {
  }



  /* Documentation Note
     ------------------
     Make sure to propagate manually any changes
     to the documentation for the superclass, i.e. AttributeDef.
  */
  /** Changes the domain of an attribute. The domain is a restriction
      on the set of possible values that this attribute can have.
      A LocalAttribute must not have "non-local" domains; therefore,
      its domain can not be a ReferenceType, a CompositionType or
      a TypeAlias that is aliasing either a ReferenceType or
      a CompositionType.


      <p>In JavaBeans terminology, the <code>domain</code>
      property is both <em>bound</em> and <em>constrained</em>.
      This means that any interested party can ask for being
      informed about changes of the property value by registering
      as a <code>PropertyChangeListener</code>. In addition,
      subscribers may oppose to changes by registering as a
      <code>VetoableChangeListener</code>.


      @param domain The new domain of this attribute.


      @exception java.lang.IllegalArgumentException if
                 <code>domain</code> is <code>null</code>.


      @exception java.lang.IllegalArgumentException If <code>domain</code>
                 is a CompositionType, a ReferenceType, or a TypeAlias that
                 is aliasing either a CompositionType or a ReferenceType.

      @exception java.lang.IllegalArgumentException if
                 <code>domain</code> is abstract, but this
                 attribute is not abstract. Only abstract
                 attributes can have abstract domains.


      @exception java.beans.PropertyVetoException if some
                 VetoableChangeListener has registered for changes
                 of the <code>domain</code> property
                 and does not agree with the change.
  */
  public void setDomain (Type domain)
    throws java.beans.PropertyVetoException
  {
    Type    realDomain; // FIXME: unused ?

    if (domain == null)
      throw new IllegalArgumentException(rsrc.getString("err_nullNotAcceptable"));

    realDomain = domain.resolveAliases();


    super.setDomain(domain);
  }

  /** attribute should take its value from the attributes referenced by the given paths
  */
  public void setBasePaths (Evaluable[] basePaths)
  {

    if ((basePaths != null) && (domain != null))
    {
      for (int i = 0; i < basePaths.length; i++)
      {
        Type typ = null;


        if (basePaths[i] != null)
          ; // TODO typ = Type.findReal (basePaths[i].getType());


        if (typ != null)
        {
          if (typ.isAbstract())
            throw new IllegalArgumentException (formatMessage (
              "err_projectionAttribute_abstractBase",
              this.toString(), basePaths[i].toString()));


          typ.checkTypeExtension (domain);
        }
      }
    }


    this.basePaths = basePaths;
  }
  public Evaluable[] getBasePaths ()
  {
    return basePaths;
  }

	public boolean isContinuous() {
		return continuous;
	}

	public boolean isSubdivision() {
		return subdivision;
	}

	public void setContinuous(boolean b) {
		continuous = b;
	}

	public void setSubdivision(boolean b) {
		subdivision = b;
	}

}

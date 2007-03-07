/*****************************************************************************
 *
 * Parameter.java
 * --------------
 *
 * Copyright (C) 1999 Adasys AG, Kronenstr. 38, 8006 Zurich, Switzerland
 *
 * Revision 0.2  February 1999    Sascha Brawer <sb@adasys.ch>
 *
 *****************************************************************************/
 
package ch.interlis.ili2c.metamodel;

import java.util.*;

/** A Parameter.
    
    @version   May 26, 1999
    @author    Sascha Brawer
*/
public class Parameter extends AbstractLeafElement
{
  protected String        name = "";
  protected Type          type = null;
  protected Parameter     extending = null;
  protected boolean       _final = false;
  
  public Parameter()
  {
  }
    

  /** Returns a String that designates this view. This is useful
      to generate error messages, window titles etc. However,
      you probably would not want to use this method for generating
      Interlis description files because the lack of support for
      name scoping rules; use <code>getScopedName</code> instead.
      
      @return A String consisting of the letters <code>VIEW</code>
              followed by the fully scoped name of this view.
      
      @see #getScopedName(ch.interlis.Container)
  */
  public String toString()
  {
    return "PARAMETER " + getScopedName (null);
  }


  /** Returns a dot-separated name sequence which correctly
      designates this parameter in a specified name space.
      
      @param scope The naming context in question. If you
                   pass <code>null</code>, a fully scoped
                   name is returned.
  */
  public String getScopedName (Container scope)
  {
    Model enclosingModel, scopeModel;
    Viewable enclosingViewable, scopeViewable;
    
    enclosingModel = (Model) getContainer(Model.class);
    if (enclosingModel == null)
      return getName();
    
    enclosingViewable = (Viewable) getContainer (Viewable.class);
    if (scope != null)
    {
      scopeViewable = (Viewable) scope.getContainerOrSame (Viewable.class);
      scopeModel = (Model) scope.getContainerOrSame (Model.class);
    }
    else
    {
      scopeViewable = null;
      scopeModel = null;
    }
    
    if ((scopeViewable != null) && scopeViewable.isExtending (enclosingViewable))
      return getName ();
    
    if ((enclosingViewable != null) && (enclosingViewable != scopeViewable))
      return enclosingViewable.getScopedName (null) + "." + getName();
    
    if ((enclosingModel != null) && (enclosingModel != scopeModel))
      return enclosingModel.getScopedName (null) + "." + getName();
    
    return getName ();
  }


  /** Returns the value of the <code>name</code> property
      which indicates the name of this parameter without any scope
      prefixes.

      @see #setName(java.lang.String)
      @see #getScopedName(ch.interlis.Container)
  */
  public String getName()
  {
    return name;
  }


  /** Sets the value of the <code>name</code> property.
      Parameters are identified and used by specifying their name.

      <p>In JavaBeans terminology, the <code>name</code>
      property is both <em>bound</em> and <em>constrained</em>.
      This means that any interested party can ask for being
      informed about changes of the property value by registering
      as a <code>PropertyChangeListener</code>. In addition,
      subscribers may oppose to changes by registering as a
      <code>VetoableChangeListener</code>.
      
      @param name The new name for this parameter.

      @exception java.lang.IllegalArgumentException if <code>name</code>
                 is <code>null</code>, an empty String, too long
                 or does otherwise not conform to the syntax of
                 acceptable INTERLIS names.

      @exception java.lang.IllegalArgumentException if the name
                 would conflict with another parameter. The
                 only acceptable conflict is with the parameter that
                 this view directly extends.
                 
      @exception java.beans.PropertyVetoException if some
                 VetoableChangeListener has registered for
                 changes of the <code>name</code> property
                 and does not agree with the change.
  */
  public void setName(String name)
    throws java.beans.PropertyVetoException
  {
    String oldValue = this.name;
    String newValue = name;
    
    checkNameSanity (name, /* empty ok? */ false);
    
    /* Make sure that the new name does not conflict
       with the name of another Topic, except the
       one that this object is extending directly.
    */
    checkNameUniqueness(newValue, Parameter.class, getExtending(),
      "err_parameter_duplicateName");

    fireVetoableChange("name", oldValue, newValue);
    this.name = newValue;
    firePropertyChange("name", oldValue, newValue);
  }
  
  
  public Type getType()
  {
    return type;
  }
  
  
  public void setType (Type type)
    throws java.beans.PropertyVetoException
  {
    Type oldValue = this.type;
    Type newValue = type;
    Type realType = Type.findReal (newValue);
    
    if (realType != null)
    {
      if (realType instanceof CompositionType)
      {
        throw new IllegalArgumentException (formatMessage (
          "err_parameter_nonLocalType",
          this.toString ()));
      }
    
      if (realType instanceof ReferenceType)
      {
        Table referred = (Table)((ReferenceType) realType).getReferred();
        TransferDescription td = (referred == null)
          ? null
          : (TransferDescription) referred.getContainer (TransferDescription.class);
      
        if ((referred != null) && !referred.isExtending (td.INTERLIS.METAOBJECT))
          throw new IllegalArgumentException (formatMessage (
            "err_parameter_refToNonMeta",
            this.toString (),
            referred.toString ()
          ));
      }
    }
    
    fireVetoableChange ("type", oldValue, newValue);
    if (newValue != null)
      newValue.setExtending (extending == null ? null : extending.getType());

    this.type = newValue;
    firePropertyChange("type", oldValue, newValue);
  }
  
  
  public Parameter getExtending ()
  {
    return extending;
  }
  
  public void setExtending (Parameter extending)
    throws java.beans.PropertyVetoException
  {
    Parameter oldValue = this.extending;
    Parameter newValue = extending;
    
    if (oldValue == newValue)
      return;
    
    if ((newValue != null) && (newValue._final))
      throw new IllegalArgumentException(
        formatMessage ("err_cantExtendFinal", newValue.toString()));
    
    /* Ensure that the extension graph will be acyclic. */
	if ((newValue != null) && newValue.isExtendingIndirectly(this))
	{
      throw new IllegalArgumentException (
        formatMessage ("err_cyclicExtension", this.toString(),
                       newValue.toString()));
    }

    if (type != null)
    {
      if (newValue == null)
        type.setExtending (null);
      else
        type.setExtending (newValue.getType ());
    }

    fireVetoableChange ("extending", oldValue, newValue);
    this.extending = newValue;
    firePropertyChange ("extending", oldValue, newValue);
  }


  /** @return whether or not <code>this</code> is extending
              <code>dd</code>
  */
  protected boolean isExtendingIndirectly (Parameter dd)
  {
    for (Parameter parent = this; parent != null;
         parent = parent.extending)
    {
      if (parent == dd)
        return true;
    }
    return false;
  }
  
  

  public boolean isDependentOn (Element e)
  {
    if (e instanceof Parameter)
      return isExtendingIndirectly((Parameter) e);
    
    if ((e instanceof Type) && (type != null))
      return type.isDependentOn (e);

    return false;
  }
}

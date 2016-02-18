package org.apache.ibatis.reflection;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * This code has been copied from class org.springframework.util.ReflectionUtils.
 * <p>
 * Spring version: 3.1.3
 * 
 * @author gargii
 *
 */
public class ReflectionUtils {

  /**
   * Perform the given callback operation on all matching methods of the given class and
   * superclasses.
   * <p>
   * The same named method occurring on subclass and superclass will appear twice, unless excluded
   * by a {@link MethodFilter}.
   * 
   * @param clazz class to start looking at
   * @param mc the callback to invoke for each method
   * @see #doWithMethods(Class, MethodCallback, MethodFilter)
   */
  public static void doWithMethods( Class<?> clazz, MethodCallback mc ) throws IllegalArgumentException {
    doWithMethods( clazz, mc, null );
  }

  /**
   * Perform the given callback operation on all matching methods of the given class and
   * superclasses (or given interface and super-interfaces).
   * <p>
   * The same named method occurring on subclass and superclass will appear twice, unless excluded
   * by the specified {@link MethodFilter}.
   * 
   * @param clazz class to start looking at
   * @param mc the callback to invoke for each method
   * @param mf the filter that determines the methods to apply the callback to
   */
  public static void doWithMethods( Class<?> clazz, MethodCallback mc, MethodFilter mf ) throws IllegalArgumentException {

    // Keep backing up the inheritance hierarchy.
    Method[] methods = clazz.getDeclaredMethods();
    for ( Method method : methods ) {
      if ( mf != null && !mf.matches( method ) ) {
        continue;
      }
      try {
        mc.doWith( method );
      }
      catch ( IllegalAccessException ex ) {
        throw new IllegalStateException( "Shouldn't be illegal to access method '" + method.getName() + "': " + ex );
      }
    }
    if ( clazz.getSuperclass() != null ) {
      doWithMethods( clazz.getSuperclass(), mc, mf );
    }
    else if ( clazz.isInterface() ) {
      for ( Class<?> superIfc : clazz.getInterfaces() ) {
        doWithMethods( superIfc, mc, mf );
      }
    }
  }

  private static final Pattern CGLIB_RENAMED_METHOD_PATTERN = Pattern.compile( "CGLIB\\$(.+)\\$\\d+" );

  /**
   * Determine whether the given method is a CGLIB 'renamed' method, following the pattern
   * "CGLIB$methodName$0".
   * 
   * @param renamedMethod the method to check
   * @see net.sf.cglib.proxy.Enhancer#rename
   */
  public static boolean isCglibRenamedMethod( Method renamedMethod ) {
    return CGLIB_RENAMED_METHOD_PATTERN.matcher( renamedMethod.getName() ).matches();
  }

  /**
   * Action to take on each method.
   */
  public interface MethodCallback {

    /**
     * Perform an operation using the given method.
     * 
     * @param method the method to operate on
     */
    void doWith( Method method ) throws IllegalArgumentException, IllegalAccessException;
  }

  /**
   * Callback optionally used to filter methods to be operated on by a method callback.
   */
  public interface MethodFilter {

    /**
     * Determine whether the given method matches.
     * 
     * @param method the method to check
     */
    boolean matches( Method method );
  }

}

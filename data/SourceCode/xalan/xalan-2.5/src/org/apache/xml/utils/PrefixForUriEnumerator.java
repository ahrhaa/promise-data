package org.apache.xml.utils;

import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Encapsulate Namespace tracking logic for use by SAX drivers.
 *
 * <p>This class is an attempt to rewrite the SAX NamespaceSupport
 * "helper" class for improved efficiency. It can be used to track the
 * namespace declarations currently in scope, providing lookup
 * routines to map prefixes to URIs and vice versa.</p>
 *
 * <p>ISSUE: For testing purposes, I've extended NamespaceSupport even
 * though I'm completely reasserting all behaviors and fields.
 * Wasteful.... But SAX did not put an interface under that object and
 * we seem to have written that SAX class into our APIs... and I don't
 * want to argue with it right now. </p>
 *
 * @see org.xml.sax.helpers.NamespaceSupport
 * */
public class NamespaceSupport2
    extends org.xml.sax.helpers.NamespaceSupport
{
    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private Context2 currentContext; // Current point on the double-linked stack


    ////////////////////////////////////////////////////////////////////
    // Constants.
    ////////////////////////////////////////////////////////////////////


    /**
     * The XML Namespace as a constant.
     *
     * <p>This is the Namespace URI that is automatically mapped
     * to the "xml" prefix.</p>
     */
    public final static String XMLNS =
        "http://www.w3.org/XML/1998/namespace";


    ////////////////////////////////////////////////////////////////////
    // Constructor.
    ////////////////////////////////////////////////////////////////////


    /**
     * Create a new Namespace support object.
     */
    public NamespaceSupport2 ()
    {
        reset();
    }


    ////////////////////////////////////////////////////////////////////
    // Context management.
    ////////////////////////////////////////////////////////////////////


    /**
     * Reset this Namespace support object for reuse.
     *
     * <p>It is necessary to invoke this method before reusing the
     * Namespace support object for a new session.</p>
     */
    public void reset ()
    {
        // Discarding the whole stack doesn't save us a lot versus
        // creating a new NamespaceSupport. Do we care, or should we
        // change this to just reset the root context?
        currentContext = new Context2(null);
        currentContext.declarePrefix("xml", XMLNS);
    }


    /**
     * Start a new Namespace context.
     *
     * <p>Normally, you should push a new context at the beginning
     * of each XML element: the new context will automatically inherit
     * the declarations of its parent context, but it will also keep
     * track of which declarations were made within this context.</p>
     *
     * <p>The Namespace support object always starts with a base context
     * already in force: in this context, only the "xml" prefix is
     * declared.</p>
     *
     * @see #popContext
     */
    public void pushContext ()
    {
        // JJK: Context has a parent pointer.
        // That means we don't need a stack to pop.
        // We may want to retain for reuse, but that can be done via
        // a child pointer.

        Context2 parentContext=currentContext;
        currentContext = parentContext.getChild();
        if (currentContext == null){
                currentContext = new Context2(parentContext);
            }
        else{
            // JJK: This will wipe out any leftover data
            // if we're reusing a previously allocated Context.
            currentContext.setParent(parentContext);
        }
    }


    /**
     * Revert to the previous Namespace context.
     *
     * <p>Normally, you should pop the context at the end of each
     * XML element.  After popping the context, all Namespace prefix
     * mappings that were previously in force are restored.</p>
     *
     * <p>You must not attempt to declare additional Namespace
     * prefixes after popping a context, unless you push another
     * context first.</p>
     *
     * @see #pushContext
     */
    public void popContext ()
    {
        Context2 parentContext=currentContext.getParent();
        if(parentContext==null)
            throw new EmptyStackException();
        else
            currentContext = parentContext;
    }



    ////////////////////////////////////////////////////////////////////
    // Operations within a context.
    ////////////////////////////////////////////////////////////////////


    /**
     * Declare a Namespace prefix.
     *
     * <p>This method declares a prefix in the current Namespace
     * context; the prefix will remain in force until this context
     * is popped, unless it is shadowed in a descendant context.</p>
     *
     * <p>To declare a default Namespace, use the empty string.  The
     * prefix must not be "xml" or "xmlns".</p>
     *
     * <p>Note that you must <em>not</em> declare a prefix after
     * you've pushed and popped another Namespace.</p>
     *
     * <p>Note that there is an asymmetry in this library: while {@link
     * #getPrefix getPrefix} will not return the default "" prefix,
     * even if you have declared one; to check for a default prefix,
     * you have to look it up explicitly using {@link #getURI getURI}.
     * This asymmetry exists to make it easier to look up prefixes
     * for attribute names, where the default prefix is not allowed.</p>
     *
     * @param prefix The prefix to declare, or null for the empty
     *        string.
     * @param uri The Namespace URI to associate with the prefix.
     * @return true if the prefix was legal, false otherwise
     * @see #processName
     * @see #getURI
     * @see #getPrefix
     */
    public boolean declarePrefix (String prefix, String uri)
    {
        if (prefix.equals("xml") || prefix.equals("xmlns")) {
            return false;
        } else {
            currentContext.declarePrefix(prefix, uri);
            return true;
        }
    }


    /**
     * Process a raw XML 1.0 name.
     *
     * <p>This method processes a raw XML 1.0 name in the current
     * context by removing the prefix and looking it up among the
     * prefixes currently declared.  The return value will be the
     * array supplied by the caller, filled in as follows:</p>
     *
     * <dl>
     * <dt>parts[0]</dt>
     * <dd>The Namespace URI, or an empty string if none is
     *  in use.</dd>
     * <dt>parts[1]</dt>
     * <dd>The local name (without prefix).</dd>
     * <dt>parts[2]</dt>
     * <dd>The original raw name.</dd>
     * </dl>
     *
     * <p>All of the strings in the array will be internalized.  If
     * the raw name has a prefix that has not been declared, then
     * the return value will be null.</p>
     *
     * <p>Note that attribute names are processed differently than
     * element names: an unprefixed element name will received the
     * default Namespace (if any), while an unprefixed element name
     * will not.</p>
     *
     * @param qName The raw XML 1.0 name to be processed.
     * @param parts A string array supplied by the caller, capable of
     *        holding at least three members.
     * @param isAttribute A flag indicating whether this is an
     *        attribute name (true) or an element name (false).
     * @return The supplied array holding three internalized strings 
     *        representing the Namespace URI (or empty string), the
     *        local name, and the raw XML 1.0 name; or null if there
     *        is an undeclared prefix.
     * @see #declarePrefix
     * @see java.lang.String#intern */
    public String [] processName (String qName, String[] parts,
                                  boolean isAttribute)
    {
        String[] name=currentContext.processName(qName, isAttribute);
        if(name==null)
            return null;

        // JJK: This recopying is required because processName may return
        // a cached result. I Don't Like It. *****
        System.arraycopy(name,0,parts,0,3);
        return parts;
    }


    /**
     * Look up a prefix and get the currently-mapped Namespace URI.
     *
     * <p>This method looks up the prefix in the current context.
     * Use the empty string ("") for the default Namespace.</p>
     *
     * @param prefix The prefix to look up.
     * @return The associated Namespace URI, or null if the prefix
     *         is undeclared in this context.
     * @see #getPrefix
     * @see #getPrefixes
     */
    public String getURI (String prefix)
    {
        return currentContext.getURI(prefix);
    }


    /**
     * Return an enumeration of all prefixes currently declared.
     *
     * <p><strong>Note:</strong> if there is a default prefix, it will not be
     * returned in this enumeration; check for the default prefix
     * using the {@link #getURI getURI} with an argument of "".</p>
     *
     * @return An enumeration of all prefixes declared in the
     *         current context except for the empty (default)
     *         prefix.
     * @see #getDeclaredPrefixes
     * @see #getURI
     */
    public Enumeration getPrefixes ()
    {
        return currentContext.getPrefixes();
    }


    /**
     * Return one of the prefixes mapped to a Namespace URI.
     *
     * <p>If more than one prefix is currently mapped to the same
     * URI, this method will make an arbitrary selection; if you
     * want all of the prefixes, use the {@link #getPrefixes}
     * method instead.</p>
     *
     * <p><strong>Note:</strong> this will never return the empty
     * (default) prefix; to check for a default prefix, use the {@link
     * #getURI getURI} method with an argument of "".</p>
     *
     * @param uri The Namespace URI.
     * @return One of the prefixes currently mapped to the URI supplied,
     *         or null if none is mapped or if the URI is assigned to
     *         the default Namespace.
     * @see #getPrefixes(java.lang.String)
     * @see #getURI */
    public String getPrefix (String uri)
    {
        return currentContext.getPrefix(uri);
    }


    /**
     * Return an enumeration of all prefixes currently declared for a URI.
     *
     * <p>This method returns prefixes mapped to a specific Namespace
     * URI.  The xml: prefix will be included.  If you want only one
     * prefix that's mapped to the Namespace URI, and you don't care 
     * which one you get, use the {@link #getPrefix getPrefix}
     *  method instead.</p>
     *
     * <p><strong>Note:</strong> the empty (default) prefix is
     * <em>never</em> included in this enumeration; to check for the
     * presence of a default Namespace, use the {@link #getURI getURI}
     * method with an argument of "".</p>
     *
     * @param uri The Namespace URI.
     * @return An enumeration of all prefixes declared in the
     *         current context.
     * @see #getPrefix
     * @see #getDeclaredPrefixes
     * @see #getURI */
    public Enumeration getPrefixes (String uri)
    {
        // JJK: The old code involved creating a vector, filling it
        // with all the matching prefixes, and then getting its
        // elements enumerator. Wastes storage, wastes cycles if we
        // don't actually need them all. Better to either implement
        // a specific enumerator for these prefixes... or a filter
        // around the all-prefixes enumerator, which comes out to
        // roughly the same thing.
        //
        // **** Currently a filter. That may not be most efficient
        // when I'm done restructuring storage!
        return new PrefixForUriEnumerator(this,uri,getPrefixes());       
    }
    

    /**
     * Return an enumeration of all prefixes declared in this context.
     *
     * <p>The empty (default) prefix will be included in this 
     * enumeration; note that this behaviour differs from that of
     * {@link #getPrefix} and {@link #getPrefixes}.</p>
     *
     * @return An enumeration of all prefixes declared in this
     *         context.
     * @see #getPrefixes
     * @see #getURI
     */
    public Enumeration getDeclaredPrefixes ()
    {
        return currentContext.getDeclaredPrefixes();
    }



}

////////////////////////////////////////////////////////////////////
// Local classes.
// These were _internal_ classes... but in fact they don't have to be,
// and may be more efficient if they aren't. 
////////////////////////////////////////////////////////////////////

/**
 * Implementation of Enumeration filter, wrapped
 * aroung the get-all-prefixes version of the operation. This is NOT
 * necessarily the most efficient approach; finding the URI and then asking
 * what prefixes apply to it might make much more sense.
 */
class PrefixForUriEnumerator implements Enumeration
{
    private Enumeration allPrefixes;
    private String uri;
    private String lookahead=null;
    private NamespaceSupport2 nsup;
     
    // Kluge: Since one can't do a constructor on an
    // anonymous class (as far as I know)...
    PrefixForUriEnumerator(NamespaceSupport2 nsup,String uri, Enumeration allPrefixes)
    {
	this.nsup=nsup;
        this.uri=uri;
        this.allPrefixes=allPrefixes;
    }
        
    public boolean hasMoreElements()
    {
        if(lookahead!=null)
            return true;
            
        while(allPrefixes.hasMoreElements())
            {
                String prefix=(String)allPrefixes.nextElement();
                if(uri.equals(nsup.getURI(prefix)))
                    {
                        lookahead=prefix;
                        return true;
                    }
            }
        return false;
    }
        
    public Object nextElement()
    {
        if(hasMoreElements())
            {
                String tmp=lookahead;
                lookahead=null;
                return tmp;
            }
        else
            throw new java.util.NoSuchElementException();
    }
}

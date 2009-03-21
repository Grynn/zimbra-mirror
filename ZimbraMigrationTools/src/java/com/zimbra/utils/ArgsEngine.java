/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArgsEngine
{
    /**
     * Holds all options configured.
     */
    private Map<String, Option> options = new HashMap<String, Option>();
    
    /**
     * Holds all non options entered.
     */
    private List<String> nonOptions = new ArrayList<String>();
    
    /**
     * Indicates if the parse method has been called.
     */
    private boolean parseCalled = false;
    
    /**
     * Configures the engine by adding options.
     * 
     * @param shortForm the short form of an option. Example: "-h".
     * 
     * @param longForm the long form of an option. Example: "--help".
     */
    public void add(String shortForm, String longForm)
    {
        this.add(shortForm, longForm, false);
    }
    
    /**
     * Configures the engine by adding options.
     * 
     * @param shortForm the short form of an option. Example: "-h".
     * 
     * @param longForm the long form of an option. Example: "--help".
     * 
     * @param valued indicates if the option expects a value. The next argument
     * will be considered as the value for this option.
     */
    public void add(String shortForm, String longForm, boolean valued)
    {
        Option option = new Option(shortForm, longForm, valued);
        
        this.options.put(shortForm, option);
        this.options.put(longForm, option);
    }
    
    /**
     * Parses the input command line arguments. The result of parsing will 
     * be stored in the current instance of <tt>ArgsEngine</tt>.
     * <p>Operations {@link #getBoolean(String)}, {@link #getNonOptions()} 
     * and {@link #getString(String)} can be then used to extract the values.
     * 
     * @param args the command line arguments.
     */
    public void parse(String[] args)
    {
        this.parseCalled = true;
        
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            
            // An option.
            if((arg.startsWith("--") || arg.startsWith("-")) 
                && this.options.containsKey(arg))
            {
                Option option = this.options.get(arg);
                
                if(option.isValued())
                {
                    // This is the last arg or next arg is another option.
                    if(i + 1 >= args.length || args[i + 1].startsWith("-"))
                    {
                        throw new RuntimeException("Value required for option "
                            + arg);
                    }
                    
                    option.setValue(args[++i]);
                }
                else
                {
                    option.setValue("not-null");
                }
            }
            else
            {
                // Looks like an option the engine hasn't been configured for.
                if(arg.startsWith("-"))
                {
                    throw new RuntimeException("Unrecognized option " + arg);
                }
                
                this.nonOptions.add(arg);
            }
        }
    }
    
    /**
     * Returns all the non-options in the arguments list.
     * <p>
     * If the arguments supplied were "Hello -i World -o D:\out.txt" and the
     * instance of <tt>ArgsEngine</tt> was configured with a no valued "-i" and
     * a valued "-o", invocation of this method would return "Hello World"
     * after removing all the options and their values if any.
     *  
     * @return an array of non optional arguments.
     */
    public String[] getNonOptions()
    {
        if(!this.parseCalled)
        {
            throw new IllegalStateException("Method parse not invoked");
        }
        
        return this.nonOptions.toArray(new String[this.nonOptions.size()]);
    }
    
    /**
     * Gets the value for a valued option.
     * 
     * @param key the valued option key. Either short form or long form 
     * can be input.
     * 
     * @return the value for the option if it's valued, <tt>null</tt> 
     * otherwise.
     */
    public String getString(String key)
    {
        if(!this.parseCalled)
        {
            throw new IllegalStateException("Method parse not invoked");
        }
        
        Option option = this.options.get(key);
        
        // If option is non null and valued, return the value, null otherwise.
        return option != null ? 
            (option.isValued() ? option.getValue() : null) : null;
    }
    
    /**
     * Gets the option.
     * 
     * @param key the option's short form or long form name.
     * 
     * @return <tt>true</tt> if the option is found in the args parsed,
     * <tt>false</tt> otherwise.
     */
    public boolean getBoolean(String key)
    {
        if(!this.parseCalled)
        {
            throw new IllegalStateException("Method parse not invoked");
        }
        
        Option option = this.options.get(key);
        
        return option != null ? option.getValue() != null : false;
    }
    
    /**
     * An object for representing and manipulating with the options.
     * <p>
     * This class is internal to the <tt>ArgsEngine</tt>.
     * 
     * @author Adarsh Ramamurthy
     * 
     * @version 1.0, 12th April 2008
     */
    private static class Option
    {
        /**
         * Short form of the option.
         */
        private String shortForm;
        
        /**
         * Long form of the option.
         */
        private String longForm;
        
        /**
         * Indicates if the option is valued.
         */
        private boolean valued;
        
        /**
         * The value of a valued option.
         */
        private String value;

        /**
         * Constructs an instance of <tt>Option</tt> taking the short and
         * long forms provided.
         * 
         * @param shortForm the short form.
         * 
         * @param longForm the long form.
         */
        public Option(String shortForm, String longForm)
        {
            this.shortForm = shortForm;
            this.longForm = longForm;
        }
        
        /**
         * Constructs an instance of <tt>Option</tt> taking the short and
         * long forms provided.
         * 
         * @param shortForm the short form.
         * 
         * @param longForm the long form.
         */
        public Option(String shortForm, String longForm, boolean valued)
        {
            this.shortForm = shortForm;
            this.longForm = longForm;
            this.valued = valued;
        }

        /**
         * Gets the short form name.
         * 
         * @return the short form name.
         */
        public String getShortForm()
        {
            return this.shortForm;
        }

        /**
         * Sets the short form name.
         * 
         * @param shortForm the short form to set.
         */
        public void setShortForm(String shortForm)
        {
            this.shortForm = shortForm;
        }

        /**
         * Gets the long form name.
         * 
         * @return the long form name.
         */
        public String getLongForm()
        {
            return this.longForm;
        }

        /**
         * Sets the long form name.
         * 
         * @param longForm the long form name.
         */
        public void setLongForm(String longForm)
        {
            this.longForm = longForm;
        }
        
        /**
         * Tells if the option is valued.
         * 
         * @return <tt>true</tt> if valued, <tt>false</tt> otherwise.
         */
        public boolean isValued()
        {
            return this.valued;
        }

        /**
         * Sets the valued state.
         * 
         * @param valued the valued state.
         */
        public void setValued(boolean valued)
        {
            this.valued = valued;
        }
        
        /**
         * Gets the value.
         * 
         * @return the value.
         */
        public String getValue()
        {
            return this.value;
        }

        /**
         * Sets the value.
         * 
         * @param value the value to set.
         */
        public void setValue(String value)
        {
            this.value = value;
        }
    }
}

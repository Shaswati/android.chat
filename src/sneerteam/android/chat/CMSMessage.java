package sneerteam.android.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Parser and composer for "Clojure Map Subset" string messages.
 * 
 * "Clojure Map Subset" is the syntax we use to compose UDP packets:
 * 
 *     { :key value :key2 value2 ... :keyn valuen }
 *     
 * This class supports a single flat map with straightforward ":key-word" map keys 
 *   and values that are either :keywords or positive integer numbers or double-quote
 *   quoted strings which allow spaces inside of them.  
 *  
 */
class CMSMessage {

	// the map in object form 
	// the value will be either of a numeric class (say, Long) or it will be
	//   a String, in which case it is a clojure keyword IFF the first 
	//   character is ":", otherwise it is some literal string
	Map<String, Object> javaMap = null;
	
	// the map in string form
	String ednString = null;
	
	/*
	 * Initialize with given EDN string  
	 */
	CMSMessage(String edn) {
		this.ednString = edn;
	}
	
	/*
	 * Initialize with given Java objects map
	 */
	CMSMessage(Map<String, Object> map) {
		this.javaMap = map;
	}
	
	/*
	 * Get in EDN form.
	 */
	String getEDNString() {
		if (ednString == null)
			ednString = javaMapToEDNString(javaMap);
		return ednString;
	}
	
	/*
	 * Get in Java map form 
	 */
	Map<String, Object> getJavaMap() {
		if (javaMap == null) {
			javaMap = ednStringToJavaMap(ednString);
		}
		return javaMap;
	}
		
	/*
	 * Parse EDN String into Java map
	 */
	private static Map<String, Object> ednStringToJavaMap(String edn) {
		
		Map<String, Object> mret = new HashMap<String, Object>();
				
		// Strip the surrounding  { } which must be there
		edn = edn.trim();
		if ((edn.charAt(0) != '{') || (edn.charAt(edn.length()-1) != '}'))
			return mret; // invalid gives you an empty map
		edn = edn.substring(1, edn.length()-1);
		edn = " " + edn + " "; // make our regex composition life easier
		
		// Now the string is in form:
		//   :keyword something :keyword something ... :keyword something
		// Search for keyword plus thing repeatedly
		//
		// works if we take out the quotation problem
		//Pattern p = Pattern.compile("(:\\S+)\\s+(\\S+)");
		//
		// works: 
		Pattern p = Pattern.compile("(:\\S+)\\s+(\"[^\"]*\"|\\S+)");
        Matcher m = p.matcher(edn);
        
        while(m.find()) {
        	
        	// to debug the pattern matcher:
        	//
        	//System.out.println("Numgroups = " + m.groupCount());
            //System.out.println("Group() = '" + m.group() + "'");
            //for (int i=1; i<=m.groupCount(); ++i) {
            //	System.out.println("Group("+i+") = '" + m.group(i) + "'");
            //}
        	
        	// a capture group has to have two elements (key, value), otherwise it is
        	//  garbage. we're just skipping garbage for now.
        	if (m.groupCount() == 2) {
        		String k = m.group(1);
        		String v = m.group(2);
        		
        		if (k.length() < 2)
        			continue; // invalid
        		if (k.charAt(0) != ':')
        			continue; // invalid map key is not a Clojure keyword
        		// we don't need the ':' at the start of the map key, actually
        		k = k.substring(1);
        		
        		// figure out what type of value we have 
        		
        		// if the value is surrounded by double quotes, we know it is a string
        		// in that case, take the double quotes out first
        		if ((v.length() >= 2) && (v.charAt(0) == '"') && (v.charAt(v.length()-1) == '"')) {
        			// take out the qoutes
        			v = v.substring(1, v.length()-1);
        			mret.put(k, v);
        			continue;
        		} 
        		
        		// check if it is a Long
        		try {
        			Long num = Long.valueOf(v);
        			// done
        			mret.put(k, num);
        			continue;
        		} catch (NumberFormatException e) {
        			// nope
        		}

        		// check if it s a Double
        		try {
        			Double num = Double.valueOf(v);
        			// done
        			mret.put(k, num);
        			continue;
        		} catch (NumberFormatException e) {
        			// nope
        		}
        		
        		// it's an unquoted string of some sort
        		// if it starts with ":" we'll consider it as 
        		//   a "clojure keyword" value automatically
        		mret.put(k, v);
        	}
        }
        return mret;
	}
	
	/*
	 * Serialize Java map into EDN string
	 */
	private static String javaMapToEDNString(Map<String, Object> map) {
		
		String edn = "{";
		
		boolean first = true;
		
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (! first)
				edn = edn + " ";
			else
				first = false;
		    
			String key = entry.getKey();
		    edn = edn + ":" + key + " ";

		    Object thing = entry.getValue();
		    
		    if ((thing instanceof String) && (((String)thing).indexOf(" ") != -1)) {
		    	edn = edn + '"' + thing + '"';		    	
		    } else { 
		    	edn = edn + thing;
		    }
		}
		
		edn = edn + "}";
		return edn;
	}
	
	/*
	 * Run example
	 */
	public static void runExample() {
		String origedn = "{:key val :key2 val2 :key2p5 :valueiskeyword :key3str \"minha string 44994haha--++== poiM\" :key4 +444567 :key42 -444567 :key43 444567 :keyfloat -123.543 :aa bb}";
		System.out.println("Starting with EDN string:\n" + origedn + "\n");
		CMSMessage msg = new CMSMessage(origedn);
		System.out.println("Dumping given EDN string in java map form:");
		Map<String, Object> msgmap = msg.getJavaMap();
		for (Map.Entry<String, Object> entry : msgmap.entrySet()) {
			String key = entry.getKey();
		    Object val = entry.getValue();
			System.out.println("Key: '" + key + "'");
			System.out.println("Value ("+ val.getClass().getCanonicalName()+"):'"+ val.toString()+"'");
		}
		System.out.println("Now starting from the map and going to the string.");
		CMSMessage msg2 = new CMSMessage(msgmap);
		System.out.println("\nAnd this is what the map becomes:\n\"" + msg2.getEDNString() + "\"\n");
		System.out.println("\nBye!");
	}
}

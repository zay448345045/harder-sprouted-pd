package com.github.dachhack.sprout.Messages;


import com.github.dachhack.sprout.ShatteredPixelDungeon;
import com.github.dachhack.sprout.utils.GLog;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;

/*
    Simple wrapper class for java resource bundles.

	The core idea here is that each string resource's key is a combination of the class definition and a local value.
	An object or static method would usually call this with an object/class reference (usually its own) and a local key.
	This means that an object can just ask for "name" rather than, say, "items.weapon.enchantments.death.name"
 */
public class Messages {
    /*
            use hashmap for two reasons. Firstly because android 2.2 doesn't support resourcebundle.containskey(),
            secondly so I can read in and combine multiple properties files,
            resulting in a more clean structure for organizing all the strings, instead of one big file.

            ..Yes R.string would do this for me, but that's not multiplatform
         */
    private static HashMap<String, String> strings;
    private static Languages lang;

    public static Languages lang() {
        return lang;
    }


    /**
     * Setup Methods
     */

    private static String[] prop_files = new String[]{
            "com.github.dachhack.sprout.actors.actors",
            "com.github.dachhack.sprout.items.items",
            "com.github.dachhack.sprout.levels.levels",
            "com.github.dachhack.sprout.plants.plants",
            "com.github.dachhack.sprout.scenes.scenes",
            "com.github.dachhack.sprout.ui.ui",
            "com.github.dachhack.sprout.windows.windows",
            "com.github.dachhack.sprout.misc.misc"
    };

    static {
        setup(ShatteredPixelDungeon.language());
    }

    public static void setup(Languages lang) {
        strings = new HashMap<>();
        Messages.lang = lang;
        Locale locale = new Locale(lang.code());

        for (String file : prop_files) {
            ResourceBundle bundle = ResourceBundle.getBundle(file, locale);
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = bundle.getString(key);

                strings.put(key, value);
            }
        }
    }


    /**
     * Resource grabbing methods
     */

    public static String get(String key, Object...args){
        return get(null, key, args);
    }

    public static String get(Object o, String k, Object...args){
        return get(o.getClass(), k, args);
    }

    public static String get(Class c, String k, Object...args) {
        return get(c, k, null, args);
    }

    private static String get(Class c, String k, String baseName, Object...args){
        String key;
        if (c != null) {
            key = c.getName().replace("com.github.dachhack.sprout.", "");
            key += "." + k;
        } else
            key = k;

        if (strings.containsKey(key.toLowerCase(Locale.ENGLISH))) {
            if (args.length > 0) return format(strings.get(key.toLowerCase(Locale.ENGLISH)), args);
            else return strings.get(key.toLowerCase(Locale.ENGLISH));
        } else {
            //this is so child classes can inherit properties from their parents.
            //in cases where text is commonly grabbed as a utility from classes that aren't mean to be instantiated
            //(e.g. flavourbuff.dispTurns()) using .class directly is probably smarter to prevent unnecessary recursive calls.
            if (baseName == null) {
                baseName = key;
            }
            if (c != null && c.getSuperclass() != null) {
                return get(c.getSuperclass(), k,baseName, args);
            } else {
                String name = "Ms: "+baseName;
                GLog.w(name);
                return name;
            }
        }
    }


    /**
     * String Utility Methods
     */

    public static String format(String format, Object... args) {
        return String.format(Locale.ENGLISH, format, args);
    }

    public static String capitalize(String str) {
        if (str.length() == 0) return str;
        else return Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    //Words which should not be capitalized in title case, mostly prepositions which appear ingame
    //This list is not comprehensive!
    private static final HashSet<String> noCaps = new HashSet<>(
            Arrays.asList(new String[]{
                    //English
                    "a", "of", "by", "to", "the", "x"
            })
    );

    public static String titleCase(String str) {
        //English capitalizes every word except for a few exceptions
        if (lang == Languages.ENGLISH) {
            String result = "";
            //split by any unicode space character
            for (String word : str.split("(?<=\\p{Zs})")) {
                if (noCaps.contains(word.trim().toLowerCase(Locale.ENGLISH).replaceAll(":|[0-9]", ""))) {
                    result += word;
                } else {
                    result += capitalize(word);
                }
            }
            //first character is always capitalized.
            return capitalize(result);
        }

        //Otherwise, use sentence case
        return capitalize(str);
    }
}

package model.util;

import java.io.File;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import oracle.adf.share.logging.ADFLogger;

public class MessageBundle {
    private static ADFLogger log = ADFLogger.createADFLogger(MessageBundle.class);

    public MessageBundle() {
        super();
    }

    private static final String defaultApplicationMessageBundle = "model.util.bundle.applicationMessageBundle";


    public static String getMessageFromApplicationMessageBundle(Locale locale, String keyMessage) {
        if(locale==null)
            locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();

        return getMessageFromApplicationMessageBundle(locale, defaultApplicationMessageBundle, keyMessage);
    }

    public static String getMessageFromApplicationMessageBundle(Locale locale, String bundleFile, String keyMessage) {
        if(locale==null)
            locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();

        String ret = "";
        try {
            ret = getMessage(locale, bundleFile, keyMessage);
        } catch (MissingResourceException e) {
            ret = bundleFile + "." + keyMessage;
        }
        return ret;
    }

    public static String getMessageFromApplicationMessageBundle(Locale locale, String keyMessage, boolean withDefaultMessage) {
        if(locale==null)
            locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();
        
        String ret = null;
        try {
            ret = getMessage(locale, defaultApplicationMessageBundle, keyMessage);
        } catch (MissingResourceException e) {
            if (withDefaultMessage)
                ret = defaultApplicationMessageBundle + "." + keyMessage;
        }
        return ret;
    }

    public static String getMessageFromApplicationMessageBundle(Locale locale, String bundleFile, String keyMessage, boolean withDefaultMessage) {
        if(locale==null)
            locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();
        
        String ret = null;
        try {
            ret = getMessage(locale, bundleFile, keyMessage);
        } catch (MissingResourceException e) {
            if (withDefaultMessage)
                ret = bundleFile + "." + keyMessage;
        }
        return ret;
    }

    private static String getMessage(Locale locale, String bundleFile, String keyMessage) throws MissingResourceException {
        if(locale==null)
            locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();

        ResourceBundle bundle = null;
        if (System.getProperty("allBundlePath") != null && !System.getProperty("allBundlePath").isEmpty() || (System.getProperty("configPath") != null && !System.getProperty("configPath").isEmpty() && (bundleFile.toLowerCase().endsWith(".config") || bundleFile.substring(bundleFile.lastIndexOf(".")+1) .toLowerCase().startsWith("config_")))) {
            String _path = ((System.getProperty("configPath") != null && !System.getProperty("configPath").isEmpty() && (bundleFile.toLowerCase().endsWith(".config") ||bundleFile.substring(bundleFile.lastIndexOf(".")+1) .toLowerCase().startsWith("config_")))) ? System.getProperty("configPath") : System.getProperty("allBundlePath");
            String _b = (bundleFile.toLowerCase().endsWith(".config"))?bundleFile.substring(bundleFile.lastIndexOf(".") + 1):bundleFile;
            try {
                File        file = new File(_path);
                URL[]       urls = { file.toURI().toURL() };
                ClassLoader loader = new URLClassLoader(urls);
                bundle = ResourceBundle.getBundle(_b, locale, loader);
            } catch (Throwable th) {
                log.warning("Resource bundle path defined in System parameter not found or file missing:" + _path + " - file:" + _b, th);
            }
        }

        if (bundle == null)
            bundle = ResourceBundle.getBundle(bundleFile,locale);
        
        return bundle.getString(keyMessage);
    }

    /**
     *
     * @param bundleFile: file da cui leggere le properties
     * @param keyPrefix: prefisso che identifica il set di proprieta da estrapolare
     * @param keyPrefixSeparator: separatore tra prefisso e nome della proprieta da estrapolare (DEFAULT .)
     * @param keepPrefix: indica se la chiave dell'oggetto properties di ritorno deve essere storato anche il prefisso o meno
     *
     * (es. properties memorizzate nel file:
     *  connection.url=.....
     *  connection.user=.....
     *  connection.password=....
     *
     *  posso usare: keyPrefix=connection, keyPrefixSeparator=., keepPrefix=true
     *  il risultato sara un oggetto properties del tipo
     *  {
     *      connection.url: ......
     *      connection.user: .......
     *      connection.password: ....
     *  }
     *  con keepPrefix=false:
     *  {
     *      url: ......
     *      user: .......
     *      password: ....
     *  }
     * )
     * @return
     */
    public static LinkedHashMap<String, String> getSubsetMessages(Locale locale, String bundleFile, String keyPrefix, Character keyPrefixSeparator, boolean keepPrefix) {
        if(locale==null)
            locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();
        
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

        ResourceBundle bundle = null;
        if (System.getProperty("allBundlePath") != null && !System.getProperty("allBundlePath").isEmpty() || (System.getProperty("configPath") != null && !System.getProperty("configPath").isEmpty() && (bundleFile.toLowerCase().endsWith(".config") ||bundleFile.substring(bundleFile.lastIndexOf(".")+1) .toLowerCase().startsWith("config_")))) {
            String _path = ((System.getProperty("configPath") != null && !System.getProperty("configPath").isEmpty() && (bundleFile.toLowerCase().endsWith(".config") ||bundleFile.substring(bundleFile.lastIndexOf(".")+1) .toLowerCase().startsWith("config_")))) ? System.getProperty("configPath") : System.getProperty("allBundlePath");
            String _b = (bundleFile.toLowerCase().endsWith(".config"))?bundleFile.substring(bundleFile.lastIndexOf(".") + 1):bundleFile;
            try {
                File        file = new File(_path);
                URL[]       urls = { file.toURI().toURL() };
                ClassLoader loader = new URLClassLoader(urls);
                bundle = ResourceBundle.getBundle(_b, locale, loader);
            } catch (Throwable th) {
                log.warning("Resource bundle path defined in System parameter not found or file missing:" + _path + " - file:" + _b, th);
            }
        }

        if (bundle == null)
            bundle = ResourceBundle.getBundle(bundleFile, locale);

        if (bundle != null) {
            if (keyPrefix != null && !keyPrefix.isEmpty()) {
                if (keyPrefixSeparator == null || keyPrefixSeparator.toString().isEmpty())
                    keyPrefixSeparator = new Character('.');

                String prefixMatch;
                String prefixSelf;
                if (keyPrefix.charAt(keyPrefix.length() - 1) != keyPrefixSeparator) {
                    prefixSelf = keyPrefix;
                    prefixMatch = keyPrefix + keyPrefixSeparator;
                } else {
                    prefixSelf = keyPrefix.substring(0, keyPrefix.length() - 1);
                    prefixMatch = keyPrefix;
                }

                for (String key : bundle.keySet()) {
                    if (keepPrefix) {
                        if (key.startsWith(prefixMatch) || key.equals(prefixSelf)) {
                            result.put(key, bundle.getString(key));
                        }
                    } else {
                        if (key.startsWith(prefixMatch)) {
                            result.put(key.substring(prefixMatch.length()), bundle.getString(key));
                        }
                    }
                }
            }
        }

        return result;
    }
}

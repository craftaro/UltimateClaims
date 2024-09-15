package com.craftaro.ultimateclaims.core.locale;

import com.craftaro.core.configuration.Config;
import com.craftaro.core.configuration.ConfigSection;
import com.craftaro.core.utils.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Locale {
    private static final Pattern OLD_NODE_PATTERN = Pattern.compile("^([^ ]+)\\s*=\\s*\"?(.*?)\"?$");
    private static final String FILE_EXTENSION = ".lang";
    private final Map<String, String> nodes = new HashMap();
    private final Plugin plugin;
    private final File file;
    private final String name;

    public Locale(Plugin plugin, File file, String name) {
        this.plugin = plugin;
        this.file = file;
        this.name = name;
    }

    public static com.craftaro.core.locale.Locale loadDefaultLocale(JavaPlugin plugin, String name) {
        saveDefaultLocale(plugin, name, name);
        return loadLocale(plugin, name);
    }

    public static com.craftaro.core.locale.Locale loadLocale(JavaPlugin plugin, String name) {
        File localeFolder = new File(plugin.getDataFolder(), "locales/");
        if (!localeFolder.exists()) {
            return null;
        } else {
            File localeFile = new File(localeFolder, name + ".lang");
            if (!localeFolder.exists()) {
                return null;
            } else {
                com.craftaro.core.locale.Locale l = new com.craftaro.core.locale.Locale(plugin, localeFile, name);
                if (!l.reloadMessages()) {
                    return null;
                } else {
                    plugin.getLogger().info("Loaded locale \"" + name + "\"");
                    return l;
                }
            }
        }
    }

    public static List<com.craftaro.core.locale.Locale> loadAllLocales(JavaPlugin plugin) {
        File localeFolder = new File(plugin.getDataFolder(), "locales/");
        List<com.craftaro.core.locale.Locale> all = new ArrayList();
        File[] var3 = localeFolder.listFiles();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File localeFile = var3[var5];
            String fileName = localeFile.getName();
            if (fileName.endsWith(".lang")) {
                fileName = fileName.substring(0, fileName.lastIndexOf(46));
                if (fileName.split("_").length == 2) {
                    com.craftaro.core.locale.Locale l = new com.craftaro.core.locale.Locale(plugin, localeFile, fileName);
                    if (l.reloadMessages()) {
                        plugin.getLogger().info("Loaded locale \"" + fileName + "\"");
                        all.add(l);
                    }
                }
            }
        }

        return all;
    }

    public static List<String> getLocales(Plugin plugin) {
        File localeFolder = new File(plugin.getDataFolder(), "locales/");
        List<String> all = new ArrayList();
        File[] var3 = localeFolder.listFiles();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File localeFile = var3[var5];
            String fileName = localeFile.getName();
            if (fileName.endsWith(".lang")) {
                fileName = fileName.substring(0, fileName.lastIndexOf(46));
                if (fileName.split("_").length == 2) {
                    all.add(fileName);
                }
            }
        }

        return all;
    }

    public static boolean saveDefaultLocale(JavaPlugin plugin, String locale, String fileName) {
        return saveLocale(plugin, plugin.getResource(locale + ".lang"), fileName, true);
    }

    public static boolean saveLocale(Plugin plugin, InputStream in, String fileName) {
        return saveLocale(plugin, in, fileName, false);
    }

    private static boolean saveLocale(Plugin plugin, InputStream in, String fileName, boolean builtin) {
        if (in == null) {
            return false;
        } else {
            File localeFolder = new File(plugin.getDataFolder(), "locales/");
            if (!localeFolder.exists()) {
                localeFolder.mkdirs();
            }

            if (!fileName.endsWith(".lang")) {
                fileName = fileName + ".lang";
            }

            File destinationFile = new File(localeFolder, fileName);
            if (destinationFile.exists()) {
                return updateFiles(plugin, in, destinationFile, builtin);
            } else {
                try {
                    Files.copy(in, destinationFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                    fileName = fileName.substring(0, fileName.lastIndexOf(46));
                    return fileName.split("_").length == 2;
                } catch (IOException var7) {
                    IOException ex = var7;
                    ex.printStackTrace();
                    return false;
                }
            }
        }
    }

    private static boolean updateFiles(Plugin plugin, InputStream defaultFile, File existingFile, boolean builtin) {
        try {
            BufferedInputStream defaultIn = new BufferedInputStream(defaultFile);

            boolean var34;
            label182: {
                try {
                    BufferedInputStream existingIn;
                    label165: {
                        existingIn = new BufferedInputStream(Files.newInputStream(existingFile.toPath()));

                        try {
                            Charset defaultCharset = TextUtils.detectCharset(defaultIn, StandardCharsets.UTF_8);
                            Charset existingCharset = TextUtils.detectCharset(existingIn, StandardCharsets.UTF_8);

                            try {
                                BufferedReader defaultReaderOriginal = new BufferedReader(new InputStreamReader(defaultIn, defaultCharset));

                                try {
                                    BufferedReader existingReaderOriginal = new BufferedReader(new InputStreamReader(existingIn, existingCharset));

                                    try {
                                        BufferedReader defaultReader = translatePropertyToYAML(defaultReaderOriginal, defaultCharset);

                                        try {
                                            BufferedReader existingReader = translatePropertyToYAML(existingReaderOriginal, existingCharset);

                                            try {
                                                Config existingLang = new Config(existingFile);
                                                existingLang.load(existingReader);
                                                translateMsgRoot(existingLang, existingFile, existingCharset);
                                                Config defaultLang = new Config();
                                                String defaultData = (String)defaultReader.lines().map((s) -> {
                                                    return s.replaceAll("[\ufeff\ufffe\u200b]", "");
                                                }).collect(Collectors.joining("\n"));
                                                defaultLang.loadFromString(defaultData);
                                                translateMsgRoot(defaultLang, defaultData, defaultCharset);
                                                List<String> added = new ArrayList();
                                                Iterator var16 = defaultLang.getKeys(true).iterator();

                                                while(var16.hasNext()) {
                                                    String defaultValueKey = (String)var16.next();
                                                    Object val = defaultLang.get(defaultValueKey);
                                                    if (!(val instanceof ConfigSection) && !existingLang.contains(defaultValueKey)) {
                                                        added.add(defaultValueKey);
                                                        existingLang.set(defaultValueKey, val);
                                                    }
                                                }

                                                if (!added.isEmpty()) {
                                                    if (!builtin) {
                                                        existingLang.setHeader(new String[]{"New messages added for " + plugin.getName() + " v" + plugin.getDescription().getVersion() + ".", "", "These translations were found untranslated, join", "our translation Discord https://discord.gg/f7fpZEf", "to request an official update!", "", String.join("\n", added)});
                                                    } else {
                                                        existingLang.setHeader(new String[]{"New messages added for " + plugin.getName() + " v" + plugin.getDescription().getVersion() + ".", "", String.join("\n", added)});
                                                    }

                                                    existingLang.setRootNodeSpacing(0);
                                                    existingLang.save();
                                                }

                                                var34 = !added.isEmpty();
                                            } catch (Throwable var25) {
                                                if (existingReader != null) {
                                                    try {
                                                        existingReader.close();
                                                    } catch (Throwable var24) {
                                                        var25.addSuppressed(var24);
                                                    }
                                                }

                                                throw var25;
                                            }

                                            if (existingReader != null) {
                                                existingReader.close();
                                            }
                                        } catch (Throwable var26) {
                                            if (defaultReader != null) {
                                                try {
                                                    defaultReader.close();
                                                } catch (Throwable var23) {
                                                    var26.addSuppressed(var23);
                                                }
                                            }

                                            throw var26;
                                        }

                                        if (defaultReader != null) {
                                            defaultReader.close();
                                        }
                                    } catch (Throwable var27) {
                                        try {
                                            existingReaderOriginal.close();
                                        } catch (Throwable var22) {
                                            var27.addSuppressed(var22);
                                        }

                                        throw var27;
                                    }

                                    existingReaderOriginal.close();
                                } catch (Throwable var28) {
                                    try {
                                        defaultReaderOriginal.close();
                                    } catch (Throwable var21) {
                                        var28.addSuppressed(var21);
                                    }

                                    throw var28;
                                }

                                defaultReaderOriginal.close();
                            } catch (InvalidConfigurationException var29) {
                                InvalidConfigurationException ex = var29;
                                plugin.getLogger().log(Level.SEVERE, "Error checking config " + existingFile.getName(), ex);
                                break label165;
                            }
                        } catch (Throwable var30) {
                            try {
                                existingIn.close();
                            } catch (Throwable var20) {
                                var30.addSuppressed(var20);
                            }

                            throw var30;
                        }

                        existingIn.close();
                        break label182;
                    }

                    existingIn.close();
                } catch (Throwable var31) {
                    try {
                        defaultIn.close();
                    } catch (Throwable var19) {
                        var31.addSuppressed(var19);
                    }

                    throw var31;
                }

                defaultIn.close();
                return false;
            }

            defaultIn.close();
            return var34;
        } catch (IOException var32) {
            return false;
        }
    }

    public boolean reloadMessages() {
        if (!this.file.exists()) {
            this.plugin.getLogger().warning("Could not find file for locale \"" + this.name + "\"");
            return false;
        } else {
            this.nodes.clear();
            Charset charset = TextUtils.detectCharset(this.file, (Charset)null);
            if (charset == null) {
                this.plugin.getLogger().warning("Could not determine charset for locale \"" + this.name + "\"");
                charset = StandardCharsets.UTF_8;
            }

            try {
                FileInputStream stream = new FileInputStream(this.file);

                boolean var6;
                try {
                    BufferedReader source = new BufferedReader(new InputStreamReader(stream, charset));

                    try {
                        BufferedReader reader = translatePropertyToYAML(source, charset);

                        try {
                            Config lang = new Config(this.file);
                            lang.load(reader);
                            translateMsgRoot(lang, this.file, charset);
                            lang.getValues(true).forEach((k, v) -> {
                                this.nodes.put(k, v instanceof List ? (String)((List)v).stream().map(Object::toString).collect(Collectors.joining("\n")) : v.toString());
                            });
                            var6 = true;
                        } catch (Throwable var10) {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (Throwable var9) {
                                    var10.addSuppressed(var9);
                                }
                            }

                            throw var10;
                        }

                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Throwable var11) {
                        try {
                            source.close();
                        } catch (Throwable var8) {
                            var11.addSuppressed(var8);
                        }

                        throw var11;
                    }

                    source.close();
                } catch (Throwable var12) {
                    try {
                        stream.close();
                    } catch (Throwable var7) {
                        var12.addSuppressed(var7);
                    }

                    throw var12;
                }

                stream.close();
                return var6;
            } catch (IOException var13) {
                IOException ex = var13;
                ex.printStackTrace();
            } catch (InvalidConfigurationException var14) {
                InvalidConfigurationException ex = var14;
                Logger.getLogger(com.craftaro.core.locale.Locale.class.getName()).log(Level.SEVERE, "Configuration error in language file \"" + this.file.getName() + "\"", ex);
            }

            return false;
        }
    }

    protected static BufferedReader translatePropertyToYAML(BufferedReader source, Charset charset) throws IOException {
        StringBuilder output = new StringBuilder();

        String line;
        for(int lineNumber = 0; (line = source.readLine()) != null; ++lineNumber) {
            if (lineNumber == 0) {
                String line1 = line;
                line = line.replaceAll("[\ufeff\ufffe\u200b]", "");
                if (line1.length() != line.length()) {
                    output.append(line1, 0, line1.length() - line.length());
                }
            }

            Matcher matcher;
            if (!(line = line.replace('\r', ' ').replace(";", "")).trim().isEmpty() && !line.trim().startsWith("#") && (matcher = OLD_NODE_PATTERN.matcher(line.trim())).find()) {
                output.append(matcher.group(1)).append(": \"").append(matcher.group(2)).append("\"\n");
            } else if (line.startsWith("//")) {
                output.append("#").append(line).append("\n");
            } else {
                output.append(line).append("\n");
            }
        }

        return new BufferedReader(new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(output.toString().getBytes(charset))), charset));
    }

    protected static void translateMsgRoot(Config lang, File file, Charset charset) throws IOException {
        List<String> msgs = (List)lang.getValues(true).entrySet().stream().filter((e) -> {
            return e.getValue() instanceof ConfigSection;
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!msgs.isEmpty()) {
            FileInputStream stream = new FileInputStream(file);

            try {
                BufferedReader source = new BufferedReader(new InputStreamReader(stream, charset));

                String line;
                try {
                    for(int lineNumber = 0; (line = source.readLine()) != null; ++lineNumber) {
                        if (lineNumber == 0) {
                            line = line.replaceAll("[\ufeff\ufffe\u200b]", "");
                        }

                        Matcher matcher;
                        if (!(line = line.trim()).isEmpty() && !line.startsWith("#") && (matcher = OLD_NODE_PATTERN.matcher(line)).find() && msgs.contains(matcher.group(1))) {
                            lang.set(matcher.group(1) + ".message", matcher.group(2));
                        }
                    }
                } catch (Throwable var11) {
                    try {
                        source.close();
                    } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                    }

                    throw var11;
                }

                source.close();
            } catch (Throwable var12) {
                try {
                    stream.close();
                } catch (Throwable var9) {
                    var12.addSuppressed(var9);
                }

                throw var12;
            }

            stream.close();
        }

    }

    protected static void translateMsgRoot(Config lang, String file, Charset charset) throws IOException {
        List<String> msgs = (List)lang.getValues(true).entrySet().stream().filter((e) -> {
            return e.getValue() instanceof ConfigSection;
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!msgs.isEmpty()) {
            String[] source = file.split("\n");

            for(int lineNumber = 0; lineNumber < source.length; ++lineNumber) {
                String line = source[lineNumber];
                if (lineNumber == 0) {
                    line = line.replaceAll("[\ufeff\ufffe\u200b]", "");
                }

                Matcher matcher;
                if (!(line = line.trim()).isEmpty() && !line.startsWith("#") && (matcher = OLD_NODE_PATTERN.matcher(line)).find() && msgs.contains(matcher.group(1))) {
                    lang.set(matcher.group(1) + ".message", matcher.group(2));
                }
            }
        }

    }

    private Message supplyPrefix(Message message) {
        return message.setPrefix((String)this.nodes.getOrDefault("general.nametag.prefix", "[" + this.plugin.getName() + "]"));
    }

    public Message newMessage(String message) {
        return this.supplyPrefix(new Message(message));
    }

    public Message getMessage(String node) {
        if (this.nodes.containsKey(node + ".message")) {
            node = node + ".message";
        }

        return this.getMessageOrDefault(node, node);
    }

    public Message getMessageOrDefault(String node, String defaultValue) {
        if (this.nodes.containsKey(node + ".message")) {
            node = node + ".message";
        }

        return this.supplyPrefix(new Message((String)this.nodes.getOrDefault(node, defaultValue)));
    }

    public String getName() {
        return this.name;
    }
}

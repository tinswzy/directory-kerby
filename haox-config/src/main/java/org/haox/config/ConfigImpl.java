package org.haox.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ConfigImpl implements Config {
	private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private String resource;
	private Map<String, ConfigObject> properties;
    private List<Config> subConfigs;

    private Set<String> propNames;

    protected ConfigImpl(String resource) {
        this.resource = resource;
        this.properties = new HashMap<String, ConfigObject>();
        this.subConfigs = new ArrayList<Config>(0);
    }

    protected void reset() {
        this.properties.clear();
        this.subConfigs.clear();
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public Set<String> getNames() {
        reloadNames();
        return propNames;
    }

    @Override
	public String getString(String name) {
		String result = null;

        ConfigObject co = properties.get(name);
		if (co != null) {
            result = co.getPropertyValue();
		}

        if (result == null) {
            for (Config sub : subConfigs) {
                result = sub.getString(name);
                if (result != null) break;
            }
        }

		return result;
	}

    @Override
    public String getString(ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return getString(name.toPropertyKey(), (String) name.getDefaultValue());
        }
        return getString(name.toPropertyKey());
    }

    @Override
    public String getString(String name, String defaultValue) {
        String result = getString(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public String getTrimmed(String name) {
        String result = getString(name);
        if (null != result) {
            result = result.trim();
        }
        return result;
    }

    @Override
    public String getTrimmed(ConfigKey name) {
        return getTrimmed(name.toPropertyKey());
    }

    @Override
    public Integer getInt(String name) {
        Integer result = null;
        String value = getTrimmed(name);
        if (value != null) {
            result = Integer.valueOf(value);
        }
        return result;
    }

    @Override
    public Integer getInt(ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return getInt(name.toPropertyKey(), (Integer) name.getDefaultValue());
        }
        return getInt(name.toPropertyKey());
    }

    @Override
    public Integer getInt(String name, int defaultValue) {
        Integer result = getInt(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public Long getLong(String name) {
        Long result = null;
        String value = getTrimmed(name);
        if (value != null) {
            result = Long.valueOf(value);
        }
        return result;
    }

    @Override
    public Long getLong(ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return getLong(name.toPropertyKey(), (Long) name.getDefaultValue());
        }
        return getLong(name.toPropertyKey());
    }

    @Override
    public Long getLong(String name, long defaultValue) {
        Long result = getLong(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public Float getFloat(String name) {
        Float result = null;
        String value = getTrimmed(name);
        if (value != null) {
            result = Float.valueOf(value);
        }
        return result;
    }

    @Override
    public Float getFloat(ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return getFloat(name.toPropertyKey(), (Float) name.getDefaultValue());
        }
        return getFloat(name.toPropertyKey());
    }

    @Override
    public Float getFloat(String name, float defaultValue) {
        Float result = getFloat(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public Boolean getBoolean(String name) {
        Boolean result = null;
        String value = getTrimmed(name);
        if (value != null) {
            result = Boolean.valueOf(value);
        }
        return result;
    }

    @Override
    public Boolean getBoolean(ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return getBoolean(name.toPropertyKey(), (Boolean) name.getDefaultValue());
        }
        return getBoolean(name.toPropertyKey());
    }

    @Override
    public Boolean getBoolean(String name, boolean defaultValue) {
        Boolean result = getBoolean(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
	public List<String> getList(String name) {
        List<String> results = null;
		ConfigObject co = properties.get(name);
		if (co != null) {
			results = co.getListValues();
		}
		return results;
	}

    @Override
    public List<String> getList(String name, String[] defaultValue) {
        List<String> results = getList(name);
        if (results == null) {
            results = Arrays.asList(defaultValue);
        }
        return results;
    }

    @Override
    public List<String> getList(ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return getList(name.toPropertyKey(), (String[]) name.getDefaultValue());
        }
        return getList(name.toPropertyKey());
    }

    @Override
    public Config getConfig(String name) {
        Config result = null;
        ConfigObject co = properties.get(name);
        if (co != null) {
            result = co.getConfigValue();
        }
        return result;
    }

    @Override
    public Config getConfig(ConfigKey name) {
        return getConfig(name.toPropertyKey());
    }

    @Override
    public Class<?> getClass(String name) throws ClassNotFoundException {
        Class<?> result = null;

        String valueString = getString(name);
        if (valueString != null) {
            Class<?> cls = Class.forName(name);
            result = cls;
        }

        return result;
    }

    @Override
    public Class<?> getClass(String name, Class<?> defaultValue) throws ClassNotFoundException {
        Class<?> result = getClass(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public Class<?> getClass(ConfigKey name) throws ClassNotFoundException {
        if (name.getDefaultValue() != null) {
            return getClass(name.toPropertyKey(), (Class<?>) name.getDefaultValue());
        }
        return getClass(name.toPropertyKey());
    }

    @Override
    public <T> T getInstance(String name) throws ClassNotFoundException {
        return getInstance(name, null);
    }

    @Override
    public <T> T getInstance(ConfigKey name) throws ClassNotFoundException {
        return getInstance(name.toPropertyKey());
    }

    @Override
    public <T> T getInstance(String name, Class<T> xface) throws ClassNotFoundException {
        T result = null;

        Class<?> cls = getClass(name, null);
        if (xface != null && !xface.isAssignableFrom(cls)) {
            throw new RuntimeException(cls + " does not implement " + xface);
        }
        try {
            result = (T) cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance with class " + cls.getName());
        }

        return result;
    }

    protected void set(String name, String value) {
		ConfigObject co = new ConfigObject(value);
		set(name, co);
	}

    protected void set(String name, Config value) {
        ConfigObject co = new ConfigObject(value);
        set(name, co);

        addSubConfig(value);
    }

    protected void set(String name, ConfigObject value) {
        this.properties.put(name, value);
    }

    private void addSubConfig(Config config) {
        this.subConfigs.add(config);
    }

    private void reloadNames() {
        if (propNames != null) {
            propNames.clear();
        }
        propNames = new HashSet<String>(properties.keySet());
        for (Config sub : subConfigs) {
            propNames.addAll(sub.getNames());
        }
    }
}

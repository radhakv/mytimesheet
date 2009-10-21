package moten.david.kv;

public interface KeyValueService {
	String get(String key);

	void put(String key, String value);

	void append(String key, String value);

	void copy(String fromKey, String toKey);
}

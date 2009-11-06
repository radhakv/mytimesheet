package moten.david.kv;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class KeyValueServiceImpl implements KeyValueService {

	private static final String ENTITY_VALUE = "value";
	private static final String KV_ENTITY_TYPE = "kv";
	private final DatastoreService datastore;

	public KeyValueServiceImpl() {
		// Get a handle on the datastore itself
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	@Override
	public String get(String key) {
		// Lookup data by known key name
		try {
			Entity valueEntity = datastore.get(KeyFactory.createKey(
					KV_ENTITY_TYPE, key));
			Object value = valueEntity.getProperty(ENTITY_VALUE);
			if (value == null)
				return null;
			Text text = (Text) value;
			if (text == null)
				return null;
			else
				return text.getValue();
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	@Override
	public void put(String key, String value) {
		Entity entity = new Entity(KV_ENTITY_TYPE, key);
		if (value == null)
			entity.setProperty(ENTITY_VALUE, null);
		else
			entity.setProperty(ENTITY_VALUE, new Text(value));
		datastore.put(entity);
	}

	@Override
	public void append(String key, String appendThis) {
		Entity entity;
		try {
			entity = datastore.get(KeyFactory.createKey(KV_ENTITY_TYPE, key));
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e);
		}
		if (entity == null)
			put(key, appendThis);
		else {
			Text currentValue = (Text) entity.getProperty(ENTITY_VALUE);
			String newValue = (currentValue == null ? appendThis : currentValue
					.getValue()
					+ appendThis);
			entity.setProperty(ENTITY_VALUE, new Text(newValue));
			datastore.put(entity);
		}
	}

	@Override
	public void copy(String fromKey, String toKey) {
		put(toKey, get(fromKey));
	}

}

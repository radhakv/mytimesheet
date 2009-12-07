package moten.david.kv;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.inject.name.Named;

public class KeyValueServiceRetryer implements KeyValueService {

	private final KeyValueService service;
	private final int maxAttempts = 2;

	public KeyValueServiceRetryer(@Named("inner") KeyValueService service) {
		this.service = service;

	}

	@Override
	public void append(String key, String value) {
		int attempt = 1;
		RuntimeException exception = null;
		while (attempt <= maxAttempts)
			try {
				service.append(key, value);
				return;
			} catch (DatastoreTimeoutException e) {
				attempt++;
				exception = e;
			}
		throw exception;

	}

	@Override
	public void copy(String fromKey, String toKey) {
		int attempt = 1;
		RuntimeException exception = null;
		while (attempt <= maxAttempts)
			try {
				service.copy(fromKey, toKey);
				return;
			} catch (DatastoreTimeoutException e) {
				attempt++;
				exception = e;
			}
		throw exception;
	}

	@Override
	public String get(String key) {
		int attempt = 1;
		RuntimeException exception = null;
		while (attempt <= maxAttempts)
			try {
				return service.get(key);
			} catch (DatastoreTimeoutException e) {
				attempt++;
				exception = e;
			}
		throw exception;
	}

	@Override
	public void put(String key, String value) {
		int attempt = 1;
		RuntimeException exception = null;
		while (attempt <= maxAttempts)
			try {
				service.put(key, value);
				return;
			} catch (DatastoreTimeoutException e) {
				attempt++;
				exception = e;
			}
		throw exception;

	}

}

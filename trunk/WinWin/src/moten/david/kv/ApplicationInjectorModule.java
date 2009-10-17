package moten.david.kv;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(KeyValueService.class).to(KeyValueServiceImpl.class).in(
				Scopes.SINGLETON);
	}

}

package moten.david.words.nines;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WordProvider.class).in(Scopes.SINGLETON);

	}

}

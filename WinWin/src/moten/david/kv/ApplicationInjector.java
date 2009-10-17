package moten.david.kv;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ApplicationInjector {

	private static final Injector injector = Guice
			.createInjector(new ApplicationInjectorModule());

	public static Injector getInjector() {
		return injector;
	}

}

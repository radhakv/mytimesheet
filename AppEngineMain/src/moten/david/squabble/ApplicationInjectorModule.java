package moten.david.squabble;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.AbstractModule;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EntityManagerFactory.class)
				.toInstance(
						Persistence
								.createEntityManagerFactory("transactions-optional"));
	}

}

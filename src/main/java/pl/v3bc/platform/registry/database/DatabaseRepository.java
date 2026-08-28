package pl.v3bc.platform.registry.database;

import dev.morphia.Datastore;

import java.util.Optional;

public abstract class DatabaseRepository<T, ID> {

    protected final Datastore datastore;
    protected final Class<T> entityClass;

    protected DatabaseRepository(Datastore datastore, Class<T> entityClass) {
        this.datastore = datastore;
        this.entityClass = entityClass;
    }

    public abstract Optional<T> find(ID id);
    public abstract void save(T entity);
}


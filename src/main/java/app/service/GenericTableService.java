package app.service;

import app.db.GenericRow;
import app.db.GenericTableRepository;


import java.sql.SQLException;
import java.util.List;

public class GenericTableService {

    private final GenericTableRepository repository;

    public GenericTableService(GenericTableRepository repository) {
        this.repository = repository;
    }

    public List<String> getColumnNames() throws SQLException {
        return repository.getColumnNames();
    }

    public List<GenericRow> getAll() throws SQLException {
        return repository.findAll();
    }

    public void create(GenericRow row) throws SQLException {
        repository.insert(row);
    }

    public void update(GenericRow row) throws SQLException {
        repository.update(row);
    }

    public void delete(Object pkValue) throws SQLException {
        repository.delete(pkValue);
    }
}


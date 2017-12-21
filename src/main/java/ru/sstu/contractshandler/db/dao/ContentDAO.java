package ru.sstu.contractshandler.db.dao;

import ru.sstu.contractshandler.db.models.Content;

import java.util.List;

public interface ContentDAO {
    void save(Content content);

    List<Content> getAll();
}

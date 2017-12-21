package ru.sstu.contractshandler.db.services;

import ru.sstu.contractshandler.db.models.Content;

import java.util.List;

public interface ContentService {
    void save(Content content);

    List<Content> getAll();
}

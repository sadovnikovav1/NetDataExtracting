package ru.sstu.contractshandler.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sstu.contractshandler.db.dao.ContentDAO;
import ru.sstu.contractshandler.db.models.Content;

import java.util.List;

@Service("storageService")
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDAO dao;

    private ContentServiceImpl() {
    }

    @Transactional
    @Override
    public void save(Content content) {
        dao.save(content);
    }

    @Override
    public List<Content> getAll() {
        return dao.getAll();
    }
}

package ru.sstu.contractshandler.db.dao;

import org.springframework.stereotype.Repository;
import ru.sstu.contractshandler.db.models.Content;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ContentDAOImpl implements ContentDAO {
    @PersistenceContext
    private EntityManager em;

    ContentDAOImpl() {
    }

    @Override
    public void save(Content content) {
        em.persist(content);
    }

    @Override
    public List<Content> getAll() {
        String query = "select content from " + Content.getEntityName() + " as content order by content.id";
        return em.createQuery(query, Content.class).getResultList();
    }
}

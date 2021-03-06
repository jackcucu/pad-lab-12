package md.jack.service;

import md.jack.model.db.Topic;
import md.jack.service.abs.EntityService;

public interface TopicService extends EntityService<Topic>
{
    void deleteByName(String name);

    Topic getByName(String name);
}

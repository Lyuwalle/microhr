package org.lyuwalle.vhr.service;

import org.lyuwalle.vhr.mapper.NationMapper;
import org.lyuwalle.vhr.model.Nation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author lyuWalle
 * @Date 2020/12/22 13:48
 * @Version 1.0
 */
@Service
public class NationService {

    @Autowired
    NationMapper nationMapper;
    public List<Nation> getAllNations() {
        return nationMapper.getAllNations();
    }
}

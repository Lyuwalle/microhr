package org.lyuwalle.vhr.service;

import org.lyuwalle.vhr.mapper.PoliticsstatusMapper;
import org.lyuwalle.vhr.model.Politicsstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author lyuWalle
 * @Date 2020/12/22 13:48
 * @Version 1.0
 */
@Service
public class PoliticsstatusService {

    @Autowired
    PoliticsstatusMapper politicsstatusMapper;
    public List<Politicsstatus> getAllPoliticsstatus() {
        return politicsstatusMapper.getAllPoliticsstatus();
    }
}

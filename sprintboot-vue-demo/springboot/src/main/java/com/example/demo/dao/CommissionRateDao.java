package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.CommissionRate;
import com.example.demo.mapper.CommissionRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommissionRateDao {

    @Autowired
    private CommissionRateMapper commissionRateMapper;
    @Autowired
    private UserDao userDao;

    public static int ORDER_ADD = 0;
    public static int ORDER_MINU = 1;

    public double getRate(int grade){//付款时可调用

        LambdaQueryWrapper<CommissionRate> wrapper = Wrappers.<CommissionRate>lambdaQuery();
        wrapper.eq(CommissionRate::getGrade,grade);
        CommissionRate rate = commissionRateMapper.selectOne(wrapper);
        if (rate!=null){
            return rate.getRate();
        }else {
            return -1;
        }
    }
}

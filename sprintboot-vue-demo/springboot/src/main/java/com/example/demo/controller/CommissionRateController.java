package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dao.CommissionRateDao;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/cms")
public class CommissionRateController {
    @Resource
    CommissionRateDao commissionRateDao;


    @GetMapping("/{grade}")
    public Result<?> getRate(@PathVariable String grade){
        int g = Integer.parseInt(grade);
        return Result.success(
                commissionRateDao.getRate(g)
        );
    }

}

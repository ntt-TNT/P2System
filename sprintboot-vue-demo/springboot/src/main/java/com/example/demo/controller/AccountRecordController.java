package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dao.AccountRecordDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.AccountRecord;
import com.example.demo.mapper.AccountRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountRecordController {
    @Resource
    AccountRecordMapper accountRecordMapper;
    @Autowired
    AccountRecordDao accountRecordDao;
    @Autowired
    UserDao userDao;

    @PostMapping
    public Result<?> save(@RequestBody AccountRecord accountRecord){//只有存，支出是自动计算地

        double ret = accountRecordDao.addAccount(accountRecord);
        if(ret==1){
            return Result.success();
        }else if (ret == -1){
            return Result.error("-1","余额不足");
        }else{
            return Result.error("-2","插入失败");
        }
        //手动增加uid后插入数据库
    }
    @PutMapping
    public Result<?> update(@RequestBody AccountRecord accountRecord){
        //存入时间//只有存，支出是自动计算地
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        accountRecord.setTime(ts);
        accountRecord.setAid(1);
        double account = userDao.addAccount(accountRecord.getAmount(),accountRecord.getUid());
        accountRecord.setAccount(account+accountRecord.getAmount());
        if(account!=-1){
            int ret = accountRecordMapper.insert(accountRecord);
            if(ret==1){
                return Result.success();
            }else{
                return Result.error("2","insert record fail!");
            }
        }else{
            return Result.error("1","update user fail!");
        }
        //手动增加uid后插入数据库


    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText){
        Page<AccountRecord> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<AccountRecord> wrapper = Wrappers.<AccountRecord>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(AccountRecord::getOid, searchText);
        }
        accountRecordMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);
    }
    @GetMapping("/{id}")
    public Result<?> findRecord(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,@PathVariable String id){
        Page<AccountRecord> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<AccountRecord> wrapper = Wrappers.<AccountRecord>lambdaQuery();
        wrapper.eq(AccountRecord::getUid,id);
        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(AccountRecord::getOid, searchText);
        }
        accountRecordMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);
    }

}

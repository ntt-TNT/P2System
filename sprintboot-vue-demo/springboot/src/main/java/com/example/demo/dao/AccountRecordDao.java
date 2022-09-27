package com.example.demo.dao;

import com.example.demo.entity.AccountRecord;
import com.example.demo.mapper.AccountRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;

@Repository
public class AccountRecordDao {

    @Autowired
    private AccountRecordMapper accountRecordMapper;
    @Autowired
    private UserDao userDao;

    public static int ORDER_ADD = 0;
    public static int ORDER_MINU = 1;

    public int minuAccount(AccountRecord accountRecord){//付款时可调用
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        accountRecord.setTime(ts);
        double account = userDao.minuAccount(accountRecord.getAmount(),accountRecord.getUid());
        System.out.println("account");
        if(account>=0){
            accountRecord.setAccount(account-accountRecord.getAmount());
            int ret = accountRecordMapper.insert(accountRecord);
            if(ret==1){
                return 1;//插入成功
            }else{
                return -2;//插入失败
            }
        }else{
            return -1;//余额不足
        }
    }
    public boolean judge(AccountRecord accountRecord){
        return userDao.judgeMinu(accountRecord.getAmount(),accountRecord.getUid());
    }
    public int updateAccount(AccountRecord accountRecord){//付款时可调用
        int ret = accountRecordMapper.updateById(accountRecord);
        if(ret==1){
            return 1;//更新成功
        }else{
            return -2;//更新失败
        }
    }
    public int addAccount(AccountRecord accountRecord){//付款时可调用
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        accountRecord.setTime(ts);
        double account = userDao.addAccount(accountRecord.getAmount(),accountRecord.getUid());
        accountRecord.setAccount(account+accountRecord.getAmount());
        if(account>=0){
            int ret = accountRecordMapper.insert(accountRecord);
            if(ret==1){
                return 1;//插入成功
            }else{
                return -2;//插入失败
            }
        }else{
            return -1;//余额不足
        }
    }
}

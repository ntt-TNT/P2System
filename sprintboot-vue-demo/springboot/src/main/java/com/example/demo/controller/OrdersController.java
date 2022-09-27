package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dao.*;
import com.example.demo.entity.*;
import com.example.demo.mapper.CartMapper;
import com.example.demo.mapper.OrdersMapper;
import com.example.demo.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Resource
    OrdersMapper ordersMapper;
    @Resource
    CartMapper cartMapper;
    @Resource
    UsersMapper usersMapper;
    @Autowired
    OrdersDao ordersDao;
    @Autowired
    GoodsDao goodsDao;
    @Autowired
    UserDao userDao;
    @Autowired
    CommissionRateDao commissionRateDao;
    @Autowired
    AccountRecordDao accountRecordDao;

    @PostMapping("/insertOrders")
    public Result<?> saveOrders(@RequestBody(required = true) List<Orders> orders){
        System.out.println("saveOrders");
        for (int i=0; i<orders.size(); i++){
            Goods good = goodsDao.selectOne(orders.get(i).getGid()+"");
            Users merchant = userDao.selectOne(good.getMid()+"");
            orders.get(i).setMid(good.getMid());
            orders.get(i).setCommission(commissionRateDao.getRate(merchant.getGrade()));
            //判断是否能增加支出记录
            AccountRecord accountRecord = new AccountRecord();
            accountRecord.setType(1);
            accountRecord.setAmount(orders.get(i).getSum());
            accountRecord.setUid(orders.get(i).getUid());
            if (!accountRecordDao.judge(accountRecord)){
                return Result.error("-1","余额不足");
            }
            if (ordersDao.Insert(orders.get(i))!=-1){
                System.out.println("oid:"+orders.get(i).getOid());
                //新增支出记录
                accountRecord.setOid(orders.get(i).getOid());
                int ret = accountRecordDao.minuAccount(accountRecord);
                if(ret==1){
                }else{
                    return Result.error("-1","支出添加失败");
                }
                //清理购物车
                LambdaQueryWrapper<Cart> wrapper = Wrappers.<Cart>lambdaQuery();
                int gid = orders.get(i).getGid();
                int uid = orders.get(i).getUid();
                cartMapper.delete(wrapper.eq(Cart::getGid,gid).eq(Cart::getUid,uid));

                //更改账户积分
                Users users = usersMapper.selectOne(Wrappers.<Users>lambdaQuery().eq(Users::getUid,uid));
                users.setPoint(0);
                userDao.Update(users);
            }else {
                return Result.error("-1","订单提交失败");
            }
        }
        return Result.success();
    }

    @PostMapping
    public Result<?> save(@RequestBody Orders orders){
        //手动增加uid后插入数据库
        int ret = ordersDao.Insert(orders);
        if(ret!=-1){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }

    }
    @PutMapping
    public Result<?> update(@RequestBody Orders orders,@RequestParam() int status){
        orders.setStatus(status);
        if (status==2){
            orders.setSucTime(new Timestamp(new Date().getTime()));
        }else if (status==-1){
            orders.setRetTime(new Timestamp(new Date().getTime()));
        }
        boolean ret = ordersDao.Update(orders);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id){
        //手动增加uid后插入数据库
        //新增用户收入记录
        Orders orders = ordersDao.selectOne(Integer.parseInt(id));
        AccountRecord accountRecord = new AccountRecord();
        accountRecord.setType(0);
        accountRecord.setAmount(orders.getSum());
        accountRecord.setOid(orders.getOid());
        accountRecord.setUid(orders.getUid());
        int res = accountRecordDao.addAccount(accountRecord);
        if(res==1){
        }else if (res==-1){
            return Result.error("-1","余额不足");
        }else{
            return Result.error("-1","支出添加失败");
        }
        boolean ret = ordersDao.DeleteByID(id);
        if(ret){
            return Result.success();
        }else{
            return Result.error("-1","未删除订单");
        }
    }

    @GetMapping("/user")
    public Result<?> UfindPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam() String uid,
                              @RequestParam() int status){
        Page<Orders> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        if (status==5){
            wrapper.eq(Orders::getUid,uid);
        }else {
            wrapper.eq(Orders::getUid,uid).eq(Orders::getStatus,status);
        }

        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(Orders::getGname, searchText);
        }
        ordersMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);

    }

    @GetMapping("/merchant")
    public Result<?> MfindPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam() String mid,
                              @RequestParam() int status){
        Page<Orders> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        wrapper.eq(Orders::getMid,mid).eq(Orders::getStatus,status);
        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(Orders::getGname, searchText);
        }
        ordersMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);

    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText){
        Page<Orders> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(Orders::getOid, searchText);
        }
        ordersMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);

    }
}

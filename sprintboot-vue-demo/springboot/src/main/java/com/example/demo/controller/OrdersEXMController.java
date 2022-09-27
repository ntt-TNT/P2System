package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dao.*;
import com.example.demo.entity.*;
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
@RequestMapping("/exmO")
public class OrdersEXMController {
    @Resource
    OrdersMapper ordersMapper;
    @Resource
    UsersMapper usersMapper;
    @Autowired
    OrdersDao ordersDao;
    @Autowired
    GoodsDao goodsDao;
    @Autowired
    UserDao userDao;
    @Autowired
    AccountRecordDao accountRecordDao;
    @Autowired
    ReviewDao reviewDao;

    //发货
    @PutMapping("/out")
    public Result<?> out(@RequestBody Orders orders){
        if(orders.getStatus()==0){//0:未发货
            //限制一个oid只能发货一次
//            updateWrapper.set("Status",1);//已发货
            orders.setStatus(1);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
//            updateWrapper.set("SentTime",ts);//发货时间
            orders.setSentTime(ts);
            goodsDao.minuStorage(orders.getNumber(),orders.getGid());
            System.out.println("发货");
        }else {
            return Result.error("2","订单不处于未发货状态！");
        }
        int ret = ordersMapper.updateById(orders);
        if(ret==1){
            return Result.success();
        }else{
            return Result.error("1","更新失败");
        }
    }

    @GetMapping("/getOut/{id}")
    public Result<?> getOut(@RequestParam(defaultValue = "1") Integer pageNumber,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(defaultValue = "") String searchText,
                            @RequestParam(defaultValue = "") String type,@PathVariable String id){//需要提供查询类型，若无则默认查询所有
        Page<Orders> ordersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        wrapper.eq(Orders::getMid,id).ge(Orders::getStatus,0);
        return getResult(searchText, type, ordersPage, wrapper);

    }

    @GetMapping("/getOrder/{id}")
    public Result<?> getOrder(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam(defaultValue = "") String type,@PathVariable String id){//需要提供查询类型，若无则默认查询所有
        Page<Orders> ordersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        wrapper.eq(Orders::getMid,id);
        return getResult(searchText, type, ordersPage, wrapper);
    }

    private Result<?> getResult(@RequestParam(defaultValue = "") String searchText,
                                @RequestParam(defaultValue = "") String type,
                                Page<Orders> ordersPage,
                                LambdaQueryWrapper<Orders> wrapper) {
        if (StrUtil.isNotBlank(searchText)&&type.equals("status")){
            //状态
            wrapper.like(Orders::getStatus,searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("uid")){
            //用户
            wrapper.like(Orders::getUid,searchText);
        }
        ordersMapper.selectPage(ordersPage,wrapper);
        return Result.success(ordersPage);
    }

    @GetMapping("/getBack/{id}")
    public Result<?> getBack(@RequestParam(defaultValue = "1") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String searchText,
                             @RequestParam(defaultValue = "") String type,@PathVariable String id){//需要提供查询类型，若无则默认查询所有
        Page<Orders> ordersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        wrapper.eq(Orders::getMid,id).lt(Orders::getStatus,0);
        return getResult(searchText, type, ordersPage, wrapper);

    }
    //获取对应商品列表
    @PutMapping("/getGoods")
    public Result<?> getGoods(@RequestBody Orders orders){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("Oid",orders.getOid());
        boolean ret = ordersDao.UpdateStatus(updateWrapper);
        if(orders.getStatus()==1){//已发货
            updateWrapper.set("Status",2);//已收货
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            updateWrapper.set("SubSucTime",ts);//成交时间
            if(ret){
                return Result.success();
            }else{
                return Result.error("1","update fail!");
            }
        }else {//弹窗提示，若确定收货啧先调用out改为1再调用getGoods
            return Result.success("商品还未发货");
        }

    }

    @PutMapping("/agreeBack")//同意退货
    public Result<?> agreeBack(@RequestBody Orders orders){
        if(orders.getStatus()==-1){//退货中
            orders.setStatus(-2);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            orders.setRetSecTime(ts);
//            //用户金额返还
//            Users users = usersMapper.selectOne(Wrappers.<Users>lambdaQuery().eq(Users::getUid,orders.getUid()));
//            users.setAccount(users.getAccount()+orders.getSum());
//            userDao.Update(users);

            //新增用户收入记录
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

            int ret = ordersMapper.updateById(orders);
            if(ret ==1){
                goodsDao.addStorage(orders.getNumber(),orders.getGid());
                return Result.success();
            }else{
                return Result.error("1","更新数据失败");
            }
        }else {//弹窗提示，若确定收货则先调用out改为1再调用getGoods
            return Result.error("2","商品未处于退货中");
        }
    }
    @PutMapping("/reject")//拒绝退货
    public Result<?> reject(@RequestBody Orders orders){
        //手动增加uid后插入数据库
        if(orders.getStatus()==-1){//退货中
            orders.setStatus(-3);

            //用户
            Users users = userDao.selectOne(orders.getUid()+"");
            users.setPoint(users.getPoint()+(int) orders.getSum());
            userDao.Update(users);
            //商品销量增加
            Goods goods = goodsDao.selectOne(orders.getGid()+"");
            System.out.println(goods);
            goods.setSale(goods.getSale()+orders.getNumber());
            //商家
            Users merchant = userDao.selectOne(orders.getMid()+"");
            merchant.setTurnover(merchant.getTurnover()+orders.getSum());
            //商品好评率
            List<Review> reviewList = reviewDao.OrderBy("time",1,orders.getGid()+"",2);
            double sumrate=0;
            for (int i=0; i<reviewList.size(); i++){
                sumrate += reviewList.get(i).getStar();
            }
            sumrate += 0;
            goods.setLikeRate(sumrate/(reviewList.size()+1));
            //手续费
            double commission = orders.getCommission();
            merchant.setAccount(merchant.getAccount()+orders.getSum()*(1-commission));
            goodsDao.Update(goods);
            userDao.Update(merchant);
            ordersDao.Update(orders);

            //新增商家收入记录
            AccountRecord accountRecord = new AccountRecord();
            accountRecord.setType(0);
            accountRecord.setAmount(orders.getSum());
            accountRecord.setOid(orders.getOid());
            accountRecord.setUid(orders.getMid());
            int res = accountRecordDao.addAccount(accountRecord);
            if(res==1){
            }else if (res==-1){
                return Result.error("-1","余额不足");
            }else{
                return Result.error("-1","支出添加失败");
            }


            int ret = ordersMapper.updateById(orders);
            if(ret == 1){
                return Result.success();
            }else{
                return Result.error("1","更新数据失败");
            }
        }else {
            return Result.error("2","商品未处于退货中");
        }
    }

    @DeleteMapping//删除订单
    public Result<?> delete(@RequestBody Orders orders){
        boolean ret = ordersDao.DeleteByID(orders.getOid()+"");
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }
    }
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam(defaultValue = "") String type){//需要提供查询类型，若无则默认查询所有
        Page<Orders> ordersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)&&type.equals("oid")){
            wrapper.like(Orders::getOid, searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("gid")){//商品
            wrapper.like(Orders::getGid,searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("status")){//状态
            wrapper.like(Orders::getStatus,searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("uid")){//用户
            wrapper.like(Orders::getUid,searchText);
        }
        ordersMapper.selectPage(ordersPage,wrapper);
        return Result.success(ordersPage);

    }
    @GetMapping("/{findStatus}")
    public Result<?> findStatus(@RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(defaultValue = "") String searchText,
                                @RequestParam(defaultValue = "") String type){//需要提供查询类型，若无则默认查询所有
        Page<Orders> ordersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.<Orders>lambdaQuery();
        return getResult(searchText, type, ordersPage, wrapper);
    }
}

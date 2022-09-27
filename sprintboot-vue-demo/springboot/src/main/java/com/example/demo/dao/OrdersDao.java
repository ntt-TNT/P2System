package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.Orders;
import com.example.demo.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class OrdersDao {

    @Autowired
    private OrdersMapper ordersMapper;

    public static int ORDER_ASC = 0;
    public static int ORDER_DESC = 1;

    public List<Orders> getAll(){
        System.out.println("-------测试查询所有商品-------");
        List<Orders> usersList=ordersMapper.selectList(null);
        System.out.println("共"+usersList.size()+"人");
        usersList.forEach(System.out::println);
        return usersList;
    }

    public int Insert(Orders orders){
        try{
            //插入新订单需要做的事情如下：
            //插入提交时间，状态位0，计算手续费
            int result = ordersMapper.insert(orders);
            System.out.println("插入oid为:"+orders);
            //判断插入结果
            return orders.getOid();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public boolean Update(Orders orders){
        try{
            int result = ordersMapper.updateById(orders);
            System.out.println("update result"+result);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean UpdateStatus(UpdateWrapper updateWrapper){
        try{
            int result = ordersMapper.update(null,updateWrapper);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean DeleteByID(String id){
        try{
            int result = ordersMapper.deleteById(id);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public List<Orders> Select(HashMap<String, Object> map){
        try{
            List<Orders> usersList = ordersMapper.selectByMap(map);
            usersList.forEach(System.out::println);
            return usersList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public boolean Delete(HashMap<String, Object> map){
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Uid",id);
        try{
            int result = ordersMapper.deleteByMap(map);
            //判断result
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean Delete(Orders orders){
        try{
            int id=orders.getGid();
            int result = ordersMapper.deleteById(id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public List<Orders> OrderBy(String column,int type){
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        if(type== OrdersDao.ORDER_ASC){//升序
            wrapper.orderByAsc(column);
        }else if(type== OrdersDao.ORDER_DESC){
            wrapper.orderByDesc(column);
        }

        List<Orders> usersList =  ordersMapper.selectList(wrapper);
        usersList.forEach(System.out::println);
        return usersList;
    }

    public Orders selectOne(int id){
        Orders orders = ordersMapper.selectOne(Wrappers.<Orders>lambdaQuery().eq(Orders::getOid,id));
        return orders;
    }
}

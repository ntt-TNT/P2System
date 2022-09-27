package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.Cart;
import com.example.demo.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CartDao {

    @Autowired
    private CartMapper cartMapper;

    public static int ORDER_ASC = 0;
    public static int ORDER_DESC = 1;

    public List<Cart> getAll(){
        System.out.println("-------测试查询购物车-------");
        List<Cart> cartList=cartMapper.selectList(null);
        System.out.println("共"+cartList.size()+"件商品");
        cartList.forEach(System.out::println);
        return cartList;
    }

    public Cart exist(String gid,String uid){
        LambdaQueryWrapper<Cart> wrapper = Wrappers.<Cart>lambdaQuery();
        Cart res = cartMapper.selectOne(wrapper.eq(Cart::getGid,gid).eq(Cart::getUid,uid));
        return res;
    }

    public boolean Insert(Cart cart){
        try{
            int result = cartMapper.insert(cart);
            System.out.println("insert result"+result);
            //判断插入结果
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean Update(Cart cart){
        try{
            int result = cartMapper.updateById(cart);
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
            int result = cartMapper.update(null,updateWrapper);
            System.out.println("update result"+result);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean DeleteByID(String id){
        try{
            int result = cartMapper.deleteById(id);
            System.out.println("update result"+result);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public List<Cart> Select(Map<String, Object> map){
        try{
            List<Cart> cartList = cartMapper.selectByMap(map);
            cartList.forEach(System.out::println);
            return cartList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public boolean Delete(HashMap<String, Object> map){
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Uid",id);
        try{
            int result = cartMapper.deleteByMap(map);
            //判断result
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean Delete(Cart cart){
        try{
            int id=cart.getCid();
            int result = cartMapper.deleteById(id);
            System.out.println("result="+result);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public List<Cart> OrderBy(String column,int type){
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        if(type== CartDao.ORDER_ASC){//升序
            wrapper.orderByAsc(column);
        }else if(type== CartDao.ORDER_DESC){
            wrapper.orderByDesc(column);
        }

        List<Cart> cartList =  cartMapper.selectList(wrapper);
        cartList.forEach(System.out::println);
        return cartList;
    }
}

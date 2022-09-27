package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.Goods;
import com.example.demo.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class GoodsDao {

    @Autowired
    private GoodsMapper goodsMapper;

    public static int ORDER_ASC = 0;
    public static int ORDER_DESC = 1;

    public List<Goods> getAll(){
        System.out.println("-------测试查询所有商品-------");
        List<Goods> usersList=goodsMapper.selectList(null);
        System.out.println("共"+usersList.size()+"人");
        usersList.forEach(System.out::println);
        return usersList;
    }

    public boolean Insert(Goods goods){
        try{
            int result = goodsMapper.insert(goods);
            //判断插入结果
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean Update(Goods goods){
        try{
            int result = goodsMapper.updateById(goods);
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
            int result = goodsMapper.update(null,updateWrapper);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean DeleteByID(String id){
        try{
            int result = goodsMapper.deleteById(id);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Goods selectOne(String gid){
        System.out.println(gid);
        Goods good = goodsMapper.selectOne(Wrappers.<Goods>lambdaQuery().eq(Goods::getGid,gid));
        return good;
    }

    public List<Goods> Select(HashMap<String, Object> map){
        try{
            List<Goods> usersList = goodsMapper.selectByMap(map);
            usersList.forEach(System.out::println);
            return usersList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public int SelectStorage(int gid){
        try{
            Goods user = goodsMapper.selectById(gid);
            if (user!=null){
                return user.getStorage();
            }
            return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }

    public boolean UpdateStorage(int gid,int num){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("Gid",gid);
        updateWrapper.set("Storage",num);
        int result = goodsMapper.update(null,updateWrapper);
        if(result == 1){
            System.out.println("gid:"+gid+"库存更新成功");
            return true;
        }else
            return false;
    }

    public boolean Delete(HashMap<String, Object> map){
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Uid",id);
        try{
            int result = goodsMapper.deleteByMap(map);
            //判断result
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean Delete(Goods goods){
        try{
            int id=goods.getGid();
            int result = goodsMapper.deleteById(id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public List<Goods> OrderBy(String column,int type){
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        if(type== GoodsDao.ORDER_ASC){//升序
            wrapper.orderByAsc(column);
        }else if(type== GoodsDao.ORDER_DESC){
            wrapper.orderByDesc(column);
        }

        List<Goods> usersList =  goodsMapper.selectList(wrapper);
        usersList.forEach(System.out::println);
        return usersList;
    }

    public double addStorage(double amount,int gid){
        try{
            LambdaQueryWrapper<Goods> wrapper = Wrappers.<Goods>lambdaQuery();
            Goods res = goodsMapper.selectOne(wrapper.eq(Goods::getGid,gid));
            double sum = res.getStorage()+amount;
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("Gid",gid);
            updateWrapper.set("Storage",sum);
            int result = goodsMapper.update(null,updateWrapper);
            return sum;//返回最终值
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }

    public double minuStorage(double amount,int gid){
        try{
            LambdaQueryWrapper<Goods> wrapper = Wrappers.<Goods>lambdaQuery();
            Goods res = goodsMapper.selectOne(wrapper.eq(Goods::getGid,gid));
            double sum = res.getStorage()-amount;
            if(sum<0){
                return sum;
            }
            System.out.println("sum:"+sum+"\namount:"+amount+"\naccount:"+res.getStorage());
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("Gid",gid);
            updateWrapper.set("Storage",sum);
            int result = goodsMapper.update(null,updateWrapper);
            return sum;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }
}

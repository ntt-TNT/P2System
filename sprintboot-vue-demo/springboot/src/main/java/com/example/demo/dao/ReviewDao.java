package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.demo.entity.Review;
import com.example.demo.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReviewDao {

    @Autowired
    private ReviewMapper reviewMapper;

    public static int ORDER_ASC = 0;
    public static int ORDER_DESC = 1;

    public List<Review> getAll(){
        System.out.println("-------测试查询评价-------");
        List<Review> reviewList=reviewMapper.selectList(null);
        System.out.println("共"+reviewList.size()+"个评价");
        reviewList.forEach(System.out::println);
        return reviewList;
    }


    public boolean Insert(Review review){
        try{
            int result = reviewMapper.insert(review);
            System.out.println("insert result"+result);
            //判断插入结果
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean Update(Review review){
        try{
            int result = reviewMapper.updateById(review);
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
            int result = reviewMapper.update(null,updateWrapper);
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
            int result = reviewMapper.deleteById(id);
            System.out.println("update result"+result);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public List<Review> Select(Map<String, Object> map){
        try{
            List<Review> reviewList = reviewMapper.selectByMap(map);
            reviewList.forEach(System.out::println);
            return reviewList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public boolean Delete(HashMap<String, Object> map){
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Uid",id);
        try{
            int result = reviewMapper.deleteByMap(map);
            //判断result
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean Delete(Review review){
        try{
            int id=review.getRid();
            int result = reviewMapper.deleteById(id);
            System.out.println("result="+result);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public List<Review> OrderBy(String column,int order,String gid,int type){
        QueryWrapper<Review> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type).eq("Toid",gid);
        if(order== ReviewDao.ORDER_ASC){//升序
            wrapper.orderByAsc(column);
        }else if(order== ReviewDao.ORDER_DESC){
            wrapper.orderByDesc(column);
        }

        List<Review> reviewList =  reviewMapper.selectList(wrapper);
        reviewList.forEach(System.out::println);
        return reviewList;
    }
}

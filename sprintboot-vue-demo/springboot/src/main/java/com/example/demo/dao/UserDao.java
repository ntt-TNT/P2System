package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private UsersMapper usersMapper;

    public static int ORDER_ASC = 0;
    public static int ORDER_DESC = 1;

    public List<Users> getAll(){
        System.out.println("-------测试查询所有用户-------");
        List<Users> usersList=usersMapper.selectList(null);
        System.out.println("共"+usersList.size()+"人");
        usersList.forEach(System.out::println);
        return usersList;
    }

    public boolean Insert(Users users){
        try{
            int result = usersMapper.insert(users);
            System.out.println("insert result"+result);
            //判断插入结果
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean Update(Users users){
        try{
            int result = usersMapper.updateById(users);
            //判断插入结果返回值
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean UpdateStatus(UpdateWrapper updateWrapper){
        try{
            int result = usersMapper.update(null,updateWrapper);
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
            int result = usersMapper.deleteById(id);
            System.out.println("update result"+result);
            //判断插入结果返回值
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public List<Users> Select(HashMap<String, Object> map){
        try{
            List<Users> usersList = usersMapper.selectByMap(map);
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
            int result = usersMapper.deleteByMap(map);
            //判断result
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean Delete(Users users){
        try{
            int id=users.getUid();
            int result = usersMapper.deleteById(id);
            System.out.println("result="+result);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public List<Users> OrderBy(String column,int type){
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if(type==UserDao.ORDER_ASC){//升序
            wrapper.orderByAsc(column);
        }else if(type==UserDao.ORDER_DESC){
            wrapper.orderByDesc(column);
        }

        List<Users> usersList =  usersMapper.selectList(wrapper);
        usersList.forEach(System.out::println);
        return usersList;
    }

    public double addAccount(double amount,int uid){
        try{
            LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
            Users res = usersMapper.selectOne(wrapper.eq(Users::getUid,uid));
            double sum = res.getAccount()+amount;
            System.out.println("sum:"+sum+"\namount:"+amount+"\naccount:"+res.getAccount());
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("Uid",uid);
            updateWrapper.set("Account",sum);
            int result = usersMapper.update(null,updateWrapper);
            return res.getAccount();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }
    public double minuAccount(double amount,int uid){
        try{
            LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
            Users res = usersMapper.selectOne(wrapper.eq(Users::getUid,uid));
            double sum = res.getAccount()-amount;
            if(sum<0){
                return sum;
            }
            System.out.println("sum:"+sum+"\namount:"+amount+"\naccount:"+res.getAccount());
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("Uid",uid);
            updateWrapper.set("Account",sum);
            int result = usersMapper.update(null,updateWrapper);
            return res.getAccount();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }
    public boolean judgeMinu(double amount, int uid){

        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUid,uid));
        double sum = res.getAccount()-amount;
        if(sum<0){
            return false;
        }else
            return true;
    }
    public Users selectOne(String uid){
        Users user = usersMapper.selectOne(Wrappers.<Users>lambdaQuery().eq(Users::getUid,uid));
        return user;
    }

    public boolean exist(String uid){
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUid,uid).eq(Users::getStatus,1));
        if (res!=null){
            return true;
        }else {
            return false;
        }
    }
}

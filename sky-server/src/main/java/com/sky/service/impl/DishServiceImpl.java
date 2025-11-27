package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishflavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 保存菜品的基本信息到菜品表dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        // 保存菜品对应的口味数据到菜品口味表dish_flavor
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long dishId = dish.getId();//菜品id
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(flavor->{flavor.setDishId(dishId);});
            dishflavorMapper.insertFlavorBatch(flavors);
        }

    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for(Long id:ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException("删除菜品中包含启售状态的菜品，删除失败");
            }

        }

        List<Long> setMealIdsByDishIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if(setMealIdsByDishIds.size()>0&&setMealIdsByDishIds!=null){
            throw new DeletionNotAllowedException("删除菜品中包含关联套餐的菜品，删除失败");
        }

        for(Long id:ids){
            dishMapper.deleteById(id);
            dishflavorMapper.deleteByDishId(id);
        }


    }
}

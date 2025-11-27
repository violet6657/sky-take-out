package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
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
}

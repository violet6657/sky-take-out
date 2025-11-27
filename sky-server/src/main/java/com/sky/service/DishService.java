package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface DishService {

    public void saveWithFlavor(DishDTO dishDTO);
}

package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.io.FoodRequest;
import com.soracel.onlinemenu.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest req, MultipartFile file);

    List<FoodResponse> getFoods();

    FoodResponse getSingleFood(String id);

    boolean deleteFile(String fileName);

    void deleteFood(String id);
}

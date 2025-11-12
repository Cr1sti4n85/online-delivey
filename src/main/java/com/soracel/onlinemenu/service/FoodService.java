package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.io.FoodRequest;
import com.soracel.onlinemenu.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest req, MultipartFile file);
}

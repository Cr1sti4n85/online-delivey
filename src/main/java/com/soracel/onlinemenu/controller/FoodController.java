package com.soracel.onlinemenu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soracel.onlinemenu.io.FoodRequest;
import com.soracel.onlinemenu.io.FoodResponse;
import com.soracel.onlinemenu.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodResponse> addFood(@RequestPart("food") String foodString,
                                  @RequestPart("file")MultipartFile file){

        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest request = null;
        try {
            request  = objectMapper.readValue(foodString, FoodRequest.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato inváido");
        }
         var createdFood = foodService.addFood(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);

    }

    @GetMapping
    public List<FoodResponse> getFoods(){
        return foodService.getFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse getFood(@PathVariable String id){
        return this.foodService.getSingleFood(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable String id){
        foodService.deleteFood(id);
    }
}

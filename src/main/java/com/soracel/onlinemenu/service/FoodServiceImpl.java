package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.entity.FoodEntity;
import com.soracel.onlinemenu.io.FoodRequest;
import com.soracel.onlinemenu.io.FoodResponse;
import com.soracel.onlinemenu.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService{
    private final S3Client s3Client;

    private final FoodRepository foodRepository;

    @Value("${aws.s3.bucketname}")
    private String bucketName;
    @Override
    public String uploadFile(MultipartFile file) {
        String filenameExt = file
                .getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString()+"."+filenameExt;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();
            PutObjectResponse response = s3Client
                    .putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if (response.sdkHttpResponse().isSuccessful()){
                return "https://"+bucketName+".s3.amazonaws.com/"+key;
            }
            throw new ResponseStatusException(HttpStatus
                    .INTERNAL_SERVER_ERROR,
                    "No se pudo subir la imagen");
        } catch(IOException ex){
            throw new ResponseStatusException(HttpStatus
                    .INTERNAL_SERVER_ERROR,
                    "Ocurrió un error al subir imagen");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest req, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(req);
        String imgUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imgUrl);

        newFoodEntity = foodRepository.save(newFoodEntity);
        return convertToResponse(newFoodEntity);
    }

    @Override
    public List<FoodResponse> getFoods() {
        List<FoodEntity> dataEntries = this.foodRepository.findAll();
        return dataEntries.stream().map(
                        this::convertToResponse)
                        .collect(Collectors.toList());
    }

    @Override
    public FoodResponse getSingleFood(String id) {
        FoodEntity existingFood = this.foodRepository
                .findById(id).orElseThrow(() ->
                        new RuntimeException("Item no encontrado"));

        return this.convertToResponse(existingFood);
    }

    @Override
    public boolean deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectReq);
        return true;
    }

    @Override
    public void deleteFood(String id) {
        FoodResponse response =  getSingleFood(id);
        String imageUrl = response.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);

        boolean isDeleted = deleteFile(fileName);

        if (isDeleted){
            foodRepository.deleteById(response.getId());
        }
    }

    private FoodEntity convertToEntity(FoodRequest request){
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity entity){
        return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}





















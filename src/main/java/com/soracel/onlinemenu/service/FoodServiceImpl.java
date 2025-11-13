package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.entity.FoodEntity;
import com.soracel.onlinemenu.io.FoodRequest;
import com.soracel.onlinemenu.io.FoodResponse;
import com.soracel.onlinemenu.repository.FoodRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

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





















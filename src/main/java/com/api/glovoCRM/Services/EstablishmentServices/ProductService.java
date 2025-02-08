package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.*;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.*;
import com.api.glovoCRM.Rest.Requests.DiscountProductRequest;
import com.api.glovoCRM.Rest.Requests.ProductCreateRequest;
import com.api.glovoCRM.Rest.Requests.ProductPatchRequest;
import com.api.glovoCRM.Rest.Requests.ProductUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class ProductService {

}

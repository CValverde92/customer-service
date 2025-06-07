package com.nttdata.customer_service.mapper;

import com.nttdata.customer_service.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CustomerMapper {

    public Customer toEntity(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setName(request.getName());
        customer.setType(CustomerType.valueOf(request.getType().name()));
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(mapAddress(request.getAddress()));
        customer.setBusinessInfo(mapBusinessInfo(request.getBusinessInfo()));
        customer.setStatus(CustomerStatus.ACTIVE);
        return customer;
    }

    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) {
            return null;
        }

        CustomerResponse response = new CustomerResponse();
        response.setId(entity.getId());
        response.setDocumentNumber(entity.getDocumentNumber());
        response.setName(entity.getName());
        response.setType(CustomerType.valueOf(entity.getType().name()));
        response.setEmail(entity.getEmail());
        response.setPhone(entity.getPhone());
        response.setAddress(mapAddressResponse(entity.getAddress()));
        response.setBusinessInfo(mapBusinessInfoResponse(entity.getBusinessInfo()));
        response.setStatus(entity.getStatus());

        // Manejo seguro de fechas
        if (entity.getCreatedAt() != null) {
            response.setCreatedAt(entity.getCreatedAt().toLocalDate());
        }
        if (entity.getUpdatedAt() != null) {
            response.setUpdatedAt(entity.getUpdatedAt().toLocalDate());
        }

        return response;
    }

    public void updateFromRequest(UpdateCustomerRequest request, Customer entity) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            entity.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            entity.setAddress(mapAddress(request.getAddress()));
        }
        if (request.getBusinessInfo() != null) {
            entity.setBusinessInfo(mapBusinessInfo(request.getBusinessInfo()));
        }
    }

    private Address mapAddress(Address address) {
        if (address == null) {
            return null;
        }

        Address entityAddress = new Address();
        entityAddress.setStreet(address.getStreet());
        entityAddress.setCity(address.getCity());
        entityAddress.setState(address.getState());
        entityAddress.setCountry(address.getCountry());
        entityAddress.setZipCode(address.getZipCode());
        return entityAddress;
    }

    private Address mapAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        Address responseAddress = new Address();
        responseAddress.setStreet(address.getStreet());
        responseAddress.setCity(address.getCity());
        responseAddress.setState(address.getState());
        responseAddress.setCountry(address.getCountry());
        responseAddress.setZipCode(address.getZipCode());
        return responseAddress;
    }

    private BusinessInfo mapBusinessInfo(BusinessInfo businessInfo) {
        if (businessInfo == null) {
            return null;
        }

        BusinessInfo entityBusinessInfo = new BusinessInfo();
        entityBusinessInfo.setBusinessName(businessInfo.getBusinessName());
        entityBusinessInfo.setIndustry(businessInfo.getIndustry());
        entityBusinessInfo.setTaxId(businessInfo.getTaxId());
        entityBusinessInfo.setFoundationDate(businessInfo.getFoundationDate());
        return entityBusinessInfo;
    }

    private BusinessInfo mapBusinessInfoResponse(BusinessInfo businessInfo) {
        if (businessInfo == null) {
            return null;
        }

        BusinessInfo responseBusinessInfo = new BusinessInfo();
        responseBusinessInfo.setBusinessName(businessInfo.getBusinessName());
        responseBusinessInfo.setIndustry(businessInfo.getIndustry());
        responseBusinessInfo.setTaxId(businessInfo.getTaxId());
        responseBusinessInfo.setFoundationDate(businessInfo.getFoundationDate());
        return responseBusinessInfo;
    }

    private String convertCustomerType(CustomerType type) {
        return type != null ? type.name() : null;
    }
}
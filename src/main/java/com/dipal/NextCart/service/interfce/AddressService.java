package com.dipal.NextCart.service.interfce;


import com.dipal.NextCart.dto.AddressDTO;
import com.dipal.NextCart.dto.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDTO addressDto);
}
package com.intbyte.wizbuddy.shop.service;

import com.intbyte.wizbuddy.exception.shop.BusinessNumDuplicateException;
import com.intbyte.wizbuddy.exception.shop.ShopModifyOtherEmployerException;
import com.intbyte.wizbuddy.exception.shop.ShopNotFoundException;
import com.intbyte.wizbuddy.exception.user.EmployerNotFoundException;
import com.intbyte.wizbuddy.mapper.EmployerMapper;
import com.intbyte.wizbuddy.mapper.ShopMapper;
import com.intbyte.wizbuddy.shop.domain.DeleteShopInfo;
import com.intbyte.wizbuddy.shop.domain.EditShopInfo;
import com.intbyte.wizbuddy.shop.domain.RegisterShopInfo;
import com.intbyte.wizbuddy.shop.domain.entity.Shop;
import com.intbyte.wizbuddy.shop.dto.ShopDTO;
import com.intbyte.wizbuddy.shop.repository.ShopRepository;
import com.intbyte.wizbuddy.shop.vo.response.ResponseEditShopVO;
import com.intbyte.wizbuddy.shop.vo.response.ResponseRegisterShopVO;
import com.intbyte.wizbuddy.user.domain.entity.Employer;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final EmployerMapper employerMapper;
    private final ShopMapper shopMapper;
    private final ModelMapper modelMapper;

    @Transactional
    public ResponseRegisterShopVO registerShop(String employerCode, RegisterShopInfo shopInfo) {
        if (employerMapper.getEmployer(employerCode) == null) throw new EmployerNotFoundException();
        if (shopMapper.findByBusinessNum(shopInfo.getBusinessNum()) != null) throw new BusinessNumDuplicateException();

        Shop shop = Shop.builder()
                .shopName(shopInfo.getShopName())
                .shopLocation(shopInfo.getShopLocation())
                .shopFlag(true)
                .shopOpenTime(shopInfo.getShopOpenTime())
                .businessNum(shopInfo.getBusinessNum())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .employerCode(employerCode)
                .build();
        
        shopRepository.save(shop);

        return new ResponseRegisterShopVO(shopInfo);
    }

    @Transactional
    public ResponseEditShopVO modifyShop(String employerCode, EditShopInfo modifyShopInfo) {
        int shopCode = modifyShopInfo.getShopCode();

        Employer employer = employerMapper.getEmployer(employerCode);
        Shop shop = shopMapper.findShopByShopCode(shopCode);

        validateRequest(employer, shop);

        shop.modify(modifyShopInfo);
        shopRepository.save(shop);

        return new ResponseEditShopVO(modifyShopInfo);
    }

    @Transactional
    public void deleteShop(String employerCode, DeleteShopInfo deleteShopInfo) {
        int shopCode = deleteShopInfo.getShopCode();

        Employer employer = employerMapper.getEmployer(employerCode);
        Shop shop = shopMapper.findShopByShopCode(shopCode);

        validateRequest(employer, shop);

        shop.removeRequest(deleteShopInfo);
        shopRepository.save(shop);
    }

    @Transactional
    public List<ShopDTO> getAllShop() {
        return convertToShopDTO(shopRepository.findAll());
    }

    @Transactional
    public ShopDTO getShop(int shopCode) {
        Shop shop = shopMapper.findShopByShopCode(shopCode);

        ShopDTO shopDTO = convertToShopDTO(shop);

        if (shopDTO == null) throw new ShopNotFoundException();


        return shopDTO;
    }

    private List<ShopDTO> convertToShopDTO(List<Shop> shops) {
        List<ShopDTO> shopDTOList = new ArrayList<>();
        for (Shop shop : shops) {
            ShopDTO shopDTO = new ShopDTO(
                    shop.getShopCode()
                    , shop.getShopName()
                    , shop.getShopLocation()
                    , shop.getShopFlag()
                    , shop.getShopOpenTime()
                    , shop.getBusinessNum()
                    , shop.getCreatedAt()
                    , shop.getUpdatedAt()
                    , shop.getEmployerCode());

            shopDTOList.add(shopDTO);
        }
        return shopDTOList;
    }

    private ShopDTO convertToShopDTO(Shop shop) {
        return new ShopDTO(
                shop.getShopCode()
                , shop.getShopName()
                , shop.getShopLocation()
                , shop.getShopFlag()
                , shop.getShopOpenTime()
                , shop.getBusinessNum()
                , shop.getCreatedAt()
                , shop.getUpdatedAt()
                , shop.getEmployerCode());
    }

    private void validateRequest(Employer employer, Shop shop) {
        if (!employer.getEmployerCode().equals(shop.getEmployerCode())) {
            throw new ShopModifyOtherEmployerException();
        }
    }
}
